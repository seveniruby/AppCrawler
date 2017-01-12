package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler
import com.xueqiu.qa.appcrawler._
import org.scalatest.FunSuite
import play.twirl.api.BaseScalaTemplate

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import org.fusesource.scalate._


/**
  * Created by seveniruby on 2017/1/7.
  */
class TestTemplate extends FunSuite {

  test("string") {
    import play.twirl.api.StringInterpolation
    import play.twirl.api.Html
    val name = "Martin\n3333\nsss"
    val dd = s"xxx${name}"
    println((dd))

    val ddo = "xxx${name}"
    println(new StringContext(ddo).s())

  }

  test("scalate") {

    val engine = new TemplateEngine
    val output = engine.layout("src/test/scala/com/xueqiu/qa/appcrawler/ut/scalate.ssp")
    println(output)
  }

  test("读取elements结果并生成page object代码模板") {

    val engine = new TemplateEngine

    val path = "/Users/seveniruby/projects/AppCrawlerSuite/AppCrawler/android_20170109145102/elements.yml"
    val store = (DataObject.fromYaml[UrlElementStore](Source.fromFile(path).mkString)).elementStore
    val elements = mutable.HashMap[String, ListBuffer[Map[String, Any]]]()
    store.foreach(s => {
      val reqDom = s._2.reqDom
      val url = s._2.element.url
      if (reqDom.size != 0) {
        val doc = RichData.toDocument(reqDom)

        if (elements.contains(url) == false) {
          elements.put(url, ListBuffer[Map[String, Any]]())
        }
        elements(url) ++= RichData.getListFromXPath("//*[@content-desc!='' or @resource-id != '' or @text!='']", doc)
        elements(url) = elements(url).distinct
      }

    })

    elements.foreach(e => {
      val file = e._1
      println(s"file=${file}")
      e._2.foreach(m => {
        val name = m("name")
        val value = m("value")
        val label = m("label")
        val xpath = m("xpath")

        println(s"name=${name} label=${label} value=${value} xpath=${xpath}")
      })

      val output = engine.layout("src/test/scala/com/xueqiu/qa/appcrawler/ut/PageObjectDemo.java.ssp",
        Map("file"->file, "elements"->elements(file)))
      println(output)



    })




  }

  test("test getpagesource "){
    val t=new appcrawler.Template
    t.getPageSource("http://localhost:8100")

  }

}
