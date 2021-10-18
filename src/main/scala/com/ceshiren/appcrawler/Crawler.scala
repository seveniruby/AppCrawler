package com.ceshiren.appcrawler

import com.ceshiren.appcrawler.driver._
import com.ceshiren.appcrawler.plugin.Plugin
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.scalatest.ConfigMap
import org.w3c.dom.Document
import sun.misc.Signal

import java.io
import java.text.SimpleDateFormat
import java.util.Date
import scala.annotation.tailrec
import scala.collection.mutable.{ListBuffer, Map}
import scala.collection.{JavaConverters, immutable, mutable}
import scala.jdk.CollectionConverters._
import scala.reflect.io.File
import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 15/11/28.
  */
class Crawler extends CommonLog {
  //todo: 需要重构为抽象和实现类
  var driver: ReactWebDriver = _
  var conf = new CrawlerConf()

  /** 存放插件类 */
  val pluginClasses = ListBuffer[Plugin]()

  var store: URIElementStore = _

  private var currentElementAction = "click"
  private var platformName = ""

  var appName = ""
  /** 当前的url路径 */
  var currentUrl = ""

  private var exitCrawl = false
  private var backRetry = 0
  //最大重试次数
  var backMaxRetry = 5
  private var afterAllRetry = 0
  private var notFoundRetry = 0
  private var notFoundMax = 2
  //滑动最大重试次数
  var stopAll = false
  val signals = new DataRecord()
  signals.append(1)
  private val startTime = new Date().getTime

  val urlStack = mutable.Stack[String]()

  protected val backDistance = new DataRecord()
  val appNameRecord = new DataRecord()
  protected val contentHash = new DataRecord

  protected val webViewRecord = new DataRecord

  private val refreshResult = new DataRecord

  private var lastBtnIndex = 3;

  private val backAction = "_Back"
  private val afterAllAction = "_AfterAll"
  private val backAppAction = "_BackApp"
  private val skipAction = "_skip"


  /**
    * 根据类名初始化插件. 插件可以使用java编写. 继承自Plugin即可
    */
  def loadPlugins(): Unit = {
    val defaultPlugins = List(
      "com.ceshiren.appcrawler.plugin.TagLimitPlugin",
      "com.ceshiren.appcrawler.plugin.ReportPlugin",
      "com.ceshiren.appcrawler.plugin.FreeMind"
    )
    defaultPlugins.foreach(name => pluginClasses.append(Class.forName(name).newInstance().asInstanceOf[Plugin]))
    //把插件jar文件放到classpath下即可
    conf.pluginList.foreach(name => {
      if (defaultPlugins.forall(_ != name)) {
        log.info(s"load com.ceshiren.appcrawler.plugin $name")
        pluginClasses.append(Class.forName(name).newInstance().asInstanceOf[Plugin])
      }
    })

    /*    //todo: 放到根目录有bug. 需要解决
    //todo: 暂时禁用，用的人太少
        val dynamicPluginDir = (new java.io.File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath))
          .getParentFile.getCanonicalPath + File.separator + "plugins" + File.separator
        log.info(s"dynamic load plugin in ${dynamicPluginDir}")
        val dynamicPlugins = Util.loadPlugins(dynamicPluginDir)
        log.info(s"found dynamic plugins size ${dynamicPlugins.size}")
        dynamicPlugins.foreach(pluginClasses.append(_))
        */
    pluginClasses.foreach(log.info)
    pluginClasses.foreach(p => p.init(this))
    pluginClasses.foreach(p => p.start())
  }

  /**
    * 加载配置文件并初始化
    */
  def loadConf(crawlerConf: CrawlerConf): Unit = {
    conf = crawlerConf
    AppCrawler.factory =new URIElementFactory()
    store = AppCrawler.factory.generateElementStore
    log.setLevel(GA.logLevel)
  }

  def loadConf(file: String): Unit = {
    conf = new CrawlerConf().load(file)
    log.setLevel(GA.logLevel)
  }


  /**
    * 启动爬虫
    */
  def start(existDriver: ReactWebDriver = null): Unit = {
    log.addAppender(AppCrawler.fileAppender)
    log.debug("crawl config")
    log.debug(conf.toYaml())
    if (conf.xpathAttributes != null) {
      log.info(s"set xpath attribute with ${conf.xpathAttributes}")
      XPathUtil.setXPathExpr(conf.xpathAttributes)
    }

    loadPlugins()

    if (existDriver == null) {
      log.info("prepare setup Appium")
      setupAppium()
      //driver.getAppStringMap
    } else {
      //集成到测试用例中，比如有人写appium测试用例，在自动化后可以直接调用api进行遍历
      log.info("use existed driver")
      this.driver = existDriver
    }

    log.info(s"platformName=${platformName} driver=${driver}")
    log.info(AppCrawler.banner)
    log.info("waiting for app load")
    Thread.sleep(conf.beforeStartWait)
    log.info(s"driver=${existDriver}")
    log.info("get screen info")
    driver.getDeviceInfo()

    //todo: 不是所有的实现都支持
    //driver.manage().logs().getAvailableLogTypes().toArray.foreach(log.info)
    //设定结果目录
    firstRefresh()
    log.info("append current app name to appWhiteList")
    conf.appWhiteList.append(appNameRecord.last().toString)

    if (conf.testcase != null) {
      log.info("run steps")
      runSteps()
    } else {
      log.info("no testcase")
    }

    if (conf.selectedList.nonEmpty) {
      crawlWithRetry(conf.maxDepth)
    } else {
      log.info("no selectedList")
    }

  }

