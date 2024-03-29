package com.ceshiren.appcrawler.plugin

import com.ceshiren.appcrawler.core.Crawler
import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.Log.log

/**
  * Created by seveniruby on 16/1/7.
  */
abstract class Plugin{
  private var crawler: Crawler=_

  def getCrawler(): Crawler ={
    this.crawler
  }
  def setCrawer(crawler:Crawler): Unit ={
    this.crawler=crawler
  }
  def init(crawler: Crawler): Unit ={
    this.crawler=crawler

    log.info(this.getClass.getName+" init")
  }
  def start(): Unit ={

  }
  def afterUrlRefresh(url:String): Unit ={

  }

  def beforeBack(): Unit ={

  }
  def fixElementAction(element: URIElement): Unit ={

  }
  def beforeElementAction(element: URIElement): Unit ={

  }
  def afterElementAction(element: URIElement): Unit ={

  }

  /**
    * 如果实现了请设置返回值为true
    * @param path
    * @return
    */
  def screenshot(path:String): Boolean ={
    false
  }

  def stop(): Unit ={

  }
}
