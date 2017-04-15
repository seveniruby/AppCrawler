package com.xueqiu.qa.appcrawler

import org.apache.commons.lang3.StringUtils
import org.scalatest
import org.scalatest._

import scala.reflect.io.File


/**
  * Created by seveniruby on 2017/3/25.
  */
class TemplateTestCase extends FunSuite with BeforeAndAfterAllConfigMap with Matchers {
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
          val req = RichData.
            toDocument(ele.reqDom)
          val res = RichData.toDocument(ele.resDom)
          AppCrawler.crawler.conf.asserts.foreach(assert => {
            val given
            = assert.getOrElse("given", Array[String]()).asInstanceOf[Array[String]]
            if (given.forall(g => RichData.getListFromXPath(g, req).size > 0) == true) {
              val existAsserts = assert.getOrElse("then", Array[String]()).
                asInstanceOf[Array[String]]
              val cp
              = new
                  scalatest.Checkpoints.
                  Checkpoint
              existAsserts.foreach(existAssert => {
                cp {
                  withClue(existAssert
                  ) {
                    RichData.
                      getListFromXPath(existAssert, res).size should be > 0
                  }
                }
              })
              cp.reportAll()
            }
          })
        }
      }
    })
  }
}
