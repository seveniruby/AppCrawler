import java.io.{ByteArrayInputStream, StringWriter}
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPath, XPathFactory, _}

import io.appium.java_client.AppiumDriver
import org.apache.commons.io.FileUtils
import org.apache.log4j._
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
class Crawler extends CommonLog{
  implicit var driver: AppiumDriver[WebElement] = _
  var conf = new CrawlerConf()
  val capabilities = new DesiredCapabilities()

  /**存放插件类*/
  var pluginClasses = List[Plugin]()
  var fileAppender: FileAppender=_

  private val elements: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map()
  private var isSkip = false
  /** 元素的默认操作 */
  var currentElementAction = "click"
  /** 点击顺序, 留作画图用 */
  val clickedList = ListBuffer[String]()

  private var preTimeStamp = 0L
  private var nowTimeStamp = 0L
  val strtTimestamp = getTimeStamp()
  //todo:留作判断当前界面是否变化
  private var currentSchema = ""
  private var lastSchema = ""
  protected var automationName = "appium"
  protected var platformName = ""

  private var screenWidth = 0
  private var screenHeight = 0

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
  private var needRefresh=true
  private val startTime = new Date().getTime

  /** 当前的url路径 */
  var url = ""
  val urlStack = mutable.Stack[String]()

  private val elementTree = TreeNode(UrlElement("Start", "", "", "", ""))
  private val elementTreeList = ListBuffer[UrlElement]()

  /**
    * 根据类名初始化插件. 插件可以使用java编写. 继承自Plugin即可
    */
  def loadPlugins(): Unit = {
    pluginClasses = conf.pluginList.map(name => {
      log.info(s"load plugin $name")
      Class.forName(name).newInstance().asInstanceOf[Plugin]
    })
    pluginClasses.foreach(log.info)
    pluginClasses.foreach(p => p.init(this))
  }

  /**
    * 加载配置文件并初始化
    */
  def loadConf(crawlerConf: CrawlerConf): Unit = {
    conf = crawlerConf
    addLogFile()
  }

  def loadConf(file: String): Unit = {
    conf = new CrawlerConf().load(file)
    addLogFile()
  }
  def addLogFile(): Unit ={
    if (conf.resultDir == "") {
      conf.resultDir = s"${platformName}_${strtTimestamp}"
    }
    AppCrawler.logPath=conf.resultDir+"/appcrawler.log"

    fileAppender=new FileAppender(layout, AppCrawler.logPath, false)
    log.addAppender(fileAppender)
    if (!new java.io.File(conf.resultDir).exists()) {
      FileUtils.forceMkdir(new java.io.File(conf.resultDir))
    }

  }

  /**
    * 启动爬虫
    */
  def start(): Unit = {
    log.trace("start")
    GA.log("start")
    setupAppium()

    log.trace("LogTypes=")
    driver.manage().logs().getAvailableLogTypes().toArray.foreach(log.trace)
    //设定结果目录

    GA.log("crawler")
    loadPlugins()
    runStartupScript()
    crawl()
  }

