package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppium

/**
  * Created by seveniruby on 16/5/21.
  */
class TestIOS extends MiniAppium {
  override def beforeAll(): Unit = {
    //start()
    config("app", "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/" +
      "Build/Products/Debug-iphonesimulator/Snowball.app")
    config("udid", "")
    config("deviceName", "iPhone 6 Plus")
    config("platformVersion", "9.3")
    config("bundleId", "com.xueqiu")
    config("fullReset", "false")
    config("noReset", "true")
    config("autoAcceptAlerts", "true")
    config("screenshotWaitTimeout", "10")
    config("newCommandTimeout", "120")
    appium()
    //todo: do something like login
    quit()
  }

  override def beforeEach(): Unit = {
    config("app", "")
    config("fullReset", "false")
    appium()
  }

  test("test android simulator") {
    see("user_profile_icon").tap()
    see("screen_name").nodes.head("text") should equal("huangyansheng")
    log.info(nodes().head)
    see("screen_name").nodes.last("text") should be("huangyansheng")
    see("screen_name").attribute("text") shouldBe "huangyansheng"
    see("screen_name")("text") shouldEqual "huangyansheng"
  }

  test("自选") {
    swipe("down")
    see("自选").tap
    see("自选").shot("自选.element.png")
    see("港股").shot("港股.element.png")
    see("沪深").shot("沪深.element.png")
    see("交易").shot("交易.element.png")
    see("雪球100").tap
    swipe("down")
    see("stock_current_price")("text").toDouble should be > 1000.0
  }
  test("自选遍历"){
    swipe("down")
    see("自选").tap.shot()
    crawl(conf="src/universal/conf/xueqiu.json", maxDepth = 2)
  }

  test("iOS 8.3 test2"){
    swipe("down")
    crawl(conf="src/universal/conf/xueqiu.json", maxDepth = 4)
  }

  override def afterEach: Unit = {
    quit()
  }

  override def afterAll(): Unit = {
    //stop()
  }

}
