import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/12/15.
  */
class TestIOSTraversal extends FunSuite{
  test("iOS"){
    val appium=new IOSTraversal
    //val android=appium.setupIOS("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphoneos/Snowball.app")
    appium.setupIOS("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphonesimulator/Snowball.app")
    //appium.setupIOS("/Users/seveniruby/Downloads/xueqiu_7.3-rc-588.ipa")


    appium.rule("请输入手机号或邮箱登录", "15600534760")
    appium.rule("密码", "hys2xueqiu")
    appium.rule("登 录", "click")
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

    //appium.rule("自选", "click")
    appium.back("nav_icon_back")
    appium.black("message")
    appium.black("消息")
    appium.black("弹幕")
    appium.black("发射")
    appium.black("Photos")
    appium.black("地址")
    appium.black("网址")
    appium.black("发送")
    appium.black("拉黑")
    appium.black("举报")
    appium.black("camera")
    appium.black("Camera")
    appium.black("Moments")

    //把列表挨个点击一遍
    appium.first("//UIAWindow[1]//UIATableView//UIATableCell[@visible='true' and @enabled='true' and @valid='true' and @name!='']")
    //日k, 月k和三年等h5页面
    appium.first("//UIAWindow[1]//UIAStaticText//UIATableCell[@dom!='' and @enabled='true' and @valid='true' and @name!='']")

    //appium.rule("edit_text_name_cube", "ZuHe")
    appium.traversal()
    println("clcikedList=")
    println(appium.clickedList.mkString("\n"))
    println("elements=")
    println(appium.elements.mkString("\n"))

  }


}
