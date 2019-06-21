package com.testerhome.appcrawler.plugin.scalatest

import com.testerhome.appcrawler.{Report, ReportFactory, TData}
import com.testerhome.appcrawler.data.AbstractElementStore
import org.scalatest.tools.Runner

import scala.collection.JavaConversions._
import scala.io.Source

/**
  * Created by seveniruby on 16/8/15.
  */
class ScalaTestRuntime extends Report {

  override def genTestCase(resultDir: String): Unit = {
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
        "com.testerhome.appcrawler.plugin.scalatest.ScalaTestTemplate",
        Map("uri"->suite, "name"->suite),
        ReportFactory.testcaseDir
      )
    })
  }


  //todo: 用junit+allure代替
  override def runTestCase(namespace: String=""): Unit = {
    var cmdArgs = Array("-R", ReportFactory.testcaseDir,
      "-oF", "-u", ReportFactory.reportPath, "-h", ReportFactory.reportPath)

    if(namespace.nonEmpty){
      cmdArgs++=Array("-s", namespace)
    }
    log.debug(cmdArgs.mkString)

    /*
    val testcaseDirFile=new java.io.File(testcaseDir)
    FileUtils.listFiles(testcaseDirFile, Array(".class"), true).map(_.split(".class").head)
    val suites= testcaseDirFile.list().filter(_.endsWith(".class")).map(_.split(".class").head).toList
    suites.map(suite => Array("-s", s"${namespace}${suite}")).foreach(array => {
      cmdArgs = cmdArgs ++ array
    })

    if (suites.size > 0) {
      log.info(s"run ${cmdArgs.toList}")
      Runner.run(cmdArgs)
      Runtimes.reset
      changeTitle
    }
    */
    log.info(s"run ${cmdArgs.mkString(" ")}")
    Runner.run(cmdArgs)
    changeTitle(ReportFactory.title)
  }

  override def changeTitle(title:String): Unit ={
    val originTitle="ScalaTest Results"
    val indexFile=ReportFactory.reportPath+"/index.html"
    val newContent=Source.fromFile(indexFile).mkString.replace(originTitle, title)
    scala.reflect.io.File(indexFile).writeAll(newContent)
  }

}