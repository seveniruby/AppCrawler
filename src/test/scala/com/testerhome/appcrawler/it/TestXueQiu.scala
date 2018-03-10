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
    Thread.sleep(30000)

  }

  test("appcrawler"){
    AppCrawler.main(Array("-c", "src/test/scala/com/testerhome/appcrawler/it/xueqiu_private.yml",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }

  test("appcrawler base example"){
    AppCrawler.main(Array(
      "-c", "src/test/scala/com/testerhome/appcrawler/it/xueqiu_automation.yml",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }

  test("appcrawler base example ios"){

    val app = "/Users/seveniruby/projects/ios-uicatalog/build/Debug-iphonesimulator/UICatalog.app"
    AppCrawler.main(Array("-c", "src/test/scala/com/testerhome/appcrawler/it/xueqiu_base.yml",
      "-a", app,
      //"-a", "/Users/seveniruby/projects/snowball-ios/DerivedData/Snowball/Build/Products/Debug-iphonesimulator/Snowball.app",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }

  test("test automation"){
    AppCrawler.main(Array("-c", "src/test/scala/com/testerhome/appcrawler/it/xueqiu_automation.yml",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }


  test("test default crawler"){
    AppCrawler.main(Array(
      "--capability", "appPackage=com.xueqiu.android,appActivity=.view.WelcomeActivityAlias,dontStopAppOnReset=true",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }

  test("test sikuli"){
    AppCrawler.main(Array("-c", "src/test/scala/com/testerhome/appcrawler/it/xueqiu_sikuli.yml",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }
  test("test xiaomi"){
    AppCrawler.main(Array("-c", "src/tes" +
      "" +
      "t/scala/com/testerhome/appcrawler/it/xiaomi.yml",
      "-o", s"/tmp/xiaomi/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }

  test("xiaomi click"){
    capability.setCapability("app", "")
    capability.setCapability("appPackage", "com.xueqiu.android")
    capability.setCapability("appActivity", ".view.WelcomeActivityAlias")
    val driver=new AndroidDriver[AndroidElement](new URL("http://127.0.0.1:4723/wd/hub"), capability)

  }




}
