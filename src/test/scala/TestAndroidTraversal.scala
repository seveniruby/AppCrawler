import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/12/15.
  */
class TestAndroidTraversal extends FunSuite{
  test("Android"){
    val appium=new AndroidTraversal
    val android=appium.setupAndroid("/Users/seveniruby/Downloads/xueqiu_7.3-rc-655.apk")

    appium.rule("account", "15600534760")
    appium.rule("password", "hys2xueqiu")
    appium.rule("button_next", "click")
    appium.rule("不保存", "click")
    appium.rule("点此进入消息通知中心", "click")
    appium.rule("点此访问个人主页进行应用设置", "click")
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click")
    appium.rule("取消", "click")
    appium.rule("关闭", "click")
    appium.rule("分时", "click")
    appium.rule("5日", "click")
    appium.rule("日k", "click")
    appium.rule("月k", "click")
    appium.rule("好", "click")


    //appium.rule("edit_text_name_cube", "ZuHe")
    appium.traversal()
    println("clcikedList=")
    println(appium.clickedList.mkString("\n"))
    println("elements=")
    println(appium.elements.mkString("\n"))
  }
}
