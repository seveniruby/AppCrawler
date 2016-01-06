import java.io.{ByteArrayInputStream, StringWriter}
import java.nio.charset.StandardCharsets
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.xpath.{XPath, XPathFactory, _}

import io.appium.java_client.AppiumDriver
import org.apache.commons.io.FileUtils
import org.apache.xml.serialize.{OutputFormat, XMLSerializer}
import org.openqa.selenium.{OutputType, TakesScreenshot, WebElement}
import org.w3c.dom.{Attr, Document, NodeList}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}
import scala.reflect.io.File
import scala.util.control.Breaks._
import scala.util.{Failure, Success, Try}





/**
  * Created by seveniruby on 15/11/28.
  */
class Traversal {
  implicit var driver: AppiumDriver[WebElement] = _

  private val elements: scala.collection.mutable.Map[String, Boolean] = scala.collection.mutable.Map()
  //包括backButton
  /**黑名单列表 matches风格*/
  val blackList = ListBuffer[String]()

  /**引导规则. name, value, times三个元素组成*/
  val rule = ListBuffer[scala.collection.mutable.Map[String, Any]]()
  private var isSkip = false
  /**点击顺序, 留作画图用*/
  val clickedList = ListBuffer[String]()
  val timestamp = new java.text.SimpleDateFormat("YYYYMMddHHmm").format(new java.util.Date())
  var md5Last = ""
  var automationName = "appium"
  var platformName = ""

  /**后退按钮标记, 主要用于iOS, xpath*/
  var backButton = ListBuffer[String]()
  //意义不大. 并不是真正的层次
  var depth=0
  /**优先遍历元素*/
  val firstList=ListBuffer[String]()
  /**默认遍历列表*/
  val selectedList = ListBuffer[String]()
  /**最后遍历列表*/
  val appendList=ListBuffer[String]()

  /**url黑名单.用于排除某些页面 contains风格. 不过最好还是正则比较好*/
  val blackUrlList = ListBuffer("StockMoreInfoActivity", "UserProfileActivity")

  var pageSource=""
  private var pageDom:Document=null
  private var img_index=0
  private var backRetry=0
  private var needExit=false

  /**当前的url路径*/
  var url=""
  /**用来确定url的元素定位xpath 他的text会被取出当作url因素*/
  var urlXPath=""
  /**设置一个起始url和maxDepth, 用来在遍历时候指定初始状态和遍历深度*/
  var baseUrl=""
  /**默认的最大深度10, 结合baseUrl可很好的控制遍历的范围*/
  var maxDepth=10

  val urlStack=mutable.Stack[String]()

  def setupApp(app: String, url: String = "http://127.0.0.1:4723/wd/hub"): AppiumDriver[WebElement] ={
    return null
  }


