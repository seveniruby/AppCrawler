

/**
  * Created by seveniruby on 15/12/15.
  */
class AndroidTimeline extends XueqiuTraversal {
  override def setupAppium(): Crawler = {
    val appium = new AndroidCrawler
    //    val android=appium.setupApp("http://qaci.snowballfinance.com/view/Snowball-Android/job/snowball-droid-rc/lastSuccessfulBuild/artifact/snowball/build/outputs/apk/xueqiu.apk",
    //      "http://127.0.0.1:4730/wd/hub")
    appium.setupApp("http://build.snowballfinance.com/static/apps/com.xueqiu.droid.test/20160106_163812/xueqiu.apk",
      "http://127.0.0.1:4730/wd/hub")
    Thread.sleep(5000)

    appium.rule("已有帐号？立即登录", "click")
    appium.rule("登录", "click")
    appium.rule("account", "15600534760")
    appium.rule("password", "hys2xueqiu")
    appium.rule("button_next", "click")
    appium.rule("点此进入消息通知中心", "click")
    appium.rule("点此访问个人主页进行应用设置", "click")
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click")


    appium.black("tabhost", "tabs", "stock_item_.*", ".*message.*")

    //优先点击列表
    appium.first("//android.widget.ListView//android.widget.TextView")
    //其次是排序按钮
    appium.first("//android.widget.ListView//android.widget.Button")
    //最后是大类
    appium.last("//*[contains(@resource-id,'group_header_view')]//android.widget.TextView")
    appium.conf.defineUrl = "//*[contains(@resource-id, '_title')]"
    appium.conf.baseUrl=".*Main.*"
    return appium
  }



  test("首页 depth=3") {
    val t = setupAppium()
    t.conf.maxDepth = 3
    subTraversal(t, "首页")
  }

}
