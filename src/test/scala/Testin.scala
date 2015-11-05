import java.net.URL
import java.util.concurrent.TimeUnit

import io.appium.java_client.XueqiuDriver
import io.appium.java_client.pagefactory.AppiumFieldDecorator
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.{By, WebElement}
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{ParallelTestExecution, BeforeAndAfterAll, FunSuite}

import scala.io.Source

/**
 * Created by seveniruby on 15/10/14.
 */

class Testin extends FunSuite with XueqiuBrowser with BeforeAndAfterAll with ParallelTestExecution {

  implicit var driver: XueqiuDriver[WebElement] = _

  def setup(url: String = "http://prj.testin.cn:4731/wd/hub") {
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
    driver = new XueqiuDriver[WebElement](new URL(url), capabilities)


    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))
  }

  override def beforeAll(): Unit = {
    //setup()
  }

  override def afterAll(): Unit = {
    driver.quit()
  }


  val raw = Source.fromURL("http://prj.testin.cn:4720/devices").mkString

  import rapture.json._
  import jsonBackends.json4s._

  println(raw)
  val json = Json.parse(raw)
  println(json)
  println(json.\\("url"))
  0 to json.as[List[Any]].length - 1 foreach (i => {
    println(json(i).url)
    println(json(i).brand.as[String])
    val mark = s"${json(i).brand.as[String]}_${json(i).release.as[String]}_${json(i).model.as[String]}"
    test(s"${mark} login testcase") {
      setup(json(i).url.as[String])
      login(mark + "_")
    }
  })


  def login(mark: String = "") {
    markup(mark+"start")
    setCaptureDir("./")
    markup("""capture to mark + "start.png" """)
    implicitlyWait(Span(10, Seconds))
    retry {
      markup("account")
      click on id("account")
    }
    driver.getKeyboard.sendKeys("15600534760")
    markup("""capture to mark + "account.png" """)
    click on id("password")
    driver.getKeyboard.sendKeys("hys2xueqiu")
    markup(""" capture to mark + "password.png" """)
    click on id("button_next")
    markup(""" capture to mark + "button_next.png" """)
    retry {
      click on id("tip_step_one")
    }
    markup("""capture to mark + "one.png" """)
    click on id("tip_step_two")
    markup("""capture to mark + "two.png" """)
    click on id("tip_step_three")
    markup("""capture to mark + "three.png" """)
    capture to mark + "three.png"

  }

}
