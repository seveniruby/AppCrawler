import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/12/15.
  */
class Android extends XueqiuTraversal {
  override def setupAppium(): Traversal = {
    val appium = new AndroidTraversal
    //    val android=appium.setupApp("http://qaci.snowballfinance.com/view/Snowball-Android/job/snowball-droid-rc/lastSuccessfulBuild/artifact/snowball/build/outputs/apk/xueqiu.apk",
    //      "http://127.0.0.1:4730/wd/hub")
    appium.setupApp("/Users/seveniruby/Downloads/xueqiu_7.3-rc-658.apk",
      "http://127.0.0.1:4730/wd/hub")
    Thread.sleep(5000)

    appium.rule("account", "15600534760")
    appium.rule("password", "hys2xueqiu")
    appium.rule("button_next", "click")
    appium.rule("点此进入消息通知中心", "click")
    appium.rule("点此访问个人主页进行应用设置", "click")
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click")

    //优先点击列表
    appium.first("//android.widget.ListView//android.widget.TextView")
    //其次是排序按钮
    appium.first("//android.widget.ListView//android.widget.Button")
    //最后是大类
    appium.last("//android.widget.TextView[@text='港股']")
    appium.last("//android.widget.TextView[@text='美股']")
    appium.last("//android.widget.TextView[@text='沪深']")
    appium.last("//android.widget.TextView[@text='持仓']")
    appium.urlXPath = "//*[contains(@resource-id, '_title')]"
    appium.baseUrl=".*Main.*"
    return appium
  }


  test("自选") {
    val t = setupAppium()
    t.black("自定义", "action_sort", "tabhost", "tabs", "stock_item_.*", ".*message.*")
    t.blackUrlList.append("正文页", "个人首页", "动态", "消息")
    subTraversal(t, "自选")
  }

  test("动态") {
    val t = setupAppium()
    t.black("自定义", "action_sort", "tabhost", "tabs", "stock_item_.*", ".*message.*")
    t.last("//*[contains(@resource-id,'group_header_view')]//android.widget.TextView")
    t.blackUrlList.append("个人首页", "消息")
    subTraversal(t, "动态")
  }

  test("组合") {
    val t = setupAppium()
    t.black("tabhost", "tabs", "stock_item_.*", ".*message.*")
    t.blackUrlList.append("正文页", "个人首页", "动态", "消息")
    subTraversal(t, "组合")
  }

  test("交易") {
    val t = setupAppium()
    t.black("tabhost", "tabs", "stock_item_.*", ".*message.*")
    t.blackUrlList.append("正文页", "个人首页", "动态", "消息")
    subTraversal(t, "交易")
  }
  test("首页") {
    val t = setupAppium()
    subTraversal(t, "首页")
  }

  test("首页 depth=2") {
    val t = setupAppium()
    t.maxDepth = 2
    t.baseUrl = ".*Main.*"
    subTraversal(t, "首页")
  }

}
