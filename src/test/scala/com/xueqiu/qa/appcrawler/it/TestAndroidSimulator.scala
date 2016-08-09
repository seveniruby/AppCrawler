package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppiumSuite

/**
  * Created by seveniruby on 16/5/21.
  */
class TestAndroidSimulator extends MiniAppiumSuite {
  override def beforeEach(): Unit = {
    config("appPackage", "com.xueqiu.android")
    config("appActivity", ".view.WelcomeActivityAlias")
    config("noReset", "false")
    //config("autoWebview", "true")
    appium()
  }

  test("test android simulator") {
    see("选股策略").tap()
    see("短线黑马").tap()
    see("最新价").shot("最新价.png")
    see("累计涨跌幅").shot("累计涨跌幅.png")

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
    crawl("src/universal/conf/xueqiu.conf", "~/temp/ipad_7.8.1-5_1", 6)
  }

  override def afterEach: Unit = {
    quit()
  }

  override def afterAll(): Unit = {
    //stop()
  }

}
