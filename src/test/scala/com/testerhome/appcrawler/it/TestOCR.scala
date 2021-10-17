/*

package com.ceshiren.appcrawler.ut

import java.awt.{BasicStroke, Color}
import javax.imageio.ImageIO

import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel
import org.scalatest.FunSuite
import net.sourceforge.tess4j._
import scala.collection.JavaConversions._


/**
  * Created by seveniruby on 16/9/20.
  */
class TestOCR extends FunSuite{

  test("test ocr"){
    val api=new Tesseract()
    api.setTessVariable("TESSDATA_PREFIX", "/Users/seveniruby/temp/ocr/")
    api.setDatapath("/Users/seveniruby/temp/ocr/tessdata/")
    api.setLanguage("eng+chi_sim")

    //val path = "/Users/seveniruby/temp/ocr/ocr.png"
    //val path="/Users/seveniruby/temp/ocr/103_com.gotokeep.keep-PersonalPageActivity_android.widget.RelativeLayout-title_bar-android.widget.RelativeLayout-left_button.clicked.png"
    val path="/Users/seveniruby/temp/ocr/t1t.jpeg"
    val img=new java.io.File(path)
    val imgFile=ImageIO.read(img)
    val graph=imgFile.createGraphics()
    graph.setStroke(new BasicStroke(5))
    graph.setColor(Color.RED)

    val result=api.doOCR(img)

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
    ImageIO.write(imgFile, "png", new java.io.File(s"${img}.mark.png"))



    println(result)

  }

}
*/
