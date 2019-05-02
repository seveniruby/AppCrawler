package com.testerhome.appcrawler

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

  def genReport(`type`: String): Report ={
    report=`type` match {
      case "scalatest" => new ScalaTestRuntime();
      case "junit5" => new JUnit5Runtime();
    }
    return report
  }

  def getInstance(): Report ={
    if (report == null) { log.error("report not init")}
    return report
  }
}