  def runStartupScript(): Unit ={
    log.info("run startup script")
    refreshPage()
    conf.startupActions.foreach(action=>{
      scrollAction(action.split(" ").last)
      saveScreen(UrlElement(url, s"Scroll_${action}", "", "", ""))
      Thread.sleep(1000)
    })
    swipeRetry=0


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
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
    println(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }


  def black(keys: String*): Unit = {
    keys.foreach(conf.blackList.append(_))
  }

  def getTimeStamp(): String = {
    preTimeStamp = nowTimeStamp
    nowTimeStamp = new java.util.Date().getTime
    val distance = nowTimeStamp - preTimeStamp
    if (preTimeStamp != 0 && distance > 500) {
      log.trace(s"time consume: $distance ms")
    }
    new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(nowTimeStamp)
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
    log.trace(s"xpath=${xpath} getAllElements")
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
      log.trace(s"urlString=$urlString")
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
      log.info(s"maybe back to desktop ${urlStack.reverse.mkString("-")}")
      needExit = true
    }
    //url黑名单
    if (conf.blackUrlList.filter(urlStack.head.matches(_)).nonEmpty) {
      log.info("in blackUrlList should return")
      return true
    }
    //滚动多次没有新元素
    if (swipeRetry > swipeMaxRetry) {
      swipeRetry = 0
      log.info(s"swipe retry too many times ${swipeRetry} > ${swipeMaxRetry}")
      return true
    }
    //超过遍历深度
    log.info(s"urlStack=${urlStack} baseUrl=${conf.baseUrl} maxDepth=${conf.maxDepth}")
    //大于最大深度并且是在进入过基础Url
    if (urlStack.length > conf.maxDepth && conf.baseUrl.map(urlStack.last.matches(_)).contains(true)) {
      log.info(s"urlStack.depth=${urlStack.length} > maxDepth=${conf.maxDepth}")
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


  def getSelectedElements(): Option[Seq[immutable.Map[String, Any]]] = {
    var all = Seq[immutable.Map[String, Any]]()
    var firstElements = Seq[immutable.Map[String, Any]]()
    var appendElements = Seq[immutable.Map[String, Any]]()
    var commonElements = Seq[immutable.Map[String, Any]]()

    log.trace(conf)
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
    log.trace(s"all length=${all.length}")
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
    log.info("refresh page")
    //获取页面结构, 最多重试10次.
    var refreshFinish = false
    pageSource = ""
    1 to 10 foreach (i => {
      if (!refreshFinish) {
        doAppium(driver.getPageSource) match {
          case Some(v) => {
            log.trace("get page source success")
            pageSource = v
            pageDom = RichData.toXML(pageSource)
            refreshFinish = true
            needRefresh=false
          }
          case None => {
            log.trace("get page source error")
            needRefresh=true
          }
        }
      }
    })
    if (!refreshFinish) {
      log.warn("retry time > 10 exit")
      System.exit(0)
    }
    val currentUrl = getUrl()
    log.trace(s"url=${currentUrl}")
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
    log.trace(s"urlStack=${urlStack.reverse}")
    url = urlStack.reverse.mkString("|")
    log.info(s"url=${url}")

    //val contexts = doAppium(driver.getContextHandles).getOrElse("")
    //val windows=doAppium(driver.getWindowHandles).getOrElse("")
    //val windows = ""
    //log.trace(s"windows=${windows}")
    lastSchema = currentSchema
    currentSchema = getSchema()
    log.info(s"currentSchema=$currentSchema lastSchema=$lastSchema")
    afterUrlRefresh()

  }

  def afterUrlRefresh(): Unit = {
    pluginClasses.foreach(p => p.afterUrlRefresh(url))
  }

  def isClicked(ele: UrlElement): Boolean = {
    if (elements.contains(ele.toString())) {
      elements(ele.toString())
    } else {
      log.trace(s"element=${ele} first show, need click")
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

  /**
    * 允许插件重新设定当前控件的行为
    * */
  def setElementAction(action: String): Unit = {
    currentElementAction = action
  }

  def doElementAction(element: UrlElement): Unit = {
    beforeElementAction(element)
    log.info(s"current element = ${element}")
    log.info(s"current element url = ${element.url}")
    log.info(s"current element xpath = ${element.loc}")
    elements(element.toString()) = true
    doAppiumAction(element, getElementAction())
    needRefresh=true
    afterElementAction(element)
  }

  def getBackElements(): ListBuffer[immutable.Map[String, Any]] = {
    conf.backButton.flatMap(getAllElements(_))
  }

  def goBack(): Unit = {
    log.trace("go back")
    //找到可能的关闭按钮, 取第一个可用的关闭按钮
    getBackElements().headOption match {
      case Some(v) => {
        getUrlElementByMap(v) match {
          case Some(element) => {
            doAppiumAction(element, "click")
          }
          case None => {
            log.warn("几乎不会发生这个异常")
          }
        }
      }
      case None => {
        log.warn("find back button error")
        driver.navigate().back()
        saveScreen(UrlElement(url, "Back", "", "", ""))
      }
    }


    //超过十次连续不停的回退就认为是需要退出
    backRetry += 1
    if (backRetry > backMaxRetry) {
      needExit = true
    } else {
      log.info(s"backRetry=${backRetry}")
    }

    depth -= 1
  }


  def getAvailableElement(): Seq[UrlElement] ={
    var all = getSelectedElements().getOrElse(Seq[immutable.Map[String, String]]())
    log.info(s"all nodes length=${all.length}")
    //去掉黑名单, 这样rule优先级高于黑名单
    all = all.filter(isBlack(_) == false)
    log.info(s"all non-black nodes length=${all.length}")
    //去掉back菜单
    all = all diff getBackElements()
    log.info(s"all non-black non-back nodes length=${all.length}")
    //把元素转换为Element对象
    var allElements = all.map(getUrlElementByMap(_).get)
    //获得所有未点击元素
    log.info(s"all elements length=${allElements.length}")
    //过滤已经被点击过的元素
    allElements = allElements.filter(!isClicked(_))
    log.info(s"fresh elements length=${allElements.length}")
    //记录未被点击的元素
    allElements.foreach(e => {
      if (!elements.contains(e.toString())) {
        elements(e.toString()) = false
        log.info(s"fresh = ${e}")
      }
    })
    allElements

  }
  /**
    * 优化后的递归方法. 尾递归.
    * 刷新->找元素->点击第一个未被点击的元素->刷新
    */
  @tailrec private def crawl(): Unit = {
    //超时退出
    if ((new Date().getTime - startTime) > conf.maxTime * 1000) {
      log.info("maxTime out Quit")
      needExit = true
    }
    if (needExit) {
      return
    }
    depth += 1
    log.trace(s"depth=${depth}")
    //如果之前刷新过就跳过,但是下一次要求重新刷新
    if(needRefresh){
      refreshPage()
    }else{
      log.info("already refresh skip for speed")
    }
    needRefresh=true

    //是否需要退出或者后退
    if (isReturn()) {
      log.info("need return")
      goBack()
      doRuleAction()
    } else {
      //先判断是否命中规则.
      if (!doRuleAction()) {
        val allElements=getAvailableElement()
        if (allElements.nonEmpty) {
          val currentElement=allElements.head
          doElementAction(currentElement)
          //刷新页面. 让schema更新
          refreshPage()
          //保存截屏和log
          saveLog()
          saveScreen(currentElement)
          backRetry = 0
          swipeRetry = 0
        } else {
          log.info("all elements had be clicked")
          if (swipeRetry > swipeMaxRetry) {
            log.info("swipeRetry > swipeMaxRetry")
            return
          }
          scroll()
        }
      }
    }
    crawl()
  }


  def scrollAction(direction: String = "up"): Option[_] ={
    log.info(s"start scroll ${direction}")
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
    )


  }
  def scroll(direction: String = "up"): Unit = {
    if (swipeCountPerUrl.contains(url) == false) {
      swipeCountPerUrl(url) = 0
    }

    if (swipeCountPerUrl(url) > swipeMaxCountPerUrl) {
      log.info("swipeRetry of per url > swipeMaxCountPerUrl")
      swipeRetry += 1
      return
    }

    scrollAction(direction) match {
      case Some(v) => {
        log.info("swipe success")
        swipeRetry += 1
        swipeCountPerUrl(url) += 1
        log.info(s"swipeCount of current Url=${swipeCountPerUrl(url)}")
        saveScreen(UrlElement(url, "Scroll", "", "", ""))
      }
      case None => {
        log.info("swipe fail")
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
    log.info(s"find element by uid ${uid}")
    if (uid.id != "") {
      log.info(s"find by id=${uid.id}")
      doAppium(driver.findElementsById(uid.id)) match {
        case Some(v) => {
          val arr=v.toArray().distinct
          if (arr.length == 1) {
            log.info("find by id success")
            return Some(arr.head.asInstanceOf[WebElement])
          } else {
            //有些公司可能存在重名id
            arr.foreach(log.info)
            log.info(s"find count ${arr.size}, change to find by xpath")
          }
        }
        case None => {
          log.warn("find by id error")
        }
      }
    }
    platformName.toLowerCase() match {
      case "ios" => {
        log.info(s"find by xpath=//${uid.tag}[@path='${uid.loc}']")
        //照顾iOS android会在findByName的时候自动找text属性.
        doAppium(driver.findElementByXPath(
          //s"//${uid.tag}[@name='${uid.id}' and @value='${uid.name}' and @x='${uid.loc.split(',').head}' and @y='${uid.loc.split(',').last}']"
          s"//${uid.tag}[@path='${uid.loc}']"
        )) match {
          case Some(v) => {
            log.info("find by xpath success")
            return Some(v)
          }
          case None => {
            log.warn("find by xpath error")
          }
        }
      }
      case "android" => {
        //findElementByName在appium1.5中被废弃 https://github.com/appium/appium/issues/6186
        /*
        if (uid.name != "") {
          log.info(s"find by name=${uid.name}")
          doAppium(driver.findElementsByName(uid.name)) match {
            case Some(v) => {
              val arr=v.toArray().distinct
              if (arr.length == 1) {
                log.info("find by name success")
                return Some(arr.head.asInstanceOf[WebElement])
              } else {
                //有些公司可能存在重名name
                arr.foreach(log.info)
                log.info(s"find count ${arr.size}, change to find by xpath")
              }
            }
            case None => {
            }
          }
        }
        */
        //xpath会较慢
        log.info(s"find by xpath=${uid.loc}")
        doAppium(driver.findElementsByXPath(uid.loc)) match {
          case Some(v) => {
            val arr=v.toArray().distinct
            if (arr.length == 1) {
              log.info("find by xpath success")
              return Some(arr.head.asInstanceOf[WebElement])
            } else {
              //有些公司可能存在重名id
              arr.foreach(log.info)
              log.warn(s"find count ${v.size()}, you should check your dom file")
              if(arr.size>0){
                log.info("just use the first one")
                return Some(arr.head.asInstanceOf[WebElement])
              }
            }
          }
          case None => {
            log.warn("find by xpath error")
          }
        }

      }
    }
    None
  }

  def doAppium[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        log.info("do appium action success")
        Some(v)
      }
      case Failure(e) => {
        log.warn("message=" + e.getMessage)
        log.warn("cause=" + e.getCause)
        //log.trace(e.getStackTrace.mkString("\n"))
        None
      }
    }

  }

  def saveLog(): Unit = {
    log.info("save log")
    //记录点击log
    File(s"${conf.resultDir}/clickedList.log").writeAll(clickedList.mkString("\n"))
    File(s"${conf.resultDir}/ElementList.log").writeAll(elements.mkString("\n"))
    File(s"${conf.resultDir}/freemind.mm").writeAll(
      elementTree.generateFreeMind(elementTreeList)
    )
    log.info("save log end")
  }

  def saveScreen(e: UrlElement): Unit = {
    imgIndex += 1
    //保存dom结构
    log.info("save dom")
    val domPath = s"${conf.resultDir}/${imgIndex}_" + e.toString().replace("\n", "").replaceAll("[ /,]", "").take(200) + ".dom"
    File(domPath).writeAll(pageSource)
    log.info("save dom end")

    //如果是schema相同. 界面基本不变. 那么就跳过截图加快速度.
    if (conf.saveScreen && lastSchema != currentSchema) {
      Thread.sleep(100)
      log.info("start screenshot")
      val path = s"${conf.resultDir}/${imgIndex}_" + e.toString().replace("\n", "").replaceAll("[ /,]", "").take(200) + ".jpg"

      val getScreen = new Thread(new Runnable {
        override def run(): Unit = {
          log.info("new thread")
          doAppium((driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)) match {
            case Some(src) => {
              FileUtils.copyFile(src, new java.io.File(path))
              log.info("save screenshot end")
            }
            case None => {
              log.warn("get screenshot error")
            }
          }
        }
      })
      getScreen.start()
      //最多等待5s
      var needStopThread=false
      var stopThreadCount=0
      while(needStopThread==false){
        if(getScreen.getState!=Thread.State.TERMINATED){
          stopThreadCount+=1
          //超时退出
          if(stopThreadCount>=10){
            log.info("timeout stop thread")
            getScreen.stop()
            needStopThread=true
          }else{
            //等待
            log.info("waiting thread")
            Thread.sleep(1000)
          }
        }else{
          //正常退出
          log.info("thread finish")
          needStopThread=true
        }
      }
    }
  }


  def appendClickedList(e: UrlElement): Unit = {
    clickedList.append(e.toString())
    elementTreeList.append(UrlElement(e.url, "url", "", "", ""))
    elementTreeList.append(e)
  }

  def doAppiumAction(e: UrlElement, action: String): Option[Unit] = {
    findElementByUrlElement(e) match {
      case Some(v) => {
        action match {
          case "click" => {
            log.info(s"click ${v}")
            val res = doAppium(v.click())
            appendClickedList(e)
            doAppium(driver.hideKeyboard())
            res
          }
          case "skip" => {
            log.info("skip")
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
            log.info(s"sendkeys ${v} with ${str}")
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
        log.warn("find error")
        None
      }
    }
  }

  def doAppiumAction(action: String = "click"): Unit = {
        action match {
          case "skip" => {
            log.info("skip")
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
    log.info("rule match start")
    //先判断是否在期望的界面里. 提升速度
    var isHit = false
    conf.elementActions.foreach(r => {
      log.trace(s"for each rule ${r}")
      val idOrName = r("idOrName").toString
      val action = r("action").toString
      val times = r("times").toString.toInt
      log.trace(s"idOrName=${idOrName} action=${action} times=${times}")
      val all = getRuleMatchNodes()

      (all.filter(_ ("name").toString.matches(idOrName)) ++ all.filter(_ ("value").toString.matches(idOrName))).distinct.foreach(x => {
        //获得正式的定位id
        getUrlElementByMap(x) match {
          case Some(e) => {
            log.info(s"element=${e} action=${action}")
            isHit = true
            doAppiumAction(e, action.toString) match {
              case None => {
                log.info("do rule action fail")
              }
              case Some(v) => {
                log.info("do rule action success")
                r("times") = times - 1
                if (times == 1) {
                  log.info(s"remove rule ${r}")
                  conf.elementActions -= r
                }
              }
            }
          }
          case None => log.warn("get element id error")
        }
      })

    })

    isHit

  }
}
