package com.xueqiu.qa.appcrawler.ut

import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/9/27.
  */
class TestGetClassFile extends FunSuite{


  test("get class file"){
    println(getClass.getClassLoader.getResources("com/xueqiu/qa/appcrawler/ut/TestDiffReport.class"))
    println(classOf[TestDiffReport].getClass.getProtectionDomain.getCodeSource.getLocation)

  }

}
