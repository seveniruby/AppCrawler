package com.xueqiu.qa.appcrawler.ut

import java.io

import com.xueqiu.qa.appcrawler.{UrlElement, CrawlerConf, CommonLog, DataObject}
import org.scalatest.FunSuite

import scala.collection.mutable
import scala.io.Source
import scala.reflect.io.File

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

  test("json to yaml"){
    val conf=new CrawlerConf().load("src/test/scala/com/xueqiu/qa/appcrawler/it/xueqiu_private.json")
    log.info(conf)
    val yaml=DataObject.toYaml(conf)
    File("src/test/scala/com/xueqiu/qa/appcrawler/it/xueqiu_private.yml").writeAll(yaml)


  }

  test("convert json to yaml"){
    val file="src/universal/conf/xueqiu.json"
    val conf=new CrawlerConf().load(file)
    log.info(conf)
    val yaml=DataObject.toYaml(conf)
    File("src/universal/conf/xueqiu.yml").writeAll(yaml)
  }

  test("read json"){
    val conf=DataObject.fromJson[CrawlerConf](Source.fromFile("src/test/scala/com/xueqiu/qa/appcrawler/it/xueqiu_private.json").getLines().mkString("\n"))
    log.info(conf.saveScreen)
  }
}
