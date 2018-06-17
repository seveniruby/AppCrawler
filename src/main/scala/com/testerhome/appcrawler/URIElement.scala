package com.testerhome.appcrawler

import java.io.File

import javax.xml.bind.annotation.XmlAttribute
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.{Dimension, Point}

import scala.collection.immutable

/**
  * Created by seveniruby on 15/12/18.
  */
case class URIElement(
                       @XmlAttribute(name = "url")
                       url: String="",
                       @XmlAttribute(name = "tag")
                       tag: String="",
                       @XmlAttribute(name = "id")
                       id: String="",
                       @XmlAttribute(name = "name")
                       name: String="",
                       @XmlAttribute(name = "instance")
                       instance: String="",
                       @XmlAttribute(name = "depth")
                       depth: String="",
                       @XmlAttribute(name = "loc")
                       loc:String="",
                       @XmlAttribute(name = "ancestor")
                       ancestor:String="",
                       @XmlAttribute(name = "x")
                       x:Int=0,
                       @XmlAttribute(name = "y")
                       y: Int=0,
                       @XmlAttribute(name = "width")
                       width: Int=0,
                       @XmlAttribute(name = "height")
                       height:Int=0
                     ) {
  //用来代表唯一的控件, 每个特定的命名控件只被点击一次. 所以这个element的构造决定了控件是否可被点击多次.
  //比如某个输入框被命名为url=xueqiu id=input, 那么就只能被点击一次
  //如果url修改为url=xueqiu/xxxActivity id=input 就可以被点击多次
  //定义url是遍历的关键. 这是一门艺术

  /**
    * 可被当作文件名的唯一标记
    * @return
    */
  def toFileName(): String ={
    (s"${url}_tag.${tag.replace("android.widget.", "")}_${instance}_depth.${depth}_" +
      s"id.${id}_name.${name}").replace(File.separator, ".").take(100)
  }

  /**
    * 提取元素的tag组成的路径
    * @return
    */
  def getAncestor(): String ={
    ancestor
  }
  def center(): Point  ={
    new Point(x+width/2, y+height/2)
  }

  def location(): Point={
    new Point(x, y)
  }

  def size(): Dimension ={
    new Dimension(width, height)
  }
  def toXml(): String ={
    s"""
      |<UIAButton dom="" enabled="true" height="${height}" hint="" label="${id}"
      |        name="${name}" path="/0/0/4" valid="true" value="${loc}" visible="true"
      |        width="${width}" x="${x}" y="${y}"/>
    """.stripMargin

  }

  override def toString: String = {
    s"${this.url}_${this.loc}"
  }


  def hash(s:String)={
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b,0,b.length)
    new java.math.BigInteger(1,m.digest()).toString(16)
  }

}

object URIElement {
  //def apply(url: String, tag: String, id: String, name: String, loc: String = ""): UrlElement = new UrlElement(url, tag, id, name, loc)

  //todo: remove
  def apply(nodeMap:scala.collection.Map[String, Any], uri:String): URIElement = {
    //name为id/name属性. 为空的时候为value属性
    //id表示android的resource-id或者iOS的name属性
    URIElement(url=uri,
      tag=nodeMap.getOrElse("tag", "NoTag").toString,
      id=nodeMap.getOrElse("name", "").toString.split('/').last,
      name=nodeMap.getOrElse("value", "").toString.replace("\n", "\\n").take(30),
      loc=nodeMap.getOrElse("xpath", "").toString,
      ancestor = nodeMap.getOrElse("ancestor", "").toString
    )
  }

/*  def apply(x: scala.collection.immutable.Map[String, Any], uri:String=""): UrlElement = {
    apply(scala.collection.mutable.Map[String, Any]()++x, uri)
  }*/
}