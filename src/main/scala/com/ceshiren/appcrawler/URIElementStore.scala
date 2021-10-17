package com.ceshiren.appcrawler

import com.ceshiren.appcrawler.data.{AbstractElement, AbstractElementInfo, AbstractElementStore}
import com.ceshiren.appcrawler.data.AbstractElementStore.Status
import com.ceshiren.appcrawler.data.AbstractElementStore

import java.util
import scala.collection.JavaConverters
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/9/8.
  */
class URIElementStore extends AbstractElementStore {
  //todo: 用枚举替代  0表示未遍历 1表示已遍历 -1表示跳过

  var elementStoreMap = scala.collection.mutable.Map[String, ElementInfo]()

  def getElementStoreMap: java.util.Map[String, AbstractElementInfo] = {
    JavaConverters.mapAsJavaMap(elementStoreMap)
  }

  /** 点击顺序, 留作画图用 */

  var clickedElementsList = ListBuffer[AbstractElement]()

  def setElementSkip(element: AbstractElement): Unit = {
    //todo: 待改进
    //clickedElementsList.remove(clickedElementsList.size - 1)
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString) = ElementInfo()
      elementStoreMap(element.toString).element = element
    }
    elementStoreMap(element.toString).action = Status.SKIPPED
  }

  def setElementClicked(element: AbstractElement): Unit = {
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString) = ElementInfo()
      elementStoreMap(element.toString).element = element
    }
    clickedElementsList.append(element)
    elementStoreMap(element.toString).action = Status.CLICKED
    elementStoreMap(element.toString).clickedIndex = clickedElementsList.indexOf(element)
  }

  def setElementClear(element: AbstractElement = clickedElementsList.last): Unit = {
    if (elementStoreMap.contains(element.toString)) {
      elementStoreMap.remove(element.toString)
    }
  }


  def saveElement(element: AbstractElement): Unit = {
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString) = ElementInfo()
      elementStoreMap(element.toString).element = element
    }
    if (elementStoreMap.contains(element.toString) == false) {
      elementStoreMap(element.toString).action = Status.CLICKED
      AppCrawler.log.info(s"first found ${element}")
    }
  }


  def saveReqHash(hash: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).reqHash.isEmpty) {
      AppCrawler.log.info(s"save reqHash to ${clickedElementsList.size - 1}")
      elementStoreMap(head).reqHash = hash
    }
  }

  def saveResHash(hash: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).resHash.isEmpty) {
      AppCrawler.log.info(s"save resHash to ${clickedElementsList.size - 1}")
      elementStoreMap(head).resHash = hash
    }
  }


  def saveReqDom(dom: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    AppCrawler.log.info(s"save reqDom to ${clickedElementsList.size - 1}")
    elementStoreMap(head).reqDom = dom
  }

  //todo: 去掉req和res的单独存储，改用链表查询
  def saveResDom(dom: String = ""): Unit = {
    val head = clickedElementsList.last.toString
    AppCrawler.log.info(s"save resDom to ${clickedElementsList.size - 1}")
    elementStoreMap(head).resDom = dom
  }

  def saveReqImg(imgName: String): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).reqImg.isEmpty) {
      AppCrawler.log.info(s"save reqImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStoreMap(head.toString).reqImg = imgName
    }
  }


  def saveResImg(imgName: String): Unit = {
    val head = clickedElementsList.last.toString
    if (elementStoreMap(head).resImg.isEmpty) {
      AppCrawler.log.info(s"save resImg ${imgName} to ${clickedElementsList.size - 1}")
      elementStoreMap(head).resImg = imgName.split('.').dropRight(2).mkString(".") + ".clicked.png"
    }
  }


  def isDiff(): Boolean = {
    val currentElement = clickedElementsList.last
    elementStoreMap(currentElement.toString).reqHash != elementStoreMap(currentElement.toString).resHash
  }


  def isClicked(element: AbstractElement): Boolean = {
    if (elementStoreMap.contains(element.toString)) {
      elementStoreMap(element.toString).action == Status.CLICKED
    } else {
      AppCrawler.log.debug(s"element=${element} first show, need click")
      false
    }
  }

  def isSkipped(ele: AbstractElement): Boolean = {
    if (elementStoreMap.contains(ele.toString)) {
      elementStoreMap(ele.toString).action == Status.SKIPPED
    } else {
      AppCrawler.log.debug(s"element=${ele} first show, need click")
      false
    }
  }

  override def getClickedElementsList: util.List[AbstractElement] = {
    JavaConverters.bufferAsJavaList(clickedElementsList)
  }

  override def saveReqTime(reqTime: String): Unit = {}

  override def saveResTime(resTime: String): Unit = {}
}
