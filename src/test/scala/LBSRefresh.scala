import java.net.URL

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.html5.Location
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.FunSuite

/**
 * Created by seveniruby on 15/10/10.
 */
class LBSRefresh extends FunSuite {
  test("set location test") {
    val capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName","emulator-5554");
    capabilities.setCapability("platformVersion", "4.4");
    capabilities.setCapability("appPackage", "com.tencent.mobileqq");
    //capabilities.setCapability("appActivity", ".ApiDemos");
    capabilities.setCapability("autoLaunch", "false")
    val driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities)
    println(driver.currentActivity())
    driver.setLocation(new Location(39, 114, 0))
  }

}

