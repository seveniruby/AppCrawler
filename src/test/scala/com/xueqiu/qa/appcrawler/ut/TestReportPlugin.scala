package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler._
import com.xueqiu.qa.appcrawler.plugin.ReportPlugin
import org.scalatest.FunSuite
import org.scalatest.tools.Runner

/**
  * Created by seveniruby on 16/8/12.
  */
class TestReportPlugin extends FunSuite with CommonLog{
  test("gen suite"){
    val report=new ReportPlugin()
    val crawler=new Crawler()
    report.setCrawer(crawler)

    val element_1=UrlElement("a", "b", "c", "d", "e")
    val info_1=new ElementInfo()
    info_1.element=element_1
    info_1.action=ElementStatus.Skiped


    val element_2=UrlElement("aa", "bb", "cc", "dd", "ee")
    val info_2=new ElementInfo()
    info_2.element=element_2
    info_2.action=ElementStatus.Clicked

    val elementsStore=scala.collection.mutable.Map(
      element_1.toString->info_1,
      element_2.toString->info_2
    )
    val code=report.genTestCase(1, "demo", elementsStore)
    log.info(code)

  }

  test("run"){

    val report=new ReportPlugin()
    val crawler=new Crawler()
    report.setCrawer(crawler)

    //Runner.run(Array("-R", "target", "-w", "com.xueqiu.qa.appcrawler.report", "-o", "-u", "target/test-reports", "-h", "target/test-reports"))
    Runner.run(Array(
      "-R", "/Users/seveniruby/projects/LBSRefresh/target",
      "-w", "com.xueqiu.qa.appcrawler",
      "-o", "-u", "target/test-reports", "-h", "target/test-reports"))

  }

}
