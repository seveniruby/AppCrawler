package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.{UrlElement, CrawlerConf, CommonLog, DataObject}
import org.scalatest.FunSuite

import scala.collection.mutable

/**
  * Created by seveniruby on 16/8/13.
  */
class TestDataObject extends FunSuite with CommonLog{
  test("save to yaml file"){
    val a="中国"
    val yaml=DataObject.toYaml(a)
    val aa=DataObject.fromYaml[String](yaml)
    log.info(aa)
  }

  test("conf yaml "){
    val conf=new CrawlerConf()
    conf.backButton.append("//北京")
    val yaml=DataObject.toYaml(conf)
    log.info(yaml)

    val confNew=DataObject.fromYaml[CrawlerConf](yaml)
    assert(confNew.backButton.last=="//北京")
  }

  test("read clickedElementsList"){
    val yaml=scala.io.Source.fromFile("iOS_20160813165030/clickedList.yml").getLines().mkString("\n")
    val elementList=DataObject.fromYaml[List[UrlElement]](yaml)
    log.info(elementList.head)
    log.info(elementList.last)

  }
}
