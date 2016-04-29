package com.xueqiu.qa.appcrawler.plugin

import java.io.File

import com.brsanthu.googleanalytics.GoogleAnalytics
import com.xueqiu.qa.appcrawler.{UrlElement, Plugin}
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.proxy.{CaptureType, ProxyServer}
import org.apache.log4j.{Level, Logger, BasicConfigurator}

/**
  * Created by seveniruby on 16/4/26.
  */
class ProxyPlugin extends Plugin {
  private var proxy: ProxyServer = _
  private var harFileName = ""
  val port = 7771

  //todo: 支持代理
  override def start(): Unit = {
    BasicConfigurator.configure()
    Logger.getRootLogger.setLevel(Level.OFF)
    Logger.getLogger("ProxyServer").setLevel(Level.WARN)

    proxy = new ProxyServer()
    proxy.start(port)

    proxy.setHarCaptureTypes(CaptureType.getNonBinaryContentCaptureTypes)
    //proxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes)
    //proxy.setHarCaptureTypes(CaptureType.getHeaderCaptureTypes)
    log.info(s"proxy server listen on ${port}")
    proxy.newHar(harFileName)
  }

  override def beforeElementAction(element: UrlElement): Unit = {
    if(harFileName.isEmpty){
      harFileName = getCrawler().getLogFileName() + ".har"
    }
    //保存上次的请求
    log.info(s"save har to ${harFileName}")
    proxy.endHar()
    val file = new File(harFileName)
    log.info(s"har entry size = ${proxy.getHar.getLog.getEntries.size()}")
    if(proxy.getHar.getLog.getEntries.size()>0){
      proxy.getHar.writeTo(file)
    }

    //创建新的har
    harFileName = getCrawler().getLogFileName() + ".har"
    proxy.newHar(harFileName)
  }

  override def afterElementAction(element: UrlElement): Unit = {

  }

  override def stop(): Unit ={
    log.info("prpxy stop")
    proxy.stop()
  }
}
