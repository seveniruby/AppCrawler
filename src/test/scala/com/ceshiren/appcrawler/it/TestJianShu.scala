package com.ceshiren.appcrawler.it

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest._

import java.net.URL
import scala.jdk.CollectionConverters._

/**
  * Created by seveniruby on 2017/6/6.
  */
class TestJianShu extends FunSuite with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  val capabilities=new DesiredCapabilities()
  capabilities.setCapability("deviceName", "emulator-5554")
  capabilities.setCapability("appPackage", "com.jianshu.haruki")
  capabilities.setCapability("appActivity", "com.baiji.jianshu.account.SplashScreenActivity")
  capabilities.setCapability("unicodeKeyboard", "true")

  var driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub/"), capabilities)

  override def beforeAll(): Unit ={
    capabilities.setCapability("app", "/Users/seveniruby/Downloads/Jianshu-2.3.1-17051515-1495076675.apk")
    driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub/"), capabilities)
    Thread.sleep(3000)
    verbose()
  }

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

  test("绕过登陆"){
    driver.findElementByXPath("//*[@text='跳过']").click()
    driver.findElementById("iv_close").click()
    driver.findElementsByXPath("//*[@text='登录']").size() should be >= 1
  }

  test("错误密码登录"){
    driver.findElementByXPath("//*[@text='跳过']").click()
    driver.findElementByXPath("//*[@text='已有帐户登录']").click()
    driver.findElementByXPath("//*[@text='手机或邮箱']").sendKeys("seveniruby@gmail.com")
    driver.findElementByXPath("//*[@password='true']").sendKeys("wrong")
    driver.findElementByXPath("//*[@text='登录']").click()
    verbose()
    driver.findElementsByXPath("//*[contains(@text, '错误')]").size() should be >= 1
  }

  test("随便看看"){
    driver.findElementByXPath("//*[@text='跳过']").click()
    driver.findElementByXPath("//*[@text='随便看看']").click()
    verbose()
    driver.findElementsByXPath("//*[contains(@resource-id, 'tag_flow_layout')]//*[contains(name(),'TextView')]").asScala.foreach(tag => {
      tag.click()
      Thread.sleep(1000)
      driver.findElementsByXPath("//*[@text='关注']").size() should be >=1
      driver.navigate().back()
    })
  }

  override def afterEach(): Unit = {
    driver.quit()
  }
}
