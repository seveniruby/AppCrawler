import java.net.URL
import java.util

import io.appium.java_client.{TouchAction, XueqiuDriver}
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.FunSuite

import scala.collection.immutable.HashMap
import scala.collection.mutable

/**
  * Created by seveniruby on 16/1/21.
  */
class TestSwipe extends FunSuite{
  implicit var driver:AndroidDriver[WebElement]=_
  def setup() {
    val capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName", "emulator-5554");
    capabilities.setCapability("platformVersion", "4.4");
    capabilities.setCapability("appPackage", "com.xueqiu.android");
    capabilities.setCapability("autoLaunch", "true")
    capabilities.setCapability("automationName", "Appium")
    capabilities.setCapability(MobileCapabilityType.APP,
      "/Users/seveniruby/Downloads/xueqiu_7_4_4.apk")
    capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY,
      "com.xueqiu.android.view.WelcomeActivityAlias")

    //driver = new XueqiuDriver[WebElement](new URL("http://127.0.0.1:4729/wd/hub"), capabilities)
    driver = new AndroidDriver[WebElement](new URL("http://127.0.0.1:4730/wd/hub"), capabilities)





    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))
  }

  test("测试swipe"){
    setup()
    Thread.sleep(5000)
    println(driver.getPageSource())
    driver.findElementById("cancel").click()
    driver.findElementById("tip_step_one").click()
    val size = driver.manage().window().getSize
    val screenHeight = size.getHeight
    val screenWidth = size.getWidth
    println(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")

    driver.swipe((screenWidth * 0.8).toInt, (screenHeight * 0.8).toInt,
      (screenWidth * 0.2).toInt, (screenHeight * 0.2).toInt, 500)

    Thread.sleep(2000)
    (new TouchAction(driver))
      .press(screenWidth * 0.8.toInt, screenHeight * 0.8.toInt)
      .moveTo(screenWidth * 0.2.toInt, screenHeight * 0.2.toInt)
      .release()
      .perform()
/*

    Thread.sleep(2000)
    val map=new util.HashMap[String, String]()
    map.put("direction", "up")
    driver.executeScript("mobile: scroll", map)
*/


  }

}
