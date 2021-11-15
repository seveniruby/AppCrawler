package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.Log.log
import org.junit.jupiter.api.Test

class MockDriverTest {
  @Test
  def waitTest(): Unit ={
    val mockDriver=new MockDriver()
    log.info("find 测吧")
    mockDriver.wait("测吧")
    log.info("find xxx")
    mockDriver.wait("xxx")
    log.info("finish")

  }

}
