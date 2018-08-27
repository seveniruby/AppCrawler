package com.testerhome.appcrawler

import org.apache.commons.lang3.StringUtils
import org.scalatest
import org.scalatest._

import scala.reflect.io.File


/**
  * Created by seveniruby on 2017/3/25.
  */
class TemplateTestCase extends FunSuite with BeforeAndAfterAllConfigMap with Matchers with CommonLog {
  var name = "template"
  var uri = ""

  override def suiteName = name

  def addTestCase() {
    val sortedElements = Report.store.elementStore
      .filter(x => x._2.element.url == uri)
      .map(_._2).toList
      .sortBy(_.clickedIndex)

    val selected = if (Report.showCancel) {
      log.info("show all elements")
      //把未遍历的放到后面
      sortedElements.filter(_.action == ElementStatus.Clicked) ++
        //sortedElements.filter(_.action == ElementStatus.Skipped) ++
        sortedElements.filter(_.action == ElementStatus.Ready)
    } else {
      log.info("only show clicked elements")
      sortedElements.filter(_.action == ElementStatus.Clicked)
    }
    selected.foreach(ele => {
      val testcase = ele.element.xpath.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "")
        .replace("\r", "")

      //todo: 增加ignore和cancel的区分
      test(s"clickedIndex=${ele.clickedIndex} action=${ele.action}\nxpath=${testcase}") {
        ele.action match {
          case ElementStatus.Clicked => {
              markup(
                s"""
                   |
               |<img src='${File(ele.reqImg).name}' width='80%' />
                   |<br></br>
                   |<p>after clicked</p>
                   |<img src='${File(ele.resImg).name}' width='80%' />
          """.stripMargin
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

              AppCrawler.crawler.conf.assertGlobal.foreach(step => {
                if (
                  step.given.forall(g=>XPathUtil.getNodeListByKey(g, ele.reqDom).size>0)
                ) {
                  log.info(s"match testcase ${ele.element.xpath}")

                  if(step.then!=null) {
                    val cp = new scalatest.Checkpoints.Checkpoint
                    step.then.foreach(existAssert => {
                      log.debug(existAssert)
                      cp {
                        withClue(s"${existAssert} 不存在\n") {
                          XPathUtil.getNodeListFromXPath(existAssert, ele.resDom).size should be > 0
                        }
                      }
                    })
                    cp.reportAll()
                  }
                } else {

/*                  XPathUtil.getNodeListFromXPath(step.getXPath(), ele.reqDom)
                    .map(_.getOrElse("xpath", "")).foreach(log.warn)
                  log.warn(s"not match ${step.getXPath()} ${ele.element.xpath}")*/
                }
              })

          }
          case ElementStatus.Ready => {
            cancel(s"${ele.action}  not click")
          }
          case ElementStatus.Skipped => {
            cancel(s"${ele.action}  skipped")
          }
        }

      }
    })
  }
}

object TemplateTestCase extends CommonLog {
  def saveTestCase(store: URIElementStore, resultDir: String): Unit = {
    log.info("save testcase")
    Report.reportPath = resultDir
    Report.testcaseDir = Report.reportPath + "/tmp/"
    //为了保持独立使用
    val path = new java.io.File(resultDir).getCanonicalPath

    val suites = store.elementStore.map(x => x._2.element.url).toList.distinct
    suites.foreach(suite => {
      log.info(s"gen testcase class ${suite}")
      //todo: 基于规则的多次点击事件只会被保存到一个状态中. 需要区分
      SuiteToClass.genTestCaseClass(
        //todo: Illegal class name  Ⅱ[@]][()
        suite,
        "com.testerhome.appcrawler.TemplateTestCase",
        Map("uri" -> suite, "name" -> suite),
        Report.testcaseDir
      )
    })
  }

}
