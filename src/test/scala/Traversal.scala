import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/11/28.
  */
class Traversal extends FunSuite{
  test("travel"){
    val appium=new XueqiuAppium
    val android=appium.setupAndroid()
    appium.rule("account", "15600534760")
    appium.rule("password", "hys2xueqiu")
    appium.rule("button_next", "click")
    appium.rule("自选", "click")
    appium.traversal()
    println("clcikedList=")
    println(appium.clickedList.mkString("\n"))
    println("elements=")
    println(appium.elements.mkString("\n"))
  }

}
