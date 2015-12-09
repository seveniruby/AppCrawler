import java.net.URL

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{By, WebElement}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.{ListBuffer, Map}
import scala.reflect.io.File
import scala.util.control.Breaks._
import scala.util.{Failure, Success, Try}


case class ELement(url: String, tag: String, id: String, name: String) {
  override def toString(): String = {
    if (tag.toLowerCase().contains("edit")) {
      s"${url},${tag}_${id},"
    } else {
      s"${url},${tag}_${id},${name}"
    }
  }
}


/**
 * Created by seveniruby on 15/11/28.
 */
class XueqiuAppium {
  type AM = Map[String, String]
  implicit var driver: AndroidDriver[WebElement] = _
  val elements: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map()
  val blackList = ListBuffer[String]()
  val rule = ListBuffer[Map[String, String]]()
  var isSkip = false
  val stack = scala.collection.mutable.Stack[String]()
  val clickedList = ListBuffer[String]()
  val timestamp = new java.text.SimpleDateFormat("YYYYMMddHHmm").format(new java.util.Date())
  var md5Last = ""
  var nId = 0


  case class Node[T](value: T, children: ListBuffer[Node[T]] = ListBuffer[Node[T]]()){
    val nId: String = {getNodeId()}

    def equals(node:Node[AM]): Boolean ={
      List("url", "id", "name").foreach(attr => {
        if (node.value(attr) != value.asInstanceOf[AM](attr)){
          return false
        }
      })
      return true
    }
  }

  val freemind = Node(Map("url" -> "Start", "id" -> "Start", "name" -> null))

  def generateFreeMind(list: ListBuffer[ELement]): Unit = {
    // 保留上一个node用来加linktarget箭头
    var lastAddedNodes = ListBuffer[Node[AM]]()
    list.foreach(l => {
      var fixedUrl = l.url
      // 去掉url的前缀: android/gz  com.xueqiu.android/gz
      if (l.url.split("/").length > 1){
        fixedUrl = l.url.split("/")(1)
      }
      var nameNode = Node(Map("url" -> fixedUrl,
        "id" -> l.id,
        "name" -> l.name))
      lastAddedNodes = appendNodes(freemind, nameNode, lastAddedNodes)
    })

    println(freemind)

    println( """<map version="1.0.1">""")
    toXml(freemind)
    println("</map>")

  }

  def getNodeId(): String = {
    nId += 1
    return nId.toString
  }

  def getArrowId(): String = {
    nId += 1
    return nId.toString
  }

  def appendNodes(currenTree: Node[AM], node: Node[AM], lastAddedNodes: ListBuffer[Node[AM]]): ListBuffer[Node[AM]] = {
    var newTree = currenTree
    var addedNodes = ListBuffer[Node[AM]]()

    //add url node
    if (node.value("url") != null) {
      val newNode = Node(Map("url" -> node.value("url"),
        "id" -> null,
        "name" -> null))
      newTree = appendNode(newTree, newNode)
      addedNodes += newTree
    }
    //add id node
    if (node.value("id") != null) {
      val newNode = Node(Map("url" -> node.value("url"),
        "id" -> node.value("id"),
        "name" -> null))
      newTree = appendNode(newTree, newNode)
      addedNodes += newTree
    }
    //add name node
    if (node.value("name") != null) {
      val newNode = Node(Map("url" -> node.value("url"),
        "id" -> node.value("id"),
        "name" -> node.value("name")))
      newTree = appendNode(newTree, newNode)
      addedNodes += newTree
    }
    //add targetlink to just append node
    if (lastAddedNodes.length > 0 && addedNodes.length > 0 && lastAddedNodes.last.value("url") != addedNodes.head.value("url")){
      var arrowId = getArrowId()
      //add attrs of linktarget to new node
      addedNodes.head.value += ("type" -> "linktarget")
      addedNodes.head.value += ("destination" -> addedNodes.head.nId)
      addedNodes.head.value += ("source" -> lastAddedNodes.last.nId)
      addedNodes.head.value += ("aid" -> s"Arrow_ID_${arrowId}")

      //add attrs of arrowlink to the last node
      lastAddedNodes.last.value += ("type" -> "arrowlink")
      lastAddedNodes.last.value += ("destination" -> addedNodes.head.nId)
      lastAddedNodes.last.value += ("aid" -> s"Arrow_ID_${arrowId}")
    }

    return addedNodes
  }

