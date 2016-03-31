import java.net.URL

import io.appium.java_client.android.AndroidDriver
import org.apache.log4j.{Level, Logger, BasicConfigurator}
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
  implicit lazy val driver = new AndroidDriver(new URL(appiumUrl), capabilities)


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
    * @param key
    * @return
    */
  def tree(key:String): Map[String, Any] ={
    RichData.parseXPath(keyToXPath(key), RichData.toXML(pageSource))(0)
  }

}
