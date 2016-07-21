import java.net.URL

import com.xueqiu.qa.appcrawler.MiniAppium
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.{MobileElement, MobileDriver}
import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.{RemoteWebDriver, DesiredCapabilities}
import org.scalatest.Tag
import org.scalatest.time.{Seconds, Span}

/**
  * Created by seveniruby on 16/3/26.
  */
class TestSelenium extends MiniAppium {

  override def beforeAll(): Unit = {
    //config("app", "/Users/seveniruby/Downloads/xueqiu.apk")

  }

  def login(): Unit = {
    Thread.sleep(5000)
    see("account").tap()
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
    see("//android.widget.EditText").tap()
    send("zxzq")
    save
    Thread.sleep(2000)
    println(pageSource)
    save
    see("SH600030").tap
    save
  }

  test("股票行情") {
    see("自选").tap()
    save
    see("股票").tap
    Thread.sleep(1000)
    save
    see("大港股份").tap
    see("分时").tap
    see("5日").tap
    see("日K").tap
    see("周K").tap
    see("月K").tap

    see("新贴").tap
    see("热贴").tap
    see("新闻").tap
    see("公告").tap
    see("球友").tap
  }

  test("添加组合") {
    config("app", "")
    config("appPackage", "com.xueqiu.android")
    config("appActivity", "com.xueqiu.android.view.WelcomeActivityAlias")
    config("deviceName", "demo")
    config("app", "/Users/seveniruby/Downloads/xueqiu.apk")
    appium()
    sleep(5)
    login()
    tree()
    tree("自选")
    see("自选").tap
    tree()
    see("组合").tap
    tree("Image")
    see("create").tap
    see("好名字").tap
    send("seveniruby")
    see("下一").tap
    see("美").tap
    see("马上").tap
    see("输入").tap
    send("alibaba")
    sleep(5)
    see("BABA").tap
    tree("Image")
    tree("Button")
    tree("TextView")
    tree()
    see("done").tap

    see("马上").tap
    see("输入").tap
    send("dangdang")
    see("DANG").tap

    tree("Image")
    tree("Button")
    tree("TextView")
    tree()
    see("done").tap
    sleep(3)
    tree("icon")
    see("(//*[@resource-id='com.xueqiu.android:id/icon_adjust'])[1]").tap
    see("100").tap
    see("99").tap
    see("98").tap
    see("97").tap
    see("96").tap
    see("确定").tap

    tree()
    see("(//*[@resource-id='com.xueqiu.android:id/icon_adjust'])[2]").tap
    see("1").tap
    see("2").tap
    see("确定").tap

    see("创建").tap
    see("不了").tap

  }

  test("股票") {
    see("自选").tap
    tree("//android.widget.ImageView")
    see("股票").tap
    tree("//android.widget.ImageView")
    see("大港股份").tap
    tree()
    tree("action_bar_title")("text") should be equals ("大港股份")
    log.warn("Start com.xueqiu.qa.appcrawler.Crawler")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl")
  }

  test("组合") {
    tree("组合")
    see("//UIATabBar/UIAButton[@name=\"组合\"]").tap
    see("组合风云榜").tap
    see("持仓").tap
    Thread.sleep(2000)
    tree("股票")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl")
  }

  test("7.6.1股票") {
    see("自选").tap
    see("雪球100").tap
    tree()
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl4")


  }

  test("登录", Tag("7.7"), Tag("iOS")) {
    see("手机号").tap
    send("15600534760")
    see("//UIASecureTextField").tap
    send("hys2xueqiu")
    see("登 录").tap
    see("//UIAButton[@path=\"/0/0/3/5\"]").tap
    tree("seveniruby")("name") should be equals "seveniruby"
    log.info("tree first")
    tree()
    log.info("crawl")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl4")
    log.info("tree sxx")
    tree()
  }

