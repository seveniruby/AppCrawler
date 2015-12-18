import java.net.URL

import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities

import scala.collection.mutable.{ListBuffer, Map}

/**
  * Created by seveniruby on 15/12/10.
  */
class IOSTraversal extends Traversal{
  val selectedList=ListBuffer[String](
    "UIATextField", "UIASecureTextField",
    "UIATableCell", "UIAButton",
    "UIAImage", "UIACollectionCell")

  override def setupApp(app: String, url: String = "http://127.0.0.1:4723/wd/hub") {
    platformName = "iOS"
    val capabilities = new DesiredCapabilities()
    capabilities.setCapability("deviceName", "iPhone 4s")
    capabilities.setCapability("platformName", "iOS")
    capabilities.setCapability("platformVersion", "9.1")
    capabilities.setCapability("autoLaunch", "true")
    capabilities.setCapability("autoAcceptAlerts", "true")
    //主要做遍历测试和异常测试. 所以暂不使用selendroid. 兼容性测试需要使用selendroid
    //capabilities.setCapability("automationName", "Selendroid")
    //todo: Appium模式太慢
    capabilities.setCapability("automationName", "Appium")

    capabilities.setCapability(MobileCapabilityType.APP, app)
    //capabilities.setCapability(MobileCapabilityType.APP, "http://xqfile.imedao.com/android-release/xueqiu_681_10151900.apk")
    //driver = new XueqiuDriver[WebElement](new URL("http://127.0.0.1:4729/wd/hub"), capabilities)
    driver = new IOSDriver[WebElement](new URL(url), capabilities)


    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))
  }
  override def getClickableElements(): Option[Seq[Map[String, String]]] ={
    doAppium(pageSource) match {
      case Some(v)=>{
        var all=Seq[Map[String,String]]()
        firstList.foreach(xpath=>{
          all++=getAllElements(v, xpath)
        })

        selectedList.foreach(xpath=>{
          val selected=getAllElements(pageSource,
            s"//UIAWindow[1]//${xpath}[@visible='true' and @enabled='true' and @valid='true']")
          all=all++selected
        })
        all=all.distinct
        println(s"all length=${all.length}")
        return Some(all)
      }
      case None=>{
        println("get page source error")
        return None
      }
    }
  }

  /**
    * 用schema作为url替代
    * @return
    */
  override def getUrl(): String ={
    val nav=getAllElements(pageSource, "//UIANavigationBar[1]")
    if(nav.length>0){
      return nav(0)("name")
    }else {
      val screenName = getSchema()
      println(s"url=${screenName}")
      return screenName
    }
  }


  /**
    * 尝试识别当前的页面
    * @return
    */
  override def getSchema(): String ={
    val nodeList=getAllElements(pageSource, "//UIAWindow[1]//*[not(ancestor-or-self::UIATableView)]")
    //todo: 未来应该支持黑名单
    val schemaBlackList=List("UIATableCell", "UIATableView", "UIAScrollView")
    md5(nodeList.filter(node=>schemaBlackList.contains(node("tag"))==false).map(node=>node("tag")).mkString(""))
  }

  override def getRuleMatchNodes(): ListBuffer[Map[String, String]] ={
    getAllElements(pageSource, "//UIAWindow[1]//*")
  }
}
