package com.xueqiu.qa.appcrawler

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/9/8.
  */
class UrlElementStore {
  //todo: 用枚举替代  0表示未遍历 1表示已遍历 -1表示跳过
  @BeanProperty
  val elementStore = scala.collection.mutable.Map[String, ElementInfo]()
  /** 点击顺序, 留作画图用 */
  @BeanProperty
  val clickedElementsList = ListBuffer[UrlElement]()

  def setElementSkip(element: UrlElement): Unit = {
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    elementStore(element.toString).action=ElementStatus.Skiped
  }

  def setElementClicked(element: UrlElement): Unit = {
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    clickedElementsList.append(element)
    elementStore(element.toString).action=ElementStatus.Clicked
    elementStore(element.toString).clickedIndex=clickedElementsList.indexOf(element)
  }

  def saveElement(element: UrlElement): Unit = {
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    if (elementStore.contains(element.toString) == false) {
      elementStore(element.toString).action=ElementStatus.Clicked
      AppCrawler.log.info(s"first found ${element}")
    }
  }

  def saveHash(hash: String = ""): Unit = {
    val head = clickedElementsList.last
    if(elementStore(head.toString).reqHash.isEmpty){
      AppCrawler.log.info(s"save reqHash to ${clickedElementsList.size-1}")
      elementStore(head.toString).reqHash=hash
    }

    if(clickedElementsList.size>1) {
      val pre = clickedElementsList.takeRight(2).head
      elementStore(pre.toString).resHash = hash
    }
  }

  def saveImg(imgName:String): Unit = {
    val head = clickedElementsList.last
    if (elementStore(head.toString).reqImg.isEmpty) {
      AppCrawler.log.info(s"save reqImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStore(head.toString).reqImg = imgName
    }
    if(clickedElementsList.size>1) {
      val pre = clickedElementsList.takeRight(2).head
      elementStore(pre.toString).resImg = imgName.split('.')(0)+".ori.jpg"
    }
  }


  def isDiff(): Boolean = {
    val currentElement = clickedElementsList.last
    elementStore(currentElement.toString).reqHash!=elementStore(currentElement.toString).resHash
  }


  def isClicked(ele: UrlElement): Boolean = {
    if (elementStore.contains(ele.toString)) {
      elementStore(ele.toString).action == ElementStatus.Clicked
    } else {
      AppCrawler.log.trace(s"element=${ele.toLoc()} first show, need click")
      false
    }
  }

  def isSkiped(ele: UrlElement): Boolean = {
    if (elementStore.contains(ele.toString)) {
      elementStore(ele.toString).action == ElementStatus.Skiped
    } else {
      AppCrawler.log.trace(s"element=${ele.toLoc()} first show, need click")
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
                        var reqImg:String="",
                        var resImg:String="",
                        var clickedIndex: Int = -1,
                        var action: ElementStatus.Value = ElementStatus.Ready,
                        var element: UrlElement = UrlElement("Init", "", "", "", "")
                      )