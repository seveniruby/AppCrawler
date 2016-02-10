import java.io.{FileWriter, BufferedWriter, File}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.mutable.ListBuffer
import scala.io.Source
import org.json4s.{FieldSerializer, DefaultFormats}
import org.json4s.native.Serialization._
import java.nio.charset.Charset

/**
  * Created by seveniruby on 16/1/6.
  */
class CrawlerConf {
  var saveScreen=true
  var currentDriver="android"
  var capability=Map[String, String](
    "app"->"http://xqfile.imedao.com/android-release/xueqiu_730_01191600.apk",
    "platformName"->"",
    "platformVersion"->"",
    "deviceName"->"",
    "noReset"->"false",
    "autoWebview"->"false",
    "autoLaunch"->"false"
  )
  var androidCapability=Map[String, String](
    "appPackage"->"com.xueqiu.android",
    "appActivity"->".view.WelcomeActivityAlias",
    "appium"->"http://127.0.0.1:4730/wd/hub"
  )
  var iosCapability=Map[String, String](
    "bundleId"->"",
    "autoAcceptAlerts"->"true",
    "platformVersion"->"9.2",
    "deviceName"-> "iPhone 6",
    "appium"->"http://127.0.0.1:4723/wd/hub"
  )
  /**用来确定url的元素定位xpath 他的text会被取出当作url因素*/
  var defineUrl=""
  /**设置一个起始url和maxDepth, 用来在遍历时候指定初始状态和遍历深度*/
  var baseUrl=""
  /**默认的最大深度10, 结合baseUrl可很好的控制遍历的范围*/
  var maxDepth=6
  /**url黑名单.用于排除某些页面*/
  var blackUrlList = ListBuffer("")

  /**后退按钮标记, 主要用于iOS, xpath*/
  var backButton = ListBuffer[String]()

  /**优先遍历元素*/
  var firstList=ListBuffer[String]()
  /**默认遍历列表*/
  var selectedList = ListBuffer[String](
    "//*[@enabled='true' and @visible='true' and contains(name(), 'Text')]",
    "//*[@enabled='true' and @visible='true' and contains(name(), 'Image')]",
    "//*[@enabled='true' and @visible='true' and contains(name(), 'Button')]",

    "//*[@enabled='true' and @clickable='true' and contains(name(), 'Text')]",
    "//*[@enabled='true' and @clickable='true' and contains(name(), 'Image')]",
    "//*[@enabled='true' and @resource-id!='']",
    "//*[@enabled='true' and @content-desc!='']"
  )
  /**最后遍历列表*/
  var lastList=ListBuffer[String]()

  //包括backButton
  /**黑名单列表 matches风格*/
  var blackList = ListBuffer[String]()
  /**引导规则. name, value, times三个元素组成*/
  var elementActions = ListBuffer[scala.collection.mutable.Map[String, Any]]()
  elementActions += scala.collection.mutable.Map("idOrName"->".*seveniruby.*", "action"->"click", "times"->0)

  def loadByJson4s(file: String): Option[this.type] ={
    implicit val formats = DefaultFormats+ FieldSerializer[this.type]()
    if (new java.io.File(file).exists()) {
      println(s"load config from ${file}")
      println(Source.fromFile(file).mkString)
      return Some(read[this.type ](Source.fromFile(file).mkString))
    }else{
      println(s"conf file ${file} no exist ")
      return None
    }
  }

  def save(path: String): Unit ={

/*    //这个方法不能正确的存储utf8编码的文字
    implicit val formats = DefaultFormats+ FieldSerializer[this.type]()
    val file = new java.io.File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    println(writePretty(this))
    println(write(this))
    bw.write(writePretty(this))
    bw.close()*/

    val file = new java.io.File(path)
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.writerWithDefaultPrettyPrinter().writeValue(file, this)
    println(mapper.writeValueAsString(this))
  }

  def load(file :String): CrawlerConf ={
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    println(mapper.writeValueAsString(classOf[CrawlerConf]))
    return mapper.readValue(Source.fromFile(file).mkString.getBytes, classOf[CrawlerConf])
  }


  def load(file :File): CrawlerConf ={
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    println(mapper.writeValueAsString(classOf[CrawlerConf]))
    return mapper.readValue(Source.fromFile(file).mkString.getBytes, classOf[CrawlerConf])
  }


}
