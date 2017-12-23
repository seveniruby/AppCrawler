package com.testerhome.appcrawler

import org.scalatest
import org.scalatest.{BeforeAndAfterAllConfigMap, ConfigMap, FunSuite, Matchers}

/**
  * Created by seveniruby on 2017/4/17.
  */
class AutomationSuite extends FunSuite with Matchers with BeforeAndAfterAllConfigMap with CommonLog {
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
      log.info(step)
      val xpath=step.getXPath()
      val action=step.getAction()
      log.info(xpath)
      log.info(action)

      driver.findMapWithRetry(xpath).headOption match {
        case Some(v) => {
          val ele = URIElement(v, "Steps")
          crawler.doElementAction(ele, action)
        }
        case None => {
          //用于生成steps的用例
          val ele = URIElement("Steps", "", "", "NOT_FOUND", xpath)
          crawler.doElementAction(ele, "")
          withClue("NOT_FOUND"){
            log.info(xpath)
            fail(s"ELEMENT_NOT_FOUND xpath=${xpath}")
          }
        }
      }



      if(step.then!=null) {
        step.then.foreach(existAssert => {
          cp {
            withClue(s"${existAssert} 不存在\n") {
              val result=driver.findMapByKey(existAssert)
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
