package com.testerhome.appcrawler.ut

import java.text.SimpleDateFormat
import java.util.Date

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri, Indexable}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.xpack.security.XPackElasticClient
import org.elasticsearch.common.settings.Settings
import com.sksamuel.elastic4s.analyzers.StopAnalyzer
import com.sksamuel.elastic4s.mappings.FieldType._
import com.testerhome.appcrawler.DataObject

/**
  * Created by seveniruby on 2017/1/22.
  */
class TestELK extends FunSuite with BeforeAndAfterAll {

  case class Hertz(version: String, url: String, t: Double, t1: Double, t2: Double, t3: Double,
                   date: String = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()))
  // how you turn the type into json is up to you
  implicit object CharacterIndexable extends Indexable[Hertz] {
    override def json(t: Hertz): String = {
      DataObject.toJson(t)
    }
  }

  val client = createESClient()

  def createESClient(): ElasticClient ={
    val host = "10.5.233.59"
    val port = 9300
    /*
    val settings = Settings.builder()
      .put("cluster.name", "elasticsearch").put("xpack.security.user", "elastic:changeme").build()
    XPackElasticClient(settings, ElasticsearchClientUri(host, port))
      */

    ElasticClient.transport(ElasticsearchClientUri(host, port))

  }


  override def beforeAll(): Unit = {

  }

  test("write elk") {

    client.execute {
      createIndex("demo") mappings (
        mapping("cities") as(
          keywordField("id"),
          textField("name"),
          textField("content") analyzer StopAnalyzer
        )
        )
    }

    client.execute {
      indexInto("demo" / "cities") id "uk" fields(
        "name" -> "London",
        "country" -> "United Kingdom",
        "continent" -> "Europe",
        "status" -> "Awesome"
      )
    }

    val cities = client.execute {
      search("demo" / "cities")
    }.await

    println(cities)
  }

  test("create index"){
    client.execute {
      createIndex("logstash-demo") mappings (
        mapping("characters") as (
          keywordField("version"),
          textField("url") ,
          doubleField("t"),
          doubleField("t1"),
          doubleField("t2"),
          doubleField("t3"),
          dateField("date")
        )
        )
    }
  }
  test("doc object") {
    // a simple example of a domain model



    // now the index request reads much cleaner
    val date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date())
    val jonsnow = Hertz("5.4", "Main", 5, 1.5, 2.1, 3.4, date)

    sendToES(jonsnow)

    val strs = client.execute {
      search("logstash-demo" / "characters")
    }.await
    println(strs)

  }

  test("read hertz"){
    import sys.process._
    val date=new SimpleDateFormat("yyyy-MM-dd").format(new Date())
    val content=s"adb shell cat  /mnt/sdcard/hertz/sample/Hertz_Render-${date}.txt".!!
    println(content)
    content.split("\n").foreach(line=>{
      if(line.contains("is_reboot")){

      }
      if(line.contains("page_name")){
        val data=DataObject.fromJson[Map[String, Any]](line)
        println(data("page_name"))
        println(data("T3"))
        val hertz=Hertz(
          version = "5.4",
          url = data("page_name").toString,
          t = data("T").toString.toDouble,
          t1 = data("T1").toString.toDouble,
          t2 = data("T2").toString.toDouble,
          t3 = data("T3").toString.toDouble
        )
        sendToES(hertz)
      }
    })


  }

  def sendToES(hertz:Hertz): Unit ={
    client.execute {
      indexInto("logstash-demo" / "characters").doc(hertz)
    }.await

  }

}
