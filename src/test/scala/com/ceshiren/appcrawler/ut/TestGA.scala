package com.ceshiren.appcrawler.ut

import com.brsanthu.googleanalytics.GoogleAnalytics
import com.ceshiren.appcrawler.utils.GA
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/2/27.
  */
class TestGA extends FunSuite{
  test("google analyse"){
    GA.log("unittest")

  }

}
