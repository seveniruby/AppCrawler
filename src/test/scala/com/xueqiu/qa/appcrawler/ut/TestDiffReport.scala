package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.DataObject
import org.scalatest.{Matchers, FunSuite}

/**
  * Created by seveniruby on 16/9/26.
  */
class TestDiffReport extends FunSuite with Matchers {
  val oldReport= DataObject.fromYaml[Map[String, Map[String, Any]]]( scala.io.Source.fromFile("/Volumes/RamDisk/xueqiu_7/diff.yml").mkString)
  val newReport= DataObject.fromYaml[Map[String, Map[String, Any]]]( scala.io.Source.fromFile("/Volumes/RamDisk/xueqiu_8/diff.yml").mkString)

  val keys=oldReport.keys.toList.intersect(newReport.keys.toList)

  keys.foreach(key=>{
    test(s"${key}"){
      val oldMap=oldReport.getOrElse(key, Map[String, Any]())
      val newMap=newReport.getOrElse(key, Map[String, Any]())
      val subKeys=oldMap.keys++newMap.keys
      subKeys.foreach(subKey=>{
        val origin=oldMap.getOrElse(subKey, null)
        val actual=newMap.getOrElse(subKey, null)
        val message=(s"\n\nkey=${subKey}\nactual=${actual} vs origin=${origin}")
        println(message)
        actual should be equals( origin)
        assert(actual==origin, message)
      }
      )
    }
  })

}
