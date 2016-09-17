package com.xueqiu.qa.appcrawler

import org.scalatest.tools.Runner

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.{Source, Codec}
import scala.reflect.io.File

/**
  * Created by seveniruby on 16/8/15.
  */
trait Report extends CommonLog {
  var reportPath = ""
  var testcaseDir = ""
  var clickedElementsList = mutable.Stack[UrlElement]()

  def saveTestCase(store: UrlElementStore, resultDir: String): Unit = {
    log.info("save testcase")
    reportPath = resultDir
    testcaseDir = reportPath + "/tmp/"
    this.clickedElementsList = clickedElementsList
    //为了保持独立使用
    val path = new java.io.File(resultDir).getCanonicalPath

    val suites = store.elementStore.map(x => x._2.element.url).toList.distinct
    suites.foreach(suite => {
      val index = suites.indexOf(suite)
      //todo: 基于规则的多次点击事件只会被保存到一个状态中. 需要区分
      val code = genTestCase(index, suite, store.elementStore.filter(x =>x._2.element.url == suite))
      val fileName = s"${path}/tmp/AppCrawler_${suites.indexOf(suite)}.scala"
      val parentDir=new java.io.File(new java.io.File(fileName).getParent)
      if(parentDir.exists()==false){
        parentDir.mkdir()
      }
      File(fileName)(Codec.UTF8).writeAll(code)
      //File(fileName).writeAll(code)
    })
  }


  def genImg(elementInfo: ElementInfo): String ={
    if (elementInfo.action==ElementStatus.Clicked) {
      s"""
         |    markup("<img src='${elementInfo.reqImg}' width='400' /><br></br><p>after clicked</p><img src='${elementInfo.resImg}' width='400' />")
         |""".stripMargin}
    else {
      """
        |    cancel("never access this element 此控件未遍历")
      """.stripMargin
    }

  }
  def genTestCase(index: Int, suite: String, elementStore: scala.collection.mutable.Map[String, ElementInfo]): String = {

    val codeTestCase = new StringBuilder

    val sortedElements=elementStore.map(_._2).toList.sortBy(_.clickedIndex)

    //把未遍历的放到后面
    val selected=if(Report.showCancel){
      sortedElements.filter(_.action==ElementStatus.Clicked) ++ sortedElements.filter(_.action==ElementStatus.Skiped)
    }else{
      sortedElements.filter(_.action==ElementStatus.Clicked)
    }
    log.info(s"selected size = ${selected.size}")
    selected.foreach(ele => {
      val testcase = ele.element.loc.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "")
        .replace("\r", "")
      //换行会导致scala编译报错.
      codeTestCase.append(
        s"""
           |  test("clickedIndex=${ele.clickedIndex} xpath=${testcase}"){
           |    ${genImg(ele)}
           |  }
        """.stripMargin)
    })

    val s =
      s"""
         |//package com.xueqiu.qa.appcrawler.report
         |
         |import org.scalatest.FunSuite
         |class AppCrawler_${index} extends FunSuite {
         |  override def suiteName="${suite.replace("\"", "\\\"")}"
         |${codeTestCase}
         |}
      """.stripMargin
    s
  }

  def runTestCase(): Unit = {
    var cmdArgs = Array("-R", testcaseDir,
      "-o", "-u", reportPath, "-h", reportPath)

    val suites = new java.io.File(testcaseDir).list().filter(_.endsWith(".scala")).map(_.split(".scala").head).toList
    suites.map(suite => Array("-s", s"${suite}")).foreach(array => {
      cmdArgs = cmdArgs ++ array
    })

    val sourceFiles = suites.map(name => s"${testcaseDir}/${name}.scala")
    log.info(s"compile testcase ${sourceFiles} into ${testcaseDir}")
    Runtimes.init(testcaseDir)
    Runtimes.compile(sourceFiles)

    if (suites.size > 0) {
      log.info(s"run ${cmdArgs.toList}")
      Runner.run(cmdArgs)
      Runtimes.reset
    }
    changeTitle
  }

  def changeTitle(): Unit ={
    val originTitle="ScalaTest Results"
    val indexFile=reportPath+"/index.html"
    val newContent=Source.fromFile(indexFile).mkString.replace(originTitle, Report.title)
    scala.reflect.io.File(indexFile).writeAll(newContent)
  }

}

object Report extends Report{
  var showCancel=false
  var title="AppCrawler"
}