  def crawlWithRetry(depth: Int): Unit = {
    //清空堆栈 开始重新计数
    conf.maxDepth = depth
    //depth=urlStack.size
    urlStack.clear()
    refreshPage()
    handleCtrlC()

    //启动第一次
    var errorCount = 1
    while (errorCount > 0) {
      Try(crawl()) match {
        //todo: exitCrawl变量设置
        case Success(v) => {
          log.info("crawl finish")
          errorCount = 0
        }
        case Failure(e) => {
          log.error("crawl not finish, return with exception")
          log.error(e.getLocalizedMessage)
          log.error(ExceptionUtils.getRootCauseMessage(e))
          log.error(ExceptionUtils.getRootCause(e))
          ExceptionUtils.getRootCauseStackTrace(e).foreach(log.error)
          log.error("create new session")

          errorCount += 1
          throw e
          restart()
        }
      }

    }

    //爬虫结束
    stop()
    sys.exit()
  }

  def restart(): Unit = {
    if (conf.beforeRestart != null) {
      log.info("execute shell on restart")
      conf.beforeRestart.foreach(DynamicEval.dsl(_))
    }
    log.info("restart appium")
    conf.capability ++= Map("app" -> "")
    conf.capability ++= Map("dontStopAppOnReset" -> "true")
    conf.capability ++= Map("noReset" -> "true")
    setupAppium()
    //todo: 采用轮询
    Thread.sleep(conf.beforeStartWait)
    firstRefresh()
  }

  def firstRefresh(): Unit = {
    refreshPage()
    log.info("first refresh")
    val element = getEventElement("Start")
    beforeElementAction(element)
    doElementAction(element)
    afterElementAction(element)

  }

  def getEventElement(actionName: String): URIElement = {
    val defaultElement = AppCrawler.factory.generateElement(currentUrl, "", "", "", "", "", "", "", "", "/*/*", "", 0, 0, driver.screenWidth, driver.screenHeight, "")
    val element = driver.asyncTask()(
      //刷新失败时会报错，所以增加一个判断
      if (driver.currentPageDom != null) {
        AppCrawler.factory.generateElement(XPathUtil.getNodeListByKey("/*/*", driver.currentPageDom).head, currentUrl)
      } else {
        defaultElement
      }
    ) match {
      case Left(value) => value
      case Right(value) => defaultElement
    }
    element.setId(actionName)
    element.setName(actionName)
    element.setTag(actionName)
    element.setAction(s"_${actionName}")
    element
  }

  def runSteps(): Unit = {
    log.info("run testcases")
    new AutomationSuite().execute("run steps", ConfigMap("crawler" -> this))
  }

