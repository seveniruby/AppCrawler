package com.xueqiu.qa.appcrawler

import scala.collection.immutable

/**
  * Created by seveniruby on 15/12/18.
  */
case class UrlElement(url: String="", tag: String="", id: String="", name: String="", loc:String="") {
  //用来代表唯一的控件, 每个特定的命名控件只被点击一次. 所以这个element的构造决定了控件是否可被点击多次.
  //比如某个输入框被命名为url=xueqiu id=input, 那么就只能被点击一次
  //如果url修改为url=xueqiu/xxxActivity id=input 就可以被点击多次
  //定义url是遍历的关键. 这是一门艺术

  /**
    * 可被当作文件名的唯一标记
    * @return
    */
  def toFileName(): String ={
    //url_[parent id]-tag-id
    s"${url}_${"\"([^/0-9][^\" =]*)\"".r.findAllMatchIn(loc).map(_.subgroups).toList.flatten.
      map(_.split("/").lastOption.getOrElse("")).mkString("-")}".replaceAll("[\\\\/?\"*<>\\|\n ]", ".").take(200)
  }

  /**
    * 唯一的定位标记
    * @return
    */
  def toLoc(): String ={
    s"${url}\t${loc}\t${tag}\t${id}\t${name}"
  }

  /**
    * 提取元素的tag组成的路径
    * @return
    */
  def toTagPath(): String ={
    //相同url下的相同元素类型控制点击额度
    //s"${element.url}_${element.tag}_${element.loc}".replaceAll("@index=[^ ]*", "") //replaceAll("\\[[^\\[]*$", "")
    s"${url}-${"(/[a-zA-Z][a-zA-Z\\.]*)".r.findAllMatchIn(loc.replaceAll(":id/", "").replaceAll("android\\.[a-z]*\\.", "")).map(_.subgroups).toList.flatten.mkString("")}"
  }

  override def toString: String = {
    s"${this.url}_${this.loc}"
  }

}

object UrlElement {
  //def apply(url: String, tag: String, id: String, name: String, loc: String = ""): UrlElement = new UrlElement(url, tag, id, name, loc)

  def apply(x:scala.collection.Map[String, Any], uri:String): UrlElement = {
    val tag = x.getOrElse("tag", "NoTag").toString

    //name为Android的description/text属性, 或者iOS的value属性
    //appium1.5已经废弃findElementByName
    val name = x.getOrElse("value", "").toString.replace("\n", "\\n").take(30)
    //name为id/name属性. 为空的时候为value属性

    //id表示android的resource-id或者iOS的name属性
    val id = x.getOrElse("name", "").toString.split('/').last
    val loc = x.getOrElse("xpath", "").toString
    UrlElement(uri, tag, id, name, loc)
  }

/*  def apply(x: scala.collection.immutable.Map[String, Any], uri:String=""): UrlElement = {
    apply(scala.collection.mutable.Map[String, Any]()++x, uri)
  }*/
}