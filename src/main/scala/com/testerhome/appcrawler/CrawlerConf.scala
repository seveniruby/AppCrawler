package com.testerhome.appcrawler

import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.openqa.selenium.interactions.Actions

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by seveniruby on 16/1/6.
  */
class CrawlerConf {
  /** 插件列表，暂时禁用，太高级了，很多人不会用 */
  //var pluginList = List[String]()
  var logLevel = "TRACE"
  /** 是否截图 */
  var saveScreen = true
  var reportTitle = ""
  //截图等待的超时时间，截图一般会消耗2s
  var screenshotTimeout = 20
  var currentDriver = "Android"
  var swipeRetryMax=2
  /**在执行action后等待多少毫秒进行刷新*/
  var waitLoading=1000
  var waitLaunch=6000
  var tagLimitMax = 3
  var tagLimit = ListBuffer[Step]()
  //var tagLimit=scala.collection.mutable.Map[String, Int]()
  var showCancel = false
  /** 最大运行时间 */
  var maxTime = 3600 * 3
  /** 结果目录 */
  var resultDir = ""
  /** sikuli的数据 */
  var sikuliImages=""
  //todo: 支持多设备
  /** 设备列表，支持兼容性测试 */
  var devices = ListBuffer(
    Map[String, Any](
      "platformName" -> "",
      "platformVersion" -> "9.2",
      "deviceName" -> "iPhone 6"
    ))
  /** appium的capability通用配置 */
  var capability = Map[String, Any](
    //默认不清空数据，防止有人用于微信和qq
    "noReset" -> "true",
    "fullReset" -> "false",
  )
  /** android专属配置 最后会和capability合并 */
  var androidCapability = Map[String, Any](
    "app" -> "",
    "appPackage" -> "",
    "appActivity" -> ""
  )
  var iosCapability = Map[String, Any](
    "app" -> "",
    "bundleId" -> "",
    "autoAcceptAlerts" -> "true",
  )
  //自动生成的xpath表达式里可以包含的匹配属
  var xpathAttributes = List("name", "label", "value", "resource-id", "content-desc", "index", "text")
  /** 用来确定url的元素定位xpath 他的text会被取出当作url因素 */
  var defineUrl = List[String]()
  /** 设置一个起始url和maxDepth, 用来在遍历时候指定初始状态和遍历深度 */
  var baseUrl = List[String]()
  var appWhiteList = ListBuffer[String]()
  /** 默认的最大深度10, 结合baseUrl可很好的控制遍历的范围 */
  var maxDepth = 6
  /** 是否是前向遍历或者后向遍历 */
  var sortByAttribute = List("depth", "selected")
  /** 是否遍历WebView控件 */
  var enterWebView = true
  /** url黑名单.用于排除某些页面 */
  var urlBlackList = ListBuffer[String]()
  var urlWhiteList = ListBuffer[String]()

  var defaultBackAction = ListBuffer[String]()


  /** 后退按钮标记, 主要用于iOS, xpath */
  var backButton = ListBuffer[Step]()
  /** 优先遍历元素 */
  var firstList = ListBuffer[Step](
  )
  /** 默认遍历列表，xpath有用，action暂时没启用*/
  var selectedList = ListBuffer[Step](
    Step(xpath="//*[contains(name(), 'Text') and @clickable='true']"),
    Step(xpath="//*[@clickable='true']//*[contains(name(), 'Text')]"),
    Step(xpath="//*[@name!='']"),
    Step(xpath="//*[contains(name(), 'Image')]"),
    Step(xpath="//*[contains(name(), 'Button')]"),
  )
  /** 最后遍历列表 */
  var lastList = ListBuffer[Step](
    Step(xpath="//*[@selected='true']/..//*"),
    Step(xpath="//*[@selected='true']/../..//*")
  )

  //包括backButton
  //todo: 支持正则表达式
  /** 黑名单列表 matches风格, 默认排除内容是2个数字以上的控件. */
  var blackList = ListBuffer[String](
    //".*[0-9]{2}.*"
  )
  /** 引导规则. name, value, times三个元素组成 */
  var triggerActions = ListBuffer[Step]()
  //todo: 用watch代替triggerActions
  var autoCrawl: Boolean = true
  var assert = ReactTestCase(
    name = "TesterHome AppCrawler",
    steps = List[Step]()
  )
  var testcase = ReactTestCase(
    name = "TesterHome AppCrawler",
    steps = List[Step]()
  )

  var beforeElementAction = ListBuffer[Step]()
  var afterElementAction = ListBuffer[Step]()
  var afterUrlFinished = ListBuffer[Step]()


  var beforeRestart=ListBuffer[String]()
  var monkeyEvents = ListBuffer[Int]()
  var monkeyRunTimeSeconds = 30


  def loadByJson4s(file: String): Option[this.type] = {
    if (new java.io.File(file).exists()) {
      println(s"load config from ${file}")
      println(Source.fromFile(file).mkString)
      Some(TData.fromYaml[this.type](Source.fromFile(file).mkString))
    } else {
      println(s"conf file ${file} no exist ")
      None
    }
  }

  def save(path: String): Unit = {

    /*
        //这个方法不能正确的存储utf8编码的文字
        implicit val formats = DefaultFormats+ FieldSerializer[this.type]()
        val file = new java.io.File(path)
        val bw = new BufferedWriter(new FileWriter(file))
        log.trace(writePretty(this))
        log.trace(write(this))
        bw.write(writePretty(this))
        bw.close()
        */

    val file = new java.io.File(path)
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.writerWithDefaultPrettyPrinter().writeValue(file, this)
    println(mapper.writeValueAsString(this))
  }

  def toJson(): String = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

  }

  def toYaml(): String = {
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
  }

  def loadYaml(fileName: File): CrawlerConf = {
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(fileName, classOf[CrawlerConf])
  }

  def loadYaml(content: String): Unit = {
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(content, classOf[CrawlerConf])
  }


  def load(file: String): CrawlerConf = {
    load(new File(file)).get
  }

  def load(file: File): Option[CrawlerConf] = {
    val content = Source.fromFile(file, "UTF-8").getLines().mkString("\n")
    file.getName match {
      case json if json.endsWith(".json") => {
        Some(TData.fromJson[CrawlerConf](content))
      }
      case yaml if yaml.endsWith(".yml") || yaml.endsWith(".yaml") => {
        Some(TData.fromYaml[CrawlerConf](content))
      }
      case path => {
        println(s"${path} not support")
        None
      }
    }
  }


}
