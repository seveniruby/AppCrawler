package com.xueqiu.qa.appcrawler

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/9/8.
  */
class URIElementStore {
  //todo: 用枚举替代  0表示未遍历 1表示已遍历 -1表示跳过
  @BeanProperty
  var elementStore = scala.collection.mutable.Map[String, ElementInfo]()
  /** 点击顺序, 留作画图用 */
  @BeanProperty
  var clickedElementsList = ListBuffer[URIElement]()

  def setElementSkip(element: URIElement): Unit = {
    clickedElementsList.remove(clickedElementsList.size - 1)
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    elementStore(element.toString).action=ElementStatus.Skiped
  }

  def setElementClicked(element: URIElement): Unit = {
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    clickedElementsList.append(element)
    elementStore(element.toString).action=ElementStatus.Clicked
    elementStore(element.toString).clickedIndex=clickedElementsList.indexOf(element)
  }

  def saveElement(element: URIElement): Unit = {
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    if (elementStore.contains(element.toString) == false) {
      elementStore(element.toString).action=ElementStatus.Clicked
      AppCrawler.log.info(s"first found ${element}")
    }
  }


  def saveReqHash(hash: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if(elementStore(head).reqHash.isEmpty){
      AppCrawler.log.info(s"save reqHash to ${clickedElementsList.size-1}")
      elementStore(head).reqHash=hash
    }
  }

  def saveResHash(hash: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if(elementStore(head).resHash.isEmpty){
      AppCrawler.log.info(s"save resHash to ${clickedElementsList.size-1}")
      elementStore(head).resHash=hash
    }
  }


  def saveReqDom(dom: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if(elementStore(head).reqDom.isEmpty){
      AppCrawler.log.info(s"save reqDom to ${clickedElementsList.size-1}")
      elementStore(head).reqDom=dom
    }
  }

  def saveResDom(dom: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if(elementStore(head).resDom.isEmpty){
      AppCrawler.log.info(s"save resDom to ${clickedElementsList.size-1}")
      elementStore(head).resDom=dom
    }
  }



  def saveReqImg(imgName:String): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStore(head).reqImg.isEmpty) {
      AppCrawler.log.info(s"save reqImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStore(head.toString).reqImg = imgName
    }
  }


  def saveResImg(imgName:String): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStore(head).reqImg.isEmpty) {
      AppCrawler.log.info(s"save reqImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStore(head.toString).reqImg = imgName.split('.').dropRight(2).mkString(".")+".ori.jpg"
    }
  }

  def getLastResponseImage(): Unit ={

  }


  def isDiff(): Boolean = {
    val currentElement = clickedElementsList.last
    elementStore(currentElement.toString).reqHash!=elementStore(currentElement.toString).resHash
  }


  def isClicked(ele: URIElement): Boolean = {
    if (elementStore.contains(ele.toString)) {
      elementStore(ele.toString).action == ElementStatus.Clicked
    } else {
      AppCrawler.log.trace(s"element=${ele.toLoc()} first show, need click")
      false
    }
  }

  def isSkiped(ele: URIElement): Boolean = {
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
                        var element: URIElement = URIElement("Init", "", "", "", "")
                      )