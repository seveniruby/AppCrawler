import java.io.{ByteArrayInputStream, StringWriter}
import java.nio.charset.StandardCharsets
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPath, XPathFactory, _}

import io.appium.java_client.remote.MobileCapabilityType
import io.appium.java_client.{TouchAction, AppiumDriver}
import org.apache.commons.io.FileUtils
import org.apache.xml.serialize.{OutputFormat, XMLSerializer}
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{OutputType, TakesScreenshot, WebElement}
import org.scalatest.ConfigMap
import org.w3c.dom.{Attr, Document, NodeList}

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}
import scala.io.Source
import scala.reflect.io.File
import scala.util.control.Breaks._
import scala.util.{Failure, Success, Try}


/**
  * Created by seveniruby on 15/11/28.
  */
class Crawler {
  implicit var driver: AppiumDriver[WebElement] = _
  var conf = new CrawlerConf()
  val capabilities = new DesiredCapabilities()

  var pluginNames=List[String]("DemoPlugin")
  var plugins=List[Plugin]()

  private val elements: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map()
  private var isSkip = false
  /** 点击顺序, 留作画图用 */
  val clickedList = ListBuffer[String]()

  var preTimeStamp="0"
  var nowTimeStamp="0"
  val timestamp = getTimeStamp()
  var md5Last = ""
  var automationName = "appium"
  var platformName = ""

  var screenWidth = 0
  var screenHeight = 0

  //意义不大. 并不是真正的层次
  var depth = 0
  var pageSource = ""
  private var pageDom: Document = null
  private var img_index = 0
  private var backRetry = 0
  private var swipeRetry=0
  private var needExit = false

  /** 当前的url路径 */
  var url = ""
  val urlStack = mutable.Stack[String]()

