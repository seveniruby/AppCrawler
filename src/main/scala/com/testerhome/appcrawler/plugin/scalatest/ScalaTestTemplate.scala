package com.testerhome.appcrawler.plugin.scalatest

import com.testerhome.appcrawler.data.AbstractElementStore
import com.testerhome.appcrawler.data.AbstractElementStore.Status
import com.testerhome.appcrawler._
import org.scalatest
import org.scalatest._

import scala.collection.JavaConversions._
import scala.reflect.io.File

/**
  * Created by seveniruby on 2017/3/25.
  */
class ScalaTestTemplate extends FunSuite with BeforeAndAfterAllConfigMap with Matchers with CommonLog {
  var name = "template"
  var uri = ""

  override def suiteName = name

  def addTestCase() {

    ReportFactory.getSelected(uri).foreach(ele => {
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
                if (
                  step.getGiven().forall(g=>XPathUtil.getNodeListByKey(g, ele.getReqDom).size>0)
                ) {
                  log.info(s"match testcase ${ele.getElement.getXpath}")

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
