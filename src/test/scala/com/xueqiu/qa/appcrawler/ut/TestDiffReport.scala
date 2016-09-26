package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.DataObject
import com.xueqiu.qa.appcrawler.plugin.FlowDiff
import org.scalatest._

/**
  * Created by seveniruby on 16/9/26.
  */
@WrapWith(classOf[ConfigMapWrapperSuite])
class TestDiffReport extends FunSuite with Matchers {
  val masterReport= DataObject.fromYaml[Map[String, Map[String, Any]]]( scala.io.Source.fromFile(FlowDiff.master).mkString)
  val candidateReport= DataObject.fromYaml[Map[String, Map[String, Any]]]( scala.io.Source.fromFile(FlowDiff.candidate).mkString)

  println(masterReport.size)
  println(candidateReport.size)
  val keys=masterReport.keys.toList.intersect(candidateReport.keys.toList)

  keys.foreach(key=>{
    test(s"${key}"){
      val masterMap=masterReport.getOrElse(key, Map[String, Any]())
      val candidateMap=candidateReport.getOrElse(key, Map[String, Any]())
      val subKeys=masterMap.keys++candidateMap.keys
      subKeys.foreach(subKey=>{
        val master=masterMap.getOrElse(subKey, null)
        val candidate=candidateMap.getOrElse(subKey, null)
        val message=(s"\n\nkey=${subKey}\nactual=${candidate} vs origin=${master}\n\n")
        println(message)
        candidate should be equals( master)
        assert(candidate==master, message)
      }
      )
    }
  })
}
