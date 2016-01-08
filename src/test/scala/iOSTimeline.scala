

/**
  * Created by seveniruby on 15/12/15.
  */
class iOSTimeline extends XueqiuTraversal{
  override def setupAppium(): Crawler ={
    val appium=new IOSCrawler
    //ipa需要开发者证书签名
    //appium.setupApp("http://build.snowballfinance.com/static/apps/com.xueqiu.ios.rc/20151218_124401/xueqiu.ipa")
    appium.setupApp("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphonesimulator/Snowball.app")
    //appium.setupApp("/Users/seveniruby/Downloads/xueqiu_7.3-rc-588.ipa")
    //appium.setupApp("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphoneos/Snowball.app")

    appium.rule("已有帐号？立即登录", "click")
    appium.rule("请输入手机号或邮箱登录", "15600534760", 1)
    appium.rule("密码", "hys2xueqiu", 1)
    appium.rule("登 录", "click", 1)
    appium.rule("点此进入消息通知中心", "click", 1)
    appium.rule("点此访问个人主页进行应用设置", "click", 1)
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click", 1)
    appium.rule("common guide icon ok", "click", 1)
    appium.back("//*[@name='nav_icon_back']")
    appium.back("//UIAButton[@name='取消']")
    appium.back("//UIAButton[@name='Cancel']")
    appium.back("//UIAButton[@name='关闭']")
    appium.back("//*[@value='首页']")
    appium.back("//UIAButton[@name='首页']")
    appium.conf.baseUrl=".*SNBHomeView.*"

    //把列表挨个点击一遍
    appium.first("//UIAWindow[1]//UIATableView//UIATableCell[@visible='true' and @enabled='true' and @valid='true' and @name!='']")
    //日k, 月k和三年等h5页面
    appium.first("//UIAWindow[1]//UIAStaticText//UIATableCell[@dom!='' and @enabled='true' and @valid='true' and @name!='']")

    return appium
  }


  test("首页 depth=3"){
    val t=setupAppium()
    t.conf.maxDepth=3
    t.conf.baseUrl=".*SNBHomeView.*"
    subTraversal(t, "首页")
  }
}
