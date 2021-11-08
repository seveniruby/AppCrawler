package com.ceshiren.appcrawler.core

import com.ceshiren.appcrawler.model.Step
import com.ceshiren.appcrawler.utils.TData
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.io.File
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by seveniruby on 16/1/6.
  */
class CrawlerConf {
  val screenshotDescription = "是否截图"
  var screenshot = true
  val reportTitleDescription = "报告的title"
  var reportTitle = "AppCrawler"
  val resultDirDescription = "结果目录，如果为空会自动创建对应时间戳_报名的结果目录"
  var resultDir = ""
  //todo: 支持重进未完全覆盖的界面
  //var tagLimit=scala.collection.mutable.Map[String, Int]()
  val showCancelDescription = "是否展示跳过的控件记录"
  var showCancel = true
  val maxTimeDescription = "最大运行时间"
  var maxTime = 3600 * 3
  val maxDepthDescription = "默认的最大深度10, 结合baseUrl可很好的控制遍历的范围"
  var maxDepth = 10

  /** sikuli的数据 */
  //var sikuliImages=""
  //todo: 通过数据驱动，支持多设备
  val capabilityDescription = "appium的capability通用配置，其中automationName代表自动化的驱动引擎，" +
    "除了支持appium的所有引擎外，额外增加了adb和selenium的支持"
  var capability = Map[String, Any](
    //默认不清空数据，防止有人用于微信和qq
    "noReset" -> "true",
    "fullReset" -> "false",
  )

  //测试用例
  val testcaseDescription = "测试用例设置，用于遍历开始之前的一些前置操作，比如自动登录"
  var testcase = ReactTestCase(
    name = "AppCrawler TestCase",
    steps = List[Step](
      Step(xpath = "/*/*", action = "Thread.sleep(1000)")
    )
  )

  //todo: 去掉triggerAction
  val triggerActionsDescription = "在遍历过程中需要随时处理的一些操作，比如弹框、登录等"
  var triggerActions = ListBuffer[Step](
    Step(xpath = "permission_allow_button", times = 3),
    Step(xpath = "允许", times = 3)
  )

  val selectedListDescription = "默认遍历列表，只有出现在这个列表里的控件范围才会被遍历"
  var selectedList = ListBuffer[Step](
    Step(xpath = "//*[contains(name(), 'Button')]"),
    //android专属
    Step(xpath = "//*[contains(name(), 'Text') and @clickable='true' and string-length(@text)<10]"),
    Step(xpath = "//*[@clickable='true']//*[contains(name(), 'Text') and string-length(@text)<10]"),
    Step(xpath = "//*[contains(name(), 'Image') and @clickable='true']"),
    Step(xpath = "//*[@clickable='true']/*[contains(name(), 'Image')]"),
    //ios专属
    Step(xpath = "//*[contains(name(), 'Image') and @name!='']"),
    Step(xpath = "//*[contains(name(), 'Text') and @name!='' and string-length(@label)<10]"),
    Step(xpath = "//a"),
    //adb,uiautomatorviewer dump生成的数据中节点名字与appium不一致
    //todo: 兼容appium
    Step(xpath = "//*[contains(@class, 'Text') and @clickable='true' and string-length(@text)<10]"),
    Step(xpath = "//*[@clickable='true']//*[contains(@class, 'Text') and string-length(@text)<10]"),
    Step(xpath = "//*[contains(@class, 'Image') and @clickable='true']"),
    Step(xpath = "//*[@clickable='true']/*[contains(@class, 'Image')]"),
    Step(xpath = "//*[@clickable='true' and contains(@class, 'Button')]"),
  )
  val blackListDescription = "黑名单列表 matches风格, 默认排除内容包含2个数字的控件"
  var blackList = ListBuffer[Step](
    Step(xpath = ".*[0-9]{2}.*")
  )
  val firstListDescription = "优先遍历列表，同时出现在selectedList与firstList中的控件会被优先遍历"
  var firstList = ListBuffer[Step](
  )
  val lastListDescription = "最后遍历列表，同时出现在selectedList与lastList中的控件会被最后遍历"
  var lastList = ListBuffer[Step](
    Step(xpath = "//*[@selected='true']/..//*"),
    Step(xpath = "//*[@selected='true']/../..//*")
  )
  val backButtonDescription = "后退按钮列表，默认在所有控件遍历完成后，才会最后点击后退按钮。目前具备了自动判断返回按钮的能力，默认不需要配置"
  var backButton = ListBuffer[Step](
    Step(xpath = "Navigate up")
  )


