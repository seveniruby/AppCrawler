package com.testerhome.appcrawler

import java.io
import java.util.Date

import com.testerhome.appcrawler.driver.{AppiumClient, MacacaDriver, ReactWebDriver}
import com.testerhome.appcrawler.plugin.Plugin
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.log4j._
import org.scalatest
import org.scalatest.ConfigMap
import org.w3c.dom.Document
import sun.misc.{Signal, SignalHandler}

import scala.annotation.tailrec
import scala.collection.mutable.{ListBuffer, Map}
import scala.collection.{immutable, mutable}
import scala.reflect.io.File
import scala.util.{Failure, Success, Try}


/**
  * Created by seveniruby on 15/11/28.
  */
class Crawler extends CommonLog {
  var driver:ReactWebDriver=_
  var conf = new CrawlerConf()

  /** 存放插件类 */
  val pluginClasses = ListBuffer[Plugin]()
  var fileAppender: FileAppender = _

  val store = new URIElementStore

  private var currentElementAction = "click"
  private var platformName=""

  var appName = ""
  /** 当前的url路径 */
  var currentUrl = ""

  var isRefreshSuccess = true
  private var exitCrawl=false
  private var backRetry = 0
  //最大重试次数
  var backMaxRetry = 5
  private var afterPageRetry = 0
  //滑动最大重试次数
  var stopAll = false
  val signals = new DataRecord()
  signals.append(1)
  private val startTime = new Date().getTime

  val urlStack = mutable.Stack[String]()

  protected val backDistance = new DataRecord()
  val appNameRecord = new DataRecord()
  protected val contentHash = new DataRecord

  /**
    * 根据类名初始化插件. 插件可以使用java编写. 继承自Plugin即可
    */
  def loadPlugins(): Unit = {
    val defaultPlugins = List(
      "com.testerhome.appcrawler.plugin.TagLimitPlugin",
      "com.testerhome.appcrawler.plugin.ReportPlugin",
      "com.testerhome.appcrawler.plugin.FreeMind"
    )
    defaultPlugins.foreach(name => pluginClasses.append(Class.forName(name).newInstance().asInstanceOf[Plugin]))

    //todo: 暂时禁用，很多人不会用
/*    conf.pluginList.foreach(name => {
      if (defaultPlugins.forall(_ != name)) {
        log.info(s"load com.testerhome.appcrawler.plugin $name")
        pluginClasses.append(Class.forName(name).newInstance().asInstanceOf[Plugin])
      }
    })*/

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
    log.setLevel(GA.logLevel)
  }

  def loadConf(file: String): Unit = {
    conf = new CrawlerConf().load(file)
    log.setLevel(GA.logLevel)
  }

  //todo: 让其他的文件也支持log输出到文件
  def addLogFile(): Unit = {
    AppCrawler.logPath = conf.resultDir + "/appcrawler.log"

    fileAppender = new FileAppender(layout, AppCrawler.logPath, false)
    log.addAppender(fileAppender)

    val resultDir = new java.io.File(conf.resultDir)
    if (!resultDir.exists()) {
      FileUtils.forceMkdir(resultDir)
      log.info("result dir path = " + resultDir.getAbsolutePath)
    }

  }

  /**
    * 启动爬虫
    */
  def start(existDriver: ReactWebDriver = null): Unit = {
    addLogFile()
    log.debug("crawl config")
    log.debug(conf.toYaml())
    if (conf.xpathAttributes != null) {
      log.info(s"set xpath attribute with ${conf.xpathAttributes}")
      XPathUtil.setXPathExpr(conf.xpathAttributes)
    }


    log.info("set xpath")
    loadPlugins()
    if (existDriver == null) {
      log.info("prepare setup Appium")
      setupAppium()
      //driver.getAppStringMap
    } else {
      log.info("use existed driver")
      this.driver = existDriver
    }
    log.info(s"platformName=${platformName} driver=${driver}")
    log.info(AppCrawler.banner)
    log.info("waiting for app load")
    Thread.sleep(conf.waitLaunch)
    log.info(s"driver=${existDriver}")
    log.info("get screen info")
    driver.getDeviceInfo()
    refreshPage()


    //todo: 不是所有的实现都支持
    //driver.manage().logs().getAvailableLogTypes().toArray.foreach(log.info)
    //设定结果目录
    firstRefresh()
    log.info("append current app name to appWhiteList")
    conf.appWhiteList.append(appNameRecord.last().toString)

    if(conf.testcase!=null){
      log.info("run steps")
      runSteps()
    }else{
      log.info("no testcase")
    }

    if(conf.selectedList.nonEmpty){
      crawl(conf.maxDepth)
    }else{
      log.info("no selectedList")
    }

  }

