package com.ceshiren.appcrawler.model

import com.ceshiren.appcrawler.core.Status
import com.ceshiren.appcrawler.utils.Log.log

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

/**
  * Created by seveniruby on 16/9/8.
  */
class URIElementStore {
  //todo: 用枚举替代  0表示未遍历 1表示已遍历 -1表示跳过

  var elementStoreMap = scala.collection.mutable.Map[String, ElementInfo]()

  def getElementStoreMap: java.util.Map[String, ElementInfo] = {
    elementStoreMap.asJava
  }

  /** 点击顺序, 留作画图用 */

  var clickedElementsList = ListBuffer[URIElement]()

  def setElementSkip(element: URIElement): Unit = {
    //todo: 待改进
    //clickedElementsList.remove(clickedElementsList.size - 1)
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString) = ElementInfo()
      elementStoreMap(element.toString).element = element
    }
    elementStoreMap(element.toString).action = Status.SKIPPED
  }

  def setElementClicked(element: URIElement): Unit = {
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString) = ElementInfo()
      elementStoreMap(element.toString).element = element
    }
    clickedElementsList.append(element)
    elementStoreMap(element.toString).action = Status.CLICKED
    elementStoreMap(element.toString).clickedIndex = clickedElementsList.indexOf(element)
  }

  def setElementClear(element: URIElement = clickedElementsList.last): Unit = {
    if (elementStoreMap.contains(element.toString)) {
      elementStoreMap.remove(element.toString)
    }
  }


  def saveElement(element: URIElement): Unit = {
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString) = ElementInfo()
      elementStoreMap(element.toString).element = element
    }
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString).action = Status.CLICKED
      log.info(s"first found ${element}")
    }
  }


  def saveReqHash(hash: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).reqHash.isEmpty) {
      log.info(s"save reqHash to ${clickedElementsList.size - 1}")
      elementStoreMap(head).reqHash = hash
    }
  }

  def saveResHash(hash: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).resHash.isEmpty) {
      log.info(s"save resHash to ${clickedElementsList.size - 1}")
      elementStoreMap(head).resHash = hash
    }
  }


  def saveReqDom(dom: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    log.info(s"save reqDom to ${clickedElementsList.size - 1}")
    elementStoreMap(head).reqDom = dom
  }

  //todo: 去掉req和res的单独存储，改用链表查询
  def saveResDom(dom: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    log.info(s"save resDom to ${clickedElementsList.size - 1}")
    elementStoreMap(head).resDom = dom
  }

  def saveReqImg(imgName: String): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).reqImg.isEmpty) {
      log.info(s"save reqImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStoreMap(head.toString).reqImg = imgName
    }
  }


  def saveResImg(imgName: String): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).resImg.isEmpty) {
      log.info(s"save resImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStoreMap(head).resImg = imgName.split('.').dropRight(2).mkString(".") + ".clicked.png"
    }
  }


  def isDiff(): Boolean = {
    val currentElement = clickedElementsList.last
    elementStoreMap(currentElement.toString).reqHash != elementStoreMap(currentElement.toString).resHash
  }


  def isClicked(element: URIElement): Boolean = {
    if (elementStoreMap.contains(element.toString)) {
      elementStoreMap(element.toString).action == Status.CLICKED
    } else {
      log.debug(s"element=${element} first show, need click")
      false
    }
  }

  def isSkipped(ele: URIElement): Boolean = {
    if (elementStoreMap.contains(ele.toString)) {
      elementStoreMap(ele.toString).action == Status.SKIPPED
    } else {
      log.debug(s"element=${ele} first show, need click")
      false
    }
  }

  def getClickedElementsList: ListBuffer[URIElement] = {
    clickedElementsList
  }

   def saveReqTime(reqTime: String): Unit = {}

   def saveResTime(resTime: String): Unit = {}
}
