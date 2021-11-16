package com.ceshiren.appcrawler.model

import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.LogicUtils.tryAndCatch
import org.apache.commons.io.FileUtils

import java.awt.{BasicStroke, Color}
import java.io.File
import javax.imageio.ImageIO
import scala.collection.mutable

class ScreenShot {
  private var currentIMG: File = null;

  def this(file: File) {
    this
    currentIMG = file
  }

  def save(file: File): Unit = {
    currentIMG = file
  }

  def get(): File = {
    currentIMG
  }

  def saveToFile(path: String): Unit = {
    FileUtils.copyFile(currentIMG, new File(path))
  }

  def clip(newImageName: String, x: Int, y: Int, w: Int, h: Int, screenWidth: Int, screenHeight: Int): Unit = {
    val img = ImageIO.read(ScreenShot.last().get())
    val graph = img.createGraphics()

    if (img.getWidth > screenWidth) {
      log.info("scale the origin image")
      graph.drawImage(img, 0, 0, screenWidth, screenHeight, null)
    }
    graph.setStroke(new BasicStroke(5))
    graph.setColor(Color.RED)
    graph.drawRect(x, y, w, h)
    graph.dispose()

    log.info(s"write png ${newImageName}")
    if (img.getWidth > screenWidth) {
      log.info("scale the origin image and save")
      //fixed: RasterFormatException: (y + height) is outside of Raster 横屏需要处理异常
      val subImg = tryAndCatch(img.getSubimage(0, 0, screenWidth, screenHeight)) match {
        case Some(value) => value
        case None => {
          img.getSubimage(0, 0, screenWidth, screenHeight)
        }
      }
      ImageIO.write(subImg, "png", new java.io.File(newImageName))
    } else {
      log.info(s"ImageIO.write newImageName ${newImageName}")
      ImageIO.write(img, "png", new java.io.File(newImageName))
    }
  }
}

object ScreenShot {
  val queue = mutable.Queue[ScreenShot]()

  def save(file: File): Unit = {
    val screen = new ScreenShot(file)
    queue.enqueue(screen)
    if (queue.size > 2) {
      queue.dequeue()
    }
  }

  def last(): ScreenShot = {
    if (queue.nonEmpty) {
      queue.last
    } else {
      null
    }
  }

  def pre(): ScreenShot = {
    if (queue.size >= 2) {
      queue(queue.size - 2)
    } else {
      null
    }
  }
}
