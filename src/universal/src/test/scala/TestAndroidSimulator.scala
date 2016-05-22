package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppium

/**
  * Created by seveniruby on 16/5/21.
  */
class TestAndroidSimulator extends MiniAppium {

  override def beforeAll(): Unit = {
    start()
    config("app",  "/Users/seveniruby/Downloads/xueqiu.apk")
    config("appPackage", "com.xueqiu.android")
    config("appActivity", ".view.WelcomeActivityAlias")
    config("fullReset", "false")
    config("noReset", "true")
    appium()
    login()
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
    config("appPackage", "com.xueqiu.android")
    config("appActivity", ".view.WelcomeActivityAlias")
    appium()
  }

  test("验证登陆用户名") {
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

  override def afterEach: Unit = {
    quit()
  }

  override def afterAll(): Unit = {
    stop()
  }

}