  def crawl(depth: Int): Unit ={
    //清空堆栈 开始重新计数
    conf.maxDepth=depth
    urlStack.clear()
    refreshPage()
    handleCtrlC()

    //启动第一次
    var errorCount=1
    while(errorCount>0) {
      Try(crawl()) match {
        case Success(v) => {
          log.info("crawl finish")
          errorCount=0
        }
        case Failure(e) => {
          log.error("crawl not finish, return with exception")
          log.error(e.getLocalizedMessage)
          log.error(ExceptionUtils.getRootCauseMessage(e))
          log.error(ExceptionUtils.getRootCause(e))
          ExceptionUtils.getRootCauseStackTrace(e).foreach(log.error)
          log.error("create new session")

          errorCount+=1
          restart()
        }
      }

    }

    //爬虫结束
    stop()
    sys.exit()
  }

  def restart(): Unit = {
    if(conf.beforeRestart!=null) {
      log.info("execute shell on restart")
      conf.beforeRestart.foreach(Util.dsl(_))
    }
    log.info("restart appium")
    conf.capability ++= Map("app"->"")
    conf.capability ++= Map("dontStopAppOnReset"->"true")
    conf.capability ++= Map("noReset"->"true")
    setupAppium()
    //todo: 采用轮询
    Thread.sleep(conf.waitLaunch)
    refreshPage()
    doElementAction(URIElement(url=s"${currentUrl}", tag="restart", id="restart",
      xpath=s"restart-${store.clickedElementsList.size}"), "")
  }

  def firstRefresh(): Unit = {
    log.info("first refresh")
    doElementAction(URIElement(url=s"${currentUrl}", tag="start", id="start",
      xpath=s"Start-Start-${store.clickedElementsList.size}"), "")

  }

  def runSteps(): Unit ={
    log.info("run testcases")
    new AutomationSuite().execute("run steps", ConfigMap("crawler"->this))
  }

