import com.xueqiu.qa.appcrawler.AppiumDSL

/**
  * Created by seveniruby on 16/4/18.
  */
class TestRss extends AppiumDSL{
  val userName="15600534760"
  val password="hys2xueqiu"
  override  def beforeAll(): Unit ={
    iOS(true)
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
    click on see("登录")
    click on see("手机号")
    send(userName)
    click on see("密码", 1)
    send(password)
    click on see("登 录")
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
    click on see("选股策略")
    swipe()
    swipe()
    swipe()
    click on see("情绪宝")
    tree()
  }
  override def afterAll(): Unit ={
    quit
  }

}
