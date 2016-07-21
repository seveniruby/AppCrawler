import com.xueqiu.qa.appcrawler.MiniAppium

/**
  * Created by seveniruby on 16/4/18.
  */
class TestRss extends MiniAppium{
  val userName="15600534760"
  val password="hys2xueqiu"
  override  def beforeAll(): Unit ={

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
    config("bundleId", "com.xueqiu")
    //config("udid", "4c1bd4ed1cc4089c10a5917959f6ddd804714b2a")

    //Android()

    appium()
    login()
  }

  def login(): Unit ={
    if(tree("登录").isEmpty){
      return
    }
    see("登录").tap()
    see("手机号").tap()
    send(userName)
    see("密码", 1).tap()
    send(password)
    see("登 录").tap()
  }
  test("测试swipe"){
    sleep(3)
    swipe()
    sleep(3)
    swipe("default")
    sleep(3)
    swipe()
    sleep(3)
    swipe("up")
    sleep(3)
    swipe("down")
  }
  test("未开未绑"){
    see("Image")
    see("选股策略").tap()
    swipe()
    swipe()
    swipe()
    see("情绪宝").tap()
    tree()
  }
  override def afterAll(): Unit ={
    quit
  }

}