  val xpathAttributesDescription = "在生成一个控件的唯一定位符中应该包含的关键属性"
  var xpathAttributes = List(
    "name()",
    //iOS
    "name", "label", "value",
    //android
    "resource-id", "content-desc", "text",
    //html
    "id", "name", "innerText", "tag", "class"
  )
  val sortByAttributeDescription = "陆续根据属性进行遍历排序微调，depth表示从dom中最深层的控件开始遍历，list表示dom中列表优先，selected表示菜单最后遍历，这是默认规则，一般不需要改变"
  var sortByAttribute = List("depth", "list", "selected")
  //todo: 通过不同的driver实现自动判别
  val findByDescription = "默认生成控件唯一定位符的表达式风格，可选项 default|android|id|xpath，默认会自动判断是否使用android定位或者ios定位"
  var findBy = "xpath"
  val suiteNameDescription = "报告中的测试套件名字可以由列表内的控件内容替换，增强报告中关键界面的辨识度"
  var suiteName = List[String](
    "//*[@selected='true']//android.widget.TextView/@text"
  )
  val baseUrlDescription = "设置一个起始点，从这个起始点开始计算深度，比如默认从登录后的界面开始计算"
  var baseUrl = List[String]()
  val appWhiteListDescription = "app白名单，允许在这些app里进行遍历"
  var appWhiteList = ListBuffer[String]()
  val urlBlackListDescription = "url黑名单，用于排除某些页面的遍历"
  var urlBlackList = ListBuffer[String]()
  val urlWhiteListDescription = "url白名单，仅在这些界面内遍历"
  var urlWhiteList = ListBuffer[String]()

  //todo: 准备废除
  val beforeStartWaitDescription = "启动一个app默认等待的时间"
  var beforeStartWait = 6000
  //在重启session之前做的事情
  var beforeRestart = ListBuffer[String]()
  val beforeElementDescription = "在遍历每个控件之前默认执行的动作"
  var beforeElement = ListBuffer[Step]()
  val afterElementDescription = "在遍历每个控件之后默认执行的动作"
  var afterElement = ListBuffer[Step](
    //Step(xpath="/*/*", action="Thread.sleep(500)")
  )
  val afterElementWaitDescription = "在遍历每个控件之后默认等待的时间，用于等待新页面加载"
  var afterElementWait = 500

  val afterAllDescription = "在遍历完当前页面内的所有控件后，是否需要刷新或者滑动"
  var afterAll = ListBuffer[Step]()

  val afterAllMaxDescription = "afterAll的最大重试次数，比如连续滑动2次都没新元素即取消"
  var afterAllMax = 2

  val tagLimitMaxDescription = "相似控件最多点击几次"
  var tagLimitMax = 2

  val tagLimitDescription = "设置部分相似控件的最大遍历次数"
  var tagLimit = ListBuffer[Step](
    //特殊的按钮，可以一直被遍历
    Step(xpath = "确定", times = 1000),
    Step(xpath = "取消", times = 1000),
    Step(xpath = "share_comment_guide_btn_name", times = 1000)
  )
  val assertGlobalDescription = "全局断言"
  var assertGlobal = List[Step]()

  val pluginListDescription = "插件列表，暂时禁用，太高级了，很多人不会用"

  val Description =
    """
      |在selectedList firstList lastList等很多配置中，需要填充的是测试步骤Step类型
      |Step类型由given（满足条件）when（条件满足的行为）then（断言）三部分组成
      |Step可以简化为xpath（定位表达式，支持xpath 正则 包含关系）与action（点击 输入等行为）
      |""".stripMargin
  var pluginList = List[String]()

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

  def loadYaml(content: String): CrawlerConf = {
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(content, classOf[CrawlerConf])
  }


  def load(file: String): CrawlerConf = {
    load(new File(file)).get
  }

  //如果没有显式配置参数，那么就会用默认值代替
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
