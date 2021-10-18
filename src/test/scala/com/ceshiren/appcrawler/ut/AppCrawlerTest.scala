package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.AppCrawler

import java.io.File
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 2017/5/25.
  */
class AppCrawlerTest extends FunSuite{
  test("parse test"){

    var uri="http://xxxx.com/aa.apk"
    var res=AppCrawler.parsePath(uri)
    println(res)

    uri="http:\\xxxx.com/aa.apk"
    res=AppCrawler.parsePath(uri)
    println(res)


    uri="/Users/seveniruby/Downloads/base.apk"
    res=AppCrawler.parsePath(uri)
    println(res)


    uri="./project/build.properties"
    res=AppCrawler.parsePath(uri)
    println(res)


  }

}
