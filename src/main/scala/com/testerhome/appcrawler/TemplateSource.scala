package com.testerhome.appcrawler

import java.io.File

import com.testerhome.appcrawler.data.{AbstractElementStore, PathElementStore}
import org.apache.commons.io.FileUtils
import org.fusesource.scalate.TemplateEngine

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import collection.JavaConversions._

/**
  * Created by seveniruby on 2017/1/10.
  */
class TemplateSource {

  val elements = mutable.HashMap[String, ListBuffer[Map[String, Any]]]()


  def getPageSource(url:String): Unit ={
    val page=Source.fromURL(s"${url}/source/xml").mkString
    val xml=TData.fromJson[Map[String, String]](page).getOrElse("value", "")
      .asInstanceOf[Map[String, String]].getOrElse("tree", "")
    elements("Demo")=ListBuffer[Map[String, Any]]()
    elements("Demo")++=XPathUtil.getNodeListByXPath("//*[]", xml)

  }
  def read(path:String): Unit = {

    //val path = "/Users/seveniruby/projects/AppCrawlerSuite/AppCrawler/android_20170109145102/elements.yml"
    val store = (TData.fromYaml[AbstractElementStore](Source.fromFile(path).mkString)).getElementStoreMap

    store.foreach(s => {
      val reqDom = s._2.getReqDom
      val url = s._2.getElement.getUrl
      if (reqDom.size != 0) {

        if (elements.contains(url) == false) {
          elements.put(url, ListBuffer[Map[String, Any]]())
        }
        elements(url) ++= XPathUtil.getNodeListByXPath("//*", reqDom)
        val tagsLimit=List("Image", "Button", "Text")
        elements(url) = elements(url)
          .filter(_.getOrElse("visible", "true")=="true")
          .filter(_.getOrElse("tag", "").toString.contains("StatusBar")==false)
          .filter(e=>tagsLimit.exists(t=>e.getOrElse("tag", "").toString.contains(t)))
          .distinct
      }

    })
  }

  def write(template:String, dir:String) {
    val engine = new TemplateEngine
    elements.foreach(e => {
      val file:String = e._1
      println(s"file=${file}")
      e._2.foreach(m => {
        val name = m("name")
        val value = m("value")
        val label = m("label")
        val xpath = m("xpath")
        println(s"name=${name} label=${label} value=${value} xpath=${xpath}")
      })

      val output = engine.layout(template, Map(
        "file" -> s"Template_${file.split('-').takeRight(1).head.toString}",
        "elements" -> elements(file))
      )
      println(output)

      val directory=new File(dir)
      if(directory.exists()==false){
        FileUtils.forceMkdir(directory)
      }
      println(s"template source directory = ${dir}")
      val appdex=template.split('.').takeRight(2).head
      scala.reflect.io.File(s"${dir}/${file}.${appdex}").writeAll(output)

    })

  }

}
