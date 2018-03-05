package com.testerhome.appcrawler.it

import java.awt.{BasicStroke, Color, Dimension, Font}
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import org.scalatest.FunSuite
import org.sikuli.basics.Settings
import org.sikuli.script.{Finder, Image, Pattern, RunTime}

import scala.tools.jline_embedded.console.WCWidth

class TestSikuli extends FunSuite{
  test("find by image"){
    val sourceImg="/Users/seveniruby/temp/ocr/t1t.png"
    val findImg="/Users/seveniruby/temp/ocr/images/2.png"
    val finder=new Finder(sourceImg)
    finder.findAll(new Pattern(findImg))
    while(finder.hasNext){
      val e=finder.next()
      println(e.getScore)
      println(e.getTarget)
      println(e.getText)
    }
  }

  test("find by image xueqiu with gray"){


    //val sourceImg="/Users/seveniruby/temp/ocr/t1t.png"
    //val findImg="/Users/seveniruby/temp/ocr/images/2.png"


    val sourceImg="/Users/seveniruby/temp/ocr/xueqiu2.png"
    val findImg="/Users/seveniruby/temp/ocr/images/行情灰.png"

    val graySourceImg=Image.convertImageToGrayscale(ImageIO.read(new File(sourceImg)))
    val grayTargetImg=Image.convertImageToGrayscale(ImageIO.read(new File(findImg)))

    //val img=new java.io.File(sourceImg)
    //val imgFile=ImageIO.read(img)
    val graph=graySourceImg.createGraphics()
    graph.setStroke(new BasicStroke(4))
    graph.setColor(Color.RED)


    val finder=new Finder(graySourceImg)
    finder.findAll(new Pattern(grayTargetImg).similar(0.5F))
    while(finder.hasNext){
      val e=finder.next()
      println(e.getScore)
      println(e.getTarget.toJSON)
      e.getTargetOffset.toJSON
      println(e.getText)
      val x=e.getTarget.x
      val y=e.getTarget.y
      val xx=e.getImage.getSize.width
      val yy=e.getImage.getSize.height
      println(s"${x} ${y} ${xx} ${yy}")
      graph.drawRect(x, y, xx, yy)
      graph.drawString("%.2f".format(e.getScore), x, y )

    }

    graph.dispose()
    ImageIO.write(graySourceImg, "png", new java.io.File(s"${sourceImg}.mark.png"))
  }


  test("find by image xueqiu with color"){


    val sourceImg="/Users/seveniruby/temp/ocr/t1t.png"
    val findImg="/Users/seveniruby/temp/ocr/images/方形.png"


    //val sourceImg="/Users/seveniruby/temp/ocr/xueqiu2.png"
    //val findImg="/Users/seveniruby/temp/ocr/images/行情灰.png"

    val img=new java.io.File(sourceImg)
    val imgFile=ImageIO.read(img)
    val graph=imgFile.createGraphics()
    graph.setStroke(new BasicStroke(4))
    graph.setColor(Color.RED)


    val finder=new Finder(sourceImg)
    finder.findAll(new Pattern(findImg).similar(0.6F))
    while(finder.hasNext){
      val e=finder.next()
      println(e.getScore)
      println(e.getTarget.toJSON)
      e.getTargetOffset.toJSON
      println(e.getText)
      val x=e.getTarget.x
      val y=e.getTarget.y
      val xx=e.getImage.getSize.width
      val yy=e.getImage.getSize.height
      println(s"${x} ${y} ${xx} ${yy}")
      graph.drawRect(x, y, xx, yy)
      graph.drawString("%.2f".format(e.getScore), x, y )

    }

    graph.dispose()
    ImageIO.write(imgFile, "png", new java.io.File(s"${sourceImg}.mark.png"))
  }



  test("find by text"){

    //todo: 所使用的tesseract版本是3.0.2太低，需要匹配的数据
    println(System.getProperty("TESSDATA_PREFIX"))
    System.setProperty("TESSDATA_PREFIX", "/Users/seveniruby/temp/ocr/")
    println(System.getProperty("TESSDATA_PREFIX"))
    println(Settings.OcrDataPath)
    Settings.OcrDataPath="/Users/seveniruby/temp/ocr/"
    Settings.OcrLanguage="eng+chi_sim"
    RunTime.reset()
    //Settings.OcrTextSearch=true
    val sourceImg="/Users/seveniruby/temp/ocr/103_com.gotokeep.keep-PersonalPageActivity_android.widget.RelativeLayout-title_bar-android.widget.RelativeLayout-left_button.clicked"
    //val findImg="/Users/seveniruby/temp/ocr/2.png"
    val finder=new Finder(sourceImg)
    finder.findAllText("100")
    while(finder.hasNext){
      val e=finder.next()
      println(e.getScore)
      println(e.getTarget)
      println(e.getText)
    }
  }


  import java.awt.image.BufferedImage

  private def crop(src: BufferedImage, x: Int, y: Int, width: Int, height: Int) = {
    val dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
    val graphics = dest.getGraphics
    graphics.drawImage(src, 0, 0, width, height, x, y, x + width, y + height, null)
    graphics.dispose
    dest
  }

}