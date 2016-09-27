package com.xueqiu.qa.appcrawler.ut

import org.scalatest.Checkpoints.Checkpoint
import org.scalatest.{Matchers, FunSuite}

/**
  * Created by seveniruby on 16/9/27.
  */
class TestGetClassFile extends FunSuite with Matchers{


  test("get class file"){
    println(getClass.getClassLoader.getResources("com/xueqiu/qa/appcrawler/ut/TestDiffReport.class"))
    println(classOf[TestDiffReport].getClass.getProtectionDomain.getCodeSource.getLocation)

  }

  test("test checkpoints"){
    val cp = new Checkpoint()
    val (x, y) = (1, 2)
    cp { x should be < 0 }
    cp { y should be > 9 }
    cp.reportAll()
  }

}
