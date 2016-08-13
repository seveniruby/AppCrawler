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

  override def start(): Unit ={
  }

  override def stop(): Unit ={
    saveTestCase()
    runTestCase()
  }

  override def afterUrlRefresh(url:String): Unit ={
    saveTestCase()
    runTestCase()
  }

  def saveTestCase(): Unit ={
    log.info("save testcase")
    val elements=getCrawler().elements
    val path=getCrawler().conf.resultDir

    elements.map(x=>x._1.url).toList.distinct.foreach(url =>{
      val suiteName=url.replaceAll("[_?\\-]", "")
      val code=genTestCase(suiteName, elements.filter(x=>x._1.url==url).toList)
      val fileName=s"${path}/${suiteName}.scala"
      File(fileName).writeAll(code)
      //Eval(new java.io.File(fileName))
      if(suites.contains(suiteName)==false){
        suites.append(suiteName)
      }
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
    val reportPath=new java.io.File(getCrawler().conf.resultDir).getCanonicalPath
    log.info(s"reportPath=${reportPath}")
    var cmdArgs=Array("-R", reportPath ,
      "-o", "-u", reportPath, "-h", reportPath)

    suites.map(suite=>Array("-s", s"${suite}")).foreach(array=>{
      cmdArgs=cmdArgs++array
    })

    val sourceFiles=suites.map(name=>new java.io.File(s"${reportPath}/${name}.scala").getCanonicalPath).toList
    log.info(s"compile testcase ${sourceFiles} into ${reportPath}")
    Runtimes.init(reportPath)
    Runtimes.compile(sourceFiles)

    log.info(s"run ${cmdArgs.toList}")
    Runner.run(cmdArgs)

  }


}
