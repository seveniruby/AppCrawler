package com.testerhome.appcrawler

import com.testerhome.appcrawler.hbh.NewURIElementStore
import com.testerhome.appcrawler.hbh.NewURIElementStore.Status
import org.apache.commons.lang3.StringUtils
import org.scalatest
import org.scalatest._

import scala.reflect.io.File
import collection.JavaConversions._

/**
  * Created by seveniruby on 2017/3/25.
  */
class TemplateTestCase extends FunSuite with BeforeAndAfterAllConfigMap with Matchers with CommonLog {
  var name = "template"
  var uri = ""

  override def suiteName = name

  def addTestCase() {
    log.trace(s"Report.store.elementStore size = ${Report.store.getNewElementStore.size}")
    log.trace(s"uri=${uri}")
    val sortedElements = Report.store.getNewElementStore
      .filter(x => x._2.getUriElement.url == uri)
      .map(_._2).toList
      .sortBy(_.getClickedIndex)

    log.trace(s"sortedElements=${sortedElements.size}")
    val selected = if (Report.showCancel) {
      log.info("show all elements")
      //把未遍历的放到后面
      sortedElements.filter(_.getAction == Status.CLICKED) ++
        //sortedElements.filter(_.action == ElementStatus.Skipped) ++
        sortedElements.filter(_.getAction == Status.READY)
    } else {
      log.info("only show clicked elements")
      sortedElements.filter(_.getAction == Status.CLICKED)
    }
    log.trace(s"selected elements size = ${selected.size}")
    selected.foreach(ele => {
      val testcase = ele.getUriElement.xpath.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "")
        .replace("\r", "")

      log.debug(s"add testcase ${testcase}")
      //todo: 增加ignore和cancel的区分
      test(s"clickedIndex=${ele.getClickedIndex} action=${ele.getAction}\nxpath=${testcase}") {
        ele.getAction match {
          case Status.CLICKED => {
              markup(
                s"""
                   |
               |<img src='${File(ele.getReqImg).name}' width='80%' />
                   |<br></br>
                   |<p>after clicked</p>
                   |<img src='${File(ele.getResImg).name}' width='80%' />
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
                  step.getGiven().forall(g=>XPathUtil.getNodeListByKey(g, ele.getReqDom).size>0)
                ) {
                  log.info(s"match testcase ${ele.getUriElement.xpath}")

                  if(step.then!=null) {
                    val cp = new scalatest.Checkpoints.Checkpoint
                    step.then.foreach(existAssert => {
                      log.debug(existAssert)
                      cp {
                        withClue(s"${existAssert} 不存在\n") {
                          XPathUtil.getNodeListByXPath(existAssert, ele.getResDom).size should be > 0
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
          case Status.READY => {
            cancel(s"${ele.getAction}  not click")
          }
          case Status.SKIPPED => {
            cancel(s"${ele.getAction}  skipped")
          }
        }

      }
    })
  }
}

object TemplateTestCase extends CommonLog {
  def saveTestCase(store: NewURIElementStore, resultDir: String): Unit = {
    log.info("save testcase")
    Report.reportPath = resultDir
    Report.testcaseDir = Report.reportPath + "/tmp/"
    //为了保持独立使用
    val path = new java.io.File(resultDir).getCanonicalPath

    val suites = store.getNewElementStore.map(x => x._2.getUriElement.url).toList.distinct
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
