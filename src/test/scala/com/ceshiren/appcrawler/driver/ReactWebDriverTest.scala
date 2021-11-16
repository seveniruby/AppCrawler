package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.utils.LogicUtils.asyncTask
import org.scalatest.FunSuite

class ReactWebDriverTest extends FunSuite {

  test("testAsyncTask") {
    val client=new AppiumClient()
    asyncTask(10){
      println("xxxx")
      println("dddddd")
      Thread.sleep(1000)
    }

  }

}
