package com.testerhome.appcrawler.plugin

import com.testerhome.appcrawler.data.AbstractElement

/**
  * Created by seveniruby on 16/9/25.
  */
class FlowDiff extends Plugin{
  override def start(): Unit ={
  }

  override def afterElementAction(element: AbstractElement): Unit ={
    //getCrawler().store.saveResDom(getCrawler().driver.currentPageSource)
  }
}