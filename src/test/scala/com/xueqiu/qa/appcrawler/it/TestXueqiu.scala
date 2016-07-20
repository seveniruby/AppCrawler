package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppium

/**
  * Created by seveniruby on 16/7/20.
  */
class TestXueqiu extends MiniAppium{
  override def beforeAll(): Unit ={
    start()
    config("appPackage", "com.xueqiu.android")
    config("appActivity", ".view.WelcomeActivityAlias")
    config("fullReset", "false")
    config("noReset", "false")
    appium()
  }
  override def afterAll(): Unit ={
    quit()
  }

  test("交易"){
    see("交易").tap()
    see("交易记录").tap()
    crawl(maxDepth = 2)

  }

}
