package com.testerhome.appcrawler.plugin

import com.testerhome.appcrawler.URIElement
import com.testerhome.appcrawler.ElementStatus

/**
  * Created by seveniruby on 16/1/21.
  *
  * 如果某种类型的控件点击次数太多, 就跳过. 设定一个阈值
  */
class TagLimitPlugin extends Plugin {
  private val tagLimit = scala.collection.mutable.Map[String, Int]()
  private var tagLimitMax = 3

  override def start(): Unit = {
    //log.addAppender(getCrawler().fileAppender)
    tagLimitMax = getCrawler().conf.tagLimitMax
  }

  //todo: conf.tagLimit未生效
  override def beforeElementAction(element: URIElement): Unit = {
    val key =getKey(element)
    if (!tagLimit.contains(key)) {
      //跳过具备selected=true的菜单栏
      getCrawler().driver.getNodeListByKey("//*[@selected='true']").foreach(m=>{
        val selectedElement=getCrawler().getUrlElementByMap(m)
        val selectedKey=getKey(selectedElement)
        tagLimit(selectedKey)=20
        log.info(s"tagLimit[${selectedKey}]=20")
      })
      //应用定制化的规则
      getTimesFromTagLimit(element) match {
        case Some(v)=> {
          tagLimit(key)=v
          log.info(s"tagLimit[${key}]=${tagLimit(key)} with conf.tagLimit")
        }
        case None => tagLimit(key)=tagLimitMax
      }
    }

    //如果达到限制次数就退出
    if (key.nonEmpty && tagLimit(key) <= 0) {
      log.warn(s"tagLimit[${key}]=${tagLimit(key)}")
      getCrawler().setElementAction("skip")
      log.info(s"$element need skip")
    }
  }

  override def afterElementAction(element: URIElement): Unit = {
    if(getCrawler().getElementAction()!="clear") {
      val key = getKey(element)
      if (tagLimit.contains(key)) {
        tagLimit(key) -= 1
        log.info(s"tagLimit[${key}]=${tagLimit(key)}")
      }
    }
  }

  def getKey(element: URIElement): String ={
    getCrawler().currentUrl + element.getAncestor()
  }


  override def afterUrlRefresh(url: String): Unit = {

  }

  def getTimesFromTagLimit(element: URIElement): Option[Int] = {
    this.getCrawler().conf.tagLimit.foreach(tag => {
      if(getCrawler().driver.getNodeListByKey(tag.getXPath())
        .map(new URIElement(_, getCrawler().currentUrl))
        .contains(element)){
        return Some(tag.times)
      }else{
        None
      }
    })
    None
  }

}
