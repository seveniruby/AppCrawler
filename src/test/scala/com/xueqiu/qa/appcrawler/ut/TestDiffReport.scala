package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.{UrlElementStore, DataObject}
import com.xueqiu.qa.appcrawler.plugin.FlowDiff
import org.scalatest._

import scala.io.Source

/**
  * Created by seveniruby on 16/9/26.
  */

class TestDiffReport extends FunSuite with Matchers {
  //只取列表的第一项
  val blackList = List(".*\\.instance.*")
  if (FlowDiff.master.isEmpty) {
    FlowDiff.master = "/Volumes/RamDisk/xueqiu_1"
  }
  if (FlowDiff.candidate.isEmpty) {
    FlowDiff.candidate = "/Volumes/RamDisk/xueqiu_2"
  }
  val masterStore = DataObject.fromYaml[UrlElementStore](Source.fromFile(s"${FlowDiff.master}/elements.yml").mkString)
  val candidateStore = DataObject.fromYaml[UrlElementStore](Source.fromFile(s"${FlowDiff.candidate}/elements.yml").mkString)
  val masterReport = masterStore.elementStore.filter(_._2.clickedIndex > 0).map(clickedElement => {
    s"clicked=${clickedElement._2.clickedIndex} xpath=${clickedElement._2.element.loc}" -> DataObject.flatten(DataObject.fromXML(clickedElement._2.resDom))
  }).toMap
  val candidateReport = candidateStore.elementStore.filter(_._2.clickedIndex > 0).map(clickedElement => {
    s"clicked=${clickedElement._2.clickedIndex} xpath=${clickedElement._2.element.loc}" -> DataObject.flatten(DataObject.fromXML(clickedElement._2.resDom))
  }).toMap

  println(masterReport.size)
  println(candidateReport.size)
  val intersectkeys = masterReport.keys.toList.intersect(candidateReport.keys.toList)
  println(intersectkeys.size)
  addTestCase("intersect", intersectkeys)

  val masterkeys = masterReport.keys.toList.diff(candidateReport.keys.toList)
  println(masterkeys.size)
  addTestCase("master", masterkeys)

  val candidatekeys = candidateReport.keys.toList.diff(masterReport.keys.toList)
  println(candidatekeys.size)
  addTestCase("candidate", candidatekeys)

  def addTestCase(name: String, keys: List[String]): Unit = {
    keys.foreach(key => {
      val testcase = s"${name} ${key}"
      println(s"add testcase ${testcase}")
      test(testcase) {
        val masterMap = masterReport.getOrElse(key, Map[String, Any]())
        val candidateMap = candidateReport.getOrElse(key, Map[String, Any]())
        val subKeys = (masterMap.keys ++ candidateMap.keys).filter(key => blackList.exists(b => key.matches(b)) == false)


        val cp = new Checkpoints.Checkpoint()

        subKeys.toList.reverse.foreach(subKey => {
          val master = masterMap.getOrElse(subKey, "master不存在")
          val candidate = candidateMap.getOrElse(subKey, "candidate不存在")
          val message = (s"\n\nkey=${subKey}\ncandidate=${candidate} vs master=${master}\n\n")
          println(message)
          println(masterMap.get(subKey))
          println(candidateMap.get(subKey))
          cp {
            if (masterMap.contains(subKey) && candidateMap.contains(subKey)) {
              withClue(message) {
                candidate should equal(master)
                //assert(candidate == master, message)
              }
            }
          }
        })
        cp.reportAll()
      }
    })

  }
}
