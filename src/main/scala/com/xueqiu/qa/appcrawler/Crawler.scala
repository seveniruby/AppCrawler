package com.xueqiu.qa.appcrawler

import java.awt.{Color, BasicStroke}
import java.util.Date
import javax.imageio.ImageIO

import io.appium.java_client.{TouchAction, AppiumDriver}
import org.apache.commons.io.FileUtils
import org.apache.log4j._
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{OutputType, TakesScreenshot, WebElement}
import org.w3c.dom.Document

import scala.annotation.tailrec
import scala.collection.mutable.{ListBuffer, Map}
import scala.collection.{immutable, mutable}
import scala.reflect.io.File
import scala.util.{Failure, Success, Try}


/**
  * Created by seveniruby on 15/11/28.
  */
class Crawler extends CommonLog {
  implicit var driver: AppiumDriver[WebElement] = _
  var conf = new CrawlerConf()
  val capabilities = new DesiredCapabilities()

  /** 存放插件类 */
  val pluginClasses = ListBuffer[Plugin]()
  var fileAppender: FileAppender = _

  val store = new UrlElementStore
  /** 元素的默认操作 */
  private var currentElementAction = "click"

  protected var automationName = "appium"
  private var screenWidth = 0
  private var screenHeight = 0

  var appName = ""
  var pageSource = ""
  private var pageDom: Document = null
  private var backRetry = 0
  //最大重试次数
  var backMaxRetry = 10
  private var swipeRetry = 0
  //滑动最大重试次数
  var swipeMaxRetry = 2
  private var needExit = false
  private val startTime = new Date().getTime

  /** 当前的url路径 */
  var currentUrl = ""
  val urlStack = mutable.Stack[String]()

  private val elementTree = TreeNode("AppCrawler")
  private val elementTreeList = ListBuffer[String]()

  protected val backDistance = new DataRecord()
  protected val appNameRecord = new DataRecord()
  protected val contentHash = new DataRecord

  /**
    * 根据类名初始化插件. 插件可以使用java编写. 继承自Plugin即可
    */
  def loadPlugins(): Unit = {
    //todo: 需要考虑默认加载一些插件,并防止重复加载
    val defaultPlugins=List(
      "com.xueqiu.qa.appcrawler.plugin.TagLimitPlugin",
      "com.xueqiu.qa.appcrawler.plugin.ReportPlugin"
    )
    defaultPlugins.foreach(name=>pluginClasses.append(Class.forName(name).newInstance().asInstanceOf[Plugin]))

    conf.pluginList.foreach(name => {
      if(defaultPlugins.forall(_!=name)) {
        log.info(s"load com.xueqiu.qa.appcrawler.plugin $name")
        pluginClasses.append(Class.forName(name).newInstance().asInstanceOf[Plugin])
      }
    })
    val dynamicPluginDir = (new java.io.File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath))
      .getParentFile.getParentFile.getCanonicalPath + File.separator + "plugins" + File.separator
    log.info(s"dynamic load plugin in ${dynamicPluginDir}")
    val dynamicPlugins = Runtimes.loadPlugins(dynamicPluginDir)
    log.info(s"found dynamic plugins size ${dynamicPlugins.size}")
    dynamicPlugins.foreach(pluginClasses.append(_))
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
  def start(existDriver: AppiumDriver[WebElement] = null): Unit = {
    addLogFile()
    loadPlugins()
    GA.log("start")
    if (existDriver == null) {
      log.info("prepare setup Appium")
      setupAppium()
      //driver.getAppStringMap
    } else {
      log.info("use existed driver")
      this.driver = existDriver
    }
    log.info("init MiniAppium")
    log.info(s"platformName=${conf.currentDriver} driver=${driver}")

    log.info("waiting for app load")
    Thread.sleep(8000)
    log.info(s"driver=${existDriver}")
    log.info("get screen info")
    getDeviceInfo()

    MiniAppium.driver = driver
    MiniAppium.screenHeight = screenHeight
    MiniAppium.screenWidth = screenWidth
    MiniAppium.setPlatformName(conf.currentDriver)


    driver.manage().logs().getAvailableLogTypes().toArray.foreach(log.info)
    //设定结果目录

    GA.log("crawler")
    runStartupScript()
    refreshPage()
    conf.appWhiteList.append(appNameRecord.last().toString)
    crawl()
    //爬虫结束
    pluginClasses.foreach(p => p.stop())
  }

