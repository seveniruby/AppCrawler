package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppium

/**
  * Created by seveniruby on 16/5/21.
  */
class TestIOS extends MiniAppium {
  override def beforeAll(): Unit = {
    start()
    config("app", "/Users/seveniruby/projects/snowball-ios/DerivedData/Snowball/Build/Products/Debug-iphoneos/Snowball.app")
    config("udid", "4c1bd4ed1cc4089c10a5917959f6ddd804714b2a")
    config("bundleId", "com.xueqiu")
    config("fullReset", "true")
    config("noReset", "true")
    appium()
    quit()
  }

  def login(): Unit = {
    swipe("left")
    swipe("down")
    see("输入手机号").send("13067754297")
    see("password").send("xueqiu4297")
    see("button_next").tap()
    see("tip").tap().tap().tap()
    swipe("down")
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
    see("雪球100").tap
    swipe("down")
    see("stock_current_price")("text").toDouble should be > 1000.0
  }
  test("自选遍历"){
    swipe("down")
    see("自选").tap
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/ipad_7.8.1-5_2", 2)
  }

  override def afterEach: Unit = {
    quit()
  }

  override def afterAll(): Unit = {
    stop()
  }

}
