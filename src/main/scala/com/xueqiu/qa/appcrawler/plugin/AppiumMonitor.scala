package com.xueqiu.qa.appcrawler.plugin

import com.xueqiu.qa.appcrawler.{MiniAppium, Plugin}

/**
  * Created by seveniruby on 16/9/19.
  */
class AppiumMonitor extends Plugin{

  override def start(): Unit ={
    monitorAppium()
  }

  def monitorAppium(): Unit = {
    val monitor = new Thread(new Runnable {
      override def run(): Unit = {
        log.info("monitor thread start")
        while (getCrawler().stopAll == false) {
          if (getCrawler().appNameRecord.intervalMS() > 1000 * 60 * 3) {
            log.error("3 mins passed after last refresh, retry launch app")
          } else {
            log.error("appium hang")
          }
          Thread.sleep(1000 * 60)
        }
      }
    })
    monitor.start()
  }

}
