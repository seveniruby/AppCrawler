import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/11/28.
  */
class Traversal extends FunSuite{
  test("travel"){
    val appium=new XueqiuAppium
    val android=appium.setupAndroid("/Users/seveniruby/Downloads/xueqiu_rc_722.apk")
    appium.rule("LoginActivity.account", "15600534760")
    appium.rule("LoginActivity.password", "hys2xueqiu")
    appium.rule("LoginActivity.button_next", "click")
    //appium.rule("edit_text_name_cube", "ZuHe")
    appium.traversal()
    println("clcikedList=")
    println(appium.clickedList.mkString("\n"))
    println("elements=")
    println(appium.elements.mkString("\n"))
  }

}
