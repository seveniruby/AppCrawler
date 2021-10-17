package com.ceshiren.appcrawler.driver

import org.scalatest.FunSuite

class ReactWebDriverTest extends FunSuite {

  test("testAsyncTask") {
    val client=new AppiumClient()
    client.asyncTask(10){
      println("xxxx")
      println("dddddd")
      Thread.sleep(1000)
    }

  }

}
