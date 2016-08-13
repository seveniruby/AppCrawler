package com.xueqiu.qa.appcrawler

import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.json4s.native.Serialization._
import org.json4s.{DefaultFormats, FieldSerializer}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by seveniruby on 16/1/6.
  */
class CrawlerConf {
  /**插件列表*/
  var pluginList=List("com.xueqiu.qa.appcrawler.plugin.TagLimitPlugin")
  var logLevel="TRACE"
  /**是否截图*/
  var saveScreen=true
  var screenshotTimeout=20
  var currentDriver="android"
  /**最大运行时间*/
  var maxTime = 3600*3
  /**结果目录*/
  var resultDir=""
  /**appium的capability通用配置*/
  var capability=Map[String, Any](
    "app"->"",
    "platformName"->"",
    "platformVersion"->"",
    "deviceName"->"demo",
    "noReset"->"false",
    "autoWebview"->"false",
    "autoLaunch"->"true"
  )
  /**android专属配置 最后会和capability合并*/
  var androidCapability=Map[String, Any](
    "appPackage"->"",
    "appActivity"->""
  )
  var iosCapability=Map[String, Any](
    "bundleId"->"",
    "autoAcceptAlerts"->"true",
    "platformVersion"->"9.2",
    "deviceName"-> "iPhone 6"
  )
  /**用来确定url的元素定位xpath 他的text会被取出当作url因素*/
  var defineUrl=List[String]()
  /**设置一个起始url和maxDepth, 用来在遍历时候指定初始状态和遍历深度*/
  var baseUrl= List[String]()
  /**默认的最大深度10, 结合baseUrl可很好的控制遍历的范围*/
  var maxDepth=6
  /**是否需要滑动来发现新的元素 会在每个页面都执行. 可能会拖慢速度*/
  var needSwipe=false
  /**是否是前向遍历或者后向遍历*/
  var headFirst=true
  /**是否遍历WebView控件*/
  var enterWebView=true
  /**url黑名单.用于排除某些页面*/
  var blackUrlList = ListBuffer("")

  /**后退按钮标记, 主要用于iOS, xpath*/
  var backButton = ListBuffer[String]()

  /**优先遍历元素*/
  var firstList=ListBuffer[String](
    "//*[@text='高手理财']",
    "//*[@text='港股高手']"
  )
  /**默认遍历列表*/
  var selectedList = ListBuffer[String](
    "//*[contains(name(), 'Text')]",
    "//*[contains(name(), 'Image')]",
    "//*[contains(name(), 'Button')]",
    "//*[contains(name(), 'CheckBox')]"
  )
  /**最后遍历列表*/
  var lastList=ListBuffer[String]()

  //包括backButton
  //todo: 支持正则表达式
  /**黑名单列表 matches风格, 默认排除内容是2个数字以上的控件.*/
  var blackList = ListBuffer[String](
    ".*[0-9]{2}.*"
  )
  /**引导规则. name, value, times三个元素组成*/
  var elementActions = ListBuffer[scala.collection.mutable.Map[String, Any]]()
  elementActions += mutable.Map("idOrName"->".*seveniruby.*", "action"->"click", "times"->0)
  var startupActions=ListBuffer[String]()
  startupActions++=List("sleep(3)", "println(\"startupAction call use scala code\")")

  def loadByJson4s(file: String): Option[this.type] ={
    implicit val formats = DefaultFormats+ FieldSerializer[this.type]()
    if (new java.io.File(file).exists()) {
      println(s"load config from ${file}")
      println(Source.fromFile(file).mkString)
      Some(read[this.type ](Source.fromFile(file).mkString))
    }else{
      println(s"conf file ${file} no exist ")
      None
    }
  }

  def save(path: String): Unit ={

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
  def toJson(): String ={
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

  }
  def toYaml(): String ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
  }
  def loadYaml(fileName:File): CrawlerConf ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(fileName, classOf[CrawlerConf])
  }

  def loadYaml(content:String): Unit ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(content, classOf[CrawlerConf])
  }


  def load(file :String): CrawlerConf ={
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(Source.fromFile(file).mkString.getBytes, classOf[CrawlerConf])
  }
  def load(file :File): CrawlerConf ={
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(Source.fromFile(file).mkString.getBytes, classOf[CrawlerConf])
  }


}
