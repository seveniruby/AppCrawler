import java.net.URL
import java.util.concurrent.TimeUnit

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import io.appium.java_client.remote.MobileCapabilityType
import io.selendroid.client.SelendroidDriver
import io.selendroid.common.SelendroidCapabilities
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.{WebDriver, By, WebElement}
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

/**
  * Created by seveniruby on 15/10/14.
  */
class SelendroidDsl extends FunSuite  with WebBrowser with BeforeAndAfterAll{
  //val capa = new SelendroidCapabilities("io.selendroid.testapp:0.16.0")
  val capa = new SelendroidCapabilities("com.xueqiu.android:6.8.1-rc-473")

  implicit val driver = new SelendroidDriver(capa)

  override def beforeAll(): Unit ={

   }
   test("login xueqiu"){
     driver.manage().timeouts().implicitlyWait(10000, TimeUnit.SECONDS)
     PageFactory.initElements(new AppiumFieldDecorator(driver, 5, TimeUnit.SECONDS), this)
     driver.findElement(By.id("account")).asInstanceOf[WebElement].sendKeys("15600534760")
     driver.findElement(By.id("password")).asInstanceOf[WebElement].sendKeys("hys2xueqiu")
     driver.findElement(By.id("button_next")).asInstanceOf[WebElement].click()
     driver.findElement(By.id("tip_step_one")).asInstanceOf[WebElement].click()
     driver.findElement(By.id("tip_step_two")).asInstanceOf[WebElement].click()
     val three:WebElement=driver.findElement(By.id("tip_step_three"))
     three.click()

   }

   test("login xueqiu with dsl"){
     implicitlyWait(Span(10, Seconds))
     click on id("account")
     driver.getKeyboard.sendKeys("15600534760")
     click on id("password")
     driver.getKeyboard.sendKeys("hys2xueqiu")
     click on id("button_next")
     click on id("tip_step_one")
     click on id("tip_step_two")
     click on id("tip_step_three")

   }

 }
