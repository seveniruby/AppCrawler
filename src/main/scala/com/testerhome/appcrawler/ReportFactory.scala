package com.testerhome.appcrawler

import java.{io, util}

import com.testerhome.appcrawler.data.{AbstractElementInfo, AbstractElementStore}
import com.testerhome.appcrawler.data.AbstractElementStore.Status
import com.testerhome.appcrawler.plugin.junit5.JUnit5Runtime
import com.testerhome.appcrawler.plugin.scalatest.{ScalaTestRuntime, ScalaTestTemplate}

import collection.JavaConverters._

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
  def getReportEngine(`type`: String="scalatest"): Report ={
    report=`type` match {
      case "scalatest" => new ScalaTestRuntime();
      case "junit5" => new JUnit5Runtime();
    }
    return report
  }

  def getInstance(): Report ={
    if (report == null) {

      log.info("report not init")
      report=getReportEngine()
    }
    return report
  }

  def getSelected(uri:String): util.Collection[AbstractElementInfo] ={
    log.trace(s"Report.store.elementStore size = ${ReportFactory.store.getElementStoreMap.size}")
    log.trace(s"uri=${uri}")
    val sortedElements = store.getElementStoreMap.asScala
      .filter(x => x._2.getElement.getUrl.replaceAllLiterally("..", ".") == uri)
      .map(_._2).toList
      .sortBy(_.getClickedIndex)

    log.trace(s"sortedElements=${sortedElements.size}")
    val selected = if (ReportFactory.showCancel) {
      log.info("show all elements")
      //把未遍历的放到后面
      sortedElements.filter(_.getAction == Status.CLICKED) ++
        //sortedElements.filter(_.action == ElementStatus.Skipped) ++
        sortedElements.filter(_.getAction == Status.READY)
    } else {
      log.info("only show clicked elements")
      sortedElements.filter(_.getAction == Status.CLICKED)
    }
    log.trace(s"selected elements size = ${selected.size}")
    return selected.asJavaCollection
  }



}
