package com.ceshiren.appcrawler.plugin.junit5

import com.ceshiren.appcrawler.plugin.report.{Report, ReportFactory}
import com.ceshiren.appcrawler.plugin.scalatest.SuiteToClass
import com.ceshiren.appcrawler.utils.Log.log

import scala.jdk.CollectionConverters._
/**
  * Created by seveniruby on 16/8/15.
  */
class JUnit5Runtime extends Report {

  override def genTestCase(resultDir: String): Unit = {
    //todo:

    log.info("save testcase")
    ReportFactory.initReportPath(resultDir)
    //为了保持独立使用

    val suites = ReportFactory.store.getElementStoreMap.asScala.map(x => x._2.getElement.getUrl.replaceAllLiterally("..", ".")).toList.distinct
    var index=0
    suites.foreach(suite => {
      log.info(s"gen testcase class ${suite}")
      //todo: 基于规则的多次点击事件只会被保存到一个状态中. 需要区分
      SuiteToClass.genTestCaseClass(
        suite,
        "com.ceshiren.appcrawler.plugin.junit5.AllureTemplate",
        Map("uri"->suite, "name"->suite),
        ReportFactory.testcaseDir
      )
    })

    //todo: 使用javaassit生成allure template的子类，并在初始化中修改page name
  }


  //todo: 用junit+allure代替
  override def runTestCase(namespace: String=""): Unit = {

    //seveniruby demo test
    //todo: execute junit5  https://junit.org/junit5/docs/current/user-guide/#launcher-api-execution


    //todo: delay allure javaagent
    //java -javaagent:xxxxxx.jar -jar appcrawler.jar
    //https://docs.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html

    //todo: allure delay
    //从jar导出javaagent的jar，然后自己exec java -jaragent。。


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