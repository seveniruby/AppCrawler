package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.plugin.ReportPlugin
import com.ceshiren.appcrawler._
import com.ceshiren.appcrawler.core.{Crawler, ElementInfo, Status}
import com.ceshiren.appcrawler.model.{URIElement, URIElementStore}
import com.ceshiren.appcrawler.plugin.ReportPlugin
import com.ceshiren.appcrawler.utils.Log.log
import org.scalatest.FunSuite
import org.scalatest.tools.Runner

/**
  * Created by seveniruby on 16/8/12.
  */
class TestReportPlugin extends FunSuite {
  test("gen suite"){
    val report=new ReportPlugin()
    val crawler=new Crawler()
    report.setCrawer(crawler)

    val element_1=URIElement("a", "b", "c", "d", "e")
    val info_1=new ElementInfo()
    info_1.element=element_1
    info_1.action=Status.SKIPPED


    val element_2=URIElement("aa", "bb", "cc", "dd", "ee")
    val info_2=new ElementInfo()
    info_2.element=element_2
    info_2.action=Status.CLICKED

    val elementsStore=scala.collection.mutable.Map(
      element_1.toString->info_1,
      element_2.toString->info_2
    )
    val store=new URIElementStore
    store.elementStoreMap ++= elementsStore
    // 由于更换了store对象，暂时关闭该测试
//    report.saveTestCase(store, "/tmp/")

  }

  test("run"){

    val report=new ReportPlugin()
    val crawler=new Crawler()
    report.setCrawer(crawler)

    //Runner.run(Array("-R", "target", "-w", "com.ceshiren.appcrawler.report", "-o", "-u", "target/test-reports", "-h", "target/test-reports"))
    Runner.run(Array(
      "-R", "/Users/seveniruby/projects/LBSRefresh/target",
      "-w", "com.ceshiren.appcrawler",
      "-o", "-u", "target/test-reports", "-h", "target/test-reports"))

  }

}
