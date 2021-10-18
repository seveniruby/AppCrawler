package com.ceshiren.appcrawler.it

import com.ceshiren.appcrawler.driver.AppiumClient
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.{By, WebElement}
import org.scalatest.FunSuite

import java.net.URL
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

/**
  * Created by seveniruby on 16/11/3.
  */
class TestWeixin extends FunSuite {
  test("weixin") {

    val capability = new DesiredCapabilities()
    capability.setCapability("app", "")
    capability.setCapability("appPackage", "com.tencent.mm")
    capability.setCapability("appActivity", ".ui.LauncherUI")
    capability.setCapability("deviceName", "emulator-5554")
    capability.setCapability("fastReset", "false")
    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
    capability.setCapability("unicodeKeyboard", "true")
    capability.setCapability("resetKeyboard", "true")
    capability.setCapability("automationName", "appium")
    capability.setCapability("platformName", "android")
    capability.setCapability("resetKeyboard", "true")
    //capability.setCapability("autoWebview", "true")

    //val url="http://192.168.100.65:7771"
    val url = "http://127.0.0.1:4723/wd/hub"
    val driver = new AndroidDriver[WebElement](new URL(url), capability)
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)


    Thread.sleep(3000)
    driver.findElement(By.xpath("//*[contains(@content-desc, '搜索')]")).click()
    driver.findElement(By.xpath("//*[@text='搜索']")).sendKeys("BQC")
    driver.findElement(By.xpath("//*[contains(@text, 'BQConf')]")).click()
    driver.findElement(By.xpath("//*[contains(@content-desc, '聊天信息')]")).click()

    AppiumClient.driver = driver
    AppiumClient.swipe(0.5, 0.9, 0.5, 0.1)
    driver.findElement(By.xpath("//*[contains(@text, '查看全部群成员')]")).click()
    Thread.sleep(3000)


