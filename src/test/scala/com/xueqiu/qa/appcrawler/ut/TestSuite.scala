package com.xueqiu.qa.appcrawler.ut

import com.twitter.util.Eval
import org.scalatest.{FunSuite, Tag}

/**
  * Created by seveniruby on 16/8/10.
  */
class TestSuite extends FunSuite{
  test("create new suite"){
    //val suite=Eval[FunSuite]("class A extends org.scalatest.FunSuite;new A()")
    val suite=new CrawlerSuite
    suite.registerTest("demo2"){
      println("demo2")
    }
    suite.name="NewDemo"
    suite.execute

    org.scalatest.run(suite)
    org.scalatest.tools.Runner.main(Array("-o", "-u", "target/test-reports", "-h", "target/test-reports"))
  }
}

class CrawlerSuite extends FunSuite{
  var name="demo"
  override def suiteName=name
}
