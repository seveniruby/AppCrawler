import java.io.{ByteArrayInputStream, StringWriter}
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.logging.Level
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPath, XPathFactory, _}

import io.appium.java_client.AppiumDriver
import org.apache.commons.io.FileUtils
import org.apache.xml.serialize.{OutputFormat, XMLSerializer}
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{OutputType, TakesScreenshot, WebElement}
import org.w3c.dom.{Attr, Document, NodeList}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}
import scala.collection.immutable
import scala.reflect.io.File
import scala.util.{Failure, Success, Try}


/**
  * Created by seveniruby on 15/11/28.
  */
class Crawler {
  implicit var driver: AppiumDriver[WebElement] = _
  var conf = new CrawlerConf()
  val capabilities = new DesiredCapabilities()

  var pluginClasses = List[Plugin]()
  var plugins = List[Plugin]()

  private val elements: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map()
  private var isSkip = false
  /** 元素的默认操作 */
  var currentElementAction = "click"
  /** 点击顺序, 留作画图用 */
  val clickedList = ListBuffer[String]()

  var preTimeStamp = "0"
  var nowTimeStamp = "0"
  val timestamp = getTimeStamp()
  //todo:留作判断当前界面是否变化
  var currentSchema = ""
  var lastSchema = ""
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
  private val startTime = new Date().getTime

  /** 当前的url路径 */
  var url = ""
  val urlStack = mutable.Stack[String]()

  val elementTree = TreeNode(UrlElement("Start", "", "", "", ""))
  val elementTreeList = ListBuffer[UrlElement]()

