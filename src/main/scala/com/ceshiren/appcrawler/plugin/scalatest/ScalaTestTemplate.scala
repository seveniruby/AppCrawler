package com.ceshiren.appcrawler.plugin.scalatest

import com.ceshiren.appcrawler.AppCrawler
import com.ceshiren.appcrawler.core.Status
import com.ceshiren.appcrawler.plugin.report.ReportFactory
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.XPathUtil
import org.scalatest
import org.scalatest._

import scala.jdk.CollectionConverters._
import scala.reflect.io.File

/**
  * Created by seveniruby on 2017/3/25.
  */
class ScalaTestTemplate extends FunSuite with BeforeAndAfterAllConfigMap with Matchers {
  var name = "template"
  var uri = ""

  override def suiteName = name

  def addTestCase() {

    ReportFactory.getSelected(uri).asScala.foreach(ele => {
      val testcase = ele.getElement.getXpath.replace("\\", "\\\\")
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
              if (step.getGiven() == null ||
                step.getGiven().forall(g => XPathUtil.getNodeListByKey(g, ele.getReqDom).nonEmpty)
              ) {
                log.info(s"match testcase ${ele.getElement.getXpath}")

                if (step.then != null) {
                  log.info("assertion start")
                  log.info(step.`then`)
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
