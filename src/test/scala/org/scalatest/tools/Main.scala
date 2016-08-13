package org.scalatest.tools

import com.xueqiu.qa.appcrawler.{CrawlerSuite, CommonLog}
import org.scalatest._

/**
  * Created by seveniruby on 16/8/12.
  */
object Main extends App with CommonLog{
  println("hello")

  val suite=new CrawlerSuite
  suite.registerTest("demo2"){
    println("demo2")
    assert(1==1)
  }
  suite.registerTest("demo3"){
    println("demo3")
    assert(1==2)
  }
  val htmlReporter=new HtmlReporter("target/reports/", false, None, None)
  val junitXml=new JUnitXmlReporter("target/reports/")
  val reports=new DispatchReporter(List(htmlReporter, junitXml))
  suite.run(testName = None, reporter = reports,
    stopper=Stopper.default, tracker = Tracker.default, filter = Filter.default,
    distributor = None, configMap = Map())

  suite.run(testName = None, reporter = reports,
    stopper=Stopper.default, tracker = Tracker.default, filter = Filter.default,
    distributor = None, configMap = Map())


  //Runner.run(Array("-R", "target", "-s", "com.xueqiu.qa.appcrawler.ut.DemoCrawlerSuite", "-o", "-u", "target/test-reports", "-h", "target/test-reports"))

}
