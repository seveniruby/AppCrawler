package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.AppCrawler
import com.ceshiren.appcrawler.core.CrawlerConf
import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.LogicUtils.tryAndCatch
import com.ceshiren.appcrawler.utils.{DynamicEval, TData}
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.{OutputType, Rectangle, TakesScreenshot, WebElement}

import java.awt.{BasicStroke, Color}
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import scala.jdk.CollectionConverters._

/**
 * Created by seveniruby on 16/8/9.
 */
class SeleniumDriver extends ReactWebDriver {
  DynamicEval.init()
  var conf: CrawlerConf = _

  val capabilities = new DesiredCapabilities()
  var driver: RemoteWebDriver = _
  var currentElement: WebElement = _

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any] = Map[String, Any]()) {
    this

    log.info(s"url=${url}")

    configMap.foreach(c => config(c._1, c._2))

    driver = new RemoteWebDriver(new URL(url), capabilities)
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
    getDeviceInfo()
    log.info(s"capture dir = ${new File(".").getAbsolutePath}")

    if (configMap.contains("app")) {
      log.error("please set app with url for your site")
      driver.get(configMap.getOrElse("app", "https://www.baidu.com/").toString)

    }

  }


  override def event(keycode: String): Unit = {
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

  override def clickLocation(): Unit = {
    val point = currentURIElement.center()
    new Actions(driver).moveByOffset(point.x, point.y).click().perform();
  }

  override def click(): this.type = {
    currentElement.click()
    this
  }

  override def tap(): this.type = {
    click()
  }

  override def tapLocation(x: Int, y: Int): this.type = {
    this
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
    val res = driver.executeScript(
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
    val dataMap = TData.fromJson[Map[String, Any]](res)
    log.trace(dataMap)
    val xml = TData.toHtml(dataMap)
    log.debug(xml)
    xml
  }


  override def findElements(element: URIElement, findBy: String): List[AnyRef] = {
    //todo: 优化速度，个别时候定位可能超过10s
    //todo: 多种策略，使用findElement 使用xml直接分析location 生成平台特定的定位符

    element match {
      case id if element.getId.nonEmpty && findBy == "id" => {
        log.info(s"findElementsById ${element.getId}")
        driver.findElementsById(element.getId).asScala.toList
      }
      case name if element.getName.nonEmpty && findBy == "accessibilityId" => {
        log.info(s"findElementsByAccessibilityId ${element.getName}")
        driver.findElementsByName(element.getName).asScala.toList
      }
      case _ => {
        //默认使用xpath
        log.info(s"findElementsByXPath ${element.getXpath}")
        //driver.findElementsByXPath(element.xpath).asScala.toList
        List(driver.findElementByXPath(element.getXpath))
      }
    }
  }

  override def findElement(element: URIElement, findBy: String): AnyRef = {
    currentElement = super.findElement(element, findBy).asInstanceOf[WebElement]
    currentElement
  }

  override def getAppName(): String = {
    tryAndCatch(
      new URL(driver.getCurrentUrl).getHost).getOrElse("default")
  }

  override def getUrl(): String = {
    driver.getCurrentUrl.split("/").last
  }

  override def getRect(): Rectangle = {
    //selenium下还没有正确的赋值，只能通过api获取
    if (currentURIElement.getHeight != 0) {
      //log.info(s"location=${location} size=${size} x=${currentURIElement.x} y=${currentURIElement.y} width=${currentURIElement.width} height=${currentURIElement.height}" )
      new Rectangle(currentURIElement.getX, currentURIElement.getY, currentURIElement.getHeight, currentURIElement.getWidth)
    } else {
      val location = currentElement.getLocation
      val size = currentElement.getSize
      new Rectangle(location.x, location.y, size.height, size.width)
    }
  }

  override def adb(command: String): String = {
    ""
  }

  override def sendText(text: String): Unit = {

  }

  override def sendKeys(content: String): Unit = {
    currentElement.sendKeys(content)
  }

  override def launchApp(): Unit = {
    //driver.get(capabilities.getCapability("app").toString)
    back()
  }

  override def reStartDriver(): Unit = {
  }

  def config(key: String, value: Any): Unit = {
    capabilities.setCapability(key, value)
  }

  override def setWaitTimeOut(timeout: Long): Unit = {
    driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS)
  }

}

