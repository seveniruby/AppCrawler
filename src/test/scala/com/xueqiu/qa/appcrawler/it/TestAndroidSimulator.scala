package com.xueqiu.qa.appcrawler.it

import com.xueqiu.qa.appcrawler.MiniAppium
import org.scalatest.FunSuite
import scala.sys.process._
/**
  * Created by seveniruby on 16/5/21.
  */
class TestAndroidSimulator extends MiniAppium{
  test("test android simulator"){
    start()
    config("app", "/Users/seveniruby/Downloads/xueqiu.apk")
    config("appPackage", "com.xueqiu.android")
    config("appActivity", ".view.WelcomeActivityAlias")
    config("deviceName", "demo")
    appium()
    click on see("输入手机号")
    send("15600534760")
    click on see("password")
    send("hys2xueqiu")
    click on see("button_next")
    stop()
  }

}
