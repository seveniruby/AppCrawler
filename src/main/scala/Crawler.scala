import java.io.{ByteArrayInputStream, StringWriter}
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPath, XPathFactory, _}

import io.appium.java_client.AppiumDriver
import org.apache.commons.io.FileUtils
import org.apache.xml.serialize.{OutputFormat, XMLSerializer}
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{OutputType, TakesScreenshot, WebElement}
import org.w3c.dom.{Attr, Document, NodeList}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}
import scala.reflect.io.File
import scala.util.{Failure, Success, Try}


/**
  * Created by seveniruby on 15/11/28.
  */
class Crawler {
  implicit var driver: AppiumDriver[WebElement] = _
  var conf = new CrawlerConf()
  val capabilities = new DesiredCapabilities()

  var pluginNames = List[String]("DemoPlugin")
  var pluginClasses = List[Plugin]()
  var plugins = List[Plugin]()

  private val elements: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map()
  private var isSkip = false
  /** 点击顺序, 留作画图用 */
  val clickedList = ListBuffer[String]()

  var preTimeStamp = "0"
  var nowTimeStamp = "0"
  val timestamp = getTimeStamp()
  //todo:留作判断当前界面是否变化
  var md5Last = ""
  var automationName = "appium"
  var platformName = ""

  var screenWidth = 0
  var screenHeight = 0

  //意义不大. 并不是真正的层次, 是递归的层次
  var depth = 0
  var pageSource = ""
  private var pageDom: Document = null
  private var imgIndex = 0
  private var backRetry = 0
  //最大重试次数
  var backMaxRetry = 5
  private var swipeRetry = 0
  //滑动最大重试次数
  var swipeMaxRetry = 2
  private val swipeCountPerUrl = Map[String, Int]()
  private val swipeMaxCountPerUrl = 8
  private var needExit = false
  private val startTime=new Date().getTime

  /** 当前的url路径 */
  var url = ""
  val urlStack = mutable.Stack[String]()

  val elementTree = TreeNode(UrlElement("Start", "", "", "", ""))
  val elementTreeList = ListBuffer[UrlElement]()

  /**
    * 根据类名初始化插件. 插件可以使用java编写. 继承自Plugin即可
    */
  def loadPlugins(): Unit = {
    pluginClasses = pluginNames.map(name => {
      Class.forName(name).newInstance().asInstanceOf[Plugin]
    })
    println(s"plugins=${pluginClasses.foreach(println)}")
    pluginClasses.foreach(p => p.init(this))
  }

  /**
    * 加载配置文件并初始化
    */
  def loadConf(crawlerConf: CrawlerConf): Unit = {
    conf = crawlerConf
  }

  def loadConf(file: String): Unit = {
    conf = new CrawlerConf().load(file)
  }

  /**
    * 启动爬虫
    */
  def start(): Unit = {
    loadPlugins()
    setupAppium()
    if(conf.resultDir==""){
      conf.resultDir=s"${platformName}_${timestamp}"
    }
    crawl()
  }

