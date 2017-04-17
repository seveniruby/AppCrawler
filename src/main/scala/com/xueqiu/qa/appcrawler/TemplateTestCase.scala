package com.xueqiu.qa.appcrawler

import org.apache.commons.lang3.StringUtils
import org.scalatest
import org.scalatest._

import scala.reflect.io.File


/**
  * Created by seveniruby on 2017/3/25.
  */
class TemplateTestCase extends FunSuite with BeforeAndAfterAllConfigMap with Matchers with CommonLog{
  var name = "template"
  var uri = ""

  override def suiteName = name

  def addTestCase() {
    val sortedElements = Report.store.elementStore
      .filter(x => x._2.element.url == uri)
      .map(_._2).toList
      .sortBy(_.clickedIndex)

    println(sortedElements.size)
    //把未遍历的放到后面
    val selected = if (Report.showCancel) {
      sortedElements.filter(_.action == ElementStatus.Clicked) ++ sortedElements.filter(_.action == ElementStatus.Skiped)
    } else {
      sortedElements.filter(_.action == ElementStatus.Clicked)
    }
    println(selected.size)
    selected.foreach(ele => {
      val testcase = ele.element.loc.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "")
        .replace("\r", "")

      test(s"clickedIndex=${ele.clickedIndex} xpath=${testcase}") {
        if (ele.action == ElementStatus.Clicked) {
          markup(
            s"""
             |
             |<img src='${File(ele.reqImg).name}' width='100%' />
             |<br></br>
             |<p>after clicked</p>
             |<img src='${File(ele.resImg).name}' width='100%' />
          """.
            stripMargin
          )

          /*
          markup(
            s"""
            |
            |<pre>
            |<xmp>
            |${ele.reqDom.replaceFirst("xml", "x_m_l")}
            |</xmp>
            |</pre>
          """.stripMargin
          )
          */
          val req = RichData.toDocument(ele.reqDom)
          val res = RichData.toDocument(ele.resDom)
          log.debug(ele.reqDom)
          AppCrawler.crawler.conf.asserts.foreach(assert => {
            val given = assert.getOrElse("given", List[String]()).asInstanceOf[List[String]]
            log.info(given.map(g=>RichData.getListFromXPath(g, req).size))
            if (given.forall(g => RichData.getListFromXPath(g, req).size > 0) == true) {
              log.info("match")
              val existAsserts = assert.getOrElse("then", List[String]()).asInstanceOf[List[String]]
              val cp = new scalatest.Checkpoints.Checkpoint
              existAsserts.foreach(existAssert => {
                log.debug(existAssert)
                cp {
                  withClue(s"${existAssert} 不存在\n") {
                    RichData.getListFromXPath(existAssert, res).size should be > 0
                  }
                }
              })
              cp.reportAll()
            }else{
              log.info("not match")
            }
          })
        }
      }
    })
  }
}

object TemplateTestCase extends CommonLog{
  def saveTestCase(store: UrlElementStore, resultDir: String): Unit = {
    log.info("save testcase")
    Report.reportPath = resultDir
    Report.testcaseDir = Report.reportPath + "/tmp/"
    //为了保持独立使用
    val path = new java.io.File(resultDir).getCanonicalPath

    val suites = store.elementStore.map(x => x._2.element.url).toList.distinct
    suites.foreach(suite => {
      log.info(s"gen testcase class ${suite}")
      //todo: 基于规则的多次点击事件只会被保存到一个状态中. 需要区分
      TemplateClass.genTestCaseClass(
        suite,
        "com.xueqiu.qa.appcrawler.TemplateTestCase",
        Map("uri"->suite, "name"->suite),
        Report.testcaseDir
      )
    })
  }

}
