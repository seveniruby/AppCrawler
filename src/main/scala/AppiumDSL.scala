import java.net.URL

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import org.apache.log4j.{Level, Logger, BasicConfigurator}
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest._
import org.scalatest.selenium.WebBrowser
import org.scalatest.selenium.WebBrowser.XPathQuery

/**
  * Created by seveniruby on 16/3/26.
  */
class AppiumDSL extends FunSuite with ShouldMatchers with WebBrowser with BeforeAndAfterAll with CommonLog{
  val capabilities = new DesiredCapabilities()
  var appiumUrl="http://127.0.0.1:4723/wd/hub"

  implicit var driver : AppiumDriver[WebElement] = _


  def config(key:String, value:Any): Unit ={
    capabilities.setCapability(key, value)
  }
  def send(keys:String): Unit ={
    driver.getKeyboard.sendKeys(keys)
  }

  def text(key:String): XPathQuery ={
    xpath(s"//*[contains(@text, '$key') or contains(@resource-id, '$key') or contains(name, '$key')]")
  }
  var index=0
  def save(): Unit ={
    index+=1
    captureTo(s"${index}.jpg")
  }

  def appium(url:String): Unit ={
    appiumUrl=url
    //todo: 无法通过url来确定是否是android, 需要改进
    println(capabilities)
    if(capabilities.getCapability("app").toString.matches(".*\\.apk$") ||
      capabilities.getCapability("appActivity").toString.nonEmpty){
      driver=new AndroidDriver[WebElement](new URL(appiumUrl), capabilities)
    }else{
      driver=new IOSDriver[WebElement](new URL(appiumUrl), capabilities)
    }
  }

  def printTree(key: String=""): Unit ={
    if(key.isEmpty){
      log.trace(RichData.toPrettyXML(pageSource))
    }else{
      RichData.parseXPath(keyToXPath(key), RichData.toXML(pageSource)).foreach(node=>{
        log.trace(node)
      })

    }
  }

  def keyToXPath(key:String): String ={
    if(key.matches("/.*")) {
      key
    }else{
      s"//*[" +
        s"contains(@text, '$key') " +
        s"or contains(@resource-id, '$key') " +
        s"or contains(@content-desc, '$key') " +
        s"or contains(@name, '$key') " +
        s"or contains(@label, '$key') " +
        s"or contains(name(), '$key') " +
        s"]"
    }
  }

  /**
    * 解析给定的xpath表达式或者text的定位标记 把节点的信息转换为map
    *
    * @param key
    * @return
    */
  def tree(key:String): Map[String, Any] ={
    RichData.parseXPath(keyToXPath(key), RichData.toXML(pageSource))(0)
  }

  //todo: not test
  def crawl(conf:String = "",  resultDir:String=""): Unit ={
    var crawler:Crawler=new Crawler
    if(conf.nonEmpty) {
      crawler.loadConf(conf)
    }
    crawler.conf.currentDriver.toLowerCase match {
      case "android"=>{
        crawler=new AndroidCrawler
      }
      case "ios" => {
        crawler=new IOSCrawler
      }
      case _ =>{
        log.trace("请指定currentDriver为Android或者iOS")
      }
    }
    if(resultDir.nonEmpty){
      crawler.conf.resultDir=resultDir
    }
    crawler.conf.startupActions.clear()

    crawler.start(driver)

  }

}
