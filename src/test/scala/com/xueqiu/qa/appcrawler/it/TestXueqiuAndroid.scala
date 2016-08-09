package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppiumSuite

/**
  * Created by seveniruby on 16/7/20.
  */
class TestXueqiuAndroid extends MiniAppiumSuite{
  override def beforeAll(): Unit ={
    //start()
    config("app", "/Users/seveniruby/Downloads/xueqiu_8.3-3.apk")
    config("appPackage", "com.xueqiu.android")
    config("appActivity", ".view.WelcomeActivityAlias")
    config("fullReset", "false")
    config("noReset", "false")
    config("unicodeKeyboard", true)
    appium("http://127.0.0.1:4723/wd/hub")
  }
  override def afterAll(): Unit ={
    quit()
  }
  test("首页"){
    crawl(conf="src/universal/conf/xueqiu.json", maxDepth = 10)
  }

  test("交易"){
    see("交易").tap()
    see("交易记录").tap()
    crawl(maxDepth = 2)

  }
  test("行情"){
    see("搜索股票").tap
    send("alibaba")
    see("BABA").tap
  }

}
