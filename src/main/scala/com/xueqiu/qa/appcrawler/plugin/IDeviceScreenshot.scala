package com.xueqiu.qa.appcrawler.plugin

import com.xueqiu.qa.appcrawler.Plugin

/**
  * Created by seveniruby on 16/4/26.
  */
class IDeviceScreenshot extends Plugin{
  var use=false
  override def start(): Unit ={
    if(getCrawler().driver.getCapabilities.getCapability("udid").toString.nonEmpty){
      use=true
      log.info("use idevicescreenshot")
    }
  }
  override def screenshot(path:String): Boolean ={
    import sys.process._
    val cmd=s"idevicescreenshot '${path}'"
    log.info(s"cmd=${cmd}")
    cmd.!
    true
  }
}