  def setupApp(app: String = "", url: String = ""): Unit = {
    conf.capability.foreach(kv=>capabilities.setCapability(kv._1, kv._2))
    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))
  }

  def getDeviceInfo(): Unit = {
    val size = driver.manage().window().getSize
    screenHeight = size.getHeight
    screenWidth = size.getWidth
    println(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }


  def black(keys: String*): Unit = {
    keys.foreach(conf.blackList.append(_))
  }

  def getTimeStamp(): String = {
    preTimeStamp=nowTimeStamp
    nowTimeStamp=new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date())
    if(preTimeStamp!="0"){
      Console.println("time consume: "+(nowTimeStamp.toDouble-preTimeStamp.toDouble))
    }
    return nowTimeStamp
  }

  def println(text: Any): Unit = {
    Console.println(getTimeStamp())
    Console.println(text)
  }

  def md5(format: String) = {
    //import sys.process._
    //s"echo ${format}" #| "md5" !

    //new java.lang.String(MessageDigest.getInstance("MD5").digest(format.getBytes("UTF-8")))
    java.security.MessageDigest.getInstance("MD5").digest(format.getBytes("UTF-8")).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
  }


  def rule(loc: String, action: String, times: Int = 0): Unit = {
    conf.elementActions.append(Map(
      "idOrName" -> loc,
      "action" -> action,
      "times" -> times))
  }


  def parseXml(raw: String): Document = {
    val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    val document: Document = builder.parse(new ByteArrayInputStream(raw.replaceAll("[\\x00-\\x1F]", "").getBytes(StandardCharsets.UTF_8)))

    val format = new OutputFormat(document); //document is an instance of org.w3c.dom.Document
    format.setLineWidth(65);
    format.setIndenting(true);
    format.setIndent(2);
    val out = new StringWriter();
    val serializer = new XMLSerializer(out, format);
    serializer.serialize(document);
    val formattedXML = out.toString();
    println(formattedXML)
    return document
  }

  /**
    * 根据xpath来获得想要的元素列表
    * @param xpath
    * @return
    */
  def getAllElements(xpath: String): ListBuffer[Map[String, String]] = {
    val nodeList = ListBuffer[Map[String, String]]()

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    val node = compexp.evaluate(pageDom, XPathConstants.NODESET)
    println(s"xpath=${xpath} getAllElements")
    node match {
      case n: NodeList => {
        println(s"xpath=${xpath} length=${n.getLength}")
        0 until n.getLength foreach (i => {
          val nodeMap = Map[String, String]()
          nodeMap("tag") = n.item(i).getNodeName
          val nodeAttributes = n.item(i).getAttributes
          0 until nodeAttributes.getLength foreach (a => {
            val attr = nodeAttributes.item(a).asInstanceOf[Attr]
            nodeMap(attr.getName) = attr.getValue
          })
          if (!nodeMap.contains("name")) {
            nodeMap("name") = ""
            nodeMap("value") = ""
          }
          if (nodeMap.contains("resource-id")) {
            //todo: /结尾的会被解释为/之前的内容
            val arr = nodeMap("resource-id").split('/')
            if (arr.length == 1) {
              nodeMap("name") = ""
            } else {
              nodeMap("name") = nodeMap("resource-id").split('/').last
            }
          }
          if (nodeMap.contains("text")) {
            nodeMap("value") = nodeMap("text")
          }
          if (nodeMap.contains("bounds")) {
            nodeMap("loc") = nodeMap("bounds")
          }
          if (nodeMap.contains("x")) {
            nodeMap("loc") = nodeMap("x") + "," + nodeMap("y")
          }
          if (nodeMap.contains("path")) {
            nodeMap("loc") = nodeMap("path")
          }

          nodeList.append(nodeMap)
        })
      }
      case _ => println("typecast to NodeList failed")
    }
    return nodeList

  }

  /**
    * 尝试识别当前的页面
    * @return
    */
  def getSchema(): String = {
    return ""
  }

  def getUrl(): String = {
    if (conf.defineUrl != "") {
      return getAllElements(conf.defineUrl).map(_ ("value")).lift(0).getOrElse("")
    }
    return ""
  }

  /**
    * 获取控件的基本属性并设置一个唯一的uid作为识别. screenName+id+name
    * @param x
    * @return
    */
  def getElementId(x: Map[String, String]): Option[Element] = {
    //控件的类型
    val tag = x.getOrElse("tag", "NoTag")

    //name为Android的description/text属性, 或者iOS的value属性
    val name = x.getOrElse("value", "").replace("\n", "\\n")
    //name为id/name属性. 为空的时候为value属性

    //id表示android的resource-id或者iOS的name属性
    val resourceId = x.getOrElse("name", "")
    val id = resourceId.split('/').last
    val loc = x.getOrElse("loc", "")
    val node = Element(url, tag, id, name, loc)
    return Some(node)

    //    if (List("android", "com.xueqiu.android").contains(appName)) {
    //      return Some(node)
    //    } else {
    //      return None
    //    }

  }

  def isReturn(): Boolean = {
    //回到桌面了
    if (urlStack.filter(_.matches("Launcher.*")).length > 0) {
      println(s"maybe back to desktop ${urlStack.reverse.mkString("-")}")
      needExit = true
    }
    //url黑名单
    if (conf.blackUrlList.filter(urlStack.head.matches(_)).length > 0) {
      println("in blackUrlList should return")
      return true
    }
    //滚动多次没有新元素
    if(swipeRetry>3){
      swipeRetry=0
      return true
    }
    //超过遍历深度
    println(s"urlStack=${urlStack} ${conf.baseUrl} maxDepth=${conf.maxDepth}")
    if (urlStack.length > conf.maxDepth && urlStack.last.matches(conf.baseUrl)) {
      println(s"urlStack.depth=${urlStack.length} > maxDepth=${conf.maxDepth}")
      return true
    }
    return false

  }

  /**
    * 黑名单过滤. 通过正则匹配
    * @param uid
    * @return
    */
  def isBlack(uid: Map[String, String]): Boolean = {
    conf.blackList.filter(b => {
      uid("value").matches(b) || uid("name").matches(b)
    }).length >= 1
  }


  def getClickableElements(): Option[Seq[Map[String, String]]] = {
    var all = Seq[Map[String, String]]()
    var firstElements = Seq[Map[String, String]]()
    var appendElements = Seq[Map[String, String]]()
    var commonElements = Seq[Map[String, String]]()

    println(conf)
    conf.firstList.foreach(xpath => {
      firstElements ++= getAllElements(xpath)
    })

    conf.lastList.foreach(xpath => {
      appendElements ++= getAllElements(xpath)
    })

    conf.selectedList.foreach(xpath => {
      commonElements ++= getAllElements(xpath)
    })

    commonElements = commonElements diff firstElements
    commonElements = commonElements diff appendElements

    all = (firstElements ++ commonElements ++ appendElements).distinct
    println(s"all length=${all.length}")
    return Some(all)

  }

  def first(xpath: String): Unit = {
    conf.firstList.append(xpath)
  }

  def last(xpath: String): Unit = {
    conf.lastList.append(xpath)
  }

  def back(name: String): Unit = {
    conf.backButton.append(name)
    conf.backButton.foreach(black(_))
  }

  def refreshPage(): Unit = {
    //获取页面结构, 最多重试10次.
    var refreshFinish = false
    pageSource = ""
    1 to 10 foreach (i => {
      if (refreshFinish == false) {
        doAppium(driver.getPageSource) match {
          case Some(v) => {
            println("get page source success")
            pageSource = v
            pageDom = parseXml(pageSource)
            refreshFinish = true
          }
          case None => {
            println("get page source error")
          }
        }
      }
    })
    if (refreshFinish == false) {
      print("retry time > 10 exit")
      System.exit(0)
    }
    val currentUrl = getUrl()
    println(s"url=${currentUrl}")
    //如果跳回到某个页面, 就弹栈到特定的页面, 比如回到首页
    if (urlStack.contains(currentUrl)) {
      while (urlStack.head != currentUrl) {
        urlStack.pop()
      }
    } else {
      urlStack.push(currentUrl)
    }
    //判断新的url堆栈中是否包含baseUrl, 如果有就清空栈记录并从新计数
    if (urlStack.head.matches(conf.baseUrl)) {
      urlStack.clear()
      urlStack.push(currentUrl)
    }
    url = urlStack.reverse.takeRight(6).mkString("|")
    println(s"urlStack=${urlStack.reverse}")
    val contexts = doAppium(driver.getContextHandles).getOrElse("")
    //val windows=doAppium(driver.getWindowHandles).getOrElse("")
    val windows = ""
    println(s"context=${contexts} windows=${windows}")
    println("schema=" + getSchema())
  }

  /*
  /**
    * 最早实现的一个递归算法. 不是尾递归. 代码也很挫, 留作娱乐
    */
  def traversal(): Unit = {
    println("traversal start")
    //等待一秒防止太快
    Thread.sleep(500)
    depth+=1
    println(s"depth=${depth}")
    println("refresh page")
    refreshPage()
    var needBack = true
    var needSkip = false
    needBack = !isReturn()
    //先判断是否命中规则.
    doRuleAction()
    //在selendroid的JsonXmlUtil.java:70有个bug. 导致无法获取当前界面的所有元素. ^_^
    val all = getClickableElements().getOrElse(Seq[Map[String, String]]())
    if (all.length == 0) {
      //获取列表失败就重试
      println("activity change")
      needBack = false
    } else {
      //获得所有的可点击元素
      breakable {
        var index = 0
        all.foreach(x => {
          index += 1
          println(s"index=${index}")
          //是否需要退出
          if (isReturn()) {
            needBack = true
            println("break")
            break()
          } else {
            println("no need to break")
          }
          //是否黑名单
          needSkip = isBlack(x)
          if(needSkip==true){
            println("in black list")
          }else{
            println("not in black list")
          }
          //如果触发了任意操作, 当前界面会变化. 需要重新刷新, 跳过无谓的循环
          val uid = getElementId(x) match {
            case Some(v) => v
            case None => {
              //遍历的元素都是有id, 如果出现了没有NoId或者NoText, 表明是获取元素属性的方法失败了. 发生了异常.
              //获取id异常表示元素出了问题. 说明界面刷新过, 需要重新刷新, 但是不需要后退
              println("exception")
              needBack = false
              break()
            }
          }
          println(s"id=${uid}")

          //是否已经点击过
          //todo: 新界面入口需要设置为false
          if(needSkip==false) {
            if (elements.contains(uid.toString())) {
              println("skip")
              needSkip = true
            } else {
              needSkip = false
              println("first show, need click")
            }
          }
          //如果未曾点击
          if (needSkip == false) {
            println("just click")
            elements(uid.toString()) = true
            //doDefaultAction(uid)
            doAppiumAction(uid, "click") match {
              case Some(v) => {
                println("do appium action success")
              }
              case None => {
                println("do appium action exception, break")
              }
            }
            //说明还不需要back到上一界面, 遍历完所有的元素才表示需要回退
            needBack = false
            //任何点击都需要重新刷新元素. 防止其他元素被遮盖

            //任何界面变化都需要进入新的递归. 而不是只到新界面.
            traversal()

          } else {
            println("already clicked, so skip")
          }
        })
      }
    }
    //子界面遍历返回后继续遍历当前界面中剩下的界面
    if (needBack == true) {
      println("back")
      if (backButton == "") {
        //todo: iOS上的back貌似有问题
        driver.navigate().back()
      } else {
        getAllElements(backButton).lift(0) match {
          case Some(v)=>{
            getElementId(v) match {
              case Some(element)=>{
                doAppiumAction(element, "click")
              }
            }
          }
        }
      }

      saveScreen(ELement(url, "Back", "Back", "Back"))
      //任何界面变化都需要进入新的递归. 而不是只到新界面.
      traversal()

    }
    depth-=1
  }
