package com.testerhome.appcrawler.ut

import com.testerhome.appcrawler.SuiteToClass
import org.scalatest.FunSuite

class SuiteToClassTest extends FunSuite {
  test("class name"){
    val name ="com.tencent.mobileqq-加好友-☞ Mr.never \"day \"心(571529295)"
    SuiteToClass.genTestCaseClass(name, "com.testerhome.appcrawler.DiffSuite", Map("suite"->name, "name"->name), "/tmp/class")
  }

}
