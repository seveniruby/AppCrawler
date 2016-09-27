package com.xueqiu.qa.appcrawler.plugin

import java.io

import com.xueqiu.qa.appcrawler.{DataObject, UrlElement, Plugin}
import org.apache.commons.io.FileUtils

import scala.reflect.io.File

/**
  * Created by seveniruby on 16/9/25.
  */
class FlowDiff extends Plugin{
  override def start(): Unit ={
  }

  override def afterElementAction(element: UrlElement): Unit ={
    getCrawler().store.saveDom(getCrawler().currentPageSource)
  }
}