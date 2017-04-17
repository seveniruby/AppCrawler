package com.xueqiu.qa.appcrawler

import org.scalatest
import org.scalatest.{BeforeAndAfterAllConfigMap, ConfigMap, FunSuite, Matchers}

/**
  * Created by seveniruby on 2017/4/17.
  */
class AutomationTestCase extends FunSuite with Matchers with BeforeAndAfterAllConfigMap with CommonLog {
  var crawler :Crawler=_
  override def beforeAll(configMap: ConfigMap): Unit = {
    log.info("beforeAll")
    crawler=configMap.get("crawler").get.asInstanceOf[Crawler]
  }
  test("run steps"){
    log.info("testcase start")
    val conf=crawler.conf
    val driver=crawler.driver

    val cp = new scalatest.Checkpoints.Checkpoint

    conf.steps.foreach(step=> {
      val when=step.getOrElse("when", Map[String, Any]()).asInstanceOf[Map[String, Any]]
      val xpath=when.getOrElse("xpath", "").toString
      val action=when.getOrElse("action", "").toString

      RichData.getListFromXPath(xpath, driver.currentPageDom).headOption match {
        case Some(v)=> {
          crawler.doElementAction(UrlElement(v, crawler.currentUrl), action)
          crawler.refreshPage()
        }
        case None=>{log.info("not found")}
      }

      val then=step.getOrElse("then", List[String]()).asInstanceOf[List[String]]
      then.foreach(existAssert=>{
        log.debug(existAssert)
        cp {
          withClue(s"${existAssert} 不存在\n") {
            RichData.getListFromXPath(existAssert, driver.currentPageDom).size should be > 0
          }
        }

      })
    })

    cp.reportAll()
    log.info("finish run steps")
  }
}
