import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/12/15.
  */
class TestAndroidTraversal extends FunSuite{
  test("Android"){
    val appium=new AndroidTraversal
    val android=appium.setupAndroid("/Users/seveniruby/Downloads/xueqiu_7.3-rc-655.apk", "http://127.0.0.1:4730/wd/hub")

    appium.rule("account", "15600534760")
    appium.rule("password", "hys2xueqiu")
    appium.rule("button_next", "click")
    appium.rule("不保存", "click")
    appium.rule("点此进入消息通知中心", "click")
    appium.rule("点此访问个人主页进行应用设置", "click")
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click")
    appium.rule("取消", "click")
    appium.rule("关闭", "click")
    appium.rule("好", "click")

    //appium.back("nav_icon_back")
    appium.black("seveniruby", "message", "消息", "弹幕", "发射", "Photos","地址", "网址", "发送", "拉黑", "举报",
      "camera","Camera", "点评")

    //appium.rule("edit_text_name_cube", "ZuHe")
    appium.traversal()
    println("clcikedList=")
    println(appium.clickedList.mkString("\n"))
    println("elements=")
    println(appium.elements.mkString("\n"))
  }
}
