package com.xueqiu.qa.appcrawler.plugin

import java.io

import com.xueqiu.qa.appcrawler._
import org.scalatest.FunSuite
import org.scalatest.tools.Runner
import sun.misc.{SignalHandler, Signal}

import scala.collection.mutable.ListBuffer
import scala.reflect.io.File

/**
  * Created by seveniruby on 16/8/12.
  */
class ReportPlugin extends Plugin with Report {
  var lastTime=0
  override def start(): Unit ={
    reportPath=new java.io.File(getCrawler().conf.resultDir).getCanonicalPath
    log.info(s"reportPath=${reportPath}")
    val tmpDir=new io.File(s"${reportPath}/tmp/")
    if(tmpDir.exists()==false){
      log.info(s"create ${reportPath}/tmp/ directory")
      tmpDir.mkdir()
    }
  }

  override def stop(): Unit ={
    generateReport()
  }


  override def afterElementAction(element: UrlElement): Unit ={
    val count=getCrawler().store.clickedElementsList.length
    log.info(s"clickedElementsList size = ${count}")
    val curTime=(System.currentTimeMillis / 1000).toInt
    if(curTime-lastTime>120){
      log.info("every 2 min to save test report ")
      generateReport()
      lastTime=curTime
    }
  }

  def generateReport(): Unit ={
    saveTestCase(getCrawler().store, getCrawler().conf.resultDir)
    runTestCase()
  }


}
