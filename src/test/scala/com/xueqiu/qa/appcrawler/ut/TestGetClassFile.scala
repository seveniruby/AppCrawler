package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.{Report, DiffSuite}
import com.xueqiu.qa.appcrawler.plugin.FlowDiff
import org.apache.commons.io.FileUtils
import org.scalatest.Checkpoints.Checkpoint
import org.scalatest.{Matchers, FunSuite}

/**
  * Created by seveniruby on 16/9/27.
  */
class TestGetClassFile extends FunSuite with Matchers{



  test("test checkpoints"){
    markup {
      """
        |dddddddd
      """.stripMargin
    }
    markup("xxxx")
    val cp = new Checkpoint()
    val (x, y) = (1, 2)
    cp { x should be < 0 }
    cp { y should be > 9 }
    cp.reportAll()
  }

  test("test markup"){
    markup {
      """
        |dddddddd
      """.stripMargin
    }
    markup("xxxx")

  }

  test("get class file"){
    val location=classOf[DiffSuite].getProtectionDomain.getCodeSource.getLocation
    println(location)
    val f=getClass.getResource("/com/xueqiu/qa/appcrawler/ut/TestDiffReport.class").getFile
    println(f)
    FileUtils.copyFile(new java.io.File(f), new java.io.File("/tmp/1.class"))



    println(getClass.getClassLoader.getResources("com/xueqiu/qa/appcrawler/ut/TestDiffReport.class"))
  }
}
