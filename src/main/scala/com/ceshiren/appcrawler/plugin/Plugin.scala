package com.ceshiren.appcrawler.plugin

import com.ceshiren.appcrawler.data.AbstractElement
import com.ceshiren.appcrawler.{AppCrawler, CommonLog, Crawler}

/**
  * Created by seveniruby on 16/1/7.
  */
abstract class Plugin extends CommonLog{
  private var crawler: Crawler=_

  def getCrawler(): Crawler ={
    this.crawler
  }
  def setCrawer(crawler:Crawler): Unit ={
    this.crawler=crawler
  }
  def init(crawler: Crawler): Unit ={
    this.crawler=crawler
    log.addAppender(AppCrawler.fileAppender)
    log.info(this.getClass.getName+" init")
  }
  def start(): Unit ={

  }
  def afterUrlRefresh(url:String): Unit ={

  }

  def beforeBack(): Unit ={

  }
  def fixElementAction(element: AbstractElement): Unit ={

  }
  def beforeElementAction(element: AbstractElement): Unit ={

  }
  def afterElementAction(element: AbstractElement): Unit ={

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