  def setupAppium(): Unit = {
    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))

    //todo: init all var
    afterPageRetry=0
    backRetry=0

    log.info(s"afterPageMax=${conf.afterPageMax}")
    Util.isLoaded=false

    //todo: 主要做遍历测试和异常测试. 所以暂不使用selendroid
    val url=conf.capability("appium").toString
    conf.capability.getOrElse("automationName", "").toString match {
      case "macaca" => {
        log.info("use macaca")
        driver=new MacacaDriver(url, conf.capability)
      }
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
        //appium 6.0.0 has bug with okhttp
        System.setProperty("webdriver.http.factory", "apache")
        driver=new AppiumClient(url, conf.capability)
        log.info(driver)
      }
    }

    GA.log(conf.capability.getOrElse("appPackage", "")+conf.capability.getOrElse("bundleId", "").toString)

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


  /**
    * 判断内容是否变化
    *
    * @return
    */
  def getContentHash(): String = {
    //var nodeList = driver.getListFromXPath("//*[not(ancestor-or-self::UIATableView)]")
    //nodeList = nodeList intersect driver.getListFromXPath("//*[not(ancestor-or-self::android.widget.ListView)]")

    //排除iOS状态栏 android不受影响
    val nodeList = driver.getNodeListByKey("//*[not(ancestor-or-self::UIAStatusBar)]")
    val schemaBlackList = List()
    //加value是考虑到某些tab, 他们只有value不同
    //<             label="" name="分时" path="/0/0/6/0/0" valid="true" value="1"
    //---
    //>             label="" name="分时" path="/0/0/6/0/0" valid="true" value="0"
    md5(nodeList.filter(node => !schemaBlackList.contains(node("tag"))).
      map(node => node.getOrElse("xpath", "")
        + node.getOrElse("value", "").toString
        + node.getOrElse("selected", "").toString
        + node.getOrElse("text", "").toString
      ).
      mkString("\n"))
  }

  /**
    * 获得布局Hash
    */
  def getSchema(): String = {
    val nodeList = driver.getNodeListByKey("//*[not(ancestor-or-self::UIAStatusBar)]")
    md5(nodeList.map(getUrlElementByMap(_).getAncestor()).distinct.mkString("\n"))
  }

  def getUri(): String = {
    val uri=if (conf.defineUrl!=null && conf.defineUrl.nonEmpty) {
      val urlString = conf.defineUrl.flatMap(driver.getNodeListByKey(_)).distinct.map(x => {
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
    }else{
      ""
    }
    if(uri.nonEmpty){
      List(driver.getAppName(), uri).distinct.filter(_.nonEmpty).mkString("-")
    }else{
      List(driver.getAppName(), driver.getUrl()).distinct.filter(_.nonEmpty).mkString("-")
    }


  }

  /**
    * 获取控件的基本属性并设置一个唯一的uid作为识别. screenName+id+name
    *
    * @param nodeMap
    * @return
    */
  def getUrlElementByMap(nodeMap: immutable.Map[String, Any]): URIElement = {
    new URIElement(nodeMap, currentUrl)
  }

  def needBackApp(): Boolean = {
    log.trace(conf.appWhiteList)
    log.trace(appNameRecord.last(10))

    //跳到了其他app. 排除点一次就没有的弹框
    if (conf.appWhiteList.forall(appNameRecord.last().toString.matches(_)==false)) {
      log.warn(s"not in app white list ${conf.appWhiteList}")
      log.warn(s"jump to other app appName=${appNameRecord.last()} lastAppName=${appNameRecord.pre()}")
      setElementAction("backApp")
      return true
    } else {
      return false
    }

  }

  def needExit(): Boolean = {
    //超时退出
    if ((new Date().getTime - startTime) > conf.maxTime * 1000) {
      log.warn("maxTime out Quit need exit")
      return true
    }
    if (backRetry > backMaxRetry) {
      log.warn(s"backRetry ${backRetry} > backMaxRetry ${backMaxRetry} need exit")
      return true
    }

    if (appNameRecord.last(5).forall(conf.appWhiteList.contains(_) == false)) {
      log.error(s"appNameRecord last 5 ${appNameRecord.last(5)}")
      return true
    }
    return false
  }

  def needReturn(): Boolean = {
    //url黑名单
    if (conf.urlBlackList.exists(urlStack.head.matches(_))) {
      log.warn(s"${urlStack.head} in urlBlackList should return")
      return true
    }

    //url白名单, 第一次进入了白名单的范围, 就始终在白名单中. 不然就算不在白名单中也得遍历.
    //上层是白名单, 当前不是白名单才需要返回
    if (conf.urlWhiteList.size > 0
      && conf.urlWhiteList.filter(urlStack.head.matches(_)).isEmpty
      && conf.urlWhiteList.filter(urlStack.tail.headOption.getOrElse("").matches(_)).nonEmpty) {
      log.warn(s"${urlStack.head} not in urlWhiteList should return")
      return true
    }

    //app黑名单
    if (appName.matches(".*browser")) {
      log.warn(s"current app is browser, back")
      return true
    }

    //超过遍历深度
    log.info(s"urlStack=${urlStack} baseUrl=${conf.baseUrl} maxDepth=${conf.maxDepth}")
    //大于最大深度并且是在进入过基础Url
    if (urlStack.length > conf.maxDepth) {
      log.warn(s"urlStack.depth=${urlStack.length} > maxDepth=${conf.maxDepth}")
      return true
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

    false

  }

  def isValid(element: URIElement): Boolean = {
    element.valid=="true"
  }


  def isSmall(element: URIElement): Boolean ={

    var res=false

    if( element.x >driver.screenHeight || element.y >driver.screenWidth){
      log.warn(element)
      log.info("not visual")
      res=true
    }

    //高度小就跳过
    if(element.height<30 && element.width<30) {
      log.warn(element)
      log.info("small")
      res=true
    }

    res
  }

  //todo: 支持xpath表达式
  def getAvailableElement(currentPageDom: Document, skipSmall:Boolean=false ): List[URIElement] = {
    var all = List[URIElement]()
    var firstElements = List[URIElement]()
    var lastElements = List[URIElement]()
    var selectedElements = List[URIElement]()
    var blackElements = List[URIElement]()
    var lastSize=0

    conf.selectedList.foreach(step => {
      log.trace(s"selectedList xpath =  ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom).map(new URIElement(_, currentUrl))
      temp.foreach(log.trace)
      selectedElements ++= temp
    })
    selectedElements=selectedElements.distinct
    log.info(s"selected nodes size = ${selectedElements.size}")
    lastSize=selectedElements.size

    //remove blackList
    conf.blackList.foreach(step => {
      log.trace(s"blackList xpath =  ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom).map(new URIElement(_, currentUrl))
      temp.foreach(log.trace)
      blackElements ++= temp
    })
    blackElements=blackElements.distinct
    selectedElements = selectedElements diff blackElements
    log.info(s"all - black elements size = ${selectedElements.size}")
    if(selectedElements.size<lastSize) {
      selectedElements.foreach(log.trace)
      lastSize=all.size
    }

    //sort
    conf.firstList.foreach(step => {
      log.trace(s"firstList xpath = ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom)
        .map(new URIElement(_, currentUrl))
        .intersect(selectedElements)
      temp.foreach(log.trace)
      firstElements ++= temp
    })

    conf.lastList.foreach(step => {
      log.trace(s"lastList xpath = ${step.getXPath()}")
      val temp = XPathUtil.getNodeListByKey(step.getXPath(), currentPageDom)
        .map(new URIElement(_, currentUrl))
        .intersect(selectedElements)
      temp.foreach(log.trace)
      lastElements ++= temp
    })

    //再根据先后顺序调整，这样只需要排序同级元素
    //去掉不在first和last中的元素
    selectedElements = selectedElements diff firstElements
    selectedElements = selectedElements diff lastElements
    log.info(s"all - first - last elements size = ${selectedElements.size}")
    if(selectedElements.size<lastSize) {
      selectedElements.foreach(log.trace)
      lastSize=all.size
    }

    //先根据depth排序selectedElements
    conf.sortByAttribute.foreach(attribute=>{
      attribute match {
        case "depth" => {
          selectedElements=selectedElements.sortWith(
            _.depth.toString.toInt >
              _.depth.toString.toInt
          )
        }
        case "selected" => {
          //todo:同级延后
          //selected=false的优先遍历
          selectedElements=selectedElements.sortWith(
            _.selected.toString.contains("false") >
              _.selected.toString.contains("false")
          )
        }
        case "list" => {
          //列表内元素优先遍历
          selectedElements=selectedElements.sortWith(
            _.ancestor.toString.contains("List") >
              _.ancestor.toString.contains("List")
          )
        }
        //todo: 居中的优先遍历

      }
      log.trace(s"sort by ${attribute}")
      selectedElements.foreach(e=>log.trace(
        s"depth=${e.depth}" +
          s" selected=${e}" +
          s" list=${e.ancestor.toString.contains("List")} e=${e}")
      )
    })


    //确保不重, 并保证顺序
    all = (firstElements ++ selectedElements ++ lastElements).distinct.filter(isValid)

    log.trace(s"sorted nodes length=${all.length}")
    all.foreach(log.trace)
    lastSize=all.size


    //remove small
    if(skipSmall==false){
      all=all.filter(isSmall(_)==false)
      log.info(s"all - small elements size = ${all.size}")
      if(all.size<lastSize) {
        all.foreach(log.trace)
        lastSize=all.size
      }
    }

    //去掉back菜单
    all = all diff getBackNodes()
    log.info(s"all - backButton size=${all.length}")
    if(all.size<lastSize) {
      all.foreach(log.trace)
      lastSize=all.size
    }

    //过滤已经被点击过的元素
    all = all.filter(!store.isClicked(_))
    log.info(s"all - clicked size=${all.size}")
    if(all.size<lastSize) {
      all.foreach(log.trace)
      lastSize=all.size
    }

    all = all.filter(!store.isSkiped(_))
    log.info(s"all - skiped fresh elements size=${all.length}")
    if(all.size<lastSize) {
      all.foreach(e=>{
        log.trace(e)
        //记录未被点击的元素
        store.saveElement(e)
      })
      lastSize=all.size
    }
    all
  }


  def hideKeyBoard(): Unit = {
    //iOS键盘隐藏
    if (driver.getNodeListByKey("//UIAKeyboard").size >= 1) {
      log.info("find keyboard , just hide")
      driver.hideKeyboard()
    }
  }

  def refreshPage(): Boolean = {
    log.info("refresh page")
    driver.getPageSourceWithRetry()
    log.trace(driver.currentPageSource)

    if (driver.currentPageSource!=null) {
      parsePageContext()
      return true
    } else {
      log.warn("page source get fail, go back")
      setElementAction("back")
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
    contentHash.append(getContentHash())
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
    if(conf.beforeElement!=null) {
      log.trace("beforeElementAction")
      conf.beforeElement.foreach(step => {
        val xpath = step.getXPath()
        val action = step.getAction()
        if (driver.getNodeListByKey(xpath).contains(element)) {
          Util.dsl(action)
        }
      })
    }
    pluginClasses.foreach(p => p.beforeElementAction(element))
  }

  def afterElementAction(element: URIElement): Unit = {
    getElementAction() match {
      case "back"  | "backApp" => {backRetry += 1}
      case nonAfter if nonAfter!="after" => { backRetry = 0 }
      case "clear" => { }
      case _ => { log.info("keep backRetry") }
    }

    log.info(s"backRetry=${backRetry}")

    if(conf.afterElement!=null){
      log.info("afterElementAction eval")
      conf.afterElement.foreach(step=>{
        Util.dsl(step.getAction())
      })
    }
    pluginClasses.foreach(p => p.afterElementAction(element))
  }

  def getElementAction(): String = {
    currentElementAction
  }

  /**
    * 允许插件重新设定当前控件的行为
    **/
  def setElementAction(action: String): Unit = {
    log.info(s"set action to ${action}")
    if(action==null){
      currentElementAction="click"
    }else{
      currentElementAction=action
    }
  }

  def getBackNodes(): ListBuffer[URIElement] = {
    conf.backButton.flatMap(step=>
      driver.getNodeListByKey(step.getXPath())
      .map(new URIElement(_, currentUrl))
      .filter(isValid))
  }

  def getBackButton(): Option[URIElement] = {
    log.info("go back")
    //找到可能的关闭按钮, 取第一个可用的关闭按钮
    getBackNodes().headOption match {
      case Some(backElement) if appNameRecord.isDiff() == false => {
        //app相同并且找到back控件才点击. 否则就默认back
        setElementAction("click")
        backRetry+=1
        return Some(backElement)
      }
      case _ => {
        log.warn("no back button")
        setElementAction("back")
        return Some(URIElement(url=s"${currentUrl}", tag="Back", id="Back",
          xpath=s"Back-${store.clickedElementsList.size}"))
      }
    }
  }


  /**
    * 优化后的递归方法. 尾递归.
    * 刷新->找元素->点击第一个未被点击的元素->刷新
    */
  @tailrec
  final def crawl(): Unit = {
    if(exitCrawl==true){
      return
    }
    log.info("\n\ncrawl next")
    //刷新页面
    var skipBeforeElementAction = true
    //是否需要退出或者后退, 得到要做的动作
    var nextElement: Option[URIElement] = None

    //todo: skip之后可以不用刷新
    getElementAction() match {
      case "skip" => {
        log.info("skip refresh page because last action is skip")
        setElementAction("click")
      }
      case "clear" => {
        log.error(s"last time not found ${store.clickedElementsList.last}")
        store.setElementClear()
        //todo: 调整taglimit的配额
      }
      case _ => {}
    }

    //判断下一步动作

    //是否应该退出
    if (needExit()) {
      log.warn("get signal to exit")
      exitCrawl=true
    }
    //页面刷新失败自动后退
    if (isRefreshSuccess == false) {
      log.warn("refresh fail")
      nextElement = Some(URIElement(url=s"${currentUrl}", tag="Back", id="Back",
        xpath=s"Back-${store.clickedElementsList.size}"))
      setElementAction("back")
    } else {
      log.debug("refresh success")
    }

    //先应用优先规则
    if (nextElement == None) {
      //todo: 优化结构
      getElementByTriggerActions() match {
        case Some(e) => {
          log.info(s"found ${e} by ElementActions")
          nextElement = Some(e)
        }
        case None => {}
      }
    }

    //是否需要回退到app
    if (nextElement == None && needBackApp()) {
      nextElement = Some(URIElement(url=s"${currentUrl}", tag="backApp", id="backApp",
        xpath=s"backApp-${appNameRecord.last()}-${store.clickedElementsList.size}"))
      setElementAction("backApp")
    }

    //判断是否需要返回上层
    if (nextElement == None) {
      if (needReturn()) {
        log.info("need to back")
        nextElement = getBackButton()
      } else {
        log.info("no need to back")
      }
    }

    //查找正常的元素
    if (nextElement == None) {
      val allElements = getAvailableElement(driver.currentPageDom, true)
      allElements.headOption match {
        case Some(e) => {
          log.info(s"found ${e} by first available element")
          nextElement = Some(e)
          //todo: 需要一个action指定表
          setElementAction("click")
          skipBeforeElementAction = false
          afterPageRetry=0
        }
        case None => {
          log.info(s"${currentUrl} all elements had be clicked")
          //滚动多次没有新元素

          if (conf.afterPage != null) {
            val isMatch=conf.afterPage.exists(step=>step.given.forall(g=>driver.getNodeListByKey(g).size>0))
            if(isMatch==false) {
              log.info("not match afterUrlFinish")
              nextElement = getBackButton()
            }else if(isMatch==true && afterPageRetry<conf.afterPageMax) {
              log.info("match afterUrlFinish")
              nextElement = Some(URIElement(url=s"${currentUrl}", tag="", id="afterUrlFinished",
                xpath=s"afterUrlFinished-${appNameRecord.last()}-${store.clickedElementsList.size}"))
              setElementAction("after")
              afterPageRetry += 1
              log.info(s"swipeRetry=${afterPageRetry}")
            }else{
              log.warn(s"swipeRetry too many times ${afterPageRetry} >= ${conf.afterPageMax}")
              nextElement = getBackButton()
              afterPageRetry = 0
              log.info(s"swipeRetry=${afterPageRetry}")
            }
          }else{
            nextElement = getBackButton()
          }
        }
      }
    }


    nextElement match {
      case Some(element) => {
        //todo: 输入情况 长按 需要考虑
        if(skipBeforeElementAction==false) {
          //决定是否需要跳过
          beforeElementAction(element)
        }else{
          log.info("skip beforeElementAction")
        }

        //不可和之前的判断合并
        getElementAction() match {
          case "skip" => {
            log.trace(s"pop ${element}")
            store.setElementSkip(element)
          }
          case _ => {
            //处理控件
            doElementAction(element, getElementAction())
            //插件处理
            afterElementAction(element)
          }
        }

      }
      case None => {
        //当前页面已经遍历完成
        log.error("never access this")
      }
    }
    crawl()
  }


  def saveLog(): Unit = {
    //记录点击log
    val logName=s"${conf.resultDir}/elements.yml"
    log.info(s"save log to ${logName}")
    File(logName).writeAll(TData.toYaml(store))
  }

  def getBasePathName(right:Int=1): String = {
    //序号_文件名
    val element=store.clickedElementsList.takeRight(right).head
    s"${conf.resultDir}/${store.clickedElementsList.size-right}_" + element.toString()
  }

  def saveDom(): Unit = {
    //保存dom结构
    val domPath = getBasePathName() + ".dom"
    //感谢QQ:434715737的反馈
    log.info(s"save to ${domPath}")
    Try(File(domPath).writeAll(driver.currentPageSource)) match {
      case Success(v) => {
        log.trace(s"save to ${domPath}")
      }
      case Failure(e) => {
        log.error(s"save to ${domPath} error")
        log.error(e.getMessage)
        log.error(e.getCause)
        log.error(e.getStackTrace)
      }
    }
  }

  def saveScreen(force: Boolean = false): Unit = {
    //如果是schema相同. 界面基本不变. 那么就跳过截图加快速度.
    val originPath = getBasePathName() + ".clicked.png"
    if (pluginClasses.map(p => p.screenshot(originPath)).contains(true)) {
      return
    }
    if (conf.saveScreen || force) {
      Thread.sleep(100)
      log.info("start screenshot")
      driver.asyncTask(60) {
        val imgFile = if (store.isDiff()) {
          log.info("ui change screenshot again")
          driver.screenshot()
        } else {
          log.info("ui no change")
          val preImageFileName = getBasePathName(2) + ".clicked.png"
          val preImageFile = new java.io.File(preImageFileName)
          if (preImageFile.exists() && preImageFileName!=originPath) {
            log.info(s"copy from pre image file ${preImageFileName}")
            //FileUtils.copyFile(preImageFile, markImageFile)
            preImageFile
          } else {
            driver.screenshot()
          }
        }
        if(imgFile.getAbsolutePath==new java.io.File(originPath).getAbsolutePath){
          log.info(s"${imgFile.getAbsolutePath} same as before")
        }else{
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


  def doElementAction(element: URIElement, action: String): Unit = {
    //找到了要点击的元素或者其他的状态标记比如back swipe
    log.debug(s"push ${element}")
    store.setElementClicked(element)
    //todo: 如果有相同的控件被重复记录, 会出问题, 比如确定退出的规则

    log.info(s"current element = ${element}")
    log.info(s"current index = ${store.clickedElementsList.size - 1}")
    log.info(s"current action = ${action}")
    log.info(s"current xpath = ${element.xpath}")
    log.info(s"current url = ${element.url}")
    log.info(s"current tag path = ${element.getAncestor()}")
    log.info(s"current file name = ${element.toString()}")

    store.saveReqHash(contentHash.last().toString)
    store.saveReqImg(getBasePathName() + ".click.png")
    store.saveReqDom(store.clickedElementsList.takeRight(2).headOption.getOrElse(element).toString())

    val originImageName = getBasePathName(2) + ".clicked.png"
    val newImageName = getBasePathName() + ".click.png"

    //todo: 支持null
    action match {
      case ""  | "log" => {
        log.info("just log")
        log.info(TData.toJson(element))
      }
      case "back" => {
        log.info("back")
        back()
      }
      case "backApp" => {
        log.info("backApp")
        driver.launchApp()
        //todo: 改进等待
        Thread.sleep(conf.waitLaunch)
        /*if (conf.defaultBackAction.size > 0) {
          log.trace(conf.defaultBackAction)
          conf.defaultBackAction.foreach(Runtimes.eval)
        } else {
          driver.backApp()
          driver.getPageSource()
          if(needReturn()){
            driver.launchApp()
          }

        }*/
      }
      case "after" => {
        if (conf.afterPage != null) {
          conf.afterPage.foreach(step => {
            step.given.forall(g=>driver.getNodeListByKey(g).size>0) match {
              case true => {
                log.info(s"match ${step}")
                //todo: 支持元素动作
                Util.dsl(step.when.action)
              }
              case false => {log.info(s"not match ${step.given}")}
            }
          })
        }else{
          log.warn("no afterUrlFinish, do not use after")
        }
      }
/*      case "monkey" => {
        val count = conf.monkeyEvents.size
        val random = util.Random.nextInt(count)
        val code = conf.monkeyEvents(random)
        driver.event(code)
      }*/
      case crawl if crawl!=null && crawl.contains("crawl\\(.*\\)") =>{
        store.clickedElementsList.remove(store.clickedElementsList.size-1)
        Util.dsl(crawl)
      }
      case code if code!=null && code.matches(".*\\(.*\\).*") => {
        Util.dsl(code)
      }
      case str: String => {
        //todo: tap和click的行为不一致. 在ipad上有时候click会点错位置, 而tap不会
        //todo: tap的缺点就是点击元素的时候容易点击到元素上层的控件

        log.info(s"need input ${str}")
        driver.findElementByURI(element, conf.findBy) match {
          case null => {
            log.error(s"not found ${element}")
            setElementAction("clear")
          }
          case _ => {
            val rect = driver.getRect()
            if(conf.saveScreen) {
              log.info(s"mark ${originImageName} to ${newImageName}")
              driver.asyncTask() {
                driver.mark(originImageName, newImageName, rect.x, rect.y, rect.width, rect.height)
              }
            }

            driver.asyncTask() {
              //支持各种动作
              str match {
                case null => {
                  //todo: 根据类型自动执行默认动作
                  log.info("default action")
                  driver.tap()
                }
                case "click" => {
                  log.info("click element")
                  //todo: 增加click支持
                  driver.click()
                }
                case "tap" => {
                  driver.tap()
                }
                case "longTap" =>{
                  driver.longTap()
                }
                case str => {
                  log.info(s"input ${str}")
                  driver.sendKeys(str)
                }
              }
            }
          }

        }
        if (List("UIATextField", "UIATextView", "EditText").map(element.tag.contains(_)).contains(true)) {
          driver.asyncTask()(driver.hideKeyboard())
        }
      }
    }

    val newImageFile=new io.File(newImageName)
    val originImageFile=new io.File(originImageName)
    if(newImageFile.exists()==false && originImageFile.exists()==true){
      log.info("use last clicked image replace mark")
      FileUtils.copyFile(originImageFile, newImageFile)
    }else{
      log.info("mark image exist")
    }

    //等待页面加载
    log.info(s"sleep ${conf.waitLoading} for loading")
    Thread.sleep(conf.waitLoading)
    isRefreshSuccess = refreshPage()
    saveDom()
    saveScreen()

    store.saveResHash(contentHash.last().toString)
    store.saveResImg(getBasePathName() + ".clicked.png")
    //todo: 内存消耗太大，改用文件存储
    store.saveResDom(element.toString())

  }

  def back(): Unit = {
    pluginClasses.foreach(_.beforeBack())
    //todo: 自动识别backButton
    if(driver.platformName.toLowerCase()=="ios"){
      log.warn("you should set backButton")
    }else {
      if (backDistance.intervalMS() < 2000) {
        log.warn("two back action too close")
        Thread.sleep(2000)
      }
      driver.asyncTask() {
        log.info("navigate back")
        driver.back()
      }
      backDistance.append("back")
      appNameRecord.pop()
    }
  }


  //todo: 结构化
  //通过规则实现操作. 不管元素是否被点击过
  def getElementByTriggerActions(): Option[URIElement] = {
    //先判断是否在期望的界面里. 提升速度
    //todo: 让when生效
    conf.triggerActions.foreach(step => {
      val xpath = step.getXPath()
      val action = step.getAction()
      log.debug(s"finding ${step}")

      driver.getNodeListByKey(xpath).map(new URIElement(_, currentUrl)).filter(isValid).headOption match {
        case Some(e) => {
          if (step.times == 1) {
            log.info(s"remove rule ${step}")
            conf.triggerActions -= step
          }
          step.use()
          log.info(s"step times = ${step.times}")

          setElementAction(action)
          if (action == "monkey") {
            return Some(URIElement(url=action, tag=action))
          } else {
            return Some(e)
          }

        }
        case None => {}
      }
    })
    None
  }

  def stop(): Unit = {
    stopAll = true
    log.info(s"ctrl c interval = ${signals.intervalMS()}")
    Try(pluginClasses.foreach(_.stop())) match {
      case Success(v)=> {}
      case Failure(e) => {
        log.error(e.getMessage)
        log.error(e.getCause)
        e.getStackTrace.foreach(log.error)
      }
    }
    log.info("generate report finish")
    System.exit(0)

  }

  def handleCtrlC(): Unit = {
    log.info("add shutdown hook")
    Signal.handle(new Signal("INT"), (sig: Signal) => {
      try{
        stop()
      }finally{
        System.exit(2)
      }

      //todo: 更好的处理退出
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
