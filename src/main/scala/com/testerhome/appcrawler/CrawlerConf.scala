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
  var useNewData = false
  /** 是否截图 */
  var screenshot = true
  var reportTitle = ""
  /** 结果目录 */
  var resultDir = ""
  //var tagLimit=scala.collection.mutable.Map[String, Int]()
  var showCancel = true
  /** 最大运行时间 */
  var maxTime = 3600 * 3
  /** 默认的最大深度10, 结合baseUrl可很好的控制遍历的范围 */
  var maxDepth = 10
  /** sikuli的数据 */
  //var sikuliImages=""
  //todo: 通过数据驱动，支持多设备
  /** appium的capability通用配置 */
  var capability = Map[String, Any](
    //默认不清空数据，防止有人用于微信和qq
    "noReset" -> "true",
      "fullReset" -> "false",
  )

  //测试用例
  var testcase = ReactTestCase(
    name = "TesterHome AppCrawler",
    steps = List[Step](
      Step(xpath = "/*/*", action = "Thread.sleep(1000)")
    )
  )

  //todo: 去掉triggerAction
  /** 引导规则. name, value, times三个元素组成 */
  var triggerActions = ListBuffer[Step](
    Step(xpath="permission_allow_button", times = 3),
    Step(xpath="允许", times = 3)
  )

  /** 默认遍历列表，xpath有用，action暂时没启用*/
  var selectedList = ListBuffer[Step](
    Step(xpath="//*[contains(name(), 'Button')]"),
    //android专属
    Step(xpath="//*[contains(name(), 'Text') and @clickable='true' and string-length(@text)<10]"),
    Step(xpath="//*[@clickable='true']/*[contains(name(), 'Text') and string-length(@text)<10]"),
    Step(xpath="//*[contains(name(), 'Image') and @clickable='true']"),
    Step(xpath="//*[@clickable='true']/*[contains(name(), 'Image')]"),
    //ios专属
    Step(xpath="//*[contains(name(), 'Image') and @name!='']"),
    Step(xpath="//*[contains(name(), 'Text') and @name!='' and string-length(@label)<10]"),
    Step(xpath="//a"),
    //adb,uiautomatorviewer dump生成的数据中节点名字与appium不一致
    //todo: 兼容appium
    Step(xpath="//*[contains(@class, 'Text') and @clickable='true' and string-length(@text)<10]"),
    Step(xpath="//*[@clickable='true']/*[contains(@class, 'Text') and string-length(@text)<10]"),
    Step(xpath="//*[contains(@class, 'Image') and @clickable='true']"),
    Step(xpath="//*[@clickable='true']/*[contains(@class, 'Image')]"),
    Step(xpath="//*[@clickable='true' and contains(@class, 'Button')]"),
  )
  /** 优先遍历元素 */
  var firstList = ListBuffer[Step](
  )
  /** 最后遍历列表 */
  var lastList = ListBuffer[Step](
    Step(xpath="//*[@selected='true']/..//*"),
    Step(xpath="//*[@selected='true']/../..//*")
  )
  /** 后退按钮标记, 主要用于iOS, xpath，目前具备了自动判断返回按钮的能力 */
  var backButton = ListBuffer[Step](
    Step(xpath="Navigate up")
  )

  //自动生成的xpath表达式里可以包含的匹配属
  var xpathAttributes = List(
    "name()",
    //iOS
    "name", "label", "value",
    //android
    "resource-id", "content-desc", "text",
    //html
    "id", "name", "innerText", "tag", "class"
  )
  /** 先按照深度depth排序，再按照list排序，最后按照selected排序。后排序是优先级别最高的 */
  var sortByAttribute = List("depth", "list", "selected")
  //todo: 通过不同的driver实现自动判别
  //可选 default|android|id|xpath，默认状态会自动判断是否使用android定位或者ios定位
  var findBy="xpath"
  /** 用来确定url的元素定位xpath 他的text会被取出当作url因素 */
  var suiteName = List[String](
    "//*[@selected='true']//android.widget.TextView/@text"
  )
  /** 设置一个起始url和maxDepth, 用来在遍历时候指定初始状态和遍历深度 */
  var baseUrl = List[String]()
  var appWhiteList = ListBuffer[String]()
  /** url黑名单.用于排除某些页面 */
  var urlBlackList = ListBuffer[String]()
  var urlWhiteList = ListBuffer[String]()
  /** 黑名单列表 matches风格, 默认排除内容是2个数字以上的控件. */
  var blackList = ListBuffer[Step](
    Step(xpath=".*[0-9]{2}.*")
  )

  //todo: 准备废除
  var beforeStartWait=6000
  //在重启session之前做的事情
  var beforeRestart=ListBuffer[String]()
  //在执行action之前和之后默认执行的动作，比如等待
  var beforeElement = ListBuffer[Step]()
  var afterElement = ListBuffer[Step](
    //Step(xpath="/*/*", action="Thread.sleep(500)")
  )
  var afterElementWait=500

  /**是否需要刷新或者滑动*/
  var afterAll = ListBuffer[Step]()
  //afterPage执行多少次后才不执行，比如连续滑动2次都没新元素即取消
  var afterAllMax=2
  //相似控件最多点击几次
  //todo: 改名为elementsInListMax
  var tagLimitMax = 2
  //个别控件可例外
  var tagLimit = ListBuffer[Step](
    //特殊的按钮，可以一直被遍历
    Step(xpath = "确定", times = 1000),
    Step(xpath = "取消", times = 1000),
    Step(xpath = "share_comment_guide_btn_name", times=1000)
  )
  //只需要写given与then即可
  var assertGlobal = List[Step]()

  /** 插件列表，暂时禁用，太高级了，很多人不会用 */
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