*/

  def isClicked(ele: Element): Boolean = {
    if (elements.contains(ele.toString())) {
      return elements(ele.toString())
    } else {
      println(s"element=${ele} first show, need click")
      return false
    }
  }

  def clickElement(uid: Element): Unit = {
    println(s"just click ${uid}")
    elements(uid.toString()) = true
    //doDefaultAction(uid)
    doAppiumAction(uid, "click") match {
      case Some(v) => {
        println("do appium action success")
      }
      case None => {
        println("do appium action exception, break")
      }
    }
  }

  def getBackElements(): ListBuffer[Map[String, String]] = {
    conf.backButton.map(getAllElements(_)).flatten
  }

  def goBack(): Unit = {
    println("go back")
    //找到可能的关闭按钮, 取第一个可用的关闭按钮
    getBackElements().lift(0) match {
      case Some(v) => {
        getElementId(v) match {
          case Some(element) => {
            doAppiumAction(element, "click")
          }
          case None => {
            println("几乎不会发生这个异常")
          }
        }
      }
      case None => {
        println("find back button error")
        driver.navigate().back()
        saveScreen(Element(url, "Back", "", "", ""))
      }
    }

    backRetry += 1
    //超过十次连续不停的回退就认为是需要退出
    if (backRetry > 10) {
      needExit = true
    } else {
      println(s"backRetry=${backRetry}")
    }

    depth -= 1
  }

  /**
    * 优化后的递归方法. 尾递归.
    */
  def start(): Unit = {
    if (needExit) {
      return
    }
    println("traversal start")
    depth += 1
    println(s"depth=${depth}")
    println("refresh page")
    refreshPage()
    //是否需要退出或者后退
    if (isReturn()) {
      goBack()
      doRuleAction()
    } else {
      //先判断是否命中规则.
      if (doRuleAction() == false) {
        //获取可点击元素
        var all = getClickableElements().getOrElse(Seq[Map[String, String]]())
        println(s"all nodes length=${all.length}")
        //去掉黑名单, 这样rule优先级高于黑名单
        all = all.filter(isBlack(_) == false)
        println(s"all non-black nodes length=${all.length}")
        //去掉back菜单
        all = all diff getBackElements()
        println(s"all non-black non-back nodes length=${all.length}")
        //把元素转换为Element对象
        var allElements = all.map(getElementId(_).get)
        //获得所有未点击元素
        println(s"all elements length=${allElements.length}")
        //过滤已经被点击过的元素
        allElements = allElements.filter(!isClicked(_))
        println(s"fresh elements length=${allElements.length}")
        //记录未被点击的元素
        allElements.foreach(e => {
          if (!elements.contains(e.toString())) {
            elements(e.toString()) = false
            println(s"fresh = ${e}")
          }
        })
        if (allElements.length > 0) {
          clickElement(allElements.head)
          backRetry = 0
          swipeRetry = 0
        } else {
          println("all elements had be clicked")
          scroll()
        }
      }
    }
    start()
  }

  def scroll(): Unit = {
    if(swipeRetry>3) {
      println("swipe until no fresh elements")
      return
    }
    doAppium(
      driver.swipe(
        (screenWidth * 0.8).toInt, (screenHeight * 0.8).toInt,
        (screenWidth * 0.2).toInt, (screenHeight * 0.2).toInt, 500
      )
    ) match {
      case Some(v) => {
        println("swipe success")
        swipeRetry+=1
        saveScreen(Element(url, "Scroll", "", "", ""))
      }
      case None => {
        println("swipe fail")
        goBack()
      }
    }


/*    doAppium((new TouchAction(driver))
      .press(screenHeight * 0.5.toInt, screenWidth * 0.5.toInt)
      .moveTo(screenHeight * 0.1.toInt, screenWidth * 0.5.toInt)
      .release()
      .perform()
    )
    doAppium(driver.executeScript("mobile: scroll", HashMap("direction" -> "up")))*/
    //doAppium(driver.swipe(screenHeight*0.6.toInt, screenWidth*0.5.toInt, screenHeight*0.1.toInt, screenWidth*0.5.toInt, 400))
  }


  //todo:优化查找方法
  //找到统一的定位方法就在这里定义, 找不到就分别在子类中重载定义
  def findElementByUid(uid: Element): Option[WebElement] = {
    println(s"find element by uid ${uid}")
    platformName.toLowerCase() match {
      case "ios" => {
        println(s"find by xpath")
        //照顾iOS android会在findByName的时候自动找text属性.
        doAppium(driver.findElementByXPath(
          //s"//${uid.tag}[@name='${uid.id}' and @value='${uid.name}' and @x='${uid.loc.split(',').head}' and @y='${uid.loc.split(',').last}']"
          s"//${uid.tag}[@path='${uid.loc}']"
        )) match {
          case Some(v) => return Some(v)
          case None => {}
        }
      }
      case "android" => {
        if (uid.id != "") {
          println(s"find by id=${uid.id}")
          doAppium(driver.findElementsById(uid.id)) match {
            case Some(v) => {
              if (v.toArray.length == 1) {
                //有些公司可能存在重名id
                return Some(v.toArray().head.asInstanceOf[WebElement])
              } else {
                v.toArray().foreach(println)
                println("find multi, change to find by name")
              }
            }
            case None => {}
          }
        }
        if (uid.name != "") {
          println(s"find by name=${uid.name}")
          doAppium(driver.findElementByName(uid.name)) match {
            case Some(v) => {
              return Some(v)
            }
            case None => {
            }
          }
        }
        //xpath会较慢
        println(s"find by xpath")
        doAppium(driver.findElementByXPath(s"//*[@bounds='${uid.loc}']")) match {
          case Some(v) => return Some(v)
          case None => {
          }
        }

      }
    }
    return None
  }

  def doAppium[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        return Some(v)
      }
      case Failure(e) => {
        println("message=" + e.getMessage)
        println("cause=" + e.getCause)
        //println(e.getStackTrace.mkString("\n"))
        return None
      }
    }

  }

  def saveLog(): Unit = {
    println("save log")
    //记录点击log
    if (new java.io.File(s"${platformName}_${timestamp}").exists() == false) {
      FileUtils.forceMkdir(new java.io.File(s"${platformName}_${timestamp}"))
    }
    File(s"${platformName}_${timestamp}/clickedList.log").writeAll(clickedList.mkString("\n"))
    File(s"${platformName}_${timestamp}/ElementList.log").writeAll(elements.mkString("\n"))
  }

  def saveScreen(e: Element): Unit = {
    println("start screenshot")
    if (conf.saveScreen == false) return
    Thread.sleep(500)
    img_index += 1
    val path = s"${platformName}_${timestamp}/${img_index}_" + e.toString().replace("\n", "").replaceAll("[ /,]", "").take(200) + ".jpg"
    doAppium((driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)) match {
      case Some(src) => {
        FileUtils.copyFile(src, new java.io.File(path))
      }
      case None => {
        println("get screenshot error")
      }
    }
  }

  def doAppiumAction(e: Element, action: String = "click"): Option[Unit] = {
    findElementByUid(e) match {
      case Some(v) => {
        action match {
          case "click" => {
            println(s"click ${v}")
            val res = doAppium(v.click())
            clickedList.append(e.toString())
            saveLog()
            saveScreen(e)
            doAppium(driver.hideKeyboard())
            return res
          }
          case str: String => {
            println(s"sendkeys ${v} with ${str}")
            doAppium(v.sendKeys(str)) match {
              case Some(v) => {
                clickedList.append(e.toString())
                saveLog()
                doAppium(driver.hideKeyboard())
                return Some(v)
              }
              case None => return None
            }
          }
        }

      }
      case None => {
        println("find error")
        return None
      }
    }


  }

  /**
    * 子类重载
    * @return
    */
  def getRuleMatchNodes(): ListBuffer[Map[String, String]] = {
    return ListBuffer[Map[String, String]]()
  }

  //通过规则实现操作. 不管元素是否被点击过
  def doRuleAction(): Boolean = {
    println("rule match start")
    //先判断是否在期望的界面里. 提升速度
    var isHit = false
    conf.elementActions.foreach(r => {
      println("for each rule")
      val idOrName = r("idOrName").toString
      val action = r("action").toString
      val times = r("times").toString.toInt
      println(s"idOrName=${idOrName} action=${action}")
      val all = getRuleMatchNodes()
      breakable {
        (all.filter(_ ("name").matches(idOrName)) ++ all.filter(_ ("value").matches(idOrName))).distinct.foreach(x => {
          println("hit rule action")
          println(x)
          //获得正式的定位id
          getElementId(x) match {
            case Some(e) => {
              println(s"element=${e} action=${action}")
              println("do rule action")
              isHit = true
              doAppiumAction(e, action.toString) match {
                case None => {
                  println("do rule action fail")
                  break()
                }
                case Some(v) => {
                  println("do rule action success")
                  r("times") = times - 1
                  if (times == 1) {
                    conf.elementActions -= r
                  }
                }
              }
              //todo: 暂不删除, 允许复用
              //rule -= r
            }
            case None => println("get element id error")
          }
        })
      }
    })

    return isHit

  }
}
