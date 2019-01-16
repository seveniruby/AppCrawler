package com.testerhome.appcrawler

import com.testerhome.appcrawler.data.{AbstractElement, AbstractElementInfo, PathElementStore}

case class ElementInfo(
                        var reqDom: String = "",
                        var resDom: String = "",
                        var reqHash: String = "",
                        var resHash: String = "",
                        var reqImg:String="",
                        var resImg:String="",
                        var clickedIndex: Int = -1,
                        var action: PathElementStore.Status = PathElementStore.Status.READY,
                        var element: URIElement = URIElement(url="Init", tag="", id="", name="", xpath="")
                      ) extends AbstractElementInfo{
  def this(){
    this("","","","","","",-1,PathElementStore.Status.READY,null)
  }
  override def getElement: AbstractElement = {
    element.asInstanceOf[AbstractElement]
  }

  override def setElement(element: AbstractElement): Unit = {
    this.element = element.asInstanceOf[URIElement]
  }

  override def setAction(status: PathElementStore.Status): Unit = {
    this.action = status
  }

  override def setClickedIndex(index: Int): Unit = {
    this.clickedIndex = index
  }

  override def getAction: PathElementStore.Status = {
    action
  }

  override def setReqDom(dom: String): Unit = {
    this.reqDom = dom
  }

  override def setReqImg(img: String): Unit = {
    this.reqImg = img
  }

  override def getReqDom: String = {
    reqDom
  }

  override def getReqImg: String = {
    reqImg
  }

  override def setResDom(resDom: String): Unit = {
    this.resDom = resDom
  }

  override def setResImg(resImg: String): Unit = {
    this.resImg = resImg
  }

  override def getResDom: String = {
    resDom
  }

  override def getResImg: String = {
    resImg
  }

  override def setReqHash(reqHash: String): Unit = {
    this.reqHash = reqHash
  }

  override def setResHash(resHash: String): Unit = {
    this.resHash = resHash
  }

  override def getReqHash: String = {
    reqHash
  }

  override def getResHash: String = {
    resHash
  }

  override def getClickedIndex: Int = {
    clickedIndex
  }
}