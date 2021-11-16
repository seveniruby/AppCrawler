package com.ceshiren.appcrawler.core

import com.ceshiren.appcrawler.AppCrawler
import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.TData
import org.scalatest
import org.scalatest.{BeforeAndAfterAllConfigMap, ConfigMap, FunSuite, Matchers}

/**
  * Created by seveniruby on 2017/4/17.
  */
class AutomationSuite extends FunSuite with Matchers with BeforeAndAfterAllConfigMap {
  var crawler: Crawler = _

  override def beforeAll(configMap: ConfigMap): Unit = {
    log.info("beforeAll")
    crawler = configMap.get("crawler").get.asInstanceOf[Crawler]
  }


  //todo: 利用suite排序进入延迟执行

  test("run steps") {
    log.info("testcase start")
    val conf = crawler.conf
    val driver = crawler.driver

    val cp = new scalatest.Checkpoints.Checkpoint

    conf.testcase.steps.foreach(step => {
      log.info(TData.toYaml(step))
      val xpath = step.getXPath()
      val action = step.getAction()

      driver.getNodeListByKey(xpath).headOption match {
        case Some(v) => {
          log.debug(v)
          val ele = new URIElement(v, "Steps")
          ele.setAction(action)
          log.debug(ele)
          // testcase里的操作也要记录下来
          crawler.beforeElementAction(ele)
          crawler.doElementAction(ele)
          crawler.afterElementAction(ele)
        }
        case None => {
          //用于生成steps的用例
          val ele = new URIElement("Steps", "", "", "", "NOT_FOUND", "", "", "", "", "", "xpath", "", "", 0, 0, 0, 0, "")

          ele.setAction("_Log")
          // testcase里的操作也要记录下来
          crawler.beforeElementAction(ele)
          crawler.doElementAction(ele)
          crawler.afterElementAction(ele)
          withClue("NOT_FOUND") {
            log.info(xpath)
            fail(s"ELEMENT_NOT_FOUND xpath=${xpath}")
          }
        }
      }


      if (step.then != null) {
        step.then.foreach(existAssert => {
          cp {
            withClue(s"${existAssert} 不存在\n") {
              val result = driver.getNodeListByKey(existAssert)
              log.info(s"${existAssert}\n${TData.toJson(result)}")
              result.size should be > 0
            }
          }
        })
      }
    })

    cp.reportAll()
    log.info("finish run steps")
  }
}