  test("登录driver版本", Tag("7.7"), Tag("iOS")) {
    see("手机号").tap
    send("15600534760")
    see("//UIASecureTextField").tap
    send("hys2xueqiu")
    see("登 录").tap
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
    val app = if (false) {
      "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/" +
        "Build/Products/Debug-iphonesimulator/Snowball.app"
    } else {
      "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/" +
        "Build/Products/Debug-iphoneos/Snowball.app"
    }
    config("app", app)
    config("bundleId", "com.xueqiu")
    config("fullReset", true)
    config("noReset", false)
    config("deviceName", "iPhone 6")
    config("platformVersion", "9.2")
    config("autoAcceptAlerts", "true")

    see("手机号").tap
    send("15600534760")
    see("//UIASecureTextField").tap
    send("hys2xueqiu")
    see("登 录").tap
    tree()
    tree("//UIAButton")
    //ipad和iphone的path并不一致
    see("//UIAButton[@path=\"/0/0/0/5\"]").tap
    tree("seveniruby")("name") should be equals "seveniruby"
  }


  test("登录验证iphone", Tag("7.7"), Tag("iOS")) {
    val app = if (true) {
      "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/" +
        "Build/Products/Debug-iphonesimulator/Snowball.app"
    } else {
      "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/" +
        "Build/Products/Debug-iphoneos/Snowball.app"
    }
    config("app", app)
    config("bundleId", "com.xueqiu")
    config("fullReset", true)
    config("noReset", false)
    config("deviceName", "iPhone 6")
    config("platformVersion", "9.2")
    config("autoAcceptAlerts", "true")

    appium()
    tree()
    tree("//UIAButton")
    see("手机号").tap
    send("15600534760")
    see("//UIASecureTextField").tap
    send("hys2xueqiu")
    see("登 录").tap
    see("//UIAButton[@path=\"/0/0/3/5\"]").tap
    tree("seveniruby")("name") should be equals "seveniruby"
  }

  test("selenium demo"){
    val capability=new DesiredCapabilities()
    capability.setCapability("app", "/Users/seveniruby/Downloads/xueqiu.apk")
    capability.setCapability("appPackage", "com.xueqiu.android")
    capability.setCapability("appActivity", "com.xueqiu.android.view.WelcomeActivityAlias")
    capability.setCapability("deviceName", "demo")
    val url="http://127.0.0.1:4723/wd/hub"
    val driver=new AndroidDriver[MobileElement](new URL(url), capability)
    driver.findElementById("button_next").click()
  }

  test("appcrawler android demo "){
    config("app", "/Users/seveniruby/Downloads/xueqiu.apk")
    config("appPackage", "com.xueqiu.android")
    config("appActivity", "com.xueqiu.android.view.WelcomeActivityAlias")
    config("deviceName", "demo")
    appium("http://127.0.0.1:4723/wd/hub")
    sleep(5)
    see("account").tap
    send("15600534760")
    see("password").tap
    send("hys2xueqiu")
    see("button_next").tap
    see("tip").tap
    see("tip").tap
    see("tip").tap
    tree("Image")
    tree("seveniruby")("name") should be equals "seveniruby"

  }

  test("appcrawler ios demo "){
    config("app", "/Users/seveniruby/Downloads/xueqiu.apk")
    config("appPackage", "com.xueqiu.android")
    config("appActivity", "com.xueqiu.android.view.WelcomeActivityAlias")
    config("deviceName", "demo")
    appium("http://127.0.0.1:4723/wd/hub")
    sleep(10)
    see("手机号").send("15600534760")
    see("//UIASecureTextField").send("hys2xueqiu")
    see("登 录").tap
    see("//UIAButton[@path=\"/0/0/3/5\"]").tap
    tree("seveniruby")("name") should be equals "seveniruby"

  }

  override def afterAll(): Unit = {
    println("afterall")
    //driver.removeApp(capabilities.getCapability("bundleId").toString)
    quit()
  }

}
