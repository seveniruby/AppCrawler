package com.testerhome.appcrawler.plugin

import java.io
import java.nio.file.{Files, Paths}

import com.testerhome.appcrawler.{Report}
import com.testerhome.appcrawler._
import com.testerhome.appcrawler.data.AbstractElement
import org.scalatest.FunSuite
import org.scalatest.tools.Runner
import sun.misc.{Signal, SignalHandler}

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.reflect.io.File
import collection.JavaConversions._

/**
  * Created by seveniruby on 16/8/12.
  */
class ReportPlugin extends Plugin with Report {

  var requestFile:String=_
  override def start(): Unit ={
    reportPath=new java.io.File(getCrawler().conf.resultDir).getCanonicalPath
    requestFile=reportPath + File.separator + "request"
    log.info(s"reportPath=${reportPath}")
    val tmpDir=new io.File(s"${reportPath}/tmp/")
    if(tmpDir.exists()==false){
      log.info(s"create ${reportPath}/tmp/ directory")
      tmpDir.mkdir()
    }
  }

  override def stop(): Unit ={
    this.getCrawler().saveLog()
    generateReport()
  }

  override def afterElementAction(element: AbstractElement): Unit ={
    //todo: 子线程处理，异步处理
    getCrawler().driver.asyncTask(timeout = 120, name = "report", needThrow = true) {
      if (needReport()) {
        log.info("generate test report ")
        getCrawler().saveLog()
        generateReport()
      }
    }
  }

  def needReport(): Boolean ={
    val curSize=getCrawler().store.getClickElementList.size
    if(curSize%5==0){
      if(curSize%20==0){
        true
      }else {
        log.info(s"read command from ${requestFile}")
        val command=if(Files.exists(Paths.get(requestFile))){
          Source.fromFile(requestFile).mkString
        }else{
          ""
        }
        log.info(command)
        if (command.contains("stop")) {
          stop()
          true
        } else if (command.contains("save")) {
          true
        } else {
          false
        }
      }
    }else{
      false
    }
  }


  //todo: 使用独立工具出报告
  def generateReport(): Unit ={
    log.info(s"reportPath=${reportPath}")
    Report.saveTestCase(getCrawler().store, reportPath)
    Report.store=getCrawler().store
    Report.runTestCase()
  }


}