  /**
    * 根据类名初始化插件. 插件可以使用java编写. 继承自Plugin即可
    */
  def loadPlugins(): Unit = {
    pluginClasses = conf.pluginList.map(name => {
      println(s"Load $name")
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
    GA.log("start")
    loadPlugins()
    setupAppium()

    println("LogTypes=")
    driver.manage().logs().getAvailableLogTypes().toArray.foreach(println)
    if (conf.resultDir == "") {
      conf.resultDir = s"${platformName}_${timestamp}"
    }
    if (!new java.io.File(conf.resultDir).exists()) {
      FileUtils.forceMkdir(new java.io.File(conf.resultDir))
    }
    GA.log("crawler")
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
    val distance = nowTimeStamp.toDouble - preTimeStamp.toDouble
    if (preTimeStamp != "0" && distance > 0) {
      Console.println(s"time consume: $distance")
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

  /**
    * 根据xpath来获得想要的元素列表
    *
    * @param xpath
    * @return
    */
  def getAllElements(xpath: String): List[immutable.Map[String, Any]] = {
    println(s"xpath=${xpath} getAllElements")
    RichData.parseXPath(xpath, pageDom)
  }

  /**
    * 尝试分析当前的页面的唯一标记
    *
    * @return
    */
  def getSchema(): String = {
    //var nodeList = getAllElements("//*[not(ancestor-or-self::UIATableView)]")
    //nodeList = nodeList intersect getAllElements("//*[not(ancestor-or-self::android.widget.ListView)]")

    //排除iOS状态栏 android不受影响
    val nodeList = getAllElements("//*[not(ancestor-or-self::UIAStatusBar)]")
    val schemaBlackList = List()
    md5(nodeList.filter(node => !schemaBlackList.contains(node("tag"))).map(node => node("tag")).mkString(""))
  }


  def getUrl(): String = {
    if (conf.defineUrl.nonEmpty) {
      val urlString = conf.defineUrl.flatMap(getAllElements(_)).map(_ ("value")).headOption.getOrElse("").toString
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
  def getUrlElementByMap(x: immutable.Map[String, Any]): Option[UrlElement] = {
    //控件的类型
    val tag = x.getOrElse("tag", "NoTag").toString

    //name为Android的description/text属性, 或者iOS的value属性
    val name = x.getOrElse("value", "").toString.replace("\n", "\\n").take(30)
    //name为id/name属性. 为空的时候为value属性

    //id表示android的resource-id或者iOS的name属性
    val id = x.getOrElse("name", "").toString.split('/').last
    val loc = x.getOrElse("loc", "").toString
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
  def isBlack(uid: immutable.Map[String, Any]): Boolean = conf.blackList.filter(b => {
    uid("value").toString.matches(b) || uid("name").toString.matches(b)
  }).nonEmpty

  //todo: 支持xpath表达式


  def getClickableElements(): Option[Seq[immutable.Map[String, Any]]] = {
    var all = Seq[immutable.Map[String, Any]]()
    var firstElements = Seq[immutable.Map[String, Any]]()
    var appendElements = Seq[immutable.Map[String, Any]]()
    var commonElements = Seq[immutable.Map[String, Any]]()

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
            pageDom = RichData.toXML(pageSource)
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

    //val contexts = doAppium(driver.getContextHandles).getOrElse("")
    //val windows=doAppium(driver.getWindowHandles).getOrElse("")
    //val windows = ""
    //println(s"windows=${windows}")
    lastSchema = currentSchema
    currentSchema = getSchema()
    println(s"currentSchema=$currentSchema lastSchema=$lastSchema")
    afterUrlRefresh()

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

  def getElementAction(): String = {
    currentElementAction
  }

  def setElementAction(action: String): Unit = {
    currentElementAction = action
  }

  def doElementAction(uid: UrlElement): Unit = {
    beforeElementAction(uid)
    println(s"just click ${uid}")
    elements(uid.toString()) = true
    doAppiumAction(uid, getElementAction())
    afterElementAction(uid)
  }

  def getBackElements(): ListBuffer[immutable.Map[String, Any]] = {
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
  @tailrec private def crawl(): Unit = {
    //超时退出
    if ((new Date().getTime - startTime) > conf.maxTime * 1000) {
      println("maxTime out Quit")
      needExit = true
    }
    if (needExit) {
      return
    }
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
        var all = getClickableElements().getOrElse(Seq[immutable.Map[String, String]]())
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
          doElementAction(allElements.head)
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

  def scroll(direction: String = ""): Unit = {
    if (swipeRetry > swipeMaxRetry) {
      println("swipeRetry > swipeMaxRetry")
      return
    }
    if (swipeCountPerUrl.contains(url) == false) {
      swipeCountPerUrl(url) = 0
    }
    if (swipeCountPerUrl(url) > swipeMaxCountPerUrl) {
      println("swipeRetry of per url > swipeMaxCountPerUrl")
      swipeRetry += 1
      return
    }

    var startX = 0.8
    var startY = 0.8
    var endX = 0.2
    var endY = 0.2
    direction match {
      case "left" => {
        startX = 0.9
        startY = 0.5
        endX = 0.1
        endY = 0.5
      }
      case "up" => {
        startX = 0.5
        startY = 0.8
        endX = 0.5
        endY = 0.2
      }
      case "down" => {
        startX = 0.5
        startY = 0.2
        endX = 0.5
        endY = 0.8
      }
      case _ => {
        startX = 0.9
        startY = 0.9
        endX = 0.1
        endY = 0.1
      }
    }
    doAppium(
      driver.swipe(
        (screenWidth * startX).toInt, (screenHeight * startY).toInt,
        (screenWidth * endX).toInt, (screenHeight * endY).toInt, 500
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
          s"//${uid.tag}[@path='${uid.loc}']"
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
    File(s"${conf.resultDir}/clickedList.log").writeAll(clickedList.mkString("\n"))
    File(s"${conf.resultDir}/ElementList.log").writeAll(elements.mkString("\n"))
    File(s"${conf.resultDir}/freemind.mm").writeAll(
      elementTree.generateFreeMind(elementTreeList)
    )
    println("save log end")
  }

  def saveScreen(e: UrlElement): Unit = {
    imgIndex += 1
    //刷新页面. 让schema更新
    refreshPage()
    println("save dom")
    val domPath = s"${conf.resultDir}/${imgIndex}_" + e.toString().replace("\n", "").replaceAll("[ /,]", "").take(200) + ".dom"
    File(domPath).writeAll(pageSource)
    println("save dom end")
    //如果是schema相同. 界面基本不变. 那么就跳过截图加快速度.
    if (conf.saveScreen && lastSchema != currentSchema) {
      Thread.sleep(100)
      println("start screenshot")
      val path = s"${conf.resultDir}/${imgIndex}_" + e.toString().replace("\n", "").replaceAll("[ /,]", "").take(200) + ".jpg"

      val getScreen = new Thread(new Runnable {
        override def run(): Unit = {
          Console.println("new thread")
          doAppium((driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)) match {
            case Some(src) => {
              FileUtils.copyFile(src, new java.io.File(path))
              Console.println("save screenshot end")
            }
            case None => {
              Console.println("get screenshot error")
            }
          }
        }
      })
      getScreen.start()
      //最多等待5s
      for(i<- 1 to 10){
        if(getScreen.getState!=Thread.State.TERMINATED){
          Thread.sleep(500)
        }
      }
      getScreen.stop()
    }
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
          case "skip" => {
            println("skip")
          }
          case "scroll left" => {
            scroll("left")
          }
          case "scroll up" => {
            scroll("up")
          }
          case "scroll down" => {
            scroll("down")
          }
          case "scroll" => {
            scroll()
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
        Some()

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
  def getRuleMatchNodes(): List[immutable.Map[String, Any]] = {
    List[immutable.Map[String, Any]]()
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

      (all.filter(_ ("name").toString.matches(idOrName)) ++ all.filter(_ ("value").toString.matches(idOrName))).distinct.foreach(x => {
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