  def setupAppium(): Unit = {
    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))

    //todo: init all var
    afterAllRetry = 0
    backRetry = 0

    log.info(s"afterAllMax=${conf.afterAllMax}")
    DynamicEval.isLoaded = false

    //todo: 主要做遍历测试和异常测试. 所以暂不使用selendroid
    val url = conf.capability("appium").toString
    val automationName = conf.capability.getOrElse("automationName", "").toString
    log.info(automationName)
    automationName match {
      case "selenium" => {
        driver = new SeleniumDriver(url, conf.capability)

      }
      //todo: 以后使用restful接口支持atx和macaca
      /*      case "macaca" => {
              log.info("use macaca")
              driver = new MacacaDriver(url, conf.capability)
            }*/
      case "adb" => {
        log.info("user adb")
        driver = new AdbDriver(url, conf.capability)
      }

      //todo: 把androidDriver与seleniumdriver独立
      //todo: 支持图片
      //todo: 支持web、windows、mac
      /*case "sikuli" => {
        log.info("use SikuliDriver")
        conf.capability++=Map("automationName"-> "Appium")
        driver=new SikuliDriver(url, conf.capability)
        if (conf.sikuliImages != null) {
          driver.imagesDir=conf.sikuliImages
        }else{
          log.error("please set sikuliImages with your images directory")
        }
      }*/
      case _ => {
        log.info("use AppiumClient")
        log.info(conf.capability)
        //fixed: appium 6.0.0 has bug with okhttp
        //System.setProperty("webdriver.http.factory", "apache")
        driver = new AppiumClient(url, conf.capability)
        log.info(driver)
      }
    }

    GA.log(conf.capability.getOrElse("appPackage", "") + conf.capability.getOrElse("bundleId", "").toString)

  }

  /**
    * 获得布局Hash
    */
  def getSchema(): String = {
    val nodeList = driver.getNodeListByKey("//*[not(ancestor-or-self::UIAStatusBar)]")
    TData.md5(1, nodeList.map(getUrlElementByMap(_).getAncestor()).distinct.mkString("\n"))
  }

  def getUri(): String = {
    val uri = if (conf.suiteName != null && conf.suiteName.nonEmpty) {
      val urlString = conf.suiteName.flatMap(driver.getNodeListByKey(_)).distinct.map(x => {
        //按照attribute, label, name顺序挨个取第一个非空的指x
        List(
          x.getOrElse("attribute", ""),
          x.getOrElse("text", ""),
          x.getOrElse("value", ""),
          x.getOrElse("content-desc", ""),
          x.getOrElse("label", "")
        ).filter(_.toString.nonEmpty).headOption.getOrElse("")
      }).filter(_.toString.nonEmpty).mkString("-")
      log.info(s"defineUrl=$urlString")
      urlString
    } else {
      ""
    }
    if (uri.nonEmpty) {
      List(driver.getAppName(), uri).distinct.filter(_.nonEmpty).mkString(".")
    } else {
      List(driver.getAppName(), driver.getUrl()).distinct.filter(_.nonEmpty).mkString(".")
    }
  }

  /**
    * 获取控件的基本属性并设置一个唯一的uid作为识别. screenName+id+name
    *
    * @param nodeMap
    * @return
    */
  def getUrlElementByMap(nodeMap: immutable.Map[String, Any]): URIElement = {
    AppCrawler.factory.generateElement(nodeMap, currentUrl)
  }

  def needBackToApp(): Option[URIElement] = {
    log.debug(conf.appWhiteList)
    log.debug(appNameRecord.last(10))

    //跳到了其他app. 排除点一次就没有的弹框
    if (conf.appWhiteList.forall(appNameRecord.last().toString.matches(_) == false)) {
      log.warn(s"not in app white list ${conf.appWhiteList}")
      log.warn(s"jump to other app appName=${appNameRecord.last()} lastAppName=${appNameRecord.pre()}")
      Some(getEventElement("BackApp"))
    } else {
      None
    }

  }

  def needExitApp(): Boolean = {
    //超时退出
    if ((new Date().getTime - startTime) > conf.maxTime * 1000) {
      log.fatal("maxTime out Quit need exit")
      return true
    }
    if (backRetry >= backMaxRetry) {
      log.fatal(s"backRetry ${backRetry} >= backMaxRetry ${backMaxRetry} need exit")
      return true
    }

    if (appNameRecord.last(5).forall(conf.appWhiteList.contains(_) == false)) {
      log.error(appNameRecord.last(10))
      log.error(conf.appWhiteList)
      log.fatal(s"appNameRecord last 5 ${appNameRecord.last(5)}")
      return true
    }
    return false
  }

  def needBackToPage(): Option[URIElement] = {
    //url黑名单
    var result = false
    if (conf.urlBlackList.exists(urlStack.head.matches(_))) {
      log.info(s"${urlStack.head} in urlBlackList should return")
      result = true
    }

    //url白名单, 第一次进入了白名单的范围, 就始终在白名单中. 不然就算不在白名单中也得遍历.
    //上层是白名单, 当前不是白名单才需要返回
    if (conf.urlWhiteList.size > 0
      && conf.urlWhiteList.filter(urlStack.head.matches(_)).isEmpty
      && conf.urlWhiteList.filter(urlStack.tail.headOption.getOrElse("").matches(_)).nonEmpty) {
      log.warn(s"${urlStack.head} not in urlWhiteList should return")
      result = true
    }

    //超过遍历深度
    log.debug(s"urlStack=${urlStack} baseUrl=${conf.baseUrl} maxDepth=${conf.maxDepth}")
    //大于最大深度并且是在进入过基础Url
    if (urlStack.length > conf.maxDepth) {
      log.info(s"urlStack.depth=${urlStack.length} > maxDepth=${conf.maxDepth}")
      result = true
    }
    /*
        //回到桌面了

        if (urlStack.filter(_.matches("Launcher.*")).nonEmpty || appName.matches("com.android\\..*")) {
          log.warn(s"maybe back to desktop ${urlStack.reverse.mkString("-")} need exit")
          //尝试后腿 而不是退出
          //needExit = true
          return true
        }
    */

    if (result) {
      log.info("need to back")
      //todo: 延迟到最终定位点击的时候
      getBackButton()
    } else {
      None
    }


  }

  def isValid(element: URIElement): Boolean = {
    element.getValid == "true"
  }


  def isSmall(element: URIElement): Boolean = {

    var res = false

    if (element.getX > driver.screenHeight || element.getY > driver.screenWidth) {
      log.warn(element)
      log.info("not visual")
      res = true
    }

    //高度小就跳过
    if (element.getHeight < 30 && element.getWidth < 30) {
      log.warn(element)
      log.info("small")
      res = true
    }

    res
  }

  //todo: 支持xpath表达式
  def getAvailableElement(currentPageDom: Document, skipSmall: Boolean = false): Option[URIElement] = {
    var all = List[URIElement]()
    var firstElements = List[URIElement]()
    var lastElements = List[URIElement]()
    var selectedElements = List[URIElement]()
    var blackElements = List[URIElement]()
    var lastSize = 0

    conf.selectedList.foreach(step => {
      log.trace(s"selectedList xpath =  ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom)
        .map(e=>AppCrawler.factory.generateElement(e, currentUrl))
      temp.foreach(log.trace)
      selectedElements ++= temp
    })
    selectedElements = selectedElements.distinct
    log.info(s"selected nodes size = ${selectedElements.size}")
    lastSize = selectedElements.size

    //remove blackList
    conf.blackList.foreach(step => {
      log.trace(s"blackList xpath =  ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom)
        .map(e=>AppCrawler.factory.generateElement(e, currentUrl))
      temp.foreach(log.trace)
      blackElements ++= temp
    })
    blackElements = blackElements.distinct
    selectedElements = selectedElements diff blackElements
    log.info(s"selectedElements - black elements size = ${selectedElements.size}")
    if (selectedElements.size < lastSize) {
      selectedElements.foreach(log.trace)
      lastSize = selectedElements.size
    }

    //done: 放到前面加速
    //remove small
    if (skipSmall == false) {
      selectedElements = selectedElements.filter(isSmall(_) == false)
      log.info(s"selectedElements - small elements size = ${selectedElements.size}")
      if (selectedElements.size < lastSize) {
        selectedElements.foreach(log.trace)
        lastSize = selectedElements.size
      }
    }

    //去掉back菜单
    selectedElements = selectedElements diff conf.backButton.flatMap(step => getURIElementsByStep(step))
    log.info(s"selectedElements - backButton size=${selectedElements.length}")
    if (selectedElements.size < lastSize) {
      selectedElements.foreach(log.trace)
      lastSize = selectedElements.size
    }

    //过滤已经被点击过的元素
    selectedElements = selectedElements.filter(!store.isClicked(_))
    log.info(s"selectedElements - clicked size=${selectedElements.size}")
    if (selectedElements.size < lastSize) {
      selectedElements.foreach(log.trace)
      lastSize = selectedElements.size
    }

    selectedElements = selectedElements.filter(!store.isSkipped(_))
    log.info(s"selectedElements - skiped fresh elements size=${selectedElements.length}")
    if (selectedElements.size < lastSize) {
      selectedElements.foreach(e => {
        log.trace(e)
        //记录未被点击的元素
        store.saveElement(e)
      })
      lastSize = selectedElements.size
    }

    //根据属性进行基本排序
    selectedElements = sortByAttribute(selectedElements)

    //sort
    conf.firstList.foreach(step => {
      log.trace(s"firstList xpath = ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom)
        .map(e => AppCrawler.factory.generateElement(e, currentUrl))
        .intersect(selectedElements)
      temp.foreach(log.trace)
      firstElements ++= temp
    })

    conf.lastList.foreach(step => {
      log.trace(s"lastList xpath = ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom)
        .map(e=>AppCrawler.factory.generateElement(e, currentUrl))
        .intersect(selectedElements)
      temp.foreach(log.trace)
      lastElements ++= temp
    })

    //再根据先后顺序调整，这样只需要排序同级元素
    //去掉不在first和last中的元素
    selectedElements = selectedElements diff firstElements
    selectedElements = selectedElements diff lastElements
    //确保不重, 并保证顺序
    all = (firstElements ++ selectedElements ++ lastElements).distinct.filter(isValid)

    log.trace(s"sorted nodes length=${all.length}")
    all.foreach(log.trace)
    lastSize = all.size

    all.headOption
  }


  def sortByAttribute(list: List[URIElement]): List[URIElement] = {
    //先根据depth排序selectedElements
    var selectedElements = list
    conf.sortByAttribute.foreach(attribute => {
      attribute match {
        //一个有趣的算法，将来其他遍历工具也会抄这段逻辑
        case "depth" => {
          selectedElements = selectedElements.sortWith(
            _.getDepth.toInt >
              _.getDepth.toInt
          )
        }
        case "selected" => {
          //todo:同级延后，在未实现之前，先通过lastList去显式声明那些菜单应该最后遍历
          //selected=false的优先遍历
          selectedElements = selectedElements.sortWith(
            _.getSelected.contains("false") >
              _.getSelected.contains("false")
          )
        }
        case "list" => {
          //列表内元素优先遍历
          selectedElements = selectedElements.sortWith(
            _.getAncestor.contains("List") >
              _.getAncestor.contains("List")
          )
          selectedElements = selectedElements.sortWith(
            _.getAncestor.contains("RecyclerView") >
              _.getAncestor.contains("RecyclerView")
          )
        }
        //todo: 居中的优先遍历

      }
      log.trace(s"sort by ${attribute}")
      selectedElements.foreach(e => log.trace(
        s"depth=${e.getDepth}" +
          s" selected=${e.elementUri()}" +
          s" list=${e.getAncestor.contains("List")} e=${e}")
      )
    })
    selectedElements
  }

  def ifWebViewPage(): Unit = {
    if (XPathUtil.getNodeListByKey("//*[contains(@class, 'WebView')]", driver.currentPageDom).size > 0) {
      webViewRecord.append(true)
    } else {
      webViewRecord.append(false)
    }
  }

  //todo: 刷新失败需要一个异常处理逻辑
  def refreshPage(): Boolean = {
    log.info("refresh page")
    driver.getPageSourceWithRetry()

    if (driver.currentPageSource != null) {

      // 获取页面信息以后判断是否包含webView
      ifWebViewPage
      // 如果是第一次加载，等3s 【暴力，暂不使用】
      if (webViewRecord.last() == true && webViewRecord.pre() == false) {
        //        log.info("The first time to enter a web page , wait 3 seconds")
        //        Thread.sleep(3000)
      }

      parsePageContext()
      refreshResult.append(true)
      return true
    } else {
      log.warn("page source get fail, go back")
      refreshResult.append(false)
      return false
    }
    //appium解析pageSource有bug. 有些页面会始终无法dump. 改成解析不了就后退
  }

  def parsePageContext(): Unit = {
    appName = driver.getAppName()
    log.info(s"appName = ${appName}")
    appNameRecord.append(appName)

    currentUrl = getUri()
    log.info(s"url=${currentUrl}")
    //如果跳回到某个页面, 就弹栈到特定的页面, 比如回到首页
    if (urlStack.contains(currentUrl)) {
      while (urlStack.head != currentUrl) {
        log.debug("pop urlStack")
        urlStack.pop()
      }
    } else {
      urlStack.push(currentUrl)
    }
    //判断新的url堆栈中是否包含baseUrl, 如果有就清空栈记录并从新计数
    if (conf.baseUrl.map(urlStack.head.matches(_)).contains(true)) {
      log.info("clear urlStack")
      urlStack.clear()
      urlStack.push(currentUrl)
    }
    log.trace(s"urlStack=${urlStack}")
    //使用当前的页面. 不再记录堆栈.先简化

    //val contexts = MiniAppium.doAppium(driver.getContextHandles).getOrElse("")
    //val windows=MiniAppium.doAppium(driver.getWindowHandles).getOrElse("")
    //val windows = ""
    //log.trace(s"windows=${windows}")

    // 通过标识what取对应的md5值
    contentHash.append(TData.md5(2, ""))
    log.info(s"currentContentHash=${contentHash.last()} lastContentHash=${contentHash.pre()}")
    if (contentHash.isDiff()) {
      log.info("ui change")
    } else {
      log.info("ui not change")
    }
    afterUrlRefresh()
  }

  def afterUrlRefresh(): Unit = {
    pluginClasses.foreach(p => p.afterUrlRefresh(currentUrl))
  }


  def beforeElementAction(element: URIElement): Unit = {
    log.debug(s"push ${element.elementUri()}")
    store.setElementClicked(element)
    //todo: 改进设计
    driver.currentURIElement = element

    if (conf.beforeElement != null) {
      log.trace("beforeElementAction")
      conf.beforeElement.foreach(step => {
        val xpath = step.getXPath()
        val action = step.getAction()
        if (driver.getNodeListByKey(xpath).contains(element)) {
          DynamicEval.dsl(action)
        }
      })
    }
    pluginClasses.foreach(p => p.beforeElementAction(element))
  }

  def afterElementAction(element: URIElement): Unit = {
    val newImageFile = new io.File(getBasePathName() + ".click.png")
    val originImageFile = new io.File(getBasePathName(2) + ".clicked.png")
    if (newImageFile.exists() == false && originImageFile.exists() == true) {
      log.info("use last clicked image replace mark for skip screenshot again")
      FileUtils.copyFile(originImageFile, newImageFile)
    } else {
      log.info("mark image exist")
    }

    if (conf.afterElement != null && conf.afterElement.nonEmpty) {
      log.info("afterElementAction eval")
      conf.afterElement.foreach(step => {
        DynamicEval.dsl(step.getAction())
      })
      //重新刷新，afterElement后内容可能发生变化
      refreshPage()
    }

    store.saveResTime(new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS").format(new Date()))
    saveDom()
    saveScreen()

    store.saveResHash(contentHash.last().toString)
    store.saveResImg(getBasePathName() + ".clicked.png")
    //todo: 内存消耗太大，改用文件存储
    store.saveResDom(driver.currentPageSource)

    element.getAction match {
      case this.backAction => {
        backRetry += 1
      }
      case this.backAppAction => {
        backRetry += 1
      }
      case this.afterAllAction => {
        //afterAllMax可以控制最大尝试次数
      }
      case _ => {
        // backRetry判断退出App的次数
        backRetry = 0
      }
    }

    log.info(s"backRetry=${backRetry}")

    pluginClasses.foreach(p => p.afterElementAction(element))
  }

  /**
    * 从历史数据中寻找可能的back按键，实现自动分析发现
    *
    * @return
    */
  def getPredictBackNodes(): List[URIElement] = {
    //去掉开头的界面切换
    var urlList = ListBuffer[String]()
    (lastBtnIndex until store.getClickedElementsList.size).map(i => {
      val curElement = store.getClickedElementsList(i)
      val preElement = store.getClickedElementsList(i - 1)
      urlList.append(curElement.getUrl)
      if (curElement.getUrl != preElement.getUrl
        && urlList.indexOf(curElement.getUrl) < i - 3 - 1
        && preElement.center().getY < driver.screenHeight / 8) {
        log.info(s"get nearby back button from history = ${preElement}")
        // 更新索引
        lastBtnIndex = i
        Some(Step(xpath = preElement.getXpath, action = preElement.getAction))
      } else {
        None
      }
    }).filter(_.nonEmpty).map(someStep => {
      getURIElementsByStep(someStep.get).map(e => {
        e.setAction(someStep.get.getAction())
        e
      })
    }).flatten.distinct.toList
      //排序，depth小的在前面，但是带有back action的控件排在最后
      .sortWith(_.getDepth < _.getDepth)
      .sortWith(_.getAction.indexOf("Back") < _.getAction.indexOf("Back"))
      //追加到backButton后面，depth小的放前面
      .map(e => {
        if (conf.backButton.filter(_.getXPath() == e.getXpath).size == 0 && e.getAction != backAction) {
          log.info(s"find new back button from history ${e}")
          conf.backButton.append(Step(xpath = e.getXpath, action = e.getAction))
        }
        e
      })
  }

  //todo: 增加when支持，当when生效的时候才返回element
  def getURIElementsByStep(step: Step): List[URIElement] = {
    driver.getNodeListByKey(step.getXPath())
      .map(e => {
        val urlElement = AppCrawler.factory.generateElement(e, currentUrl)
        urlElement.setAction(step.getAction())
        urlElement
      }).filter(isValid)
  }

  def isEndlessLoop(): Boolean = {
    val index = store.getClickedElementsList.length
    var curA: URIElement = null
    var curB: URIElement = null
    for (i <- index - 1 to index - 6) {
      if (i % 2 == 0) {
        if (curA == null) {
          curA = store.getClickedElementsList(i)
        } else if (curA != store.getClickedElementsList(i)) {
          false
        }
      } else {
        if (curB == null) {
          curB = store.getClickedElementsList(i)
        } else if (curB != store.getClickedElementsList(i)) {
          false
        }
      }
    }
    true
  }

  def getBackButton(): Option[URIElement] = {
    log.info("go back")
    //找到可能的关闭按钮, 取第一个可用的关闭按钮
    log.trace(conf.backButton)
    conf.backButton.flatMap(step => getURIElementsByStep(step)).headOption match {
      case Some(backElement) if appNameRecord.isDiff() == false => {

        if (isEndlessLoop()) {
          return Some(getEventElement("Back"))
        }

        //app相同并且找到back控件才点击. 否则就默认back
        log.trace(backElement)
        if (backElement.getAction.isEmpty) {
          backElement.setAction("click")
        } else {
          log.info(s"use origin action ${backElement.getAction}")
        }

        // 通过配置文件设置的Xpath找到返回键，将其真实Xpath添加进List
        if (conf.backButton.filter(_.getXPath() == backElement.getXpath).size == 0 && backElement.getAction != backAction) {
          log.info(s"find new back button from configuration file ${backElement}")
          conf.backButton.append(Step(xpath = backElement.getXpath, action = backElement.getAction))
        }

        return Some(backElement)
      }
      case _ => {
        log.info("can't find backButton button from config")
        log.info("find backButton from history")
        getPredictBackNodes().headOption match {
          case Some(backElement) if appNameRecord.isDiff() == false => {
            //app相同并且找到back控件才点击. 否则就默认back
            //todo: bug hierarchy click出现
            if (backElement.getAction.isEmpty) {
              backElement.setAction("click")
            } else {
              log.info(s"use origin action ${backElement.getAction}")
            }
            log.info(s"find back button ${backElement}")
            return Some(backElement)
          }
          case _ => {
            log.info("can't find backButton from history")
            return Some(getEventElement("Back"))
          }
        }
      }
    }
  }


  def fixElementAction(element: URIElement) = {
    pluginClasses.foreach(c => c.fixElementAction(element))
  }

  //todo: 可以将来用队列重构下看看效果

  /**
    * 优化后的递归方法. 尾递归.
    * 刷新->找元素->点击第一个未被点击的元素->刷新
    */
  @tailrec
  final def crawl(): Unit = {
    if (exitCrawl) {
      log.fatal("exitCrawl=true, return")
      return
    }
    log.info("\n\ncrawl next")
    //是否需要退出或者后退, 得到要做的动作
    var nextElement: Option[URIElement] = None
    //判断下一步动作

    //是否应该退出
    if (needExitApp()) {
      log.fatal("get signal to exit")
      exitCrawl = true
    }
    //页面刷新失败自动后退
    if (refreshResult.last() == false) {
      log.error("refresh fail")
      //todo: 使用自动获取的元素
      nextElement = Some(getEventElement("Back"))

    } else {
      log.debug("refresh success")
    }

    //todo: 将来与selectedList合并，通过优先级别标记
    //先应用优先规则，trigger中的元素可以不用包含在selectedList中
    if (nextElement == None) {
      nextElement = getElementByTriggerActions()
    }

    //是否需要回退到app
    if (nextElement == None) {
      nextElement = needBackToApp()
    }

    //判断是否需要返回上层
    if (nextElement == None) {
      nextElement = needBackToPage()
    }

    //查找正常的元素
    if (nextElement == None) {
      nextElement = getAvailableElement(driver.currentPageDom, true)
    }

    if (nextElement == None) {
      log.info(s"${currentUrl} all elements had be clicked")
      //滚动多次没有新元素

      if (store.getClickedElementsList.size < 10) {
        log.info("just start, maybe loading is slow ,so just wait")
        nextElement = Some(getEventElement("Log"))
      }
      else if (conf.afterAll != null && conf.afterAll.nonEmpty) {
        val isMatch = conf.afterAll.exists(step => step.getGiven().forall(g => driver.getNodeListByKey(g).size > 0))
        if (isMatch == false) {
          log.info("not match afterAll")
          nextElement = getBackButton()
        } else if (isMatch == true && afterAllRetry < conf.afterAllMax) {
          log.info("match afterAll")
          nextElement = Some(getEventElement("AfterAll"))
        } else {
          log.warn(s"afterAll too many times ${afterAllRetry} >= ${conf.afterAllMax}")
          nextElement = getBackButton()
        }
      } else {
        nextElement = getBackButton()
      }
    }


    nextElement match {
      case Some(element) => {
        fixElementAction(element)
        if (element.getAction != skipAction) {
          beforeElementAction(element)
          doElementAction(element)
          //todo: 使用队列模型替代
          if (refreshResult.last() == true) {
            afterElementAction(element)
          } else {
            log.error("refresh fail skip afterElementAction")
          }
        } else {
          store.setElementSkip(element)
        }
      }
      case None => {
        //当前页面已经遍历完成
        log.fatal("never access this")
      }
    }

    crawl()
  }

  def doElementAction(element: URIElement): Unit = {
    //todo: 如果有相同的控件被重复记录, 会出问题, 比如确定退出的规则
    log.info(s"current index = ${store.getClickedElementsList.size - 1}")
    log.info(s"current xpath = ${element.getXpath}")
    log.info(s"current action = ${element.getAction}")
    log.info(s"current element = ${element.elementUri}")
    log.info(s"current url = ${element.getUrl}")
    log.info(s"current tag path = ${element.getAncestor()}")
    log.info(s"current file name = ${element.elementUri.take(100)}")

    store.saveReqHash(contentHash.last().toString)
    store.saveReqDom(driver.currentPageSource)
    saveElementScreenshot()
    store.saveReqTime(new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS").format(new Date()))

    element.getAction match {
      case "_Log" | "_Start" => {
        log.info("just log")
        log.info(TData.toJson(element))
      }
      case this.backAction => {
        log.info("back")
        back()
      }
      case this.backAppAction => {
        log.info("backApp")
        driver.launchApp()
        //todo: 改进等待
        Thread.sleep(conf.beforeStartWait)
      }
      case this.afterAllAction => {
        if (conf.afterAll != null) {
          //todo: bug
          if (store.getClickedElementsList.last.getAction.equals(afterAllAction)) {
            afterAllRetry += 1
            log.info(s"afterAll=${afterAllRetry}")
          } else {
            afterAllRetry = 0
            log.info("afterAllRetry = 0 because of last action not equal to after")
          }
          conf.afterAll.foreach(step => {
            step.getGiven().forall(g => driver.getNodeListByKey(g).size > 0) match {
              case true => {
                log.info(s"match ${step}")
                //todo: 支持元素动作
                DynamicEval.dsl(step.getAction())
              }
              case false => {
                log.info(s"not match ${step.getGiven()}")
              }
            }
          })
        } else {
          log.warn("no afterAll, do not use after")
        }
      }
      /*      case "monkey" => {
              val count = conf.monkeyEvents.size
              val random = util.Random.nextInt(count)
              val code = conf.monkeyEvents(random)
              driver.event(code)
            }*/
      case crawl if crawl != null && crawl.contains("crawl\\(.*\\)") => {
        store.getClickedElementsList.remove(store.getClickedElementsList.size - 1)
        DynamicEval.dsl(crawl)
      }
      case str: String => {
        //todo: tap和click的行为不一致. 在ipad上有时候click会点错位置, 而tap不会
        //todo: tap的缺点就是点击元素的时候容易点击到元素上层的控件

        log.info(s"need input ${str}")
        driver.findElementByURI(element, conf.findBy) match {
          case null => {
            log.error(s"not found ${element}")
            element.setAction("_notFound")
          }
          case _ => {
            driver.asyncTask(name = "action") {
              //支持各种动作
              str match {
                case "" => {
                  //todo: 根据类型自动执行默认动作
                  log.info("default action")
                  //driver.tap()
                  driver.click()
                  //todo: 直接使用xml的位置

                }
                case "click" => {
                  log.info("click element")
                  //todo: 增加click支持
                  driver.click()
                }
                case "tap" => {
                  driver.tap()
                }
                case "longTap" => {
                  driver.longTap()
                }
                case batchCommand if batchCommand.matches("shell:.*") => {
                  DynamicEval.shell(batchCommand.slice(batchCommand.indexOf(":") + 1, batchCommand.size))
                }
                case code if code != null && code.matches(".*\\(.*\\).*") => {
                  DynamicEval.dsl(code)
                }
                case str => {
                  log.debug(s"input ${str}")
                  driver.sendKeys(str)
                }
              }
            }
          }

        }
      }
    }
    if (conf.afterElementWait > 0) {
      log.info(s"sleep ${conf.afterElementWait} ms")
      Thread.sleep(conf.afterElementWait)
    }
    refreshPage()
  }

  def saveElementScreenshot(): Unit = {
    if (conf.screenshot && store.getClickedElementsList.size > 1) {
      store.saveReqImg(getBasePathName() + ".click.png")
      val originImageName = getBasePathName(2) + ".clicked.png"
      val newImageName = getBasePathName() + ".click.png"
      val rect = driver.getRect()
      log.info(s"mark ${originImageName} to ${newImageName}")
      driver.asyncTask(name = "mark") {
        driver.mark(originImageName, newImageName, rect.x, rect.y, rect.width, rect.height)
      }
    }
  }


  def saveLog(): Unit = {
    //记录点击log
    val logName = s"${conf.resultDir}/elements.yml"
    log.info(s"save log to ${logName}")
    val logfile = File(logName)
    logfile.writeAll(TData.toYaml(store))
  }

  def getBasePathName(right: Int = 1): String = {
    //序号_文件名
    val element = store.getClickedElementsList.takeRight(right).head
    s"${conf.resultDir}/${store.getClickedElementsList.size - right}_" + element.elementUri().take(100)
  }

  def saveDom(): Unit = {
    //保存dom结构
    val domPath = getBasePathName() + ".dom"
    //感谢QQ:434715737的反馈
    log.info(s"save to ${domPath}")
    driver.asyncTask(name = "saveDom") {
      File(domPath).writeAll(driver.currentPageSource)
    }
  }

  def saveScreen(force: Boolean = false): Unit = {
    //如果是schema相同. 界面基本不变. 那么就跳过截图加快速度.
    val originPath = getBasePathName() + ".clicked.png"
    if (pluginClasses.map(p => p.screenshot(originPath)).contains(true)) {
      return
    }
    if (conf.screenshot || force) {
      Thread.sleep(100)
      log.info("start screenshot")
      driver.asyncTask(60, name = "screenshot") {
        val imgFile = if (store.isDiff()) {
          log.info("ui change screenshot again")
          driver.screenshot()
        } else {
          log.info("ui no change")
          val preImageFileName = getBasePathName(2) + ".clicked.png"
          val preImageFile = new java.io.File(preImageFileName)
          if (preImageFile.exists() && preImageFileName != originPath) {
            log.info(s"copy from pre image file ${preImageFileName}")
            //FileUtils.copyFile(preImageFile, markImageFile)
            preImageFile
          } else {
            driver.screenshot()
          }
        }
        if (imgFile.getAbsolutePath == new java.io.File(originPath).getAbsolutePath) {
          log.info(s"${imgFile.getAbsolutePath} same as before")
        } else {
          FileUtils.copyFile(imgFile, new java.io.File(originPath))
        }
      } match {
        case Left(x) => {
          log.info("screenshot success")
        }
        case Right(e) => {
          log.error("screenshot error")
          log.error(e.getMessage)
        }
      }
    } else {
      log.info("skip screenshot")
    }
  }


  def back(): Unit = {
    pluginClasses.foreach(_.beforeBack())
    //todo: 自动识别backButton
    if (driver.platformName.toLowerCase() == "ios") {
      log.warn("you should set backButton")
    } else {
      if (backDistance.intervalMS() < 2000) {
        log.warn("two back action too close")
        Thread.sleep(2000)
      }
      driver.asyncTask(name = "back") {
        log.info("navigate back")
        driver.back()
      }
      backDistance.append("back")
    }
  }


  //todo: 结构化
  //通过规则实现操作. 不管元素是否被点击过
  def getElementByTriggerActions(): Option[URIElement] = {
    //先判断是否在期望的界面里. 提升速度
    //todo: 让when生效
    //todo: times bug，默认的times为0，默认规则不生效
    conf.triggerActions.filter(step => step.times != 0).foreach(step => {
      log.debug(s"finding ${step}")
      getURIElementsByStep(step).headOption match {
        case Some(e) => {
          step.use()
          log.trace(s"step times = ${step.times}")
          e.setAction(step.getAction())
          return Some(e)
        }
        case None => {
          log.trace(s"not found trigger ${step.getXPath()}")
        }
      }
    })
    None
  }

  def stop(): Unit = {
    stopAll = true
    //exitCrawl=true

    log.info(s"ctrl c interval = ${signals.intervalMS()}")
    Try(pluginClasses.foreach(_.stop())) match {
      case Success(v) => {}
      case Failure(e) => {
        driver.handleException(e)
      }
    }
    log.info("generate report finish")
    System.exit(0)

  }

  def handleCtrlC(): Unit = {
    log.info("add shutdown hook")
    Signal.handle(new Signal("INT"), (sig: Signal) => {
      try {
        stop()
      } finally {
        System.exit(2)
      }

      //fixed: 更好的处理退出
      /*
      log.info("send INT, do you want stop? please input y")
      if(scala.io.StdIn.readChar().toLower=='y'){
        signals.append(1)
        stop()
      }else{
        log.info("continue run")
      }
      */
    })

  }
}
