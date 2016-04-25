package com.xueqiu.qa.appcrawler.plugin

import com.xueqiu.qa.appcrawler.{Plugin, UrlElement}

/**
  * Created by seveniruby on 16/1/21.
  *
  * 如果某种类型的控件点击次数太多, 就跳过. 设定一个阈值
  */
class TagLimitPlugin extends Plugin{
  private val tagLimit: scala.collection.mutable.Map[String, Int] = scala.collection.mutable.Map()
  private val tagLimitMax=5
  override def beforeElementAction(element: UrlElement): Unit ={
    val key=element.toTagPath()
    log.trace(s"tag path = ${key}")
    if(!tagLimit.contains(key)){
      tagLimit(key)=tagLimitMax
    }
    if(tagLimit(key)<=0){
      getCrawler().setElementAction("skip")
      log.info(s"$element need skip")
    }
  }

  override def afterElementAction(element: UrlElement): Unit ={
    val key=element.toTagPath()
    tagLimit(key)-=1
    log.info(s"tagLimit[${key}]=${tagLimit(key)}")
  }


  override def afterUrlRefresh(url:String): Unit ={

  }

}
