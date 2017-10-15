package com.testerhome.appcrawler.it

import java.net.URL

import com.testerhome.appcrawler.AppCrawler
import io.appium.java_client.android.{AndroidDriver, AndroidElement}
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.FunSuite

class TestXueQiu extends FunSuite{
  val capability=new DesiredCapabilities()
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

  test("all app "){
    capability.setCapability("app", "")
    capability.setCapability("appPackage", "com.xueqiu.android")
    capability.setCapability("appActivity", ".view.WelcomeActivityAlias")
    val driver=new AndroidDriver[AndroidElement](new URL("http://127.0.0.1:4723/wd/hub"), capability)

  }

  test("appcrawler"){
    AppCrawler.main(Array("-c", "src/test/scala/com/testerhome/appcrawler/it/xueqiu_private.yml",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }

}
