package com.ceshiren.appcrawler.utils

import org.scalatest.FunSuite

import scala.collection.mutable.ListBuffer

class LogicUtilsTest extends FunSuite {

  test("testAsyncTask") {
    val s = ListBuffer[Integer](1, 2, 3)
    LogicUtils.asyncTask(timeout = 5) {
      do{
        Thread.sleep(1000)
        Log.log.info("wait")
        s.append(1)
      }while(s.size<10)
    }

  }

}
