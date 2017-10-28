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

    log.info(sortedElements.size)

    val selected = if (Report.showCancel) {
      log.info("show all elements")
      //把未遍历的放到后面
      sortedElements.filter(_.action == ElementStatus.Clicked) ++
        sortedElements.filter(_.action == ElementStatus.Skipped) ++
        sortedElements.filter(_.action == ElementStatus.Ready)
    } else {
      log.info("only show clicked elements")
      sortedElements.filter(_.action == ElementStatus.Clicked)
    }
    log.info(selected.size)
    selected.foreach(ele => {
      val testcase = ele.element.loc.replace("\\", "\\\\")
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
              val req = XPathUtil.toDocument(ele.reqDom)
              val res = XPathUtil.toDocument(ele.resDom)
              log.debug(ele.reqDom)
              AppCrawler.crawler.conf.asserts.foreach(assert => {
                val given = assert.getOrElse("given", List[String]()).asInstanceOf[List[String]]
                log.info(given.map(g => XPathUtil.getListFromXPath(g, req).size))
                if (given.forall(g => XPathUtil.getListFromXPath(g, req).size > 0) == true) {
                  log.info(s"asserts match")
                  val existAsserts = assert.getOrElse("then", List[String]()).asInstanceOf[List[String]]
                  val cp = new scalatest.Checkpoints.Checkpoint
                  existAsserts.foreach(existAssert => {
                    log.debug(existAssert)
                    cp {
                      withClue(s"${existAssert} 不存在\n") {
                        XPathUtil.getListFromXPath(existAssert, res).size should be > 0
                      }
                    }
                  })
                  cp.reportAll()
                } else {
                  log.info("not match")
                }
              })

              AppCrawler.crawler.conf.testcase.steps.foreach(step => {
                if (XPathUtil.getListFromXPath(step.when.xpath, req)
                  .map(_.getOrElse("xpath", ""))
                  .headOption == Some(ele.element.loc)
                ) {
                  log.info(s"match testcase ${ele.element.loc}")

                  if(step.then!=null) {
                    val cp = new scalatest.Checkpoints.Checkpoint
                    step.then.foreach(existAssert => {
                      log.debug(existAssert)
                      cp {
                        withClue(s"${existAssert} 不存在\n") {
                          XPathUtil.getListFromXPath(existAssert, res).size should be > 0
                        }
                      }
                    })
                    cp.reportAll()
                  }
                } else {
                  log.info("not match")
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
        suite,
        "com.testerhome.appcrawler.TemplateTestCase",
        Map("uri" -> suite, "name" -> suite),
        Report.testcaseDir
      )
    })
  }

}
