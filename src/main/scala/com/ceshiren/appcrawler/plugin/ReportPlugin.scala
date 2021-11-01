package com.ceshiren.appcrawler.plugin

import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.plugin.report.ReportFactory
import com.ceshiren.appcrawler.report.MvnReplace

import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.reflect.io.File

/**
  * Created by seveniruby on 16/8/12.
  */
class ReportPlugin extends Plugin {

  var requestFile:String=_
  override def start(): Unit ={
    ReportFactory.initReportPath(new java.io.File(getCrawler().conf.resultDir).getCanonicalPath)
    requestFile=ReportFactory.reportPath + File.separator + "request"
  }

  override def stop(): Unit ={
    this.getCrawler().saveLog()
    generateReport()
  }

  override def afterElementAction(element: URIElement): Unit ={
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
    val curSize=getCrawler().store.getClickedElementsList.size
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

    if(getCrawler().conf.useNewData){
      // 生成allure报告
      log.info("allure report generate")
      MvnReplace.runTest()
      if(MvnReplace.isExist) MvnReplace.executeCommand("cmd /c allure generate " + getCrawler().conf.resultDir + "/allure-results" +" -o " + getCrawler().conf.resultDir + "/report")
    }else {
      log.info(s"reportPath=${ReportFactory.reportPath}")
      ReportFactory.initStore(getCrawler().store)
      ReportFactory.getReportEngine("scalatest")
      ReportFactory.getInstance().genTestCase(ReportFactory.reportPath)
      ReportFactory.getInstance().runTestCase()
    }
  }
}
