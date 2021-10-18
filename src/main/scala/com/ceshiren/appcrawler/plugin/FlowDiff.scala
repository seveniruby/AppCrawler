package com.ceshiren.appcrawler.plugin

import com.ceshiren.appcrawler.URIElement

/**
  * Created by seveniruby on 16/9/25.
  */
class FlowDiff extends Plugin{
  override def start(): Unit ={
  }

  override def afterElementAction(element: URIElement): Unit ={
    //getCrawler().store.saveResDom(getCrawler().driver.currentPageSource)
  }
}