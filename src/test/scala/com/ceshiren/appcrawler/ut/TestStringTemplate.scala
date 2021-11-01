package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.utils.CommonLog
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/8/12.
  */
class TestStringTemplate extends FunSuite with CommonLog{

  def genNumber(): String ={
    1 to 5 map (_.toString) mkString ("\n"+" "*4)
  }
  test("string template"){
    val s=
      s"""
        |class A extends B {
        |  test("ddddd"){
        |    ${genNumber()}
        |  }
        |}
      """.stripMargin
    log.info(s)
  }

  test("string template from file"){
    //todo:
  }

}
