package com.testerhome.appcrawler

import java.io

import com.testerhome.appcrawler.data.AbstractElementStore
import com.testerhome.appcrawler.plugin.junit5.JUnit5Runtime
import com.testerhome.appcrawler.plugin.scalatest.{ScalaTestRuntime, ScalaTestTemplate}

object ReportFactory extends CommonLog {

  var showCancel=false
  var title="AppCrawler"
  var master=""
  var candidate=""
  var reportDir=""
  var store: AbstractElementStore = _


  var reportPath = ""
  var testcaseDir = ""
  var report: Report=_


  def initStore(store: AbstractElementStore): Unit ={
    this.store=store
  }

  def initReportPath(path: String): Unit ={
    reportPath=path
    log.info(s"reportPath=${ReportFactory.reportPath}")
    testcaseDir = reportPath + "/tmp/"
    log.info(s"testcaseDir=${ReportFactory.testcaseDir}")
    val tmpDir=new io.File(s"${reportPath}/tmp/")
    if(tmpDir.exists()==false){
      log.info(s"create ${tmpDir.getPath} directory")
      tmpDir.mkdir()
    }
  }
  def genReport(`type`: String="scalatest"): Report ={
    report=`type` match {
      case "scalatest" => new ScalaTestRuntime();
      case "junit5" => new JUnit5Runtime();
    }
    return report
  }

  def getInstance(): Report ={
    if (report == null) {
      log.error("report not init")
      genReport()
    }
    return report
  }
}
