package com.xueqiu.qa.appcrawler

import java.net.URL

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 15/12/10.
  */
class AndroidCrawler extends Crawler {
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
    val capabilities = new DesiredCapabilities()
    conf.capability.foreach(kv => capabilities.setCapability(kv._1, kv._2))
    //todo: 主要做遍历测试和异常测试. 所以暂不使用selendroid
    //todo: Appium模式太慢

    val url=conf.capability("appium").toString
    driver = new AndroidDriver[WebElement](new URL(url), capabilities)
    //driver.launchAp
    log.info(s"driver=${driver}")
  }

  override def getUrl(): String = {
    //todo:selendroid和appium在这块上不一致. api不一样.  appium不遵从标准. 需要改进
    val screenName = conf.capability.getOrElse("automationName", "").toString.toLowerCase() match {
      case "selendroid" => {
        driver.getCurrentUrl.split('.').last
      }
      case _ => {
        (MiniAppium.asyncTask() {
          driver.asInstanceOf[AndroidDriver[WebElement]].currentActivity()
        }).getOrElse("").split('.').last
      }
    }
    val baseUrl=super.getUrl()
    List(getAppName(), screenName, baseUrl).distinct.filter(_.nonEmpty).mkString("-")
  }

  override def getAppName(): String ={
    getAllElements("(//*[@package!=''])[1]").head.getOrElse("package", "").toString
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

}
