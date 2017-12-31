package com.testerhome.appcrawler.driver

import java.awt.{BasicStroke, Color, Image}
import java.io.{File, FilenameFilter}
import java.time.Duration
import javax.imageio.ImageIO

import com.testerhome.appcrawler.{TData, URIElement}
import io.appium.java_client.TouchAction
import org.openqa.selenium.Rectangle
import org.sikuli.script
import org.sikuli.script.{Finder, Pattern}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class SikuliDriver extends AppiumClient {
  private var images=ListBuffer[Pattern]()
  val result=ListBuffer[URIElement]()

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()) {
    this
    appium(url, configMap)
  }

  override def tap(): this.type = {
    val point=currentURIElement.center()
    driver.performTouchAction(new TouchAction(driver).tap(point.x, point.y))
    this
  }

  override def press(sec:Int): this.type = {
    val point=currentURIElement.center()
    driver.performTouchAction(new TouchAction(driver).press(point.x, point.y).waitAction(Duration.ofSeconds(sec)).release())
    this
  }
  override def getRect(): Rectangle ={
    val location=currentURIElement.location
    val size=currentURIElement.size
    new Rectangle(location.x, location.y, size.height, size.width)
  }


  override def findElementsByURI(element: URIElement): List[AnyRef] = {
    List(element)
  }
  override def findElementByURI(element: URIElement): AnyRef = {
    currentURIElement=element
    currentURIElement
  }

  //todo: 截图
  override def getPageSource(): String = {
    val screen=screenshot()

    val imgFile=ImageIO.read(screen)
    val graph=imgFile.createGraphics()
    graph.setStroke(new BasicStroke(4))
    graph.setColor(Color.RED)


    val finder=new Finder(ImageIO.read(screen))
    //todo: 递归
    if(images.isEmpty){
      log.info("load images from images directory")
      new File(imagesDir).listFiles().foreach(f=>{
        log.info(s"load image ${f.getCanonicalPath}")
        images.append(new Pattern(f.getCanonicalPath).similar(0.4F))
      })
    }


    result.clear()
    images.foreach(img=>{
      finder.findAll(img)
      val temp=ListBuffer[URIElement]()
      while(finder.hasNext){
        val e=finder.next()
        val size=e.getImage.getSize
        val x=e.getTarget.x-size.width/2
        val y=e.getTarget.y-size.height/2
        val name=e.getImageFilename.split(File.separator).last+"_"+"%.4f".format(e.getScore)

        graph.drawRect(x, y, size.width, size.height)
        graph.drawString(name, x, y )

        val element=URIElement(
          id=e.getScore.toString,
          name=name,
          x=x,
          y=y,
          width = size.width,
          height = size.height,
          tag=s"Image",
          loc = System.currentTimeMillis().toString
        )
        temp.append(element)
      }
      if(temp.nonEmpty) {
        val best = temp.sortBy(_.id.toFloat).last
        result.append(best)
      }


    })
    finder.destroy()

    graph.dispose()
    ImageIO.write(imgFile, "png", new java.io.File(s"${}.layout.png"))

    TData.toXML(Map("sikuli"->Map("element"->result)))
  }

  override def getAppName(): String = {
    "sikuli"
  }

  //todo: 重构到独立的trait中
  override def mark(fileName: String, newImageName:String,  x: Int, y: Int, w: Int, h: Int): Unit = {
    super.mark(fileName, newImageName, x, y, w, h)

    val layoutName=newImageName.replaceAllLiterally(".click.", ".layout.")

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

    result.foreach(element=>{
      graph.drawRect(element.x, element.y, element.width, element.height)
      graph.drawString(element.name, element.center().x, element.center().y)
    })
    graph.dispose()

    log.info(s"write png ${fileName}")
    if(img.getWidth>screenWidth){
      log.info("scale the origin image and save")
      val subImg=img.getSubimage(0, 0, screenWidth, screenHeight)
      ImageIO.write(subImg, "png", new java.io.File(layoutName))
    }else{
      ImageIO.write(img, "png", new java.io.File(layoutName))
    }

    //绘制其他的布局

  }
}
