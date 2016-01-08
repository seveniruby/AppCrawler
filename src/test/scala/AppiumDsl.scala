import java.net.URL
import java.util.concurrent.TimeUnit

import io.appium.java_client.XueqiuDriver
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.{By, WebElement}
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

/**
 * Created by seveniruby on 15/10/14.
 */

class AppiumDsl extends FunSuite with XueqiuBrowser with BeforeAndAfterAll {

  implicit var driver:XueqiuDriver[WebElement]=_
  def setup() {
    val capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName", "emulator-5554");
    capabilities.setCapability("platformVersion", "4.4");
    capabilities.setCapability("appPackage", "com.xueqiu.android");
    capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, "com.xueqiu.android.view.WelcomeActivityAlias")
    //capabilities.setCapability("appActivity", ".ApiDemos");
    capabilities.setCapability("autoLaunch", "true")
    capabilities.setCapability("automationName", "Selendroid")
    //capabilities.setCapability(MobileCapabilityType.APP, "/Users/seveniruby/Downloads/xueqiu.apk")
    capabilities.setCapability(MobileCapabilityType.APP, "http://xqfile.imedao.com/android-release/xueqiu_681_10151900.apk")
    //driver = new XueqiuDriver[WebElement](new URL("http://127.0.0.1:4729/wd/hub"), capabilities)
    driver = new XueqiuDriver[WebElement](new URL("http://prj.testin.cn:4731/wd/hub"), capabilities)


    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))
  }
  override def beforeAll(): Unit = {
    setup()

  }

  test("login xueqiu") {
    println(driver.currentActivity())
    Thread.sleep(100000)
    driver.manage().timeouts().implicitlyWait(10000, TimeUnit.SECONDS)
    PageFactory.initElements(new AppiumFieldDecorator(driver, 5, TimeUnit.SECONDS), this)
    driver.findElement(By.id("account")).sendKeys("15600534760")
    driver.findElement(By.id("password")).sendKeys("hys2xueqiu")
    driver.findElement(By.id("button_next")).click()
    val one=driver.findElement(By.id("tip_step_one"))
    one.click()
    driver.findElement(By.id("tip_step_two")).click()
    val three: WebElement = driver.findElement(By.id("tip_step_three"))
    three.click()

  }

  test("login xueqiu with dsl") {
    markup("start")
    implicitlyWait(Span(10, Seconds))
    markup("set capture dir")
    setCaptureDir("/Users/seveniruby/projects/LBSRefresh/")
    markup("start.png")
    //capture to "start.png"
    retry {
      markup("account_before.png")
      //capture to "account_before.png"
      markup("click on account")
      click on id("account")
    }
    markup("15600534760")
    driver.getKeyboard.sendKeys("15600534760")
    markup("account.png")
    //capture to "account.png"
    markup("password")
    click on id("password")
    driver.getKeyboard.sendKeys("hys2xueqiu")
    //capture to "password.png"
    markup("next")
    click on id("button_next")
    //capture to "button_next.png"
    retry{
      //capture to "tip_step_one_before.png"
      markup("one")
      click on id("tip_step_one")
    }
    //capture to "one.png"
    markup("two")
    click on id("tip_step_two")
    //capture to "two.png"
    markup("three")
    click on id("tip_step_three")
    //capture to "three.png"

  }

/*  test("ast traverl"){
    import scala.reflect.runtime.universe._
    val tree=reify{

      val a=1
      val b=a+2
      implicitlyWait(Span(10, Seconds))
      click on id("account")
      driver.getKeyboard.sendKeys("15600534760")
      click on id("password")
      driver.getKeyboard.sendKeys("hys2xueqiu")
      click on id("button_next")
      1 to 5 foreach (i=>{
        click on id("demo")
        driver.getKeyboard.sendKeys("xxxx")
      })
      click on id("tip_step_one")

      println(a)
      click on id("tip_step_two")
      click on id("tip_step_three")

    }
    traverser.traverse(tree.tree)

  }*/

}
