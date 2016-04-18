import org.scalatest.Tag
import org.scalatest.time.{Seconds, Span}

/**
  * Created by seveniruby on 16/3/26.
  */
class TestSelenium extends AppiumDSL {

  override def beforeAll(): Unit = {
    //config("app", "/Users/seveniruby/Downloads/xueqiu.apk")

  }

  def login(): Unit = {
    Thread.sleep(5000)
    click on see("account")
    click on id("account")
    send("15600534760")
    save()
    click on id("password")
    send("hys2xueqiu")
    save
    click on id("button_next")
    save
    click on id("tip_step_one")
    click on id("tip_step_two")
    click on id("tip_step_three")
    save
  }

  test("搜索") {
    println(pageSource)
    click on id("home_search")
    save
    println(pageSource)
    click on see("//android.widget.EditText")
    send("zxzq")
    save
    Thread.sleep(2000)
    println(pageSource)
    save
    click on see("SH600030")
    save
  }

  test("股票行情") {
    click on see("自选")
    save
    click on see("股票")
    Thread.sleep(1000)
    save
    click on see("大港股份")
    click on see("分时")
    click on see("5日")
    click on see("日K")
    click on see("周K")
    click on see("月K")

    click on see("新贴")
    click on see("热贴")
    click on see("新闻")
    click on see("公告")
    click on see("球友")
  }

  test("添加组合") {
    tree()
    tree("自选")
    click on see("自选")
    tree()
    click on see("组合")
    tree("Image")
    click on see("create")
    click on see("好名字")
    send("seveniruby")
    click on see("下一")
    click on see("美")
    click on see("马上")
    click on see("输入")
    send("alibaba")
    click on see("BABA")
    tree("Image")
    tree("Button")
    tree("TextView")
    tree()
    click on see("done")

    click on see("马上")
    click on see("输入")
    send("dangdang")
    click on see("DANG")

    tree("Image")
    tree("Button")
    tree("TextView")
    tree()
    Thread.sleep(1000)
    click on see("done")
    Thread.sleep(1000)
    click on see("(//*[@resource-id='com.xueqiu.android:id/icon_adjust'])[1]")
    click on see("100")
    click on see("99")
    click on see("98")
    click on see("97")
    click on see("96")
    click on see("确定")

    tree()
    click on see("(//*[@resource-id='com.xueqiu.android:id/icon_adjust'])[2]")
    click on see("1")
    click on see("2")
    click on see("确定")

    click on see("创建")
    click on see("不了")

  }

  test("股票") {
    click on see("自选")
    tree("//android.widget.ImageView")
    click on see("股票")
    tree("//android.widget.ImageView")
    click on see("大港股份")
    tree()
    tree("action_bar_title")("text") should be equals ("大港股份")
    log.warn("Start Crawler")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl")
  }

  test("组合") {
    tree("组合")
    click on see("//UIATabBar/UIAButton[@name=\"组合\"]")
    click on see("组合风云榜")
    click on see("持仓")
    Thread.sleep(2000)
    tree("股票")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl")
  }

  test("7.6.1股票") {
    click on see("自选")
    click on see("雪球100")
    tree()
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl4")


  }

  test("登录", Tag("7.7"), Tag("iOS")) {
    click on see("手机号")
    send("15600534760")
    click on see("//UIASecureTextField")
    send("hys2xueqiu")
    click on see("登 录")
    click on see("//UIAButton[@path=\"/0/0/3/5\"]")
    tree("seveniruby")("name") should be equals "seveniruby"
    log.info("tree first")
    tree()
    log.info("crawl")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl4")
    log.info("tree sxx")
    tree()
  }

  test("登录driver版本", Tag("7.7"), Tag("iOS")) {
    click on see("手机号")
    send("15600534760")
    click on see("//UIASecureTextField")
    send("hys2xueqiu")
    click on see("登 录")
    driver.findElementByXPath("//UIAButton[@path=\"/0/0/3/5\"]").click()
    tree("seveniruby")("name") should be equals "seveniruby"
    log.info("tree first")
    tree()
    log.info("crawl")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl4")
    log.info("tree sxx")
    tree()
  }

  test("登录验证ipad", Tag("7.7"), Tag("iOS")) {
    iOS()
    click on see("手机号")
    send("15600534760")
    click on see("//UIASecureTextField")
    send("hys2xueqiu")
    click on see("登 录")
    tree()
    tree("//UIAButton")
    //ipad和iphone的path并不一致
    click on see("//UIAButton[@path=\"/0/0/0/5\"]")
    tree("seveniruby")("name") should be equals "seveniruby"
  }


  test("登录验证iphone", Tag("7.7"), Tag("iOS")) {
    iOS(true)
    appium()
    tree()
    tree("//UIAButton")
    click on see("手机号")
    send("15600534760")
    click on see("//UIASecureTextField")
    send("hys2xueqiu")
    click on see("登 录")
    click on see("//UIAButton[@path=\"/0/0/3/5\"]")
    tree("seveniruby")("name") should be equals "seveniruby"
  }

  override def afterAll(): Unit = {
    println("afterall")
    //driver.removeApp(capabilities.getCapability("bundleId").toString)
    quit()
  }

}