  def appendNode(currenTree: Node[AM], node: Node[AM]): Node[AM] = {
    find(currenTree, node) match {
      case Some(v) => {
        return v
      }
      case None => {
        currenTree.children.append(node)
        return node
      }
    }
  }


  def toXml(tree: Node[AM]): Unit = {
    val before = (tree: Node[AM]) => {
      var output = ""
      if (tree.value("name") != null) {
        output = tree.value("name")
      } else if (tree.value("id") != null) {
        output = tree.value("id")
      } else if (tree.value("url") != null) {
        output = tree.value("url")
      }
      println( s"""<node ID="ID_${tree.nId}" TEXT="${output}">""")

      //add linktarget and arrowlink if needed
      if (tree.value.contains("type")){
        tree.value("type") match {
          case "linktarget" => {
            println(s"""<linktarget COLOR="#b0b0b0" DESTINATION="ID_${tree.value("destination")}" ENDARROW="Default" ENDINCLINATION="24;0;" ID="${tree.value("aid")}" SOURCE="${tree.value("source")}" STARTARROW="None" STARTINCLINATION="24;0;"/>""")
          }
          case "arrowlink" => {
            println(s"""<arrowlink DESTINATION="ID_${tree.value("destination")}" ENDARROW="Default" ENDINCLINATION="24;0;" ID="${tree.value("aid")}" STARTARROW="None" STARTINCLINATION="24;0;"/>""")
          }
        }
      }
    }
    val after = (tree: Node[AM]) => {
      println("</node>")
    }
    traversal[AM](tree, before, after)
  }

  def traversal[T](tree: Node[T],
                   before: (Node[T]) => Unit = (x: Node[T]) => Unit,
                   after: (Node[T]) => Unit = (x: Node[T]) => Unit): Unit = {
    before(tree)
    tree.children.foreach(t => {
      traversal(t, before, after)
    })
    after(tree)
  }

  def find(tree: Node[AM], node: Node[AM]): Option[Node[AM]] = {
    if (tree.equals(node)) {
      return Some(tree)
    }
    tree.children.map(t => {
      find(t, node) match {
        case Some(v) => return Some(v)
        case None => {}
      }
    })
    return None
  }