  def setupAppium(): Unit = {
    conf.capability.foreach(kv => capabilities.setCapability(kv._1, kv._2))
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
    preTimeStamp = nowTimeStamp
    nowTimeStamp = new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date())
    if (preTimeStamp != "0") {
      Console.println("time consume: " + (nowTimeStamp.toDouble - preTimeStamp.toDouble))
    }
    nowTimeStamp
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
    conf.elementActions.append(mutable.Map(
      "idOrName" -> loc,
      "action" -> action,
      "times" -> times))
  }


  def parseXml(raw: String): Document = {
    val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    val document: Document = builder.parse(new ByteArrayInputStream(raw.replaceAll("[\\x00-\\x1F]", "").getBytes(StandardCharsets.UTF_8)))

    val format = new OutputFormat(document); //document is an instance of org.w3c.dom.Document
    format.setLineWidth(65)
    format.setIndenting(true)
    format.setIndent(2)
    val out = new StringWriter()
    val serializer = new XMLSerializer(out, format)
    serializer.serialize(document)
    val formattedXML = out.toString
    println(formattedXML)
    document
  }

  /**
    * 根据xpath来获得想要的元素列表
    *
    * @param xpath
    * @return
    */
  def getAllElements(xpath: String): ListBuffer[mutable.Map[String, String]] = {
    println(s"xpath=${xpath} getAllElements")
    val nodeList = ListBuffer[mutable.Map[String, String]]()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    val node = compexp.evaluate(pageDom, XPathConstants.NODESET)
    node match {
      case n: NodeList => {
        println(s"xpath=${xpath} length=${n.getLength}")
        0 until n.getLength foreach (i => {
          val nodeMap = mutable.Map[String, String]()
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
    nodeList

  }

  /**
    * 尝试分析当前的页面的唯一标记
    *
    * @return
    */
  def getSchema(): String = {
    var nodeList = getAllElements("//*[not(ancestor-or-self::UIATableView)]")
    nodeList = nodeList intersect getAllElements("//*[not(ancestor-or-self::android.widget.ListView)]")
    //todo: 未来应该支持黑名单
    val schemaBlackList = List("UIATableCell", "UIATableView", "UIAScrollView")
    md5(nodeList.filter(node => !schemaBlackList.contains(node("tag"))).map(node => node("tag")).mkString(""))
  }


  def getUrl(): String = {
    if (conf.defineUrl.nonEmpty) {
      val urlString = conf.defineUrl.flatMap(getAllElements(_)).map(_ ("value")).headOption.getOrElse("")
      println(s"urlString=$urlString")
      return urlString
    }
    ""
  }

  /**
    * 获取控件的基本属性并设置一个唯一的uid作为识别. screenName+id+name
    *
    * @param x
    * @return
    */
  def getUrlElementByMap(x: mutable.Map[String, String]): Option[UrlElement] = {
    //控件的类型
    val tag = x.getOrElse("tag", "NoTag")

    //name为Android的description/text属性, 或者iOS的value属性
    val name = x.getOrElse("value", "").replace("\n", "\\n").take(30)
    //name为id/name属性. 为空的时候为value属性

    //id表示android的resource-id或者iOS的name属性
    val resourceId = x.getOrElse("name", "")
    val id = resourceId.split('/').last
    val loc = x.getOrElse("loc", "")
    val node = UrlElement(url, tag, id, name, loc)
    Some(node)

    //    if (List("android", "com.xueqiu.android").contains(appName)) {
    //      return Some(node)
    //    } else {
    //      return None
    //    }

  }

  def isReturn(): Boolean = {
    //回到桌面了
    if (urlStack.filter(_.matches("Launcher.*")).nonEmpty) {
      println(s"maybe back to desktop ${urlStack.reverse.mkString("-")}")
      needExit = true
    }
    //url黑名单
    if (conf.blackUrlList.filter(urlStack.head.matches(_)).nonEmpty) {
      println("in blackUrlList should return")
      return true
    }
    //滚动多次没有新元素
    if (swipeRetry > swipeMaxRetry) {
      swipeRetry = 0
      return true
    }
    //超过遍历深度
    println(s"urlStack=${urlStack} baseUrl=${conf.baseUrl} maxDepth=${conf.maxDepth}")
    //大于最大深度并且是在进入过基础Url
    if (urlStack.length > conf.maxDepth && conf.baseUrl.map(urlStack.last.matches(_)).contains(true)) {
      println(s"urlStack.depth=${urlStack.length} > maxDepth=${conf.maxDepth}")
      return true
    }
    false

  }

  /**
    * 黑名单过滤. 通过正则匹配, 判断name和value是否包含黑名单
    *
    * @param uid
    * @return
    */
  def isBlack(uid: mutable.Map[String, String]): Boolean = conf.blackList.filter(b => {
    uid("value").matches(b) || uid("name").matches(b)
  }).nonEmpty

  //todo: 支持xpath表达式


  def getClickableElements(): Option[Seq[mutable.Map[String, String]]] = {
    var all = Seq[mutable.Map[String, String]]()
    var firstElements = Seq[mutable.Map[String, String]]()
    var appendElements = Seq[mutable.Map[String, String]]()
    var commonElements = Seq[mutable.Map[String, String]]()

    println(conf)
    val invalidElements = getAllElements("//*[@visible='false' or @enabled='false' or @valid='false']")
    conf.firstList.foreach(xpath => {
      firstElements ++= getAllElements(xpath)
    })
    firstElements = firstElements diff invalidElements

    conf.lastList.foreach(xpath => {
      appendElements ++= getAllElements(xpath)
    })
    appendElements = appendElements diff invalidElements

    conf.selectedList.foreach(xpath => {
      commonElements ++= getAllElements(xpath)
    })
    commonElements = commonElements diff invalidElements


    commonElements = commonElements diff firstElements
    commonElements = commonElements diff appendElements

    //确保不重, 并保证顺序
    all = (firstElements ++ commonElements ++ appendElements).distinct
    println(s"all length=${all.length}")
    Some(all)

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
      if (!refreshFinish) {
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
    if (!refreshFinish) {
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
    if (conf.baseUrl.map(urlStack.head.matches(_)).contains(true)) {
      urlStack.clear()
      urlStack.push(currentUrl)
    }
    println(s"urlStack=${urlStack.reverse}")

    url = urlStack.reverse.mkString("|")
    println(s"url=${url}")
    afterUrlRefresh()

    val contexts = doAppium(driver.getContextHandles).getOrElse("")
    //val windows=doAppium(driver.getWindowHandles).getOrElse("")
    val windows = ""
    println(s"context=${contexts} windows=${windows}")
    println("schema=" + getSchema())
  }

  def afterUrlRefresh(): Unit = {
    pluginClasses.foreach(p => p.afterUrlRefresh(url))
  }

  def isClicked(ele: UrlElement): Boolean = {
    if (elements.contains(ele.toString())) {
      elements(ele.toString())
    } else {
      println(s"element=${ele} first show, need click")
      false
    }
  }


  def beforeElementAction(element: UrlElement): Unit = {
    pluginClasses.foreach(p => p.beforeElementAction(element))
  }

  def afterElementAction(element: UrlElement): Unit = {
    pluginClasses.foreach(p => p.afterElementAction(element))
  }

  def clickElement(uid: UrlElement): Unit = {
    beforeElementAction(uid)
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
    afterElementAction(uid)
  }

  def getBackElements(): ListBuffer[mutable.Map[String, String]] = {
    conf.backButton.flatMap(getAllElements(_))
  }

  def goBack(): Unit = {
    println("go back")
    //找到可能的关闭按钮, 取第一个可用的关闭按钮
    getBackElements().headOption match {
      case Some(v) => {
        getUrlElementByMap(v) match {
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
        saveScreen(UrlElement(url, "Back", "", "", ""))
      }
    }

    backRetry += 1
    //超过十次连续不停的回退就认为是需要退出
    if (backRetry > backMaxRetry) {
      needExit = true
    } else {
      println(s"backRetry=${backRetry}")
    }

    depth -= 1
  }

  /**
    * 优化后的递归方法. 尾递归.
    */
  def crawl(): Unit = {
    //超时退出
    if((new Date().getTime-startTime) > conf.maxTime*1000){
      println("maxTime out Quit")
      needExit=true
    }
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
      if (!doRuleAction()) {
        //获取可点击元素
        var all = getClickableElements().getOrElse(Seq[mutable.Map[String, String]]())
        println(s"all nodes length=${all.length}")
        //去掉黑名单, 这样rule优先级高于黑名单
        all = all.filter(isBlack(_) == false)
        println(s"all non-black nodes length=${all.length}")
        //去掉back菜单
        all = all diff getBackElements()
        println(s"all non-black non-back nodes length=${all.length}")
        //把元素转换为Element对象
        var allElements = all.map(getUrlElementByMap(_).get)
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
        if (allElements.nonEmpty) {
          clickElement(allElements.head)
          backRetry = 0
          swipeRetry = 0
        } else {
          println("all elements had be clicked")
          scroll()
        }
      }
    }
    crawl()
  }

  def scroll(): Unit = {
    if (swipeRetry > swipeMaxRetry) {
      println("swipeRetry > swipeMaxRetry")
      return
    }
    if (swipeCountPerUrl.contains(url) == false) {
      swipeCountPerUrl(url) = 0
    }
    if (swipeCountPerUrl(url) > swipeMaxCountPerUrl) {
      println("swipeRetry of per url > swipeMaxCountPerUrl")
      swipeRetry+=1
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
        swipeRetry += 1
        swipeCountPerUrl(url) += 1
        println(s"swipeCount of current Url=${swipeCountPerUrl(url)}")
        saveScreen(UrlElement(url, "Scroll", "", "", ""))
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
  def findElementByUrlElement(uid: UrlElement): Option[WebElement] = {
    println(s"find element by uid ${uid}")
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
    platformName.toLowerCase() match {
      case "ios" => {
        println(s"find by xpath")
        //照顾iOS android会在findByName的时候自动找text属性.
        doAppium(driver.findElementByXPath(
          //s"//${uid.tag}[@name='${uid.id}' and @value='${uid.name}' and @x='${uid.loc.split(',').head}' and @y='${uid.loc.split(',').last}']"
          s"//${uid.tag}[@name='${uid.id}' and @value='${uid.name}' and @path='${uid.loc}']"
        )) match {
          case Some(v) => return Some(v)
          case None => {}
        }
      }
      case "android" => {
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
    None
  }

  def doAppium[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        Some(v)
      }
      case Failure(e) => {
        println("message=" + e.getMessage)
        println("cause=" + e.getCause)
        //println(e.getStackTrace.mkString("\n"))
        None
      }
    }

  }

  def saveLog(): Unit = {
    println("save log")
    //记录点击log
    if (!new java.io.File(conf.resultDir).exists()) {
      FileUtils.forceMkdir(new java.io.File(conf.resultDir))
    }
    File(s"${conf.resultDir}/clickedList.log").writeAll(clickedList.mkString("\n"))
    File(s"${conf.resultDir}/ElementList.log").writeAll(elements.mkString("\n"))
    File(s"${conf.resultDir}/freemind.mm").writeAll(
      elementTree.generateFreeMind(elementTreeList)
    )
    println("save log end")
  }

  def saveScreen(e: UrlElement): Unit = {
    println("start screenshot")
    if (!conf.saveScreen) return
    Thread.sleep(500)
    imgIndex += 1
    val path = s"${conf.resultDir}/${imgIndex}_" + e.toString().replace("\n", "").replaceAll("[ /,]", "").take(200) + ".jpg"
    doAppium((driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)) match {
      case Some(src) => {
        FileUtils.copyFile(src, new java.io.File(path))
      }
      case None => {
        println("get screenshot error")
      }
    }
    println("save screenshot end")
  }


  def appendClickedList(e: UrlElement): Unit = {
    clickedList.append(e.toString())
    elementTreeList.append(UrlElement(e.url, "url", "", "", ""))
    elementTreeList.append(e)
    saveLog()

  }

  def doAppiumAction(e: UrlElement, action: String = "click"): Option[Unit] = {
    findElementByUrlElement(e) match {
      case Some(v) => {
        action match {
          case "click" => {
            println(s"click ${v}")
            val res = doAppium(v.click())
            appendClickedList(e)
            saveScreen(e)
            doAppium(driver.hideKeyboard())
            res
          }
          case str: String => {
            println(s"sendkeys ${v} with ${str}")
            doAppium(v.sendKeys(str)) match {
              case Some(v) => {
                appendClickedList(e)
                doAppium(driver.hideKeyboard())
                Some(v)
              }
              case None => None
            }
          }
        }

      }
      case None => {
        println("find error")
        None
      }
    }


  }

  /**
    * 子类重载
    *
    * @return
    */
  def getRuleMatchNodes(): ListBuffer[mutable.Map[String, String]] = {
    ListBuffer[mutable.Map[String, String]]()
  }

  //通过规则实现操作. 不管元素是否被点击过
  def doRuleAction(): Boolean = {
    println("rule match start")
    //先判断是否在期望的界面里. 提升速度
    var isHit = false
    conf.elementActions.foreach(r => {
      println(s"for each rule ${r}")
      val idOrName = r("idOrName").toString
      val action = r("action").toString
      val times = r("times").toString.toInt
      println(s"idOrName=${idOrName} action=${action} times=${times}")
      val all = getRuleMatchNodes()

      (all.filter(_ ("name").matches(idOrName)) ++ all.filter(_ ("value").matches(idOrName))).distinct.foreach(x => {
        //获得正式的定位id
        getUrlElementByMap(x) match {
          case Some(e) => {
            println(s"element=${e} action=${action}")
            isHit = true
            doAppiumAction(e, action.toString) match {
              case None => {
                println("do rule action fail")
              }
              case Some(v) => {
                println("do rule action success")
                r("times") = times - 1
                if (times == 1) {
                  println(s"remove rule ${r}")
                  conf.elementActions -= r
                }
              }
            }
          }
          case None => println("get element id error")
        }
      })

    })

    isHit

  }
}
