package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.CommonLog
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/8/12.
  */
class TestStringTemplate extends FunSuite with CommonLog{
  test("string template"){
    val s=
      s"""
        |class A extends B {
        |  test("ddddd"){
        |    ${1 to 5 map (_.toString) mkString ("\n"+" "*4)}
        |  }
        |}
      """.stripMargin
    log.info(s)
  }

  test("string template from file"){
    //todo:
  }

}
