
import org.scalatest.FunSuite

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by seveniruby on 15/11/28.
  */
class Traversal extends FunSuite{
  test("travel"){
    val appium=new XueqiuAppium
    val android=appium.setupAndroid("/Users/seveniruby/Downloads/xueqiu_rc_723.apk")
    appium.rule("LoginActivity.account", "15600534760")
    appium.rule("LoginActivity.password", "hys2xueqiu")
    appium.rule("LoginActivity.button_next", "click")
    appium.rule("WriteStatusActivity.不保存", "click")
    //appium.rule("edit_text_name_cube", "ZuHe")
    appium.traversal()
    println("clcikedList=")
    println(appium.clickedList.mkString("\n"))
    println("elements=")
    println(appium.elements.mkString("\n"))
  }

  test("test freemind"){
    val appium=new XueqiuAppium
    appium.generateFreeMind(ListBuffer(
      ELement("com/a","","1",""),
      ELement("com/a","","2",""),
      ELement("abc/a","","3",""),
      ELement("com/b","","11",""),
      ELement("abc/b","","12",""),
      ELement("com/b","","13",""),
      ELement("abc/c","","21","")
    ))

    val click=ListBuffer[ELement]()
    Source.fromFile("clicked.log").getLines().foreach(line=>{
      val arr=line.split(',')
      if (arr.length==3) {
        val url = arr(0)
        val id = arr(1)
        val text = arr(2)
        click.append(ELement(url,"",id,text))
      }
    })

    appium.generateFreeMind(click)



  }

}
