package com.testerhome.appcrawler

import java.util

import com.testerhome.appcrawler.data.AbstractElementStore.Status
import com.testerhome.appcrawler.data.{AbstractElement, AbstractElementInfo, AbstractElementStore}

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters

/**
  * Created by seveniruby on 16/9/8.
  */
class URIElementStore extends AbstractElementStore{
  //todo: 用枚举替代  0表示未遍历 1表示已遍历 -1表示跳过

  var elementStore = scala.collection.mutable.Map[String, ElementInfo]()

  def storeMap : java.util.Map[String,AbstractElementInfo] = {
    JavaConverters.mapAsJavaMap(elementStore)
  }

  /** 点击顺序, 留作画图用 */

  var clickedElementsList = ListBuffer[AbstractElement]()

  def setElementSkip(element: AbstractElement): Unit = {
    //todo: 待改进
    //clickedElementsList.remove(clickedElementsList.size - 1)
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    elementStore(element.toString).action=Status.SKIPPED
  }

  def setElementClicked(element: AbstractElement): Unit = {
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    clickedElementsList.append(element)
    elementStore(element.toString).action=Status.CLICKED
    elementStore(element.toString).clickedIndex=clickedElementsList.indexOf(element)
  }
  def setElementClear(element: AbstractElement=clickedElementsList.last): Unit = {
    if(elementStore.contains(element.toString)){
      elementStore.remove(element.toString)
    }
  }


  def saveElement(element: AbstractElement): Unit = {
    if(elementStore.contains(element.toString)==false){
      elementStore(element.toString)=ElementInfo()
      elementStore(element.toString).element=element
    }
    if (elementStore.contains(element.toString) == false) {
      elementStore(element.toString).action=Status.CLICKED
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
    AppCrawler.log.info(s"save reqDom to ${clickedElementsList.size-1}")
    elementStore(head).reqDom=dom
  }

  //todo: 去掉req和res的单独存储，改用链表查询
  def saveResDom(dom: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    AppCrawler.log.info(s"save resDom to ${clickedElementsList.size-1}")
    elementStore(head).resDom=dom
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
    if (elementStore(head).resImg.isEmpty) {
      AppCrawler.log.info(s"save resImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStore(head).resImg = imgName.split('.').dropRight(2).mkString(".")+".clicked.png"
    }
  }


  def isDiff(): Boolean = {
    val currentElement = clickedElementsList.last
    elementStore(currentElement.toString).reqHash!=elementStore(currentElement.toString).resHash
  }


  def isClicked(element: AbstractElement): Boolean = {
    if (elementStore.contains(element.toString)) {
      elementStore(element.toString).action == Status.CLICKED
    } else {
      AppCrawler.log.debug(s"element=${element} first show, need click")
      false
    }
  }

  def isSkipped(ele: AbstractElement): Boolean = {
    if (elementStore.contains(ele.toString)) {
      elementStore(ele.toString).action == Status.SKIPPED
    } else {
      AppCrawler.log.debug(s"element=${ele} first show, need click")
      false
    }
  }

  override def clickElementList: util.List[AbstractElement] = {
    JavaConverters.bufferAsJavaList(clickedElementsList)
  }
}

object ElementStatus extends Enumeration {
  val Ready, Clicked, Skipped = Value
}
