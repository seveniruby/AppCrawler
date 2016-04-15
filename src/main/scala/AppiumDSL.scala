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
import org.scalatest.time.{Seconds, Span}

/**
  * Created by seveniruby on 16/3/26.
  */
class AppiumDSL extends FunSuite
  with ShouldMatchers
  with WebBrowser
  with BeforeAndAfterAll
  with BeforeAndAfterEach with CommonLog{
  import org.scalatest.prop.TableDrivenPropertyChecks._

  val capabilities = new DesiredCapabilities()
  var appiumUrl="http://127.0.0.1:4723/wd/hub"

  implicit var driver : AppiumDriver[WebElement] = _


  private def initAppium(): Unit ={
    setCaptureDir(".")
    implicitlyWait(Span(10, Seconds))
    //login()

    if(tree("稍后再说").nonEmpty){
      click on see("稍后再说")
    }
  }


  def Android(): Unit = {
    config("appPackage", "com.xueqiu.android")
    config("appActivity", "com.xueqiu.android.view.WelcomeActivityAlias")
    config("deviceName", "demo")
    appium("http://127.0.0.1:4730/wd/hub")

  }

  def iOS(sim: Boolean = false): Unit = {
    val app = if (sim) {
      "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/" +
        "Build/Products/Debug-iphonesimulator/Snowball.app"
    } else {
      "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/" +
        "Build/Products/Debug-iphoneos/Snowball.app"
    }
    config("app", app)
    config("bundleId", "com.xueqiu")
    config("fullReset", true)
    config("noReset", false)
    config("deviceName", "iPhone 6")
    config("platformVersion", "9.2")
    config("autoAcceptAlerts", "true")
  }


  def config(key:String, value:Any): Unit ={
    capabilities.setCapability(key, value)
  }
  def send(keys:String): Unit ={
    driver.getKeyboard.sendKeys(keys)
  }

  def sleep(seconds: Int=1): Unit ={
    Thread.sleep(seconds*1000)
  }

  def see(key:String): XPathQuery ={
    if(key.matches("/.*")){
      xpath(key)
    }else {
      xpath(keyToXPath(key))
    }
  }
  var index=0
  def save(): Unit ={
    index+=1
    captureTo(s"${index}.jpg")
  }

  def appium(url:String="http://127.0.0.1:4723/wd/hub"): Unit ={
    appiumUrl=url
    //todo: 无法通过url来确定是否是android, 需要改进
    println(capabilities)
    if(
      capabilities.getCapability("app").toString.matches(".*\\.apk$") ||
      capabilities.is("appActivity") == true ||
      capabilities.is("appPackage")
    ){
      driver=new AndroidDriver[WebElement](new URL(appiumUrl), capabilities)
    }else{
      driver=new IOSDriver[WebElement](new URL(appiumUrl), capabilities)
    }

  }

  def keyToXPath(key:String): String ={
    key.charAt(0) match {
      case '/' => {
        key
      }
      case '^' => {
        s"//*[" +
          s"matches(@text, '$key') " +
          s"or matches(@resource-id, '$key') " +
          s"or matches(@content-desc, '$key') " +
          s"or matches(@name, '$key') " +
          s"or matches(@label, '$key') " +
          s"or matches(@value, '$key') " +
          s"or matches(name(), '$key') " +
          s"]"
      }
      case _ => {
        s"//*[" +
          s"contains(@text, '$key') " +
          s"or contains(@resource-id, '$key') " +
          s"or contains(@content-desc, '$key') " +
          s"or contains(@name, '$key') " +
          s"or contains(@label, '$key') " +
          s"or contains(@value, '$key') " +
          s"or contains(name(), '$key') " +
          s"]"
      }
    }
  }

  /**
    * 解析给定的xpath表达式或者text的定位标记 把节点的信息转换为map
    *
    * @param key
    * @return
    */
  def tree(key:String="//*"): Map[String, Any] ={
    log.info(s"find by key = ${key}")
      val nodes=RichData.parseXPath(keyToXPath(key), RichData.toXML(pageSource))
      nodes.foreach(node=>{
        log.info("xpath="+node.get("loc"))
        log.info(node)
        log.info("")
      })
      log.info("return first")
      nodes.headOption.getOrElse(Map[String, Any]())
  }

  //todo: not test
  def crawl(conf:String = "",  resultDir:String=""): Unit ={
    var crawler:Crawler=new Crawler
    driver.getClass.getSimpleName match {
      case "AndroidDriver"=>{
        crawler=new AndroidCrawler
      }
      case "IOSDriver" => {
        crawler=new IOSCrawler
      }
      case _ =>{
        log.warn("never heard this driver before")
      }
    }
    if(conf.nonEmpty) {
      crawler.loadConf(conf)
    }
    if(resultDir.nonEmpty){
      crawler.conf.resultDir=resultDir
    }
    crawler.conf.startupActions.clear()
    crawler.log.setLevel(Level.TRACE)
    crawler.conf.maxDepth=1
    crawler.start(driver)

  }

}
