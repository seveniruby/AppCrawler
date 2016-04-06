import org.scalatest.time.{Seconds, Span}

/**
  * Created by seveniruby on 16/3/26.
  */
class TestSelenium extends AppiumDSL {

  override def beforeAll(): Unit ={
    config("app", "/Users/seveniruby/Downloads/xueqiu.apk")

    config("appPackage", "com.xueqiu.android")
    config("appActivity", "com.xueqiu.android.view.WelcomeActivityAlias")
    config("deviceName", "demo")
    appium("http://127.0.0.1:4730/wd/hub")
    setCaptureDir(".")
    implicitlyWait(Span(10, Seconds))
    login()
  }

  def login(): Unit ={
    Thread.sleep(5000)
    click on text("account")
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
  test("搜索"){
    println(pageSource)
    click on id("home_search")
    save
    println(pageSource)
    click on xpath("//android.widget.EditText")
    send("zxzq")
    save
    Thread.sleep(2000)
    println(pageSource)
    save
    click on text("SH600030")
    save
  }

  test("股票行情"){
    click on text("自选")
    save
    click on text("股票")
    Thread.sleep(1000)
    save
    click on text("大港股份")
    click on text("分时")
    click on text("5日")
    click on text("日K")
    click on text("周K")
    click on text("月K")

    click on text("新贴")
    click on text("热贴")
    click on text("新闻")
    click on text("公告")
    click on text("球友")
  }

  test("添加组合"){
    click on text("自选")
    printTree()
    click on text("组合")
    printTree("Image")
    click on text("create")
    click on text("好名字")
    send("seveniruby")
    click on text("下一")
    click on text("美")
    click on text("马上")
    click on text("输入")
    send("alibaba")
    click on text("BABA")
    printTree("Image")
    printTree("Button")
    printTree("TextView")
    printTree()
    click on text("done")

    click on text("马上")
    click on text("输入")
    send("dangdang")
    click on text("DANG")

    printTree("Image")
    printTree("Button")
    printTree("TextView")
    printTree()
    click on text("done")

    click on xpath("(//*[@resource-id='com.xueqiu.android:id/icon_adjust'])[1]")
    click on text("100")
    click on text("99")
    click on text("98")
    click on text("97")
    click on text("96")
    click on text("确定")

    printTree()
    click on xpath("(//*[@resource-id='com.xueqiu.android:id/icon_adjust'])[2]")
    click on text("1")
    click on text("2")
    click on text("确定")

    click on text("创建")
    click on text("不了")

  }

  test("股票"){
    click on text("自选")
    printTree("//android.widget.ImageView")
    click on text("股票")
    printTree("//android.widget.ImageView")
    click on text("大港股份")
    printTree()
    tree("action_bar_title")("text") should be equals("大港股份")
    log.warn("Start Crawler")
    crawl("/Users/seveniruby/projects/LBSRefresh/src/universal/conf/xueqiu.json", "/Users/seveniruby/temp/crawl")

  }

  override def afterAll(): Unit ={
    println("afterall")
    quit()
  }

}
