package com.xueqiu.qa.appcrawler.plugin

import com.xueqiu.qa.appcrawler.{DataObject, UrlElement, Plugin}

import scala.reflect.io.File

/**
  * Created by seveniruby on 16/9/25.
  */
class FlowDiff extends Plugin{
  override def start(): Unit ={

  }

  override def afterElementAction(element: UrlElement): Unit ={
    val store=getCrawler().store
    val head=store.clickedElementsList.last
    store.saveDom(getCrawler().currentPageSource)
    val elementMap=DataObject.flatten(DataObject.fromXML(getCrawler().currentPageSource)).toList
    elementMap.filter(_._1.matches(".*\\.text"))
  }

  override def beforeBack(): Unit ={
    report()
  }

  override def stop(): Unit ={
    report()
  }

  def report(): Unit ={

    val store=getCrawler().store
    val diffData=store.elementStore.filter(_._2.clickedIndex>0).map(clickedElement=>{
      clickedElement._1 -> DataObject.flatten(DataObject.fromXML(clickedElement._2.resDom))
    }).toMap
    File(getCrawler().conf.resultDir+"/diff.yml").writeAll(DataObject.toYaml(diffData))
  }
  def diff(oldDom: String, newDom:String){

  }

}
