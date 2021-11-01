package com.ceshiren.appcrawler.core

import com.ceshiren.appcrawler.AppCrawler
import com.ceshiren.appcrawler.core.Status.Status
import com.ceshiren.appcrawler.model.URIElement

case class ElementInfo(
                        var reqDom: String = "",
                        var resDom: String = "",
                        var reqHash: String = "",
                        var resHash: String = "",
                        var reqImg: String = "",
                        var resImg: String = "",
                        var reqTime: String = "",
                        var resTime: String = "",
                        var clickedIndex: Int = -1,
                        var action: Status = Status.READY,
                        var element: URIElement = AppCrawler.factory.generateElement
                      ) {
  def this() {
    this("", "", "", "", "", "", "", "", -1, Status.READY, null)
  }

  def getElement = {
    element
  }

  def setElement(element: URIElement): Unit = {
    this.element = element
  }

  def setAction(status: Status): Unit = {
    this.action = status
  }

  def setClickedIndex(index: Int): Unit = {
    this.clickedIndex = index
  }

  def getAction: Status = {
    action
  }

  def setReqDom(dom: String): Unit = {
    this.reqDom = dom
  }

  def setReqImg(img: String): Unit = {
    this.reqImg = img
  }

  def getReqDom: String = {
    reqDom
  }

  def getReqImg: String = {
    reqImg
  }

  def setResDom(resDom: String): Unit = {
    this.resDom = resDom
  }

  def setResImg(resImg: String): Unit = {
    this.resImg = resImg
  }

  def getResDom: String = {
    resDom
  }

  def getResImg: String = {
    resImg
  }

  def setReqHash(reqHash: String): Unit = {
    this.reqHash = reqHash
  }

  def setResHash(resHash: String): Unit = {
    this.resHash = resHash
  }

  def getReqHash: String = {
    reqHash
  }

  def getResHash: String = {
    resHash
  }

  def getClickedIndex: Int = {
    clickedIndex
  }

  def setReqTime(reqTime: String): Unit = {
    this.reqTime = reqTime
  }

  def setResTime(resTime: String): Unit = {
    this.resTime = resTime
  }

  def getReqTime: String = {
    reqTime
  }

  def getResTime: String = {
    resTime
  }
}