  def setupAndroid(app: String, url: String = "http://127.0.0.1:4723/wd/hub") {
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

  def md5(format: String) = {
    //import sys.process._
    //s"echo ${format}" #| "md5" !

    //new java.lang.String(MessageDigest.getInstance("MD5").digest(format.getBytes("UTF-8")))
    java.security.MessageDigest.getInstance("MD5").digest(format.getBytes("UTF-8")).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
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
  def getElementId(x: WebElement): Option[ELement] = {
    val tag = doAppium(x.getTagName) match {
      case Some(v) => {
        println(s"tag=${v}")
        v.split('.').last
      }
      case None => {
        //如果发生了异常就不需要再读取其他属性了, 因为读取一个不存在的元素会有一个默认等待时间. 浪费资源
        println("read tag name exception")
        return None
      }
    }
    val text = doAppium(x.getText).getOrElse("NoText").replace("\n", "\\n")
    val resourceId = driver.getCapabilities.getCapability("automationName").toString.toLowerCase() match {
      case "selendroid" => {
        doAppium(x.getAttribute("NativeId"))
      }
      case "appium" => {
        doAppium(x.getAttribute("resourceId"))
      }
    }
    println(s"id=${resourceId}")
    val appName = resourceId.getOrElse("NoId").split(':').head
    var id = resourceId.getOrElse("NoId").split('/').last
    if (id == "") {
      println("set id to NoId")
      id = "NoId"
    }
    val url = s"${appName}/${getUrl()}"
    //val uid = s"${getUrl()}.${tag}.${id}_${text}"
    val node = ELement(url, tag, id, text)

    if (List("android", "com.xueqiu.android").contains(appName)) {
      return Some(node)
    } else {
      return None
    }


  }

  def isReturn(): Boolean = {
    //selendroid和appium实现模式还是不一样.
    val activityName = getUrl()
    //黑名单需要back. launcher可以直接退出.
    //todo:  "StockMoreInfoActivity", "StockDetailActivity"
    //Laucher不直接退出是为了看到底递归了多少层. 并且可以留出时间让你手工辅助点到其他的界面继续挽救遍历.
    val blackScreenList = List("Launcher", "StockMoreInfoActivity", "UserProfileActivity")
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
  def isBlack(uid: ELement): Boolean = {
    val blackList = List("stock_item_value", "[0-9]{2}", "弹幕", "发送", "保存", "确定",
      "up", "user_profile_icon", "selectAll", "cut", "copy", "send")
    blackList.filter(b => {
      uid.id.matches(s".*${b}.*") || uid.name.matches(s".*${b}.*")
    }).length > 0
  }

  def traversal(): Unit = {
    var needBack = true
    var needSkip = false

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
        var index = 0
        all.foreach(x => {
          index += 1
          println(s"index=${index}")
          //是否需要退出
          if (isReturn()) {
            needBack = true
            break()
          }
          //如果触发了任意操作, 当前界面会变化. 需要重新刷新, 跳过无谓的循环
          val uid = getElementId(x) match {
            case Some(v) => v
            case None => {
              //遍历的元素都是有id, 如果出现了没有NoId或者NoText, 表明是获取元素属性的方法失败了. 发生了异常.
              //获取id异常表示元素出了问题. 说明界面刷新过, 需要重新刷新, 但是不需要后退
              println("exception")
              needBack = false
              break()
            }
          }
          println(s"id=${uid}")

          //是否黑名单
          needSkip = isBlack(uid)
          //是否已经点击过
          //todo: 新界面入口需要设置为false
          if (elements.contains(uid.toString())) {
            println("skip")
            needSkip = true
          }
          //如果未曾点击
          if (needSkip == false) {
            println("first show")
            elements(uid.toString()) = true
            doDefaultAction(uid, x)
            //说明还不需要back到上一界面, 遍历完所有的元素才表示需要回退
            needBack = false
            println("md5=" + md5(driver.getPageSource))
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

  def doDefaultAction(uid: ELement, x: WebElement): Unit = {
    //放前面是怕有的控件的确是无法点击. 无法点击的可以先跳过.不影响遍历
    clickedList.append(uid.toString())
    uid.tag match {
      case "EditText" => {
        doAppium(x.sendKeys(uid.id))
      }
      case _ => {
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

  def doAppiumAction(v: WebElement, action: String): Unit = {
    action match {
      case "click" => {
        doAppium(v.click())
      }
      case str: String => {
        doAppium(v.sendKeys(str))
      }
    }

  }

  //通过规则实现操作. 不管元素是否被点击过
  def doRuleAction(): Unit = {
    val currentScreenName = getUrl()
    //先判断是否在期望的界面里. 提升速度
    rule.filter(r => {
      val screenName = r.head._1.split('.').head
      currentScreenName.matches(s".*${screenName}.*")
    }).foreach(r => {
      println("hit rule action")
      val idOrName = r.head._1.split('.').last
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
          getElementId(v) match {
            case Some(e) => {
              println(s"element=${e} action=${action}")
              clickedList.append(e.toString())
              doAppiumAction(v, action)
            }
            case None => println("get none")
          }

        }
        case None => {
          println("can't find")
        }
      }
    })

  }

}
