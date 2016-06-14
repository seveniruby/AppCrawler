package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppium
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/6/3.
  */
class TestWebDriverAgent extends MiniAppium{
  test("test facebook webdriver"){
    config("app", "/Users/seveniruby/projects/snowball-ios/DerivedData/Snowball/Build/Products/Debug-iphonesimulator/Snowball.app")
    config("bundleId", "com.xueqiu")
    config("fullReset", "true")
    config("noReset", "true")
    appium("http://192.168.100.40:8100")

    println(driver.getPageSource)
    see("选股策略").tap()
    quit()
  }

}
