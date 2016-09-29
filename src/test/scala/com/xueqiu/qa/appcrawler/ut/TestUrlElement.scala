package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.UrlElement
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/9/29.
  */
class TestUrlElement extends FunSuite {
  test("windows file name"){
    val element=UrlElement("", "", "", "", "//xxfxx[@text=\"fff<xxx->>>dddff\"]")
    println(element.toFileName())
  }

}
