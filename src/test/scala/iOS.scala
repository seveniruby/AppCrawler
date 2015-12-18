import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/12/15.
  */
class iOS extends FunSuite{
  test("iOS"){
    val appium=new IOSTraversal
    //ipa需要开发者证书签名
    //appium.setupApp("http://build.snowballfinance.com/static/apps/com.xueqiu.ios.rc/20151218_124401/xueqiu.ipa")
    //appium.setupApp("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphonesimulator/Snowball.app")
    //appium.setupApp("/Users/seveniruby/Downloads/xueqiu_7.3-rc-588.ipa")
    appium.setupApp("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphoneos/Snowball.app")

    appium.rule("请输入手机号或邮箱登录", "15600534760")
    appium.rule("密码", "hys2xueqiu")
    appium.rule("登 录", "click")
    appium.rule("点此进入消息通知中心", "click")
    appium.rule("点此访问个人主页进行应用设置", "click")
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click")
    appium.rule("关闭", "click")
    appium.rule("取消", "click")
    appium.rule("Cancel", "click")
    appium.rule("不保存", "click")
    appium.rule("好", "click")
    appium.rule("确定", "click")
    //appium.rule("自选", "click")
    appium.back("nav_icon_back")
    appium.black("seveniruby", "message", "消息", "弹幕", "发射", "Photos","地址", "网址", "发送", "拉黑", "举报",
      "camera","Camera", "点评", "nav_icon_home", "点评", "评论", "回复", "咨询", "分享", "转发")

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
