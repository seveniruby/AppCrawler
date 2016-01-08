/**
 * Created by seveniruby on 15/10/15.
 */

//package org.scalatest.selenium
//package org.scalatest.selenium

import java.lang.reflect.{Modifier, Field}

import org.openqa.selenium.{WebElement, WebDriver}
import org.scalatest.selenium.WebBrowser
import scala.reflect.runtime.universe._

/*
sealed trait Query {
  def findElement(implicit driver: WebDriver): Option[org.scalatest.selenium.WebBrowser.Element] =
    try {
      Some(createTypedElement(driver.findElement(by)))
    }
    catch {
      case e: org.openqa.selenium.NoSuchElementException => None
    }


}
*/
/*

object XueqiuDriver{
  var xueqiuDriver:XueqiuDriver[WebElement]=_
  implicit def AndroidDriver2XueqiuDriver(driver: AndroidDriver[WebElement]): XueqiuDriver[WebElement] = {
    println("replace with xueqiu driver")
    this.xueqiuDriver=new XueqiuDriver[WebElement]()
    this.xueqiuDriver.driver=driver
    this.xueqiuDriver
  }

  def main(args: Array[String]) {
    val capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName", "emulator-5554");
    capabilities.setCapability("platformVersion", "4.4");
    capabilities.setCapability("appPackage", "com.xueqiu.android");
    capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, "com.xueqiu.android.view.WelcomeActivityAlias")
    //capabilities.setCapability("appActivity", ".ApiDemos");
    capabilities.setCapability("autoLaunch", "true")
    capabilities.setCapability("automationName", "Selendroid")
    capabilities.setCapability(MobileCapabilityType.APP, "/Users/seveniruby/Downloads/xueqiu.apk")

    val driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub"), capabilities)
    driver.update()
  }
}

class XueqiuDriver[T <:WebElement] {
  var driver:AndroidDriver[T]=_
  def update(): Unit ={
    typeOf[AndroidDriver[WebElement]].decls.foreach(m=>{
      println(m.name)
    })

    val rm=runtimeMirror(getClass.getClassLoader)
    val im=rm.reflect(this.driver)
    val methodx=driver.getClass.getDeclaredMethod("findElement", classOf[org.openqa.selenium.By])
    methodx.setAccessible(true)
    val method=typeOf[AndroidDriver[WebElement]].decl(TermName("findElement")).asMethod
    val m=im.reflectMethod(method)
    //因为权限无法clone
    m.clone()
    println(m)
    println(m.clone())

  }
}
*/


trait clickimp {
  var x:Any=_
  object click {
    def on(args:Any*): Unit ={
      println(args)
    }
  }
}
trait XueqiuBrowser extends WebBrowser {
  this.getClass.getDeclaredFields.foreach(m => println(m.getName))
  val clickField = this.getClass.getDeclaredField("click$module")
  clickField.setAccessible(true)
  val fieldModify = clickField.getClass.getDeclaredField("modifiers")
  fieldModify.setAccessible(true)
  fieldModify.setInt(clickField, clickField.getModifiers & ~Modifier.FINAL)
  clickField.set(this, null)

  object click2 {
    def on(query: Query)(implicit driver: WebDriver) {
      println("click2")
      query.webElement.click()
    }
  }

  def demo(): Unit = {

  }

  def retry(callback: => Unit): Unit = {
    val fun = () => {
      callback
    }
    import scala.reflect.runtime.universe._
    val tree = reify(callback)
    println(show(tree))
    println(showRaw(tree))
    val tree2 = reify(fun())
    println(show(tree2))
    println(showRaw(tree2))

    var isRetry = true
    var index = 0
    while (isRetry) {
      try {
        index += 1
        println("retry time=" + index)
        if (index < 10) {
          fun()
        }
        isRetry = false
      } catch {
        case e: Exception => {
          println(e.getMessage)
          isRetry = true
        }
      }
    }
  }


  def travel(): Unit = {
    val tree = reify {
      val a = 1
      val b = 2
      println(a + b)
      println("demo")
      1 to 5 foreach (i => {
        println(i)
      })
    }
    traverser.traverse(tree.tree)

  }

  object cc{
    def on(args:Any*): Unit ={
      println(args)

    }
  }
  //利用重名覆盖原来的调用. 不过a b这种调用还是回优先调用object和方法
  def click3:()=>cc.type=()=>{cc}
  /*  def click(x:String="demo"): super.click.type ={
      println("click method")
      super.click
    }*/
  //override val click:click.type =click
}


/*

trait WebBrowser {
  sealed trait Query {
    override def findElement(implicit driver: WebDriver): Option[WebBrowser.Element] = {
      println("xueqiu find element")
      try {
        Some(createTypedElement(driver.findElement(by)))
      }
      catch {
        case e: org.openqa.selenium.NoSuchElementException => None
      }

    }
  }

}
*/
