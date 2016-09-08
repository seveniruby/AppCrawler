package com.xueqiu.qa.appcrawler

/**
  * Created by seveniruby on 16/9/8.
  */
class UrlElementStore extends CommonLog{
  //todo: 用枚举替代  0表示未遍历 1表示已遍历 -1表示跳过
  val elements: scala.collection.mutable.Map[UrlElement, ElementStatus.Value] = scala.collection.mutable.Map()

  def setElementSkip(element: UrlElement): Unit ={
    elements(element)=ElementStatus.Skiped
  }

  def setElementClicked(element: UrlElement): Unit ={
    elements(element)=ElementStatus.Clicked
  }

  def saveElement(e: UrlElement): Unit ={
    if (elements.contains(e)==false) {
      elements(e) = ElementStatus.Ready
      log.info(s"first found ${e}")
    }
  }
  def isClicked(ele: UrlElement): Boolean = {
    if (elements.contains(ele)) {
      elements(ele)==ElementStatus.Clicked
    } else {
      log.trace(s"element=${ele.toLoc()} first show, need click")
      false
    }
  }
  def isSkiped(ele: UrlElement): Boolean = {
    if (elements.contains(ele)) {
      elements(ele)==ElementStatus.Skiped
    } else {
      log.trace(s"element=${ele.toLoc()} first show, need click")
      false
    }
  }



}

object ElementStatus extends Enumeration {
  val Ready, Clicked, Skiped = Value
}