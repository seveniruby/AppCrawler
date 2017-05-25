package com.testerhome.appcrawler.plugin

import java.io.File

import com.brsanthu.googleanalytics.GoogleAnalytics
import com.testerhome.appcrawler.URIElement
import com.testerhome.appcrawler.Plugin
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.proxy.CaptureType
import org.apache.log4j.{BasicConfigurator, Level, Logger}

import scala.util.Try

/**
  * Created by seveniruby on 16/4/26.
  */
class ProxyPlugin extends Plugin {
  private var proxy: BrowserMobProxyServer = _
  val port = 7777

  //todo: 支持代理
  override def start(): Unit = {
    BasicConfigurator.configure()
    Logger.getRootLogger.setLevel(Level.INFO)
    Logger.getLogger("ProxyServer").setLevel(Level.WARN)

    proxy = new BrowserMobProxyServer()
    proxy.setHarCaptureTypes(CaptureType.getNonBinaryContentCaptureTypes)
    proxy.setTrustAllServers(true)
    proxy.start(port)

    //proxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes)
    //proxy.setHarCaptureTypes(CaptureType.getHeaderCaptureTypes)
    log.info(s"proxy server listen on ${port}")
    proxy.newHar("start")
  }

  override def beforeElementAction(element: URIElement): Unit = {
    log.info("clear har")
    proxy.endHar()
    //创建新的har
    val harFileName = getCrawler().getBasePathName() + ".har"
    proxy.newHar(harFileName)
  }

  override def afterElementAction(element: URIElement): Unit = {
    log.info("save har")
    val harFileName = getCrawler().getBasePathName() + ".har"
    val file = new File(harFileName)
    try {
      log.info(proxy.getHar)
      log.info(proxy.getHar.getLog)
      log.info(proxy.getHar.getLog.getEntries.size())
      log.info(s"har entry size = ${proxy.getHar.getLog.getEntries.size()}")
      if (proxy.getHar.getLog.getEntries.size() > 0) {
        proxy.getHar.writeTo(file)
      }
    } catch {
      case e: Exception =>{
        log.error("read har error")
        log.error(e.getCause)
        log.error(e.getMessage)
        e.getStackTrace.foreach(log.error)
      }
    }

  }

  override def stop(): Unit ={
    log.info("prpxy stop")
    proxy.stop()
  }
}