  def runStartupScript(): Unit = {
    log.info("run startup script")
    log.info(conf.startupActions)
    conf.startupActions.foreach(action => {
      MiniAppium.dsl(action)
      refreshPage()
      store.setElementClicked(UrlElement(s"${currentUrl}", "", "", "",
        s"startupActions-${action}-${store.clickedElementsList.size}"))
      store.saveHash(contentHash.last().toString)
      log.info(s"index = ${store.clickedElementsList.size} current =  ${store.clickedElementsList.last.loc}")
      saveDom()
      saveScreen(true)
      Thread.sleep(1000)
    })
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
  }


  def black(keys: String*): Unit = {
    keys.foreach(conf.blackList.append(_))
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
    conf.triggerActions.append(mutable.Map(
      "xpath" -> loc,
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
    RichData.getListFromXPath(xpath, pageDom)
  }

  /**
    * 判断内容是否变化
    *
    * @return
    */
  def getContentHash(): String = {
    //var nodeList = getAllElements("//*[not(ancestor-or-self::UIATableView)]")
    //nodeList = nodeList intersect getAllElements("//*[not(ancestor-or-self::android.widget.ListView)]")

    //排除iOS状态栏 android不受影响
    val nodeList = getAllElements("//*[not(ancestor-or-self::UIAStatusBar)]")
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
    val nodeList = getAllElements("//*[not(ancestor-or-self::UIAStatusBar)]")
    md5(nodeList.map(getUrlElementByMap(_).toTagPath()).distinct.mkString("\n"))
  }

  def getAppName(): String = {
    return ""
  }


  def getUrl(): String = {
    if (conf.defineUrl.nonEmpty) {
      val urlString = conf.defineUrl.flatMap(getAllElements(_)).distinct.map(x => {
        //按照attribute, label, name顺序挨个取第一个非空的指
        log.info("getUrl")
        log.info(x)
        if (x.contains("attribute")) {
          x.getOrElse("attribute", "")
        } else {
          if (x.get("label").nonEmpty) {
            x.getOrElse("label", "")
          } else {
            x.getOrElse("name", "")
          }
        }
      }).filter(_.toString.nonEmpty).mkString("-")
      log.info(s"defineUrl=$urlString")
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
  def getUrlElementByMap(x: immutable.Map[String, Any]): UrlElement = {
    //控件的类型
    val tag = x.getOrElse("tag", "NoTag").toString

    //name为Android的description/text属性, 或者iOS的value属性
    //appium1.5已经废弃findElementByName
    val name = x.getOrElse("value", "").toString.replace("\n", "\\n").take(30)
    //name为id/name属性. 为空的时候为value属性

    //id表示android的resource-id或者iOS的name属性
    val id = x.getOrElse("name", "").toString.split('/').last
    val loc = x.getOrElse("xpath", "").toString
    UrlElement(currentUrl, tag, id, name, loc)

  }

  def isReturn(): Boolean = {
    //超时退出
    if ((new Date().getTime - startTime) > conf.maxTime * 1000) {
      log.warn("maxTime out Quit need exit")
      needExit = true
      setElementAction("exit")
      return true
    }
    if (backRetry > backMaxRetry) {
      log.warn(s"backRetry ${backRetry} > backMaxRetry ${backMaxRetry} need exit")
      needExit = true
    }

    //跳到了其他app
    if (conf.appWhiteList.forall(appNameRecord.last().toString!=_)) {
      log.warn(s"not in app white list ${conf.appWhiteList}")
      log.warn(s"jump to other app appName=${appNameRecord.last()} lastAppName=${appNameRecord.pre()}")
      setElementAction("backApp")
      return true
    }
    //url黑名单
    if (conf.urlBlackList.filter(urlStack.head.matches(_)).nonEmpty) {
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
    //滚动多次没有新元素
    if (swipeRetry > swipeMaxRetry) {
      swipeRetry = 0
      log.warn(s"swipe retry too many times ${swipeRetry} > ${swipeMaxRetry}")
      return true
    }
    //超过遍历深度
    log.info(s"urlStack=${urlStack} baseUrl=${conf.baseUrl} maxDepth=${conf.maxDepth}")
    //大于最大深度并且是在进入过基础Url
    if (urlStack.length > conf.maxDepth) {
      log.warn(s"urlStack.depth=${urlStack.length} > maxDepth=${conf.maxDepth}")
      return true
    }
    //回到桌面了
    if (urlStack.filter(_.matches("Launcher.*")).nonEmpty || appName.matches("com.android\\..*")) {
      log.warn(s"maybe back to desktop ${urlStack.reverse.mkString("-")} need exit")
      //尝试后腿 而不是退出
      //needExit = true
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
  def isBlack(uid: immutable.Map[String, Any]): Boolean = {
    conf.blackList.foreach(b => {
      if (List(uid("name"), uid("label"), uid("value")).exists(_.toString.matches(b))){
        return true
      }
    })
    false
  }


  def isValid(m: immutable.Map[String, Any]): Boolean = {
    m.getOrElse("visible", "true") == "true" &&
      m.getOrElse("enabled", "true") == "true" &&
      m.getOrElse("valid", "true") == "true"
  }

  //todo: 支持xpath表达式
  def getSelectedElements(): Option[List[immutable.Map[String, Any]]] = {
    var all = List[immutable.Map[String, Any]]()
    var firstElements = List[immutable.Map[String, Any]]()
    var lastElements = List[immutable.Map[String, Any]]()
    var selectedElements = List[immutable.Map[String, Any]]()
    var blackElements = List[immutable.Map[String, Any]]()

    val allElements = getAllElements("//*")
    log.trace(s"all elements = ${allElements.size}")

    conf.blackList.filter(_.head == '/').foreach(xpath => {
      log.trace(s"blackList xpath = ${xpath}")
      val temp = getAllElements(xpath).filter(isValid)
      temp.map(_.getOrElse("xpath", "no xpath")).foreach(log.trace)
      blackElements ++= temp
    })
    conf.selectedList.foreach(xpath => {
      log.trace(s"selectedList xpath =  ${xpath}")
      val temp = getAllElements(xpath).filter(isValid)
      temp.map(_.getOrElse("xpath", "no xpath")).foreach(log.trace)
      selectedElements ++= temp
    })
    selectedElements = selectedElements diff blackElements

    log.trace(conf.firstList)
    conf.firstList.foreach(xpath => {
      log.trace(s"firstList xpath = ${xpath}")
      val temp = getAllElements(xpath).filter(isValid).intersect(selectedElements)
      temp.map(_.getOrElse("xpath", "no xpath")).foreach(log.trace)
      firstElements ++= temp
    })
    log.trace("first elements")
    firstElements.map(_.getOrElse("xpath", "no xpath")).foreach(log.trace)

    conf.lastList.foreach(xpath => {
      log.trace(s"lastList xpath = ${xpath}")
      val temp = getAllElements(xpath).filter(isValid).intersect(selectedElements)
      temp.map(_.getOrElse("xpath", "no xpath")).foreach(log.trace)
      lastElements ++= temp
    })

    //去掉不在first和last中的元素
    selectedElements = selectedElements diff firstElements
    selectedElements = selectedElements diff lastElements

    //确保不重, 并保证顺序
    all = (firstElements ++ selectedElements ++ lastElements).distinct
    log.trace("all elements")
    all.map(_.getOrElse("xpath", "no xpath")).foreach(log.trace)
    log.trace(s"all selected length=${all.length}")
    Some(all)

  }


  def hideKeyBoard(): Unit = {
    //iOS键盘隐藏
    if (getAllElements("//UIAKeyboard").size >= 1) {
      log.info("find keyboard , just hide")
      MiniAppium.retry(driver.hideKeyboard())
    }
  }

  def refreshPage(): Boolean = {
    log.info("refresh page")


    if (pageSource.nonEmpty) {
      hideKeyBoard()
    }

    //获取页面结构, 最多重试10次.
    var refreshFinish = false
    pageSource = ""
    1 to 3 foreach (i => {
      if (!refreshFinish) {
        MiniAppium.retry(driver.getPageSource) match {
          case Some(v) => {
            log.trace("get page source success")
            pageSource = v
            pageSource = RichData.toPrettyXML(pageSource)
            Try(RichData.toXML(pageSource)) match {
              case Success(v) => {
                pageDom = v
                refreshFinish = true
              }
              case Failure(e) => {
                log.warn("convert to xml fail")
                log.warn(pageSource)
              }
            }
          }
          case None => {
            log.trace("get page source error")
          }
        }
      }
    })
    //todo:appium解析pageSource有bug. 有些页面会始终无法dump. 改成解析不了就后退
    //todo: 迁移到主流程里面去
    if (!refreshFinish) {
      log.warn("page source get fail, go back")
      setElementAction("back")
      return false
    }else{
      parsePageContext()
      return true
    }
  }

  def parsePageContext():Unit={
    appName=getAppName()
    log.info(s"appName = ${appName}")
    appNameRecord.append(appName)

    currentUrl = getUrl()
    log.info(s"url=${currentUrl}")
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


  def beforeElementAction(element: UrlElement): Unit = {
    log.trace("beforeElementAction")
    conf.beforeElementAction.foreach(elementAction => {
      val xpath = elementAction.get("xpath").get
      val action = elementAction.get("action").get
      if (getAllElements(xpath).contains(element)) {
        Runtimes.eval(action)
      }
    })
    pluginClasses.foreach(p => p.beforeElementAction(element))
  }

  def afterElementAction(element: UrlElement): Unit = {
    log.trace("afterElementAction")
    /*
    if (getElementAction() != "skip") {
      refreshPage()
    }
    */

    if(getElementAction()=="back"){
      backRetry+=1
      log.info(s"backRetry=${backRetry}")
    }else {
      log.info(s"backRetry=0")
      backRetry = 0
    }

    log.info("afterElementAction eval")
    conf.afterElementAction.foreach(Runtimes.eval)
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
    currentElementAction = action
  }

  def getBackElements(): ListBuffer[immutable.Map[String, Any]] = {
    conf.backButton.flatMap(getAllElements(_).filter(isValid))
  }

  def getBackButton(): Option[UrlElement] = {
    log.info("go back")
    //找到可能的关闭按钮, 取第一个可用的关闭按钮
    getBackElements().headOption match {
      case Some(v) if appNameRecord.isDiff() == false => {
        //app相同并且找到back控件才点击. 否则就默认back
        val element = getUrlElementByMap(v)
        setElementAction("click")
        return Some(element)
      }
      case _ => {
        log.warn("find back button error")
        setElementAction("back")
        return Some(UrlElement(s"${currentUrl}", "", "", "",
          s"Back-${store.clickedElementsList.size}"))
      }
    }
  }


  def getAvailableElement(): Seq[UrlElement] = {
    var all = getSelectedElements().getOrElse(List[immutable.Map[String, Any]]())
    log.info(s"all nodes size=${all.length}")
    //去掉黑名单, 这样rule优先级高于黑名单
    all = all.filter(isBlack(_) == false)
    log.info(s"all - black size=${all.length}")
    //去掉back菜单
    all = all diff getBackElements()
    log.info(s"all - back size=${all.length}")
    //把元素转换为Element对象
    var allElements = all.map(getUrlElementByMap(_))
    //获得所有未点击元素
    log.info(s"all elements size=${allElements.length}")

    //过滤已经被点击过的元素
    allElements = allElements.filter(!store.isClicked(_))
    log.info(s"all - clicked size=${allElements.size}")
    allElements = allElements.filter(!store.isSkiped(_))
    log.info(s"fresh elements size=${allElements.length}")
    //记录未被点击的元素
    allElements.foreach(e => {
      store.saveElement(e)
    })
    allElements
  }

  /**
    * 优化后的递归方法. 尾递归.
    * 刷新->找元素->点击第一个未被点击的元素->刷新
    */
  @tailrec final def crawl(): Unit = {
    log.info("crawl next")
    //刷新页面
    var isRefreshSuccess=true
    var skipBeforeElementAction=true
    //是否需要退出或者后退, 得到要做的动作
    var nextElement: Option[UrlElement] = None

    //todo: skip之后可以不用刷新
    if(getElementAction()=="skip"){
      log.info("skip refresh page because last action is skip")
      setElementAction("click")
    }else{
      Thread.sleep(200)
      isRefreshSuccess=refreshPage()
      setElementAction("click")
    }
    //判断下一步动作

    //是否应该退出
    if (needExit) {
      log.warn("get signal to exit")
      return
    }

    //页面刷新失败自动后退
    if(nextElement==None) {
      if (isRefreshSuccess == false) {
        nextElement=Some(UrlElement(s"${currentUrl}", "", "", "",
          s"Back-${store.clickedElementsList.size}"))
        setElementAction("back")
      }else{
        log.info("refresh success")
      }
    }

    //先应用优先规则
    if(nextElement==None) {
      getElementByElementActions() match {
        case Some(e) => {
          log.info(s"found ${e} by ElementActions")
          nextElement = Some(e)
          setElementAction("click")
        }
        case None => {}
      }
    }

    //判断是否需要返回
    if(nextElement==None){
      if (isReturn()) {
        log.info("need to back")
        getElementAction() match {
          case "backApp" =>
            nextElement = Some(UrlElement(s"${currentUrl}", "", "", "",
              s"backApp-${appNameRecord.last()}-${store.clickedElementsList.size}"))
          case "exit" =>
            nextElement=Some(UrlElement(s"${currentUrl}-CrawlStop", "", "", "", "CrawlStop"))
          case _ =>
            nextElement = getBackButton()
        }
      }else{
        log.info("no need to back")
      }
    }

    //查找正常的元素
    if(nextElement==None){
      val allElements = getAvailableElement()
      allElements.headOption match {
        case Some(e) => {
          log.info(s"found ${e} by first available element")
          nextElement = Some(e)
          setElementAction(getActionFromNormalActions(e))
          skipBeforeElementAction=false
        }
        case None => {
          log.warn("all elements had be clicked")
          setElementAction("back")
          nextElement = getBackButton()
          conf.afterUrlFinished.foreach(Runtimes.eval)
        }
      }
    }


    nextElement match {
      case Some(element) => {
        //找到了要点击的元素或者其他的状态标记比如back swipe
        store.setElementClicked(element)
        store.saveHash(contentHash.last().toString)
        store.saveImg(getBasePathName()+".ps.jpg")

        //加载插件分析
        if (skipBeforeElementAction == true) {
          //直接后退即可
          log.info("skip beforeElementActionAction to back")
        } else {
          beforeElementAction(element)
        }

        log.info(s"current element = $element action = ${getElementAction()}")
        if (getElementAction() == "skip") {
          store.clickedElementsList.remove(store.clickedElementsList.size-1)
          store.setElementSkip(element)
        } else {
          //处理控件
          doElementAction(element, getElementAction())
          //插件处理
          afterElementAction(element)
        }

      }
      case None => {
        //当前页面已经遍历完成
        log.error("never access this")
      }
    }
    crawl()
  }


  def getActionFromNormalActions(element:UrlElement):String ={
    val normalActions=conf.triggerActions.filter(_.getOrElse("pri", 1).toString.toInt==0)
    log.info(s"normal actions size = ${normalActions.size}")
    normalActions.foreach(r => {
      val xpath = r("xpath").toString
      val action = r("action").toString

      val allMap = if (xpath.matches("/.*")) {
        //支持xpath
        getAllElements(xpath)
      } else {
        //支持正则通配
        val all = getRuleMatchNodes()
        (all.filter(_ ("name").toString.matches(xpath)) ++ all.filter(_ ("value").toString.matches(xpath))).distinct
      }

      allMap.foreach(m=>{
        val item=getUrlElementByMap(m)
        if(item==element){
          return action
        }else{
          log.info(s"not find ${item} not equal ${element}")
        }
      })
    })
    "click"
  }


  //todo:优化查找方法
  //找到统一的定位方法就在这里定义, 找不到就分别在子类中重载定义
  def findElementByUrlElement(element: UrlElement): Option[WebElement] = {
    //为了加速去掉id定位, 测试表明比xpath竟然还慢
    /*
    log.info(s"find element by uid ${element}")
    if (element.id != "") {
      log.info(s"find by id=${element.id}")
      MiniAppium.doAppium(driver.findElementsById(element.id)) match {
        case Some(v) => {
          val arr = v.toArray().distinct
          if (arr.length == 1) {
            log.trace("find by id success")
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
    */
    //todo: 用其他定位方式优化
    log.info(s"find by xpath= ${element.loc}")
    MiniAppium.retry(driver.findElementsByXPath(element.loc)) match {
      case Some(v) => {
        val arr = v.toArray().distinct
        arr.length match {
          case len if len == 1 => {
            log.info("find by xpath success")
            return Some(arr.head.asInstanceOf[WebElement])
          }
          case len if len > 1 => {
            log.warn(s"find count ${v.size()}, you should check your dom file")
            //有些公司可能存在重名id
            arr.foreach(log.info)
            log.warn("just use the first one")
            return Some(arr.head.asInstanceOf[WebElement])
          }
          case len if len == 0 => {
            log.warn("find by xpath error no element found")
          }
        }

      }
      case None => {
        log.warn("find by xpath error")
      }
    }


    /*
    platformName.toLowerCase() match {
      case "ios" => {
        //照顾iOS android会在findByName的时候自动找text属性.
        MiniAppium.doAppium(driver.findElementByXPath(
          //s"//${uid.tag}[@name='${uid.id}' and @value='${uid.name}' and @x='${uid.loc.split(',').head}' and @y='${uid.loc.split(',').last}']"
          s"//${element.tag}[@path='${element.loc}']"
        )) match {
          case Some(v) => {
            log.trace("find by xpath success")
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
          MiniAppium.doAppium(driver.findElementsByName(uid.name)) match {
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
        MiniAppium.doAppium(driver.findElementsByXPath(element.loc)) match {
          case Some(v) => {
            val arr = v.toArray().distinct
            if (arr.length == 1) {
              log.trace("find by xpath success")
              return Some(arr.head.asInstanceOf[WebElement])
            } else {
              //有些公司可能存在重名id
              arr.foreach(log.info)
              log.warn(s"find count ${v.size()}, you should check your dom file")
              if (arr.size > 0) {
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
    */
    None
  }


  def saveLog(): Unit = {
    //记录点击log
    var index = 0
    File(s"${conf.resultDir}/elements.yml").writeAll(DataObject.toYaml(store))
    File(s"${conf.resultDir}/freemind.mm").writeAll(
      elementTree.generateFreeMind(elementTreeList)
    )
  }

  def getBasePathName(element: UrlElement = store.clickedElementsList.last): String = {
    //序号_文件名
    s"${conf.resultDir}/${store.clickedElementsList.indexOf(element)}_" + element.toFileName()
  }

  def saveDom(): Unit = {
    //保存dom结构
    val domPath = getBasePathName() + ".dom"
    //感谢QQ:434715737的反馈
    Try(File(domPath).writeAll(pageSource)) match {
      case Success(v) => {
        log.trace(s"save to ${domPath}")
      }
      case Failure(e) => {
        log.error(s"save to ${domPath} error")
        log.error(e.getMessage)
        log.error(e.getCause.toString)
        log.error(e.getStackTrace)
      }
    }
  }


  /**
    * 间隔一定时间判断线程是否完成, 未完成就重试
    *
    * @param interval
    * @param retry
    */
  def retryThread(interval: Int = conf.screenshotTimeout, retry: Int = 1)(callback: => Unit): Unit = {
    var needRetry = true
    1 to retry foreach (i => {
      if (needRetry == true) {
        log.info(s"retry time = ${i}")
        val thread = new Thread(new Runnable {
          override def run(): Unit = {
            callback
          }
        })
        thread.start()
        log.info(s"${thread.getId} ${thread.getState}")

        var stopThreadCount = 0
        var needStopThread = false
        while (needStopThread == false) {
          if (thread.getState != Thread.State.TERMINATED) {
            stopThreadCount += 1
            //超时退出
            if (stopThreadCount >= interval * 2) {
              log.warn("screenshot timeout stop thread")
              thread.stop()
              needStopThread = true
              log.info(thread.getState)
              //refreshPage()
              //发送一个命令测试appium
              //log.info(driver.manage().window().getSize)
            } else {
              //未超时等待
              log.debug("screenshot wait")
              Thread.sleep(500)
            }
          } else {
            //正常退出
            log.trace("screenshot finish")
            needRetry = false
            needStopThread = true
          }
        }
      }

    })

  }

  def saveScreen(force: Boolean = false, element: WebElement = null): Unit = {
    //如果是schema相同. 界面基本不变. 那么就跳过截图加快速度.
    val markPath = getBasePathName() + ".ps.jpg"
    val originPath=getBasePathName() + ".ori.jpg"
    val markImageFile = new java.io.File(markPath)
    if (pluginClasses.map(p => p.screenshot(markPath)).contains(true)) {
      return
    }
    if (conf.saveScreen || force) {
      Thread.sleep(100)
      log.info("start screenshot")
      retryThread() {
        val imgFile = if (store.isDiff()) {
          log.info("ui change screenshot again")
          MiniAppium.screenshot()
        } else {
          log.info("ui no change")
          val preImageFileName = getBasePathName(store.clickedElementsList.takeRight(2).head) + ".ori.jpg"
          val preImageFile = new java.io.File(preImageFileName)
          if (preImageFile.exists()) {
            log.info(s"copy from pre image file ${preImageFileName}")
            //FileUtils.copyFile(preImageFile, markImageFile)
            preImageFile
          } else {
            MiniAppium.screenshot()
          }
        }
        FileUtils.copyFile(imgFile, new java.io.File(originPath))
        val newImageFile = MiniAppium.mark(imgFile, element)
        FileUtils.copyFile(newImageFile, markImageFile)
      }
    } else {
      log.info("skip screenshot")
    }
  }


  def appendClickedList(e: UrlElement): Unit = {
    elementTreeList.append(e.url)
    elementTreeList.append(e.loc)
  }

  def doElementAction(element: UrlElement, action: String): Unit = {
    log.info(s"index = ${store.clickedElementsList.size-1} action=${action}")
    log.info(s"current element = ${element}")
    log.info(s"current element url = ${element.url}")
    log.info(s"current element xpath = ${element.loc}")
    log.info(s"current element tag path = ${element.toTagPath()}")
    log.info(s"current element file name = ${element.toFileName()}")
    log.info(s"current element uri = ${element.toLoc()}")

    action match {
      case "skip" => {
        log.info("skip")
      }
      case "tap" => {
        saveDom()
        saveScreen()
        MiniAppium.tap()
      }
      case "back" => {
        saveDom()
        saveScreen()
        back()
        saveLog()
      }
      case "backApp" => {
        MiniAppium.backApp()
      }
      case event if event.matches(".*\\(.*\\).*") => {
        saveDom()
        saveScreen()
        Runtimes.eval(event)
      }
      case str: String => {
        //todo: tap和click的行为不一致. 在ipad上有时候click会点错位置, 而tap不会
        //todo: tap的缺点就是点击元素的时候容易点击到元素上层的控件

        findElementByUrlElement(element) match {
          case Some(webElement) => {
            saveDom()
            saveScreen(false, webElement)
            MiniAppium.retry(
              if (str == "click") {
                webElement.click()
              } else {
                webElement.sendKeys(str)
              }
            ) match {
              case Some(v) => appendClickedList(element)
              case None => {}
            }
          }
          case None => {}
        }
        if (List("UIATextField", "UIATextView", "EditText").map(element.tag.contains(_)).contains(true)) {
          MiniAppium.retry(driver.hideKeyboard())
        }
      }
    }

  }

  def back(): Unit = {
    if (conf.currentDriver.toLowerCase == "android") {
      if (backDistance.intervalMS() < 4000) {
        log.warn("two back action too close")
        Thread.sleep(4000)
      }
      driver.navigate().back()

      backDistance.append("back")
      appNameRecord.pop()
    } else {
      log.warn("you should define you back button in the conf file")
    }
  }

  /**
    * 子类重载
    *
    * @return
    */
  def getRuleMatchNodes(): List[immutable.Map[String, Any]] = {
    getAllElements("//*").filter(isValid)
  }

  //通过规则实现操作. 不管元素是否被点击过
  def getElementByElementActions(): Option[UrlElement] = {
    log.trace("rule match start")
    //先判断是否在期望的界面里. 提升速度
    conf.triggerActions.filter(_.getOrElse("pri", 1).toString.toInt==1).foreach(r => {
      val xpath = r("xpath").toString
      val action = r("action").toString
      val times = r("times").toString.toInt

      val allMap = if (xpath.matches("/.*")) {
        //支持xpath
        getAllElements(xpath)
      } else {
        //支持正则通配
        val all = getRuleMatchNodes()
        (all.filter(_ ("name").toString.matches(xpath)) ++ all.filter(_ ("value").toString.matches(xpath))).distinct
      }
      allMap.headOption match {
        case Some(e) => {
          if (times == 1) {
            log.info(s"remove rule ${r}")
            conf.triggerActions -= r
          }
          r("times") = times - 1
          setElementAction(action)
          return Some(getUrlElementByMap(e))
        }
        case None => {}
      }
    })
    None
  }
}
