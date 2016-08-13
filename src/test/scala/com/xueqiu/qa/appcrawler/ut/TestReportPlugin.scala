package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.{Crawler, UrlElement, CommonLog}
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
    val elements=List(
      UrlElement("a", "b", "c", "d", "e")->true,
      UrlElement("a", "b", "c", "d", "e")->true,
      UrlElement("a", "b", "c", "d", "e2")->true,
      UrlElement("aa", "b", "c", "d", "e3")->false

    )
    val code=report.genTestCase(1, "demo", elements)
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