  def black(keys: String*): Unit = {
    keys.foreach(blackList.append(_))
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


  def rule(loc: String, action: String, times:Int=0): Unit = {
    rule.append(Map(
      "idOrName" -> loc,
      "action"->action,
      "times"->times))
  }


  def parseXml(raw:String): Document ={
    val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    val document: Document = builder.parse(new ByteArrayInputStream(raw.replaceAll("[\\x00-\\x1F]", "").getBytes(StandardCharsets.UTF_8)))

    val format = new OutputFormat(document); //document is an instance of org.w3c.dom.Document
    format.setLineWidth(65);
    format.setIndenting(true);
    format.setIndent(2);
    val out = new StringWriter();
    val serializer = new XMLSerializer(out, format);
    serializer.serialize(document);
    val formattedXML = out.toString();
    println(formattedXML)
    return document
  }

  /**
    * 根据xpath来获得想要的元素列表
    * @param xpath
    * @return
    */
  def getAllElements(xpath: String): ListBuffer[Map[String, String]] = {
    val nodeList = ListBuffer[Map[String, String]]()

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val compexp = xPath.compile(xpath)
    val node = compexp.evaluate(pageDom, XPathConstants.NODESET)
    node match {
      case n: NodeList => {
        println(s"xpath=${xpath} length=${n.getLength}")
        0 until n.getLength foreach (i => {
          val nodeMap = Map[String, String]()
          nodeMap("tag") = n.item(i).getNodeName
          val nodeAttributes = n.item(i).getAttributes
          0 until nodeAttributes.getLength foreach (a => {
            val attr = nodeAttributes.item(a).asInstanceOf[Attr]
            nodeMap(attr.getName) = attr.getValue
          })
          if(!nodeMap.contains("name")){
            nodeMap("name")=""
            nodeMap("value")=""
          }
          if(nodeMap.contains("resource-id")){
            //todo: /结尾的会被解释为/之前的内容
            val arr=nodeMap("resource-id").split('/')
            if(arr.length==1){
              nodeMap("name") = ""
            }else {
              nodeMap("name") = nodeMap("resource-id").split('/').last
            }
          }
          if(nodeMap.contains("text")){
            nodeMap("value")=nodeMap("text")
          }
          nodeList.append(nodeMap)
        })
      }
      case _ => println("typecast to NodeList failed")
    }
    return nodeList

  }

  /**
    * 尝试识别当前的页面
    * @return
    */
  def getSchema(): String ={
    return ""
  }

  def getUrl(): String = {
    if(urlXPath!="") {
      return getAllElements(urlXPath).map(_("value")).lift(0).getOrElse("")
    }
    return ""
  }

  /**
    * 获取控件的基本属性并设置一个唯一的uid作为识别. screenName+id+name
    * @param x
    * @return
    */
  def getElementId(x: Map[String, String]): Option[ELement] = {
    //控件的类型
    val tag = x.getOrElse("tag", "NoTag")

    //name为Android的description/text属性, 或者iOS的value属性
    val name = x.getOrElse("value", "").replace("\n", "\\n")
    //name为id/name属性. 为空的时候为value属性

    //id表示android的resource-id或者iOS的name属性
    val resourceId = x.getOrElse("name", "")
    val id = resourceId.split('/').last
    println("id=xx")
    println(resourceId)
    println(id)

    val node = ELement(url, tag, id, name)
    return Some(node)

    //    if (List("android", "com.xueqiu.android").contains(appName)) {
    //      return Some(node)
    //    } else {
    //      return None
    //    }

  }

  def isReturn(): Boolean = {
    if (url.matches("Launcher.*")) {
      println(s"maybe back to desktop ${urlStack.reverse.mkString("-")}")
      needExit=true
    }
    if (blackUrlList.filter(url.contains(_)).length > 0) {
      println("should return")
      return true
    }
    if(urlStack.length>maxDepth){
      println(s"urlStack.depth=${urlStack.length} > maxDepth=${maxDepth}")
      return true
    }
    return false

  }

  /**
    * 黑名单过滤. 通过正则匹配
    * @param uid
    * @return
    */
  def isBlack(uid: Map[String,String]): Boolean = {
    blackList.filter(b => {
      uid("value").matches(b) || uid("name").matches(b)
    }).length>=1
  }


  def getClickableElements(): Option[Seq[Map[String, String]]] = {
    var all = Seq[Map[String, String]]()
    var firstElements=Seq[Map[String, String]]()
    var appendElements=Seq[Map[String, String]]()
    var commonElements=Seq[Map[String, String]]()

    firstList.foreach(xpath => {
      firstElements ++= getAllElements(xpath)
    })

    appendList.foreach(xpath => {
      appendElements ++= getAllElements(xpath)
    })

    selectedList.foreach(xpath => {
      commonElements ++= getAllElements(xpath)
    })

    commonElements=commonElements diff firstElements
    commonElements=commonElements diff appendElements

    all = (firstElements++commonElements++appendElements).distinct
    println(s"all length=${all.length}")
    return Some(all)

  }

  def first(xpath:String): Unit ={
    firstList.append(xpath)
  }
  def last(xpath:String): Unit ={
    appendList.append(xpath)
  }
  def back(name: String): Unit = {
    backButton.append(name)
    backButton.foreach(black(_))
  }

  def refreshPage(): Unit ={
    //获取页面结构, 最多重试10次.
    var refreshFinish=false
    pageSource=""
    1 to 10 foreach(i=>{
      if(refreshFinish==false) {
        doAppium(driver.getPageSource) match {
          case Some(v) => {
            println("get page source success")
            pageSource = v
            pageDom=parseXml(pageSource)
            refreshFinish = true
          }
          case None => {
            println("get page source error")
          }
        }
      }
    })
    if(refreshFinish==false){
      print("retry time > 10 exit")
      System.exit(0)
    }
    val currentUrl=getUrl()
    //保存url深度
    if(urlStack.contains(currentUrl)){
      while(urlStack.head!=currentUrl){
        urlStack.pop()
      }
    }else {
      urlStack.push(currentUrl)
    }
    //判断新的url堆栈中是否包含baseUrl, 如果有就清空栈记录并从新计数
    if(urlStack.head.matches(baseUrl)) {
      urlStack.clear()
      urlStack.push(currentUrl)
    }
    url=urlStack.reverse.takeRight(3).mkString("-")
    println(s"urlStack=${urlStack.reverse}")
    val contexts=doAppium(driver.getContextHandles).getOrElse("")
    //val windows=doAppium(driver.getWindowHandles).getOrElse("")
    val windows=""
    println(s"context=${contexts} windows=${windows}")
    println("schema="+getSchema())
  }

  /*
  /**
    * 最早实现的一个递归算法. 不是尾递归. 代码也很挫, 留作娱乐
    */
  def traversal(): Unit = {
    println("traversal start")
    //等待一秒防止太快
    Thread.sleep(500)
    depth+=1
    println(s"depth=${depth}")
    println("refresh page")
    refreshPage()
    var needBack = true
    var needSkip = false
    needBack = !isReturn()
    //先判断是否命中规则.
    doRuleAction()
    //在selendroid的JsonXmlUtil.java:70有个bug. 导致无法获取当前界面的所有元素. ^_^
    val all = getClickableElements().getOrElse(Seq[Map[String, String]]())
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
            println("break")
            break()
          } else {
            println("no need to break")
          }
          //是否黑名单
          needSkip = isBlack(x)
          if(needSkip==true){
            println("in black list")
          }else{
            println("not in black list")
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

          //是否已经点击过
          //todo: 新界面入口需要设置为false
          if(needSkip==false) {
            if (elements.contains(uid.toString())) {
              println("skip")
              needSkip = true
            } else {
              needSkip = false
              println("first show, need click")
            }
          }
          //如果未曾点击
          if (needSkip == false) {
            println("just click")
            elements(uid.toString()) = true
            //doDefaultAction(uid)
            doAppiumAction(uid, "click") match {
              case Some(v) => {
                println("do appium action success")
              }
              case None => {
                println("do appium action exception, break")
              }
            }
            //说明还不需要back到上一界面, 遍历完所有的元素才表示需要回退
            needBack = false
            //任何点击都需要重新刷新元素. 防止其他元素被遮盖

            //任何界面变化都需要进入新的递归. 而不是只到新界面.
            traversal()

          } else {
            println("already clicked, so skip")
          }
        })
      }
    }
    //子界面遍历返回后继续遍历当前界面中剩下的界面
    if (needBack == true) {
      println("back")
      if (backButton == "") {
        //todo: iOS上的back貌似有问题
        driver.navigate().back()
      } else {
        getAllElements(backButton).lift(0) match {
          case Some(v)=>{
            getElementId(v) match {
              case Some(element)=>{
                doAppiumAction(element, "click")
              }
            }
          }
        }
      }

      saveScreen(ELement(url, "Back", "Back", "Back"))
      //任何界面变化都需要进入新的递归. 而不是只到新界面.
      traversal()

    }
    depth-=1
  }
