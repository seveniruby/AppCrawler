import java.net.URL

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{By, WebElement}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.reflect.io.File
import scala.util.{Failure, Success, Try}
import scala.util.control.Breaks._

/**
  * Created by seveniruby on 15/11/28.
  */
class XueqiuAppium {
  implicit var driver: AndroidDriver[WebElement] = _
  val elements: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map()
  val blackList = ListBuffer[String]()
  val rule = ListBuffer[Map[String, String]]()
  var isSkip = false
  val stack = scala.collection.mutable.Stack[String]()
  val clickedList = ListBuffer[String]()

  case class Element(url: String, text: String)

  def setupAndroid(url: String = "http://127.0.0.1:4723/wd/hub") {
    val capabilities = new DesiredCapabilities()
    capabilities.setCapability("deviceName", "emulator-5554");
    capabilities.setCapability("platformVersion", "4.4");
    capabilities.setCapability("appPackage", "com.xueqiu.android");
    capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, "com.xueqiu.android.view.WelcomeActivityAlias")
    //capabilities.setCapability("appActivity", ".ApiDemos");
    capabilities.setCapability("autoLaunch", "true")
    capabilities.setCapability("unicodeKeyboard", "true")
    //主要做遍历测试和异常测试. 所以暂不使用selendroid. 兼容性测试需要使用selendroid
    //capabilities.setCapability("automationName", "Selendroid")
    //todo: Appium模式太慢
    capabilities.setCapability("automationName", "Appium")

    capabilities.setCapability(MobileCapabilityType.APP, "/Users/seveniruby/Downloads/xueqiu_rc.apk")
    //capabilities.setCapability(MobileCapabilityType.APP, "http://xqfile.imedao.com/android-release/xueqiu_681_10151900.apk")
    //driver = new XueqiuDriver[WebElement](new URL("http://127.0.0.1:4729/wd/hub"), capabilities)
    driver = new AndroidDriver[WebElement](new URL(url), capabilities)


    //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    //PageFactory.initElements(new AppiumFieldDecorator(driver, 10, TimeUnit.SECONDS), this)
    //implicitlyWait(Span(10, Seconds))
  }

  def whiteList(): Unit = {

  }

  def black(key: String): Unit = {
    blackList.append(key)
  }

  def inputRule(): Unit = {

  }

  def rule(loc: String, action: String): Unit = {
    rule.append(Map(loc -> action))
  }


  def getUrl(): String = {
    val screenName = driver.currentActivity().split('.').last
    //如果存在说明返回到了某个上层, 否则就认为是新的层
    if (stack.contains(screenName)) {
      while (stack.head != screenName) {
        stack.pop()
      }
    } else {
      stack.push(screenName)
    }
    return stack.reverse.mkString(".")
  }

  def getElementId(x: WebElement): String = {
    val text = doAppium(x.getText).getOrElse("NoText")
    val resourceId = driver.getCapabilities.getCapability("automationName").toString.toLowerCase() match {
      case "selendroid" => {
        doAppium(x.getAttribute("NativeId")).getOrElse("NoId").split('/').last
      }
      case "appium" => {
        doAppium(x.getAttribute("resourceId")).getOrElse("NoId").split('/').last
      }
    }
    val uid = s"${getUrl()}.${resourceId}_${text}"
    return uid

  }

  def isReturn(): Boolean = {
    val activityName = getUrl()
    //todo:  "StockMoreInfoActivity", "StockDetailActivity"
    val blackScreenList = List("Launcher")
    if (blackScreenList.filter(activityName.contains(_)).length > 0) {
      println("should return")
      return true
    } else {
      return false
    }
  }

  def traversal(): Unit = {
    var needBack = true
    if (stack.size <= 1) {
      Thread.sleep(10000)
    } else {
      Thread.sleep(2000)
    }
    needBack = !isReturn()
    println(s"current context=${driver.getContext} activity=${getUrl()}")
    doRuleAction()
    //在selendroid的JsonXmlUtil.java:70有个bug. 导致无法获取当前界面的所有元素. ^_^
    val all_ids: Option[Seq[WebElement]] = doAppium(driver.findElements(By.xpath("//*[@resource-id!='']")))
    //val all_clickable: Option[Seq[WebElement]] = doAppium(driver.findElements(By.xpath("//*[@clickable='true']")))
    //val all:Seq[WebElement]=driver.findElements(By.xpath("//*[@clickable=true]"))

    var all = all_ids.getOrElse(Seq[WebElement]()) // ++ all_clickable.getOrElse(Seq[WebElement]())
    all = all.distinct

    if (all.length == 0) {
      //获取列表失败就重试
      println("activity change")
      needBack = false
    } else {
      println("get all elements")
      //获得所有的可点击元素
      breakable {
        all.foreach(x => {
          //是否需要退出
          if (isReturn()) {
            needBack = true
            break()
          }
          //如果触发了任意操作, 当前界面会变化. 需要重新刷新, 跳过无谓的循环
          val uid = getElementId(x)
          println(s"id=${uid}")

          //如果当前元素不存在, 说明界面刷新过, 需要重新刷新
          if(uid.contains("NoId") || uid.contains("NoText")){
            needBack=false
            break()
          }
          //只关注重要控件, 没id没text的就跳过
          if (uid.takeRight(2) != "._" && uid.contains("stock_item_value") == false && uid.matches(".*[0-9]{2}.*")==false) {
            //如果第一次出现
            if (!elements.contains(uid)) {
              println("first show")
              elements(uid) = true
              doDefaultAction(uid, x)
              needBack=false
            }
          }
        })

      }
    }
    //子界面遍历返回后继续遍历当前界面中剩下的界面
    if (needBack == true) {
      println("back")
      driver.navigate().back()
    }

    File("clickedList.log").writeAll(clickedList.mkString("\n"))
    traversal()


  }

  def doDefaultAction(uid: String, x: WebElement): Unit = {
    println(s"click ${uid}")
    clickedList.append(uid)
    doAppium(x.click())
  }

  def doAppium[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        return Some(v)
      }
      case Failure(e) => {
        println("message=" + e.getMessage)
        println("cause=" + e.getCause)
        //println(e.getStackTrace.mkString("\n"))
        return None
      }
    }

  }

  def doAppiumAction(uid: String, v: WebElement, action: String): Unit = {
    action match {
      case "skip" => {
        elements(uid) = true
      }
      case "click" => {
        println(s"click ${uid}")
        clickedList.append(uid)
        doAppium(v.click())
        traversal()
      }
      case str: String => {
        println(s"sendKeys ${uid}")
        doAppium(v.sendKeys(str))
      }
    }

  }

  //通过规则实现操作. 不管元素是否被点击过
  def doRuleAction(): Unit = {
    rule.foreach(r => {
      var uid = r.head._1
      val action = r.head._2
      println(s"Find ${uid}")
      var x = doAppium(driver.findElementsById(uid).head)
      x = x match {
        case Some(v) => {
          Some(v)
        }
        case None => {
          doAppium(driver.findElementsByName(uid).last)
        }
      }

      x match {
        case Some(v) => {
          //先删除已经命中的规则
          rule.foreach(r => {
            println(s"r=${r}")
            if (r.head._1 == uid) {
              println(s"remove ${r}")
              rule -= r
            }
          })
          //获得正式的定位id
          uid = doAppium(getElementId(v)).getOrElse("not found")
          doAppiumAction(uid, v, action)
        }
        case None => {
          println("can't find")
        }
      }
    })

  }

}
