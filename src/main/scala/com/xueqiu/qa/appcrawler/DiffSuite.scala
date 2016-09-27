package com.xueqiu.qa.appcrawler

import com.xueqiu.qa.appcrawler.plugin.FlowDiff
import org.scalatest._

import scala.io.Source

/**
  * Created by seveniruby on 16/9/26.
  */

class DiffSuite extends FunSuite with Matchers {
  //只取列表的第一项
  val blackList = List(".*\\.instance.*")
  if (Report.master.isEmpty) {
    Report.master = "/Volumes/RamDisk/xueqiu_1"
  }
  if (Report.candidate.isEmpty) {
    Report.candidate = "/Volumes/RamDisk/xueqiu_2"
  }
  val masterStore = DataObject.fromYaml[UrlElementStore](Source.fromFile(s"${Report.master}/elements.yml").mkString).elementStore
  val candidateStore = DataObject.fromYaml[UrlElementStore](Source.fromFile(s"${Report.candidate}/elements.yml").mkString).elementStore

  val intersectkeys = masterStore.keys.toList.intersect(candidateStore.keys.toList)
  println(intersectkeys.size)
  addTestCase("intersect", intersectkeys)

  override def suiteName="Intersect"
  def addTestCase(name: String, keys: List[String]): Unit = {
    keys.foreach(key => {

      val masterReport = DataObject.flatten(DataObject.fromXML(masterStore(key).resDom))
      val candidateReport = DataObject.flatten(DataObject.fromXML(candidateStore(key).resDom))
      val testcase = s"${name} clicked=${masterStore(key).clickedIndex} xpath=${candidateStore(key).element.loc}"

      test(testcase) {
        val subKeys = (masterReport.keys ++ candidateReport.keys).filter(key => blackList.exists(b => key.matches(b)) == false)
        val cp = new Checkpoints.Checkpoint()
        var markOnce=false
        subKeys.toList.reverse.foreach(subKey => {
          val master = masterReport.getOrElse(subKey, "master不存在")
          val candidate = candidateReport.getOrElse(subKey, "candidate不存在")
          val message = (s"\n\nkey=${subKey}\ncandidate=${candidate} vs master=${master}\n\n")

          if (masterReport.contains(subKey) && candidateReport.contains(subKey)) {
            if (master != candidate && !markOnce) {
              markOnce=true
              markup(
                s"""
                  |candidate image
                  |-------
                  |<img src='${candidateStore(key).resImg}' width='400' />
                  |
                  |master image
                  |--------
                  |<img src='${masterStore(key).resImg}' width='400' />
                  |
                """.stripMargin)
            }

            cp {
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
