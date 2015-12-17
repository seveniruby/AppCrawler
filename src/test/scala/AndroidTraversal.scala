import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement

import scala.collection.mutable.{ListBuffer, Map}
import scala.xml.XML

/**
  * Created by seveniruby on 15/12/10.
  */
class AndroidTraversal extends XueqiuAppium {

  val selectedList=ListBuffer[String]("*")
  override def getClickableElements(): Option[Seq[Map[String, String]]] ={
    doAppium(pageSource) match {
      case Some(v)=>{
        var all=Seq[Map[String,String]]()
        firstList.foreach(xpath=>{
          all++=getAllElements(v, xpath)
        })

        selectedList.foreach(xpath=>{
          val selected=getAllElements(pageSource,
            s"//${xpath}[@enabled='true' and @resource-id!='']")
          all=all++selected
        })
        selectedList.foreach(xpath=>{
          val selected=getAllElements(pageSource,
            s"//android.widget.EditText[@enabled='true' and @text!='']")
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



  override def isReturn(): Boolean = {
    //selendroid和appium实现模式还是不一样.
    val activityName = getUrl()
    //黑名单需要back. launcher可以直接退出.
    //todo:  "StockMoreInfoActivity", "StockDetailActivity"
    //Laucher不直接退出是为了看到底递归了多少层. 并且可以留出时间让你手工辅助点到其他的界面继续挽救遍历.
    if("Launcher"==activityName){
      System.exit(0)
    }
    val blackScreenList = List("StockMoreInfoActivity", "UserProfileActivity")
    if (blackScreenList.filter(activityName.contains(_)).length > 0) {
      println("should return")
      return true
    } else {
      return false
    }
  }

  override def getUrl(): String = {
    //selendroid和appium在这块上不一致. api不一样.  appium不遵从标准. 需要改进
    val screenName = automationName.toLowerCase() match {
      case "appium" => {
        doAppium(driver.asInstanceOf[AndroidDriver[WebElement]].currentActivity()).getOrElse("").split('.').last
      }
      case "selendroid" => {
        driver.getCurrentUrl.split('.').last
      }
    }

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

  override def getRuleMatchNodes(): ListBuffer[Map[String, String]] ={
    getAllElements(pageSource, "//*")
  }
}
