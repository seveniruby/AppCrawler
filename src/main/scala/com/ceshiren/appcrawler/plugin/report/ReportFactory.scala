package com.ceshiren.appcrawler.plugin.report

import com.ceshiren.appcrawler.core.{ElementInfo, Status}
import com.ceshiren.appcrawler.model.URIElementStore
import com.ceshiren.appcrawler.plugin.junit5.JUnit5Runtime
import com.ceshiren.appcrawler.plugin.scalatest.ScalaTestRuntime
import com.ceshiren.appcrawler.utils.CrawlerLog.log

import java.{io, util}
import scala.jdk.CollectionConverters.{IterableHasAsJava, MapHasAsScala}

object ReportFactory  {

  var showCancel = false
  var title = "AppCrawler"
  var master = ""
  var candidate = ""
  var reportDir = ""
  var store: URIElementStore = _


  var reportPath = ""
  var testcaseDir = ""
  var report: Report = _


  def initStore(store: URIElementStore): Unit = {
    this.store = store
  }

  def initReportPath(path: String): Unit = {
    reportPath = path
    log.info(s"reportPath=${ReportFactory.reportPath}")
    testcaseDir = reportPath + "/tmp/"
    log.info(s"testcaseDir=${ReportFactory.testcaseDir}")
    val tmpDir = new io.File(s"${reportPath}/tmp/")
    if (tmpDir.exists() == false) {
      log.info(s"create ${tmpDir.getPath} directory")
      tmpDir.mkdir()
    }
  }

  def getReportEngine(`type`: String = "scalatest"): Report = {
    report = `type` match {
      case "scalatest" => new ScalaTestRuntime();
      case "junit5" => new JUnit5Runtime();
    }
    return report
  }

  def getInstance(): Report = {
    if (report == null) {

      log.info("report not init")
      report = getReportEngine()
    }
    return report
  }

  def getSelected(uri: String): util.Collection[ElementInfo] = {
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
