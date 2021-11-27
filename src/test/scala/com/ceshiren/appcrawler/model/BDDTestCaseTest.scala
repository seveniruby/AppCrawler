package com.ceshiren.appcrawler.model

import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.TData
import org.junit.jupiter.api.Test

import scala.collection.mutable


class BDDTestCaseTest {

  @Test
  def runStep() {
    val step1 = mutable.HashMap[String, Any]()
    step1.put("find", null)

    val step2 = mutable.HashMap[String, Any]()
    step2.put("find", "11")
    val step3 = mutable.HashMap[String, Any]()
    step3.put("find", step2)


    val testcase1 = BDDTestCase(when = List(step1.toMap, step2.toMap, step3.toMap))
    val str = TData.toYaml(testcase1)
    log.info(str)

    val testcase2 = TData.fromYaml[BDDTestCase](str)
    log.info(testcase2)

  }

  @Test
  def mockDriver(){

    val yamlStr =
      s"""
        |when:
        |- driver: mock
        |- chrome:
        |- get: https://ceshiren.com
        |- click: { id: search-button }
        |- find: { id: search-term }
        |- sendKeys: appium demo
        |- find: { id: search-term }
        |- shell:
        |    format:
        |      - "echo {} {}"
        |      -
        |       - attribute: text
        |       -  end
        |
        |""".stripMargin

    val yamlObject = TData.fromYaml[BDDTestCase](yamlStr)
    log.info(yamlObject)
    yamlObject.run()

  }

  @Test
  def seleniumDriver(){

    val yamlStr =
      s"""
         |when:
         |- driver: selenium
         |- chrome:
         |- get: https://ceshiren.com
         |- click: { id: search-button }
         |- find: { id: search-term }
         |- sendKeys: appium demo
         |- find: { id: search-term }
         |- shell:
         |    concat:
         |      - echo
         |      - attribute: text
         |      -  end
         |
         |""".stripMargin

    val yamlObject = TData.fromYaml[BDDTestCase](yamlStr)
    log.info(yamlObject)
    yamlObject.run()

  }


  @Test
  def dynamic(): Unit = {
    log.info(this)
    log.info(this.getClass)
    this.getClass.getDeclaredMethods.foreach(method => {
      method.getParameterTypes.foreach(p => {
        log.info(p)
      })
    })
  }

}
