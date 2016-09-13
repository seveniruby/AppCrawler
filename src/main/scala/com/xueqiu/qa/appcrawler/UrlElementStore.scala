package com.xueqiu.qa.appcrawler

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/9/8.
  */
class UrlElementStore extends CommonLog {
  //todo: 用枚举替代  0表示未遍历 1表示已遍历 -1表示跳过
  val elements: scala.collection.mutable.Map[UrlElement, ElementStatus.Value] = scala.collection.mutable.Map()
  val elementStore = scala.collection.mutable.Map[String, ElementInfo]()
  /** 点击顺序, 留作画图用 */
  val clickedElementsList = mutable.Stack[UrlElement]()

  def setElementSkip(element: UrlElement): Unit = {
    elements(element) = ElementStatus.Skiped
  }

  def setElementClicked(element: UrlElement): Unit = {
    elements(element) = ElementStatus.Clicked
    clickedElementsList.push(element)
    elementStore(element.toString)=ElementInfo()
    elementStore(element.toString).element=element
  }

  def saveHash(hash: String = ""): Unit = {
    val head = clickedElementsList.head
    if(elementStore(head.toString).reqHash.isEmpty){
      elementStore(head.toString).reqHash=hash
    }else if(elementStore(head.toString).resHash.isEmpty){
      elementStore(head.toString).resHash=hash
    }
  }

  def isDomDiff(): Boolean = {
    val head = clickedElementsList.head
    elementStore(head.toString).reqHash==elementStore(head.toString).resHash
  }

  def saveElement(e: UrlElement): Unit = {
    if (elements.contains(e) == false) {
      elements(e) = ElementStatus.Ready
      log.info(s"first found ${e}")
    }
  }

  def isClicked(ele: UrlElement): Boolean = {
    if (elements.contains(ele)) {
      elements(ele) == ElementStatus.Clicked
    } else {
      log.trace(s"element=${ele.toLoc()} first show, need click")
      false
    }
  }

  def isSkiped(ele: UrlElement): Boolean = {
    if (elements.contains(ele)) {
      elements(ele) == ElementStatus.Skiped
    } else {
      log.trace(s"element=${ele.toLoc()} first show, need click")
      false
    }
  }


}

object ElementStatus extends Enumeration {
  val Ready, Clicked, Skiped = Value
}

case class ElementInfo(
                        var reqDom: String = "",
                        var resDom: String = "",
                        var reqHash: String = "",
                        var resHash: String = "",
                        var element: UrlElement = null
                      )