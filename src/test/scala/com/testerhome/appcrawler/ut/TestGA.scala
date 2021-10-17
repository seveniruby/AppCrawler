package com.ceshiren.appcrawler.ut

import com.brsanthu.googleanalytics.{GoogleAnalytics, PageViewHit}
import org.apache.log4j.{BasicConfigurator, Level, Logger}
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/2/27.
  */
class TestGA extends FunSuite{
  test("google analyse"){
    println("ga start")

    BasicConfigurator.configure()
    Logger.getRootLogger().setLevel(Level.WARN)
    val ga = new GoogleAnalytics("UA-74406102-1")
    1 to 10 foreach(x=>{
      ga.postAsync(new PageViewHit(s"http://appcrawler.io/demo${x}", "test"))
    })
    Thread.sleep(10000)

    1 to 10 foreach(x=>{
      ga.postAsync(new PageViewHit(s"http://appcrawler.io/dem1${x}", "test"))
    })

    Thread.sleep(10000)
    1 to 10 foreach(x=>{
      ga.postAsync(new PageViewHit(s"http://appcrawler.io/dem2${x}", "test"))
    })
    //ga.post(new PageViewHit("http://appcrawler.io/test2", "test"))
    println("ga end")

  }

}
