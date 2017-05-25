package com.testerhome.appcrawler.it

import com.testerhome.appcrawler.AppiumSuite

/**
  * Created by seveniruby on 16/5/21.
  */
class TestAndroidSimulator extends AppiumSuite {
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

}
