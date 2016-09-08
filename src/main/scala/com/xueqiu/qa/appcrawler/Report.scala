package com.xueqiu.qa.appcrawler

import org.scalatest.tools.Runner

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Codec
import scala.reflect.io.File

/**
  * Created by seveniruby on 16/8/15.
  */
trait Report extends CommonLog {
  var reportPath = ""
  var testcaseDir = ""
  var clickedElementsList = mutable.Stack[UrlElement]()

  def saveTestCase(elements: scala.collection.mutable.Map[UrlElement, ElementStatus.Value], clickedElementsList: mutable.Stack[UrlElement], resultDir: String): Unit = {
    log.info("save testcase")
    reportPath = resultDir
    testcaseDir = reportPath + "/tmp/"
    this.clickedElementsList = clickedElementsList
    //为了保持独立使用
    val path = new java.io.File(resultDir).getCanonicalPath

    val suites = elements.map(x => x._1.url).toList.distinct
    suites.foreach(suite => {
      val index = suites.indexOf(suite)
      val code = genTestCase(index, suite, elements.filter(x => x._1.url == suite).toList)
      val fileName = s"${path}/tmp/AppCrawler_${suites.indexOf(suite)}.scala"
      File(fileName)(Codec.UTF8).writeAll(code)
      //File(fileName).writeAll(code)
    })
  }

  def genTestCase(index: Int, suite: String, elements: List[(UrlElement, ElementStatus.Value)]): String = {

    val codeTestCase = new StringBuilder
    //判断有无xpath重叠的元素, 这样会导致生成的测试用例因为重名出问题
    val locs = elements.map(_._1.loc)
    if (locs.distinct.size != locs.size) {
      log.warn("duplicate element")
      elements.foreach(log.warn)
    }
    val sortedElements=elements.map(ele => {
      val testcase = ele._1.loc.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "")
        .replace("\r", "")
      val isPass = ele._2==ElementStatus.Clicked
      val imgIndex = (clickedElementsList.reverse.lastIndexOf(ele._1))
      val img = imgIndex + s"_${ele._1.toFileName()}.jpg"
      (imgIndex, testcase, isPass, img)
    }).sortBy(_._1)
    //把未遍历的放到后面
    (sortedElements.filter(_._1 != -1) ++ sortedElements.filter(_._1 == -1)).foreach(ele => {
      val imgIndex = ele._1
      val testcase = ele._2
      val isPass = ele._3
      val img = ele._4
      //换行会导致scala编译报错.
      codeTestCase.append(
        s"""
           |  test("clickedIndex=${imgIndex} xpath=${testcase}"){
           |    ${
          if (isPass) {s"""markup("<img src='${img}' width='400' />")"""} else {
            ""
          }
        }
           |    if(true==${isPass}){
           |
           |    }else{
           |      cancel("never access this element 此控件未遍历")
           |    }
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
  }


}
