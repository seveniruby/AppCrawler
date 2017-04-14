package com.xueqiu.qa.appcrawler.it

import java.text.SimpleDateFormat
import java.util.Date

import com.sksamuel.elastic4s.ElasticDsl.indexInto
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri, Indexable}
import com.xueqiu.qa.appcrawler.{AppCrawler, DataObject}
import org.scalatest.FunSuite
import com.sksamuel.elastic4s.ElasticDsl._
import sys.process._


/**
  * Created by seveniruby on 2017/2/7.
  */
class TestMeiTuan extends FunSuite{

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


  def readHertz():Unit ={
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

  test("遍历美团app并把hertz数据发送到elk"){
    val date=new SimpleDateFormat("yyyy-MM-dd").format(new Date())
    val content=s"adb shell rm /mnt/sdcard/hertz/sample/Hertz_Render-${date}.txt".!!
    println(content)
    AppCrawler.main(Array("-c", "src/test/scala/com/xueqiu/qa/appcrawler/it/meituanwaimai_private.yml"))
    readHertz()
  }

}
