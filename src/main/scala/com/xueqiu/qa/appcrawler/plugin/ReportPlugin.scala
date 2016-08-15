package com.xueqiu.qa.appcrawler.plugin

import java.io

import com.twitter.util.Eval
import com.xueqiu.qa.appcrawler._
import org.scalatest.FunSuite
import org.scalatest.tools.Runner

import scala.collection.mutable.ListBuffer
import scala.reflect.io.File

/**
  * Created by seveniruby on 16/8/12.
  */
class ReportPlugin extends Plugin with Report{
  var lastTime=0

  override def start(): Unit ={
    reportPath=new java.io.File(getCrawler().conf.resultDir).getCanonicalPath
    log.info(s"reportPath=${reportPath}")

  }

  override def stop(): Unit ={
    saveTestCase(getCrawler().elements, getCrawler().clickedElementsList,getCrawler().conf.resultDir)
    runTestCase()
  }


  override def afterElementAction(element: UrlElement): Unit ={
    val count=getCrawler().clickedElementsList.length
    log.info(s"clickedElementsList size = ${count}")
    val curTime=(System.currentTimeMillis / 1000).toInt
    if(curTime-lastTime>120){
      saveTestCase(getCrawler().elements, getCrawler().clickedElementsList, getCrawler().conf.resultDir)
      runTestCase()
      lastTime=curTime
    }
  }


}
