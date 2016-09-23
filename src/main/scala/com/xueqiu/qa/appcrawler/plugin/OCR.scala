package com.xueqiu.qa.appcrawler.plugin

import java.awt.BasicStroke
import javax.imageio.ImageIO

import com.xueqiu.qa.appcrawler.Plugin
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel
import net.sourceforge.tess4j.Tesseract
import scala.collection.JavaConversions._


/**
  * Created by seveniruby on 16/9/20.
  */
class OCR extends Plugin{

  val api=new Tesseract()
  api.setDatapath("/Users/seveniruby/Downloads/")
  api.setLanguage("eng+chi_sim")

  override def getPageSource(): String ={
    val imgName=getCrawler().getBasePathName()+".ori.jpg"
    log.info(imgName)
    val img=new java.io.File(imgName)
    val imgFile=ImageIO.read(img)
    val graph=imgFile.createGraphics()
    graph.setStroke(new BasicStroke(5))

    val words=api.getWords(imgFile, TessPageIteratorLevel.RIL_WORD).toList
    words.foreach(word=>{
      val box=word.getBoundingBox
      val x=box.getX.toInt
      val y=box.getY.toInt
      val w=box.getWidth.toInt
      val h=box.getHeight.toInt

      graph.drawRect(x, y, w, h)
      graph.drawString(word.getText, x, y)

      println(word.getBoundingBox)
      println(word.getText)


    })
    graph.dispose()
    ImageIO.write(imgFile, "png", new java.io.File(s"${img}.ocr.png"))
    ""

  }

  def toXml(text:String): Unit ={
    s"""
      | <android.widget.TextView bounds="[320,60][448,104]"
      |                checkable="false" checked="false"
      |                class="android.widget.TextView" clickable="false"
      |                content-desc="" enabled="true" focusable="false"
      |                focused="false" index="0" instance="0"
      |                long-clickable="false" package=""
      |                password="false"
      |                resource-id=""
      |                scrollable="false" selected="false" text="${xml.Utility.escape(text)}"/>
      |
    """.stripMargin
  }

}
