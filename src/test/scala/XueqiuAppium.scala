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
  val timestamp=new java.text.SimpleDateFormat("YYYYMMddHHmm").format(new java.util.Date())

  case class Element(url: String, text: String)

  def setupAndroid(app:String, url: String = "http://127.0.0.1:4723/wd/hub") {
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

    capabilities.setCapability(MobileCapabilityType.APP, app)
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
    //selendroid和appium在这块上不一致. api不一样.  appium不遵从标准.
    val screenName = driver.getCapabilities.getCapability("automationName").toString.toLowerCase() match {
      case "appium" => {
        driver.currentActivity().split('.').last
      }
      case "selendroid" => {
        driver.getCurrentUrl.split('.').last
      }
    }
    println(s"url=${screenName}")
    return screenName
  }

  /**
    * 获取控件的基本属性并设置一个唯一的uid作为识别. screenName+id+name
    * @param x
    * @return
    */
  def getElementId(x: WebElement): String = {
    val text = doAppium(x.getText).getOrElse("NoText").replace("\n", "\\n")
    val tag = doAppium(x.getTagName).getOrElse("NoTag").split('.').last
    val resourceId = driver.getCapabilities.getCapability("automationName").toString.toLowerCase() match {
      case "selendroid" => {
        doAppium(x.getAttribute("NativeId"))
      }
      case "appium" => {
        doAppium(x.getAttribute("resourceId"))
      }
    }
    var id=resourceId.getOrElse("NoId").split('/').last
    if(id==""){
      println("set id to NoId")
      id="NoId"
    }
    val uid = s"${getUrl()}.${tag}.${id}_${text}"
    return uid

  }

  def isReturn(): Boolean = {
    //selendroid和appium实现模式还是不一样.
    val activityName = getUrl()
    //黑名单需要back. launcher可以直接退出.
    //todo:  "StockMoreInfoActivity", "StockDetailActivity"
    //Laucher不直接退出是为了看到底递归了多少层. 并且可以留出时间让你手工辅助点到其他的界面继续挽救遍历.
    val blackScreenList = List("Launcher")
    if (blackScreenList.filter(activityName.contains(_)).length > 0) {
      println("should return")
      return true
    } else {
      return false
    }
  }

  /**
    * 黑名单过滤. 通过正则匹配
    * @param uid
    * @return
    */
  def isBlack(uid: String): Boolean = {
    val blackList = List(".*stock_item_value.*", ".*[0-9]{2}.*", ".*\\._$", "取消", "up", "home", "user_profile_icon")
    blackList.filter(uid.matches(_)).length > 0
  }

  def traversal(): Unit = {
    var needBack = true
    var needSkip = false

    if (stack.size <= 1) {
      Thread.sleep(10000)
    } else {
      //不等也没关系. 会自动刷新的
      //Thread.sleep(2000)
    }
    needBack = !isReturn()
    println(s"current context=${driver.getContext} activity=${getUrl()}")
    doRuleAction()
    //在selendroid的JsonXmlUtil.java:70有个bug. 导致无法获取当前界面的所有元素. ^_^
    println("get all elements")
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
      //获得所有的可点击元素
      breakable {
        all.foreach(x => {
          println(x.toString)
          //是否需要退出
          if (isReturn()) {
            needBack = true
            break()
          }
          //如果触发了任意操作, 当前界面会变化. 需要重新刷新, 跳过无谓的循环
          val uid = getElementId(x)
          println(s"id=${uid}")

          //遍历的元素都是有id, 如果出现了没有NoId或者NoText, 表明是获取元素属性的方法失败了. 发生了异常.
          //获取id异常表示元素出了问题. 说明界面刷新过, 需要重新刷新, 但是不需要后退
          if (uid.contains("NoId") || uid.contains("NoText")) {
            needBack = false
            break()
          }
          //是否黑名单
          needSkip = isBlack(uid)
          //是否已经点击过
          //todo: 新界面入口需要设置为false
          if (elements.contains(uid)) {
            needSkip = true
          }
          //如果未曾点击
          if (needSkip == false) {
            println("first show")
            elements(uid) = true
            doDefaultAction(uid, x)
            //说明还不需要back到上一界面, 遍历完所有的元素才表示需要回退
            needBack = false
          }
        })
      }
    }
    //子界面遍历返回后继续遍历当前界面中剩下的界面
    if (needBack == true) {
      println("back")
      driver.navigate().back()
    }

    //记录点击log
    File(s"clickedList_${timestamp}.log").writeAll(clickedList.mkString("\n"))
    //任何界面变化都需要进入新的递归. 而不是只到新界面.
    traversal()
  }

  def doDefaultAction(uid: String, x: WebElement): Unit = {
    println(s"click ${uid}")
    //放前面是怕有的控件的确是无法点击. 无法点击的可以先跳过.不影响遍历
    clickedList.append(uid)
    uid.split('.')(1) match {
      case "EditText"=>{
        doAppium(x.sendKeys(uid.split('.').take(2).mkString("")))
      }
      case _=>{
        doAppium(x.click())
      }
    }
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
      }
      case str: String => {
        println(s"sendKeys ${uid}")
        clickedList.append(uid)
        doAppium(v.sendKeys(str))
      }
    }

  }

  //通过规则实现操作. 不管元素是否被点击过
  def doRuleAction(): Unit = {
    val currentScreenName=getUrl()
    //先判断是否在期望的界面里. 提升速度
    rule.filter(r=>{
      val screenName=r.head._1.split('.').head
      currentScreenName.matches(s".*${screenName}.*")
    }).foreach(r => {
      println("hit rule action")
      var idOrName = r.head._1.split('.').last
      val action = r.head._2
      println(s"Find ${idOrName}")
      var x = doAppium(driver.findElementsById(idOrName).head)
      x = x match {
        case Some(v) => {
          Some(v)
        }
        case None => {
          //找不到id再找name, name在appium表示元素的文本标签或者内容
          doAppium(driver.findElementsByName(idOrName).last)
        }
      }

      x match {
        case Some(v) => {
          //先删除已经命中的规则
          rule.foreach(r => {
            println(s"r=${r}")
            if (r.head._1 == idOrName) {
              println(s"remove ${r}")
              rule -= r
            }
          })
          //获得正式的定位id
          idOrName = doAppium(getElementId(v)).getOrElse("not found")
          doAppiumAction(idOrName, v, action)
        }
        case None => {
          println("can't find")
        }
      }
    })

  }

}
