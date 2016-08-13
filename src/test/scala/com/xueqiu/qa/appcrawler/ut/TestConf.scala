package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.{CommonLog, CrawlerConf}
import org.scalatest.{Matchers, FunSuite}

/**
  * Created by seveniruby on 16/8/11.
  */
class TestConf extends FunSuite with CommonLog with Matchers{



  test("save config"){
    val conf=new CrawlerConf
    conf.save("conf.json")
  }

  /*
    test("load config"){
      var conf=new com.xueqiu.qa.appcrawler.CrawlerConf
      conf.baseUrl="xxx"
      println(conf.baseUrl)
      conf=conf.loadByJson4s("conf.json").get
      println(conf.baseUrl)
    }
  */

  test("load config by jackson"){
    var conf=new CrawlerConf
    conf.baseUrl=List("xxx")
    println(conf.baseUrl)
    conf.save("conf.json")
    conf=conf.load("conf.json")
    println(conf.baseUrl)
    assert(conf.baseUrl==List("xxx"))
  }



  test("yaml save"){
    val conf=new CrawlerConf
    conf.screenshotTimeout=100
    val yaml=conf.toYaml()
    log.info(yaml)

    val conf2=new CrawlerConf
    conf2.loadYaml(yaml)
    conf2.screenshotTimeout should be equals(conf.screenshotTimeout)
    conf2.screenshotTimeout should be equals(100)

  }

}
