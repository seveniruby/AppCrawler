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
  var lastSize=0
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

  override def afterElementAction(element: URIElement): Unit ={
    val count=getCrawler().store.clickedElementsList.length
    log.info(s"clickedElementsList size = ${count}")
    val curSize=getCrawler().store.clickedElementsList.size
    if(curSize-lastSize > curSize/10+10 ){
      log.info(s"${curSize}-${lastSize} > ${curSize}/10+10  ")
      log.info("generate test report ")
      generateReport()
    }
  }

  def generateReport(): Unit ={
    Report.saveTestCase(getCrawler().store, getCrawler().conf.resultDir)
    Report.store=getCrawler().store
    Report.runTestCase()

    lastSize=getCrawler().store.clickedElementsList.size
  }


}
