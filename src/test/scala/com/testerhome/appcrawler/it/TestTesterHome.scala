package com.ceshiren.appcrawler.it

import java.net.URL

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest._

import scala.collection.JavaConversions._

/**
  * Created by seveniruby on 2017/6/6.
  */
class TestTesterHome extends FunSuite with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  val capabilities=new DesiredCapabilities()
  capabilities.setCapability("deviceName", "emulator-5554")
  capabilities.setCapability("app", "/Users/seveniruby/Downloads/app-release.apk_1.1.0.apk")
  capabilities.setCapability("appPackage", "com.ceshiren.nativeandroid")
  capabilities.setCapability("appActivity", ".views.MainActivity")
  capabilities.setCapability("unicodeKeyboard", "true")

  var driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub/"), capabilities)

  override def beforeEach(): Unit = {
    capabilities.setCapability("app", "")
    driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub/"), capabilities)
    Thread.sleep(3000)
    verbose()

  }

  def verbose(): Unit ={
    println()
    println(driver.currentActivity())
    println(driver.getPageSource)
  }

  test("招聘"){
    driver.findElementByXPath("//*[@content-desc='Open navigation drawer']").click()
    driver.findElementByXPath("//*[@text='招聘']").click()
    driver.getContextHandles.foreach(println)
    verbose()
    driver.findElementsByXPath("//*[@text='欢迎报名第三届中国移动互联网测试开发大会']").size() should be >=1
  }
  test("精华帖"){
    driver.findElementByXPath("//*[@content-desc='Open navigation drawer']").click()
    driver.findElementByXPath("//*[@text='社区']").click()
    //等待动画切换完成
    Thread.sleep(3000)
    driver.findElementByXPath("//*[@text='精华']").click()
    driver.findElementByXPath("//*[contains(@text, '王者荣耀')]").click()
    driver.findElementByXPath("//*[contains(@text, '评论')]").click()
    driver.findElementsByXPath("//*[@text='恒温']").size() should be >=1
  }

  override def afterEach(): Unit = {
    driver.quit()
  }
}
