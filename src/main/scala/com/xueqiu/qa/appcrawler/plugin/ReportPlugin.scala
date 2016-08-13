package com.xueqiu.qa.appcrawler.plugin

import java.io

import com.twitter.util.Eval
import com.xueqiu.qa.appcrawler.{Runtimes, UrlElement, Plugin, CommonLog}
import org.scalatest.FunSuite
import org.scalatest.tools.Runner

import scala.collection.mutable.ListBuffer
import scala.reflect.io.File

/**
  * Created by seveniruby on 16/8/12.
  */
class ReportPlugin extends Plugin{
  val suites=ListBuffer[String]()
  var reportPath=""

  override def start(): Unit ={
    reportPath=new java.io.File(getCrawler().conf.resultDir).getCanonicalPath
    log.info(s"reportPath=${reportPath}")

  }

  override def stop(): Unit ={
    saveTestCase()
    runTestCase()
  }

  override def afterUrlRefresh(url:String): Unit ={
    if(suites.contains(url)){
      //新页面出现的时候保存老数据
      return
    }else {
      suites.append(url)
      saveTestCase()
      runTestCase()
    }
  }

  def saveTestCase(): Unit ={
    log.info("save testcase")
    val elements=getCrawler().elements
    //为了保持独立使用
    val path=new java.io.File(getCrawler().conf.resultDir).getCanonicalPath

    val suites=elements.map(x=>x._1.url).toList.distinct
    suites.foreach(suite =>{
      val code=genTestCase(suite, elements.filter(x=>x._1.url==suite).toList)
      val fileName=s"${path}/AppCrawler_${suites.indexOf(suite)}.scala"
      File(fileName).writeAll(code)
    })
  }

  def genTestCase(suite:String, elements:List[(UrlElement, Boolean)]): String ={

    val codeTestCase=new StringBuilder
    elements.foreach(ele=>{
      val testcase=ele._1.loc.replace("\"", "\\\"")
      val isPass=ele._2
      val img=(getCrawler().clickedElementsList.reverse.indexOf(ele._1)+1)+s"_${ele._1.toFileName()}.jpg"
      codeTestCase.append(
        s"""
          |  test("${testcase}"){
          |    ${if(isPass){s"""markup("<img src='${img}'/>")"""}else{""}}
          |    assert(true==${isPass})
          |  }
        """.stripMargin)
    })

    val s=
      s"""
         |//package com.xueqiu.qa.appcrawler.report
         |
         |import org.scalatest.FunSuite
         |class AppCrawler_${suites.indexOf(suite)} extends FunSuite {
         |  override def suiteName="${suite.replace("\"", "\\\"")}"
         |${codeTestCase}
         |}
      """.stripMargin
    s
  }

  def runTestCase(): Unit ={
    var cmdArgs=Array("-R", reportPath+"/tmp/" ,
      "-o", "-u", reportPath, "-h", reportPath)

    val suites=new java.io.File(reportPath).list().filter(_.endsWith(".scala")).map(_.split(".scala").head).toList
    suites.map(suite=>Array("-s", s"${suite}")).foreach(array=>{
      cmdArgs=cmdArgs++array
    })

    val sourceFiles=suites.map(name=>s"${reportPath}/${name}.scala")
    log.info(s"compile testcase ${sourceFiles} into ${reportPath}")
    Runtimes.init(reportPath+"/tmp/")
    Runtimes.compile(sourceFiles)

    log.info(s"run ${cmdArgs.toList}")
    Runner.run(cmdArgs)
  }


}
