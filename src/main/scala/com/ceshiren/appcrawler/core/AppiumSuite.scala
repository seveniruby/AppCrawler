package com.ceshiren.appcrawler.core

import com.ceshiren.appcrawler.utils.CommonLog
import org.scalatest._
import org.scalatest.selenium.WebBrowser
/**
  * Created by seveniruby on 16/3/26.
  */
class AppiumSuite extends FunSuite
  with Matchers
  with WebBrowser
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with CommonLog {

}
