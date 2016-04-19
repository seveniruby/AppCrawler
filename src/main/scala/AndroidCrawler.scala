import java.net.URL

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 15/12/10.
  */
class AndroidCrawler extends Crawler {
  platformName = "Android"
  if(conf.selectedList.isEmpty){
    conf.selectedList.insertAll(0, ListBuffer[String](
      "//*[@enabled='true' and @resource-id!='' and not(contains(name(), 'Layout'))]",
      "//*[@enabled='true' and @content-desc!='' and not(contains(name(), 'Layout'))]",
      "//android.widget.TextView[@enabled='true' and @clickable='true']",
      "//android.widget.ImageView[@clickable='true']",
      "//android.widget.ImageView[@enabled='true' and @clickable='true']"
    ))
  }

  override def setupAppium(): Unit = {
    super.setupAppium()
    //todo:主要做遍历测试和异常测试. 所以暂不使用selendroid. 兼容性测试需要使用selendroid
    //capabilities.setCapability("automationName", "Selendroid")
    //todo: Appium模式太慢
    capabilities.setCapability("automationName", "Appium")
    capabilities.setCapability("unicodeKeyboard", "true")
    conf.capability.foreach(kv=>capabilities.setCapability(kv._1, kv._2))

    val url=conf.capability("appium").toString
    driver = new AndroidDriver[WebElement](new URL(url), capabilities)
    //driver.launchAp
    log.info(s"driver=${driver}")
    getDeviceInfo()
  }

  override def getUrl(): String = {
    //todo:selendroid和appium在这块上不一致. api不一样.  appium不遵从标准. 需要改进
    val screenName = automationName.toLowerCase() match {
      case "appium" => {
        doAppium(driver.asInstanceOf[AndroidDriver[WebElement]].currentActivity()).getOrElse("").split('.').last
      }
      case "selendroid" => {
        driver.getCurrentUrl.split('.').last
      }
    }
    lastAppName=appName
    appName=getAllElements("/*/*[1]").head.getOrElse("package", "").toString
    val baseUrl=super.getUrl()
    List(screenName, baseUrl).filter(_.nonEmpty).mkString("-")
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

  override def getRuleMatchNodes(): List[immutable.Map[String, Any]] = {
    getAllElements("//*")
  }

}
