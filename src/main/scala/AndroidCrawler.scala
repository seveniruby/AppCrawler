import java.net.URL

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities

import scala.collection.mutable.{ListBuffer, Map}

/**
  * Created by seveniruby on 15/12/10.
  */
class AndroidCrawler extends Crawler {

  if(conf.selectedList.length==0){
    conf.selectedList.insertAll(0, ListBuffer[String](
      "//*[@enabled='true' and @resource-id!='' and not(contains(name(), 'Layout'))]",
      "//*[@enabled='true' and @content-desc!='' and not(contains(name(), 'Layout'))]",
      "//android.widget.TextView[@enabled='true' and @clickable='true']",
      "//android.widget.ImageView[@clickable='true']",
      "//android.widget.ImageView[@enabled='true' and @clickable='true']"
    ))
  }

  override def setupApp(app: String, url: String = "http://127.0.0.1:4723/wd/hub"): Unit = {
    platformName = "Android"
    super.setupApp(app, url)
    capabilities.setCapability("deviceName", "emulator-5554");
    capabilities.setCapability("platformVersion", "4.4");
    capabilities.setCapability("appPackage", "com.xueqiu.android");
    capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, "com.xueqiu.android.view.WelcomeActivityAlias")
    //capabilities.setCapability("appActivity", ".ApiDemos");
    capabilities.setCapability("unicodeKeyboard", "true")
    //todo:主要做遍历测试和异常测试. 所以暂不使用selendroid. 兼容性测试需要使用selendroid
    //capabilities.setCapability("automationName", "Selendroid")
    //todo: Appium模式太慢
    capabilities.setCapability("automationName", "Appium")

    driver = new AndroidDriver[WebElement](new URL(url), capabilities)
    getDeviceInfo()
  }

  override def getUrl(): String = {
    //selendroid和appium在这块上不一致. api不一样.  appium不遵从标准. 需要改进
    var screenName = automationName.toLowerCase() match {
      case "appium" => {
        doAppium(driver.asInstanceOf[AndroidDriver[WebElement]].currentActivity()).getOrElse("").split('.').last
      }
      case "selendroid" => {
        driver.getCurrentUrl.split('.').last
      }
    }
    screenName = s"${screenName}_${super.getUrl()}"
    println(s"url=${screenName}")
    return screenName
  }

  //
  //  /**
  //    * 尝试识别当前的页面, 在android中无效
  //    * @return
  //    */
  //  override def getSchema(): String ={
  //    val nodeList=getAllElements(pageSource, "//UIAWindow[1]//*")
  //    //todo: 未来应该支持黑名单
  //    md5(nodeList.filter(node=>node("tag")!="UIATableCell").map(node=>node("tag")).mkString(""))
  //  }

  override def getRuleMatchNodes(): ListBuffer[Map[String, String]] = {
    getAllElements("//*")
  }

  override def getSchema(): String = {
    val nodeList = getAllElements("//UIAWindow[1]//*[not(ancestor-or-self::UIATableView)]")
    //todo: 未来应该支持黑名单
    val schemaBlackList = List("UIATableCell", "UIATableView", "UIAScrollView")
    md5(nodeList.filter(node => schemaBlackList.contains(node("tag")) == false).map(node => node("tag")).mkString(""))
  }

}
