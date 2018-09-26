package com.testerhome.appcrawler.driver

import java.awt.{BasicStroke, Color}
import java.io.File
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

import com.testerhome.appcrawler.{AppCrawler, URIElement, _}
import io.appium.java_client.touch.offset.{ElementOption, PointOption}
import io.appium.java_client.touch.{LongPressOptions, TapOptions}
import io.appium.java_client.{AppiumDriver, TouchAction}
import javax.imageio.ImageIO
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.{OutputType, Rectangle, TakesScreenshot, WebElement}

import scala.collection.JavaConverters._
import scala.sys.process._

/**
  * Created by seveniruby on 16/8/9.
  */
class SeleniumDriver extends ReactWebDriver{
  Util.init()
  var conf: CrawlerConf = _

  val capabilities = new DesiredCapabilities()
  var driver: RemoteWebDriver = _
  var currentElement:WebElement=_

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()) {
    this
    log.addAppender(AppCrawler.fileAppender)
    log.info(s"url=${url}")

    configMap.foreach(c=>config(c._1, c._2))


    driver=new RemoteWebDriver(new URL(url), capabilities)
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
    getDeviceInfo()
    log.info(s"capture dir = ${new File(".").getAbsolutePath}")

    if(configMap.contains("app")){
      log.error("please set app with url for your site")
      driver.get(configMap.getOrElse("app", "https://www.baidu.com/").toString)

    }

  }


  override def event(keycode: Int): Unit = {
    log.error("not implement")
  }

  override def getDeviceInfo(): Unit = {
    val size = driver.manage().window().getSize
    screenHeight = size.getHeight
    screenWidth = size.getWidth
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }


  override def swipe(startX: Double = 0.9, startY: Double = 0.1, endX: Double = 0.9, endY: Double = 0.1): Unit = {
    log.error("not implement")
  }


  override def screenshot(): File = {
    (driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)
  }

  //todo: 重构到独立的trait中
  override def mark(fileName: String, newImageName:String,  x: Int, y: Int, w: Int, h: Int): Unit = {
    val file = new java.io.File(fileName)
    log.info(s"read from ${fileName}")
    val img = ImageIO.read(file)
    val graph = img.createGraphics()

    if(img.getWidth>screenWidth){
      log.info("scale the origin image")
      graph.drawImage(img, 0, 0, screenWidth, screenHeight, null)
    }
    graph.setStroke(new BasicStroke(5))
    graph.setColor(Color.RED)
    graph.drawRect(x, y, w, h)
    graph.dispose()

    log.info(s"write png ${fileName}")
    if(img.getWidth>screenWidth){
      log.info("scale the origin image and save")
      //fixed: RasterFormatException: (y + height) is outside of Raster 横屏需要处理异常
      val subImg=tryAndCatch(img.getSubimage(0, 0, screenWidth, screenHeight)) match {
        case Some(value)=>value
        case None => {
          getDeviceInfo()
          img.getSubimage(0, 0, screenWidth, screenHeight)
        }
      }
      ImageIO.write(subImg, "png", new java.io.File(newImageName))
    }else{
      log.info(s"ImageIO.write newImageName ${newImageName}")
      ImageIO.write(img, "png", new java.io.File(newImageName))
    }
  }
  override def clickLocation(): Unit = {
    val point=currentURIElement.center()
    new Actions(driver).moveByOffset(point.x, point.y).click().perform();
  }
  override def click(): this.type ={
    log.info(currentElement)
    currentElement.click()
    this
  }
  override def tap(): this.type = {
    click()
  }

  override def longTap(): this.type = {
    log.error("not implement")
    this
  }

  override def back(): Unit = {
    driver.navigate().back()
  }

  override def backApp(): Unit = {
    driver.navigate().back()
  }

  //todo:convert to xml
  override def getPageSource(): String = {
    val res=driver.executeScript(
      """
        |function getNodeTree(node) {
        |    var res = {}
        |    var tags = ["href", "class", "id", "name"]
        |    if (node.tagName != undefined
        |        && node.tagName != "SCRIPT"
        |        && node.tagName != "STYLE"
        |        && node.getBoundingClientRect().height != 0
        |    ) {
        |        res["tag"] = node.tagName.toLowerCase()
        |
        |        var rect = node.getBoundingClientRect()
        |        res["x"] = rect.x
        |        res["y"] = rect.y
        |        res["width"] = rect.width
        |        res["height"] = rect.height
        |        var attributes = node.attributes
        |        if (attributes != undefined) {
        |            for (var i = 0; i < attributes.length - 1; i++) {
        |                if (tags.indexOf(attributes[i].name) > -1) {
        |                    res[attributes[i].name] = attributes[i].value
        |                }
        |            }
        |        }
        |
        |        if (node.tagName == "BODY"
        |            || node.innerText == undefined) {
        |            res["innerText"] = ""
        |        } else {
        |            res["innerText"] = node.innerText.split("\n", 1)[0] || ""
        |        }
        |
        |        if (node.hasChildNodes()) {
        |            var children = [];
        |            for (var j = 0; j < node.childNodes.length; j++) {
        |                var subNode = getNodeTree(node.childNodes[j])
        |                if (subNode["tag"] != undefined) {
        |                    children.push(subNode);
        |                }
        |            }
        |            res["children"] = children
        |        } else {
        |            res["children"] = []
        |        }
        |    }
        |    return res;
        |
        |}
        |
        |
        |return JSON.stringify(getNodeTree(document.body))
      """.stripMargin).toString
    val dataMap=TData.fromJson[Map[String, Any]](res)
    log.trace(dataMap)
    val xml=TData.toHtml(dataMap)
    log.trace(xml)
    xml
  }


  override def findElementsByURI(element: URIElement, findBy: String): List[AnyRef] = {
    //todo: 优化速度，个别时候定位可能超过10s
    //todo: 多种策略，使用findElement 使用xml直接分析location 生成平台特定的定位符

    element match {
      case id if element.id.nonEmpty && findBy=="id" =>{
        log.info(s"findElementsById ${element.id}")
        driver.findElementsById(element.id).asScala.toList
      }
      case name if element.name.nonEmpty && findBy=="accessibilityId" => {
        log.info(s"findElementsByAccessibilityId ${element.name}")
        driver.findElementsByName(element.name).asScala.toList
      }
      case _ => {
        //默认使用xpath
        log.info(s"findElementsByXPath ${element.xpath}")
        //driver.findElementsByXPath(element.xpath).asScala.toList
        List(driver.findElementByXPath(element.xpath))
      }
    }
  }

  override def findElementByURI(element: URIElement, findBy:String): AnyRef = {
    currentElement=super.findElementByURI(element,findBy).asInstanceOf[WebElement]
    currentElement
  }

  override def getAppName(): String = {
    tryAndCatch(
    new URL(driver.getCurrentUrl).getHost).getOrElse("default")
  }

  override def getUrl(): String = {
    driver.getCurrentUrl.split("/").last
  }

  override def getRect(): Rectangle ={
    //selenium下还没有正确的赋值，只能通过api获取
    if(currentURIElement.height!=0){
      //log.info(s"location=${location} size=${size} x=${currentURIElement.x} y=${currentURIElement.y} width=${currentURIElement.width} height=${currentURIElement.height}" )
      new Rectangle(currentURIElement.x, currentURIElement.y, currentURIElement.height, currentURIElement.width)
    }else {
      val location = currentElement.getLocation
      val size = currentElement.getSize
      new Rectangle(location.x, location.y, size.height, size.width)
    }
  }

  override def sendKeys(content: String): Unit = {
    currentElement.sendKeys(content)
  }

  override def launchApp(): Unit = {
    //driver.get(capabilities.getCapability("app").toString)
    back()
  }


  def config(key: String, value: Any): Unit = {
    capabilities.setCapability(key, value)
  }

}

