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
  private var currentKey=""

  override def start(): Unit = {
    //log.addAppender(getCrawler().fileAppender)
    tagLimitMax = getCrawler().conf.tagLimitMax
  }

  //fixed: conf.tagLimit未生效
  override def beforeElementAction(element: URIElement): Unit = {
    currentKey =getAncestor(element)
    if (!tagLimit.contains(currentKey)) {
      //应用定制化的规则
      getTimesFromTagLimit(element) match {
        case Some(v)=> {
          tagLimit(currentKey)=v
          log.info(s"tagLimit[${currentKey}]=${tagLimit(currentKey)} with conf.tagLimit")
        }
        case None => tagLimit(currentKey)=tagLimitMax
      }

      //跳过具备selected=true的菜单栏
      getCrawler().driver.getNodeListByKey("//*[@selected='true']").foreach(m=>{
        val selectedElement=getCrawler().getUrlElementByMap(m)
        val selectedKey=getAncestor(selectedElement)
        tagLimit(selectedKey)=20
        log.info(s"tagLimit[${selectedKey}]=20")
      })
    }

    log.info(s"tagLimit[${currentKey}]=${tagLimit(currentKey)}")
    //如果达到限制次数就退出
    if (currentKey.nonEmpty && tagLimit(currentKey) <= 0) {
      getCrawler().setElementAction("skip")
      log.info(s"$element need skip")
    }
  }

  override def afterElementAction(element: URIElement): Unit = {
    if(getCrawler().getElementAction()!="clear") {
      if (tagLimit.contains(currentKey)) {
        tagLimit(currentKey) -= 1
        log.info(s"tagLimit[${currentKey}]=${tagLimit(currentKey)}")
      }else{
        log.trace(s"not contains ${currentKey}")
      }
    }else{
      log.trace("action=clear")
    }
  }

  def getAncestor(element: URIElement): String ={
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