*/

  def isClicked(ele: ELement): Boolean ={
    if (elements.contains(ele.toString())) {
      return elements(ele.toString())
    } else {
      println(s"element=${ele} first show, need click")
      return false
    }
  }

  def clickElement(uid:ELement): Unit ={
    println(s"just click ${uid}")
    elements(uid.toString()) = true
    //doDefaultAction(uid)
    doAppiumAction(uid, "click") match {
      case Some(v) => {
        println("do appium action success")
      }
      case None => {
        println("do appium action exception, break")
      }
    }
  }
  def goBack(): Unit ={
    println("go back")
    if (backButton.length == 0) {
      //todo: iOS上的back貌似有问题
      driver.navigate().back()
      saveScreen(ELement(url, "Back", "Back", "Back"))
    } else {
      //找到可能的关闭按钮, 取第一个可用的关闭按钮
      backButton.map(getAllElements(_)).flatten.lift(0) match {
        case Some(v)=>{
          getElementId(v) match {
            case Some(element)=>{
              doAppiumAction(element, "click")
            }
            case None=>{println("几乎不会发生这个异常")}
          }
        }
        case None=>println("find back button error")
      }
    }
    backRetry+=1
    //超过十次连续不停的回退就认为是需要退出
    if(backRetry>10){
      needExit=true
    }else{
      println(s"backRetry=${backRetry}")
    }
  }

  /**
    * 优化后的递归方法. 尾递归.
    */
  def traversal(): Unit = {
    if(needExit){
      return
    }
    println("traversal start")
    //等待一秒防止太快
    Thread.sleep(500)
    depth+=1
    println(s"depth=${depth}")
    println("refresh page")
    refreshPage()
    //是否需要退出或者后退
    if (isReturn()) {
      goBack()
      depth-=1
    } else {
      //先判断是否命中规则.
      if(doRuleAction()==false){
        //获取可点击元素
        var all = getClickableElements().getOrElse(Seq[Map[String, String]]())
        println(s"all nodes length=${all.length}")
        //去掉黑名单, 这样rule优先级高于黑名单
        all = all.filter(isBlack(_) == false)
        println(all.length)
        println(s"all non-black nodes length=${all.length}")
        //把元素转换为Element对象
        var allElements = all.map(getElementId(_).get)
        //获得所有未点击元素
        println(s"all elements length=${allElements.length}")
        //过滤已经被点击过的元素
        allElements = allElements.filter(!isClicked(_))
        println(s"fresh elements length=${allElements.length}")
        //记录未被点击的元素
        allElements.foreach(e=>{
          if(!elements.contains(e.toString())){
            elements(e.toString())=false
          }
        })
        if (allElements.length > 0) {
          clickElement(allElements.head)
          backRetry=0
        } else {
          goBack()
          depth -= 1
        }
      }
    }
    traversal()
  }


  //todo:优化查找方法
  //找到统一的定位方法就在这里定义, 找不到就分别在子类中重载定义
  def findElementByUid(uid: ELement): Option[WebElement] = {
    println(s"find element by uid ${uid}")
    platformName.toLowerCase() match {
      case "ios"=>{
        println(s"find by xpath")
        //照顾iOS android会在findByName的时候自动找text属性.
        doAppium(driver.findElementByXPath(s"//${uid.tag}[@name='${uid.id}' and @value='${uid.name}']")) match {
          case Some(v) => return Some(v)
          case None => {}
        }
      }
      case "android"=>{
        if(uid.id !="") {
          println(s"find by id=${uid.id}")
          doAppium(driver.findElementsById(uid.id)) match {
            case Some(v) => {
              if(v.toArray.length==1){
                //公司的首页有4个id一摸一样的控件, 已经通知他们修改. 这是个临时性的方案.
                return Some(v.toArray().head.asInstanceOf[WebElement])
              }else{
                println("find multi, change to find by name")
              }
            }
            case None => {}
          }
        }
        if(uid.name!=""){
          println(s"find by name=${uid.name}")
          doAppium(driver.findElementByName(uid.name)) match {
            case Some(v) => return Some(v)
            case None => {
            }
          }
        }
      }
    }
    return None
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

  def doAppiumActionByName(name: String, action: String = "click"): Option[Unit] = {
    val element = ELement("", "*", "", name)
    doAppiumAction(element, action)
  }

  def saveLog(): Unit ={
    //记录点击log
    if(new java.io.File(s"${platformName}_${timestamp}").exists()==false){
      FileUtils.forceMkdir(new java.io.File(s"${platformName}_${timestamp}"))
    }
    File(s"${platformName}_${timestamp}/clickedList.log").writeAll(clickedList.mkString("\n"))
    File(s"${platformName}_${timestamp}/ElementList.log").writeAll(elements.mkString("\n"))
  }

  def saveScreen(e: ELement): Unit ={
    Thread.sleep(1000)
    img_index+=1
    val path=s"${platformName}_${timestamp}/${img_index}_"+e.toString().replace("\n", "").replaceAll("[ /,]", "").take(200)+".jpg"
    doAppium((driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)) match {
      case Some(src)=>{
        FileUtils.copyFile(src, new java.io.File(path))
      }
      case None=>{
        println("get screenshot error")
      }
    }
  }

  def doAppiumAction(e: ELement, action: String = "click"): Option[Unit] = {
    findElementByUid(e) match {
      case Some(v) => {
        action match {
          case "click" => {
            println(s"click ${v}")
            val res = doAppium(v.click())
            clickedList.append(e.toString())
            saveLog()
            saveScreen(e)
            doAppium(driver.hideKeyboard())
            return res
          }
          case str: String => {
            println(s"sendkeys ${v} with ${str}")
            doAppium(v.sendKeys(str)) match {
              case Some(v) => {
                clickedList.append(e.toString())
                saveLog()
                doAppium(driver.hideKeyboard())
                return Some(v)
              }
              case None => return None
            }
          }
        }

      }
      case None => {
        println("find error")
        return None
      }
    }


  }

  /**
    * 子类重载
    * @return
    */
  def getRuleMatchNodes(): ListBuffer[Map[String, String]] ={
    return ListBuffer[Map[String, String]]()
  }

  //通过规则实现操作. 不管元素是否被点击过
  def doRuleAction(): Boolean = {
    println("rule match start")
    //先判断是否在期望的界面里. 提升速度
    var isHit=false
    rule.foreach(r => {
      println("for each rule")
      val idOrName = r("idOrName").toString.split('.').last
      val action = r("action").toString
      val times=r("times").toString.toInt
      println(s"idOrName=${idOrName} action=${action}")
      val all = getRuleMatchNodes()
      breakable{
        (all.filter(_ ("name").matches(idOrName)) ++ all.filter(_ ("value").matches(idOrName))).distinct.foreach(x => {
        println("hit rule action")
        println(x)
        //获得正式的定位id
        getElementId(x) match {
          case Some(e) => {
            println(s"element=${e} action=${action}")
            println("do rule action")
            isHit=true
            doAppiumAction(e, action.toString) match {
              case None=>{
                println("do rule action fail")
                break()
              }
              case Some(v)=>{
                println("do rule action success")
                r("times")=times-1
                if(times==1){
                  rule-=r
                }
              }
            }
            //todo: 暂不删除, 允许复用
            //rule -= r
          }
          case None => println("get element id error")
        }
      })
      }
    })

    return isHit

  }


}

object Traversal{
  def sbt(args: String): Unit = {
    import scala.sys.process._
    //val sbt="/usr/local/Cellar/sbt/0.13.8/libexec/sbt-launch.jar"
    val project_dir=getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath.
      split("/").dropRight(2).mkString("/")
    val sbt = s"${project_dir}/lib/sbt-launch.jar"
    val cmd = Seq("java", "-jar", sbt, args) // You
    println(cmd)
    cmd ! ProcessLogger(stdout append _ + "\n", stderr append _ + "\n")
  }

  def main(args: Array[String]) {
    args.foreach(arg=>{
      arg.toLowerCase() match {
        case "ios"=>{
          sbt("test-only iOS")
        }
        case "android"=>{
          sbt("test-only Android")
        }
        case cmd: String =>{
          sbt(s"test-only ${cmd}")
        }
      }
    })

  }

}
