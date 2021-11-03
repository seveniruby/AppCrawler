package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.core.CrawlerConf
import com.ceshiren.appcrawler.utils.Log.log
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by seveniruby on 16/8/11.
  */
class TestConf extends FunSuite  with Matchers{



  test("save config"){
    val conf=new CrawlerConf
    conf.save("conf.json")
  }

  /*
    test("load config"){
      var conf=new com.ceshiren.appcrawler.core.CrawlerConf
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
    conf.beforeStartWait=100
    val yaml=conf.toYaml()
    log.info(yaml)

    val conf2=new CrawlerConf
    conf2.loadYaml(yaml)
    conf2.beforeStartWait should be equals(conf.beforeStartWait)
    conf2.beforeStartWait should be equals(100)

  }

  test("one line yaml"){
    val conf=new CrawlerConf().loadYaml("blackList: [ {xpath: action_night}, {xpath: action_setting} ]")
    println(conf.selectedList)
    println(conf.blackList)
  }

}