    while (true) {
      try {
        val friends = driver.findElementsByXPath("//*[@resource-id='com.tencent.mm:id/a8s']")
        friends.asScala.foreach(image => {
          image.click()
          val e = driver.findElementByXPath("//*[@text='发消息' or @text='添加到通讯录']")
          if (e.getText == "添加到通讯录") {
            Thread.sleep(2000)
            e.click()
            driver.findElementByXPath("//*[@text='发送' or @text='确定']").click()
            Thread.sleep(1000)
          }
          driver.navigate().back()
        })
        AppiumClient.swipe(0.5, 0.9, 0.5, 0.1)
      } catch {
        case e: Exception => {
          println(e.getMessage)
          e.printStackTrace()
          println(driver.getPageSource)
          driver.navigate().back()
        }
      }
    }
  }

  test("test weixin h5") {

    val capability = new DesiredCapabilities()
    capability.setCapability("app", "")
    capability.setCapability("appPackage", "com.tencent.mm")
    capability.setCapability("appActivity", ".ui.LauncherUI")
    capability.setCapability("deviceName", "emulator-5554")
    capability.setCapability("fastReset", "false")
    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
    //capability.setCapability("unicodeKeyboard", "true")
    capability.setCapability("resetKeyboard", "true")
    capability.setCapability("automationName", "appium")
    capability.setCapability("platformName", "android")
    capability.setCapability("resetKeyboard", "true")
    capability.setCapability("newCommandTimeout", "600")
    //capability.setCapability("chromeAndroidProcess", "com.tencent.mm:tools")


    val options = new ChromeOptions()
//    options.setExperimentalOption("androidPackage", "com.tencent.mm")
//    options.setExperimentalOption("androidUseRunningApp", true)
//    options.setExperimentalOption("androidActivity", ".plugin.webview.ui.tools.WebViewUI")
    options.setExperimentalOption("androidProcess", "com.tencent.mm:tools")
    capability.setCapability(ChromeOptions.CAPABILITY, options)


    //val url="http://192.168.100.65:7771"
    val url = "http://127.0.0.1:4723/wd/hub"
    val driver = new AndroidDriver[WebElement](new URL(url), capability)
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
    println(driver.getPageSource)
    driver.findElementByXPath("//*[@text='我']").click
    driver.findElementByXPath("//*[@text='收藏']").click
    driver.findElementByXPath("//*[contains(@text, '美团外卖')]").click
    driver.context("WEBVIEW_com.tencent.mm:tools")
    Thread.sleep(10000)

    find(driver, By.xpath("//*[text()='美食']")).click()
    Thread.sleep(10000)
    performance(driver)
  }
  def performance(driver: RemoteWebDriver): Unit ={
    val js =
      """
        |var perf=window.performance.timing
        |var ttfb=perf.responseStart-perf.requestStart
        |var dom=perf.domContentLoadedEventEnd-perf.navigationStart
        |
        |//var array=$x('//img')
        |var array=document.getElementsByTagName('img')
        |var maxResponseEnd=0
        |for(i=0;i<array.length;i++) {
        |  if(array[i].src==undefined) continue
        |  if(array[i].src.indexOf("data:image")==0) continue
        |  rect=array[i].getBoundingClientRect()
        |  if(rect.top>screen.height || rect.width==0) continue
        |  var src=array[i].src
        |  if(performance.getEntriesByName(src).length==0) continue
        |  var responseEnd=performance.getEntriesByName(src)[0].responseEnd
        |  if(maxResponseEnd>responseEnd) continue
        |  maxResponseEnd=responseEnd;
        |  console.log(src)
        |  console.log(maxResponseEnd)
        |}
        |var load=perf.loadEventEnd-perf.navigationStart
        |console.log("TTFB, DOM, Screen, Load")
        |var result=ttfb+", "+dom+", "+maxResponseEnd+", "+load
        |console.log(result)
        |return result
        |
      """.stripMargin

    val stack=ListBuffer[String]()
    while(stack.takeRight(5).distinct.size!=1 ) {
      stack.append(driver.executeScript(js).toString)
      println(stack.last)
      Thread.sleep(1000)
    }

  }
  def find(driver: RemoteWebDriver, by: By): WebElement ={
    var head:Option[WebElement]=None
    var index=0
    do {
      println(s"index=${index} by=${by}")
      head=driver.findElements(by).asScala.toList.headOption
      index+=1
    }while(head==None && index<10)
    head match {
      case Some(w)=>w
      case None=>null
    }

  }
  test("chrome") {
    val capability = new DesiredCapabilities()
    capability.setCapability("app", "")
    //capability.setCapability("appPackage", "com.tencent.mm")
    //capability.setCapability("appActivity", ".ui.LauncherUI")
    capability.setCapability("deviceName", "emulator-5554")
    capability.setCapability("fastReset", "false")
    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
    capability.setCapability("unicodeKeyboard", "true")
    capability.setCapability("resetKeyboard", "true")
    capability.setCapability("automationName", "appium")
    capability.setCapability("platformName", "android")
    capability.setCapability("resetKeyboard", "true")
    capability.setCapability("chromeAndroidPackage", "com.tencent.mm:tools")

//    val logPrefs = new LoggingPreferences();
//    logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
//    capability.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

    println("ready")
    val driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub/"), capability)
    println("get")

    1 to 5 foreach (x => {
      driver.get("https://m.dianping.com/waimai/wxwallet#!index/source=redir")

      val js =
        """
          |var perf=window.performance.timing
          |var ttfb=perf.responseStart-perf.requestStart
          |var dom=perf.domContentLoadedEventEnd-perf.navigationStart
          |
          |//var array=$x('//img')
          |var array=document.getElementsByTagName('img')
          |var maxResponseEnd=0
          |for(i=0;i<array.length;i++) {
          |  if(array[i].src==undefined) continue
          |  if(array[i].src.indexOf("data:image")==0) continue
          |  rect=array[i].getBoundingClientRect()
          |  if(rect.top>screen.height || rect.width==0) continue
          |  var src=array[i].src
          |  if(performance.getEntriesByName(src).length==0) continue
          |  var responseEnd=performance.getEntriesByName(src)[0].responseEnd
          |  if(maxResponseEnd>responseEnd) continue
          |  maxResponseEnd=responseEnd;
          |  console.log(src)
          |  console.log(maxResponseEnd)
          |}
          |var load=perf.loadEventEnd-perf.navigationStart
          |console.log("TTFB, DOM, Screen, Load")
          |var result=ttfb+", "+dom+", "+maxResponseEnd+", "+load
          |console.log(result)
          |return result
          |
      """.stripMargin
      val result = driver.executeScript(js)
      println(result)
    })

  }


  test("test weixin h5 2") {

    val capability = new DesiredCapabilities()
    capability.setCapability("app", "")
    capability.setCapability("appPackage", "com.tencent.mm")
    capability.setCapability("appActivity", ".ui.LauncherUI")
    capability.setCapability("deviceName", "emulator-5554")
    capability.setCapability("fastReset", "false")
    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
    //capability.setCapability("unicodeKeyboard", "true")
    capability.setCapability("resetKeyboard", "true")
    capability.setCapability("automationName", "appium")
    capability.setCapability("platformName", "android")
    capability.setCapability("resetKeyboard", "true")

    capability.setCapability("chromeAndroidPackage", "com.tencent.mm:tools")
    //capability.setCapability("autoWebview", "true")

    //val url="http://192.168.100.65:7771"
    val url = "http://127.0.0.1:4723/wd/hub"
    val driver = new AndroidDriver[WebElement](new URL(url), capability)
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
    println(driver.getPageSource)
    driver.findElementByXPath("//*[@text='登录']").click

    Thread.sleep(5000)
    println(driver.getContextHandles)
    println(driver.getPageSource)
    driver.findElementByXPath("//*[@text='登录遇到问题']").click

    //driver.context("WEBVIEW_com.sankuai.xmpp")

    driver.context("WEBVIEW_com.tencent.mm:tools")

    println(driver.getPageSource)
    driver.findElementsByXPath("//*").asScala.foreach(x => println(x.getText))


    //driver.context("WEBVIEW_com.tencent.mm:tools")


    //println(driver.getPageSource)
    //driver.findElementsByXPath("//*").foreach(x=>println(x.getText))


  }


  test("test chromedriver weixin") {
    val options = new ChromeOptions()
    options.setExperimentalOption("androidPackage", "com.tencent.mm")
    options.setExperimentalOption("androidUseRunningApp", true)
    options.setExperimentalOption("androidActivity", ".plugin.webview.ui.tools.WebViewUI")
    options.setExperimentalOption("androidProcess", "com.tencent.mm:tools")
    val capability = DesiredCapabilities.chrome()
    capability.setCapability(ChromeOptions.CAPABILITY, options)
    val url = "http://127.0.0.1:8000/wd/hub"
    val driver = new AndroidDriver[WebElement](new URL(url), capability)
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
    //driver.get("http://www.baidu.com")

    println(driver.getPageSource)
    driver.findElementsByXPath("//button").asScala.foreach(x => println(x.getText))
    driver.findElementByXPath("//*[text()='美食']").click
    println(driver.getPageSource)
    driver.findElementsByXPath("//title").asScala.foreach(x => println(x.getText))
    driver.quit()

  }

  test("测试微信小程序") {

    val capability = new DesiredCapabilities()
    capability.setCapability("app", "")
    capability.setCapability("appPackage", "com.tencent.mm")
    capability.setCapability("appActivity", ".ui.LauncherUI")
    capability.setCapability("deviceName", "emulator-5554")
    capability.setCapability("fastReset", "false")
    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
    //capability.setCapability("unicodeKeyboard", "true")
    //capability.setCapability("resetKeyboard", "true")
    capability.setCapability("automationName", "appium")
    capability.setCapability("platformName", "android")
    capability.setCapability("resetKeyboard", "true")
    capability.setCapability("newCommandTimeout", "600")
    //capability.setCapability("chromeAndroidProcess", "com.tencent.mm:tools")


    val options = new ChromeOptions()
    //    options.setExperimentalOption("androidPackage", "com.tencent.mm")
    //    options.setExperimentalOption("androidUseRunningApp", true)
    //    options.setExperimentalOption("androidActivity", ".plugin.webview.ui.tools.WebViewUI")
    options.setExperimentalOption("androidProcess", "com.tencent.mm:appbrand1")
    capability.setCapability(ChromeOptions.CAPABILITY, options)


    //val url="http://192.168.100.65:7771"
    val url = "http://127.0.0.1:4723/wd/hub"
    val driver = new AndroidDriver[WebElement](new URL(url), capability)
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
    println(driver.getPageSource)
    driver.findElementByXPath("//*[@text='发现']").click
    driver.findElementByXPath("//*[@text='小程序']").click
    driver.findElementByXPath("//*[contains(@text, '美团外卖')]").click
    println(driver.getContextHandles)
    driver.context("WEBVIEW_com.tencent.mm:tools")
    Thread.sleep(5000)
    println(driver.getPageSource)
//    driver.findElementsByXPath("//*").foreach(x=>{
//      println(x.getTagName)
//      println(x.getText)
//    })
    find(driver, By.xpath("//*[contains(@url, '美食')]")).click()
    find(driver, By.xpath("//*[contains(., '金百万')]")).click()
    //performance(driver)
  }



}