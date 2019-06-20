package com.testerhome.appcrawler.plugin.junit5

import com.testerhome.appcrawler.data.AbstractElementStore
import com.testerhome.appcrawler.plugin.scalatest.SuiteToClass
import com.testerhome.appcrawler.{Report, ReportFactory, TData}
import org.scalatest.tools.Runner

import scala.collection.JavaConversions._
import scala.io.Source

/**
  * Created by seveniruby on 16/8/15.
  */
class JUnit5Runtime extends Report {

  override def genTestCase(resultDir: String): Unit = {
    //todo:

    log.info("save testcase")
    ReportFactory.initReportPath(resultDir)
    //为了保持独立使用

    val suites = ReportFactory.store.getElementStoreMap.map(x => x._2.getElement.getUrl.replaceAllLiterally("..", ".")).toList.distinct
    var index=0
    suites.foreach(suite => {
      log.info(s"gen testcase class ${suite}")
      //todo: 基于规则的多次点击事件只会被保存到一个状态中. 需要区分
      SuiteToClass.genTestCaseClass(
        suite,
        "com.testerhome.appcrawler.plugin.junit5.AllureTemplate",
        Map("uri"->suite, "name"->suite),
        ReportFactory.testcaseDir
      )
    })
  }


  //todo: 用junit+allure代替
  override def runTestCase(namespace: String=""): Unit = {
    //todo:
    //seveniruby demo test
    //execute junit5
    //allure javaagent delay
  }

  override def changeTitle(title:String): Unit ={
    //todo:
    //malu test demo
    //seveniruby need merge
    //naruto test
    //naruto 3
    //seveniruby2
    //sevenriby3
  }

}