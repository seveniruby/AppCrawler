package com.testerhome.appcrawler

import com.testerhome.appcrawler.hbh.NewURIElementStore
import org.apache.commons.io.FileUtils
import org.scalatest.tools.Runner
import sun.security.provider.MD5

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.{Codec, Source}
import scala.reflect.io.File
import collection.JavaConversions._

/**
  * Created by seveniruby on 16/8/15.
  */
trait Report extends CommonLog {
  var reportPath = ""
  var testcaseDir = ""

  def saveTestCase(store: NewURIElementStore, resultDir: String): Unit = {
    log.info("save testcase")
    reportPath = resultDir
    testcaseDir = reportPath + "/tmp/"
    //为了保持独立使用
    val path = new java.io.File(resultDir).getCanonicalPath

    val suites = store.getNewElementStore.map(x => x._2.getUriElement.url).toList.distinct
    var index=0
    suites.foreach(suite => {
      log.info(s"gen testcase class ${suite}")
      //todo: 基于规则的多次点击事件只会被保存到一个状态中. 需要区分
      SuiteToClass.genTestCaseClass(
        suite,
        "com.testerhome.appcrawler.TemplateTestCase",
        Map("uri"->suite, "name"->suite),
        testcaseDir
      )
    })
  }


  //todo: 用junit+allure代替
  def runTestCase(namespace: String=""): Unit = {
    var cmdArgs = Array("-R", testcaseDir,
      "-oF", "-u", reportPath, "-h", reportPath)

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
    changeTitle()
  }

  def changeTitle(title:String=Report.title): Unit ={
    val originTitle="ScalaTest Results"
    val indexFile=reportPath+"/index.html"
    val newContent=Source.fromFile(indexFile).mkString.replace(originTitle, title)
    scala.reflect.io.File(indexFile).writeAll(newContent)
  }

}

object Report extends Report{
  var showCancel=false
  var title="AppCrawler"
  var master=""
  var candidate=""
  var reportDir=""
  var store=new NewURIElementStore


  def loadResult(elementsFile: String): NewURIElementStore ={
    val content=Source.fromFile(elementsFile).mkString
    log.info(s"${elementsFile} size = ${content.size}")
    //todo: cannot deserialize from Object value (no delegate- or property-based Creator)
    log.warn("一定概率失败，底层依赖库的bug")
    TData.fromYaml[NewURIElementStore](content)
  }
}
