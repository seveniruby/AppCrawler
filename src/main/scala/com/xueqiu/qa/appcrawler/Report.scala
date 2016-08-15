package com.xueqiu.qa.appcrawler

import org.scalatest.tools.Runner

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.io.File

/**
  * Created by seveniruby on 16/8/15.
  */
trait Report extends CommonLog{
  var reportPath=""
  var clickedElementsList=mutable.Stack[UrlElement]()
  def saveTestCase(elements:scala.collection.mutable.Map[UrlElement, Boolean], clickedElementsList: mutable.Stack[UrlElement],resultDir:String): Unit ={
    log.info("save testcase")
    reportPath=resultDir
    this.clickedElementsList=clickedElementsList
    //为了保持独立使用
    val path=new java.io.File(resultDir).getCanonicalPath

    val suites=elements.map(x=>x._1.url).toList.distinct
    suites.foreach(suite =>{
      val index=suites.indexOf(suite)
      val code=genTestCase(index, suite, elements.filter(x=>x._1.url==suite).toList)
      val fileName=s"${path}/AppCrawler_${suites.indexOf(suite)}.scala"
      File(fileName).writeAll(code)
    })
  }

  def genTestCase(index:Int, suite:String, elements:List[(UrlElement, Boolean)]): String ={

    val codeTestCase=new StringBuilder
    //先展示点击过的
    val newElements=ListBuffer[(UrlElement, Boolean)]()
    newElements.appendAll(elements.filter(ele=>ele._2==true))
    newElements.appendAll(elements.filter(ele=>ele._2==false))
    //判断有无xpath重叠的元素, 这样会导致生成的测试用例因为重名出问题
    val locs=newElements.map(_._1.loc)
    if(locs.distinct.size!=locs.size){
      log.warn("duplicate element")
      newElements.foreach(log.warn)
    }
    newElements.foreach(ele=>{
      val testcase=ele._1.loc.replace("\"", "\\\"")
      val isPass=ele._2
      val imgIndex=(clickedElementsList.reverse.lastIndexOf(ele._1)+1)
      val img=imgIndex+s"_${ele._1.toFileName()}.jpg"
      codeTestCase.append(
        s"""
           |  test("xpath=${testcase}"){
           |    ${if(isPass){s"""markup("<img src='${img}'/>")"""}else{""}}
           |    assert(true==${isPass}, "未遍历")
           |  }
        """.stripMargin)
    })

    val s=
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

    if(suites.size>0) {
      log.info(s"run ${cmdArgs.toList}")
      Runner.run(cmdArgs)
      Runtimes.reset
    }
  }


}
