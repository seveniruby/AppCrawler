package com.xueqiu.qa.appcrawler

import org.scalatest.{BeforeAndAfterAllConfigMap, ConfigMap, FunSuite}

/**
  * Created by seveniruby on 16/1/7.
  */
class AppCrawlerTestCase extends FunSuite with BeforeAndAfterAllConfigMap with CommonLog{
  var cm=ConfigMap()
  override def beforeAll(cm: ConfigMap): Unit ={
    this.cm=cm
  }
  test("App com.xueqiu.qa.appcrawler.Crawler"){
    val conf=this.cm.get("conf").get.asInstanceOf[CrawlerConf]
    var crawler:Crawler=new Crawler

    conf.currentDriver.toLowerCase match {
      case "android"=>{
        crawler=new AndroidCrawler
      }
      case "ios" => {
        crawler=new IOSCrawler
      }
      case _ =>{
        log.trace("请指定currentDriver为Android或者iOS")
      }
    }
    crawler.loadConf(conf)
    crawler.start()
  }
}
