package com.testerhome.appcrawler.plugin

import java.io

import com.testerhome.appcrawler.URIElement
import com.testerhome.appcrawler.DataObject
import org.apache.commons.io.FileUtils

import scala.reflect.io.File

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