package com.ceshiren.appcrawler.plugin

import com.ceshiren.appcrawler.utils.Log.log

import scala.sys.process._

/**
  * Created by seveniruby on 16/4/26.
  */
class IDeviceScreenshot extends Plugin {

  var use = false

  override def start(): Unit = {
    getCrawler().conf.capability("udid") match {
      case null => {
        use = false
        log.info("udid=null use simulator")
      }
      case "" => {
        use = false
        log.info("udid= use simulator")
      }
      case _ => {
        use = true
        log.info("use idevicescreenshot")
      }
    }
  }

  override def screenshot(path: String): Boolean = {
    //非真机不使用
    if (use == false) return false
    val cmd = s"idevicescreenshot ${path}"
    log.info(s"cmd=${cmd}")
    cmd.!
    true
  }
}
