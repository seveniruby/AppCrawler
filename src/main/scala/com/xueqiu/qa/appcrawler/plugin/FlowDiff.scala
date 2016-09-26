package com.xueqiu.qa.appcrawler.plugin

import com.xueqiu.qa.appcrawler.{DataObject, UrlElement, Plugin}

import scala.reflect.io.File

/**
  * Created by seveniruby on 16/9/25.
  */
class FlowDiff extends Plugin{
  override def start(): Unit ={

  }

  override def afterElementAction(element: UrlElement): Unit ={
    val store=getCrawler().store
    val head=store.clickedElementsList.last
    store.saveDom(getCrawler().currentPageSource)
    val elementMap=DataObject.flatten(DataObject.fromXML(getCrawler().currentPageSource)).toList
    elementMap.filter(_._1.matches(".*\\.text"))
  }

  override def beforeBack(): Unit ={
    report()
  }

  override def stop(): Unit ={
    report()
  }

  def report(): Unit ={

    val store=getCrawler().store
    val diffData=store.elementStore.filter(_._2.clickedIndex>0).map(clickedElement=>{
      clickedElement._1 -> DataObject.flatten(DataObject.fromXML(clickedElement._2.resDom))
    }).toMap
    File(getCrawler().conf.resultDir+"/diff.yml").writeAll(DataObject.toYaml(diffData))
  }
  def diff(master: String, candidate:String){


  }


}
object FlowDiff extends FlowDiff{
  var master=""
  var candidate=""
  var reportDir="."

  def generateTestCase(): Unit ={
    val code=
      """
        |import com.xueqiu.qa.appcrawler.DataObject
        |import com.xueqiu.qa.appcrawler.plugin.FlowDiff
        |import org.scalatest._
        |
        |/**
        |  * Created by seveniruby on 16/9/26.
        |  */
        |class TestDiffReport extends FunSuite with Matchers {
        |  val masterReport= DataObject.fromYaml[Map[String, Map[String, Any]]]( scala.io.Source.fromFile(FlowDiff.master).mkString)
        |  val candidateReport= DataObject.fromYaml[Map[String, Map[String, Any]]]( scala.io.Source.fromFile(FlowDiff.candidate).mkString)
        |
        |  println(masterReport.size)
        |  println(candidateReport.size)
        |  val keys=masterReport.keys.toList.intersect(candidateReport.keys.toList)
        |
        |  keys.foreach(key=>{
        |    test(s"${key}"){
        |      val masterMap=masterReport.getOrElse(key, Map[String, Any]())
        |      val candidateMap=candidateReport.getOrElse(key, Map[String, Any]())
        |      val subKeys=masterMap.keys++candidateMap.keys
        |      subKeys.foreach(subKey=>{
        |        val master=masterMap.getOrElse(subKey, null)
        |        val candidate=candidateMap.getOrElse(subKey, null)
        |        val message=(s"\n\nkey=${subKey}\nactual=${candidate} vs origin=${master}\n\n")
        |        println(message)
        |        candidate should be equals( master)
        |        assert(candidate==master, message)
        |      }
        |      )
        |    }
        |  })
        |}
        |
      """.stripMargin
    File(FlowDiff.reportDir+"/TestDiffReport.scala").writeAll(code)
  }

}
