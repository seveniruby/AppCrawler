import com.xueqiu.qa.appcrawler.MiniAppium
import org.scalatest.Tag
import org.scalatest.selenium.WebBrowser.click

/**
  * Created by seveniruby on 16/4/15.
  */
class TestAppiumDSL extends MiniAppium {
  import org.scalatest.prop.TableDrivenPropertyChecks._
  val table = Table(
    ("iPhone 4s", "9.1"),
    ("iPhone 5", "8.1"),
    ("iPhone 5", "9.2"),
    ("iPhone 5s", "9.1"),
    ("iPhone 6", "8.1"),
    ("iPhone 6", "9.2"),
    ("iPhone 6 Plus", "9.1"),
    ("iPhone 6s", "9.1"),
    ("iPhone 6s", "9.2"),
    ("iPad Air", "9.1"),
    ("iPad Air 2", "9.1"),
    ("iPad Pro", "9.1"),
    ("iPad Retina", "8.1"),
    ("iPad Retina", "8.2")
  )
  forAll(table) { (device: String, version: String) => {
    test(s"兼容性测试-${device}-${version}_登录验证iphone", Tag("7.7"), Tag("iOS"), Tag("兼容性测试")) {
      iOS(true)
      config("deviceName", device)
      config("platformVersion", version)
      setCaptureDir("/Users/seveniruby/temp/crawl4")
      appium()
      captureTo(s"${device}-${version}_init.png")
      see("手机号").tap()
      send("15600534760")
      see("//UIASecureTextField").tap()
      send("hys2xueqiu")
      captureTo(s"${device}-${version}_login.png")
      see("登 录").tap()
      captureTo(s"${device}-${version}_main.png")
      if(device.matches(".*iPad.*")){
        see("//UIAButton[@path=\"/0/0/0/5\"]").tap()
      }else {
        see("//UIAButton[@path=\"/0/0/3/5\"]").tap
      }
      tree("seveniruby")("name") should be equals "seveniruby"
      captureTo(s"${device}-${version}_profile.png")
    }
  }
  }

  override def afterEach(): Unit ={
    log.info("quit")
    quit()
  }
}
