package com.testerhome.appcrawler

import com.testerhome.appcrawler.hbh.NewElementInfo
import com.testerhome.appcrawler.hbh.NewURIElementStore.Status
import com.testerhome.appcrawler.plugin.FlowDiff
import org.scalatest._

import scala.io.Source
import scala.reflect.io.File
import collection.JavaConversions._

/**
  * Created by seveniruby on 16/9/26.
  */

class DiffSuite extends FunSuite with Matchers with CommonLog{
  //只取列表的第一项
  var name="新老版本对比"
  var suite="Diff"

  override def suiteName=name

  def addTestCase(): Unit = {
    //每个点击事件

    val allKeys = DiffSuite.masterStore.filter(_._2.getUriElement.url==suite).keys ++
      DiffSuite.candidateStore.filter(_._2.getUriElement.url==suite).keys
    log.debug(allKeys.size)

    allKeys.foreach(key => {
      val masterElements=DiffSuite.masterStore.get(key) match {
        case Some(elementInfo) if elementInfo.getAction==Status.CLICKED && elementInfo.getResDom.nonEmpty => {
          log.debug(elementInfo)
          log.debug(elementInfo.getResDom)
          DiffSuite.range.map(XPathUtil.getNodeListByXPath(_, elementInfo.getResDom))
            .flatten.map(m=>{
            val ele=new URIElement(m, key)
            ele.xpath->ele
          }).toMap
        }
        case _ =>{
          Map[String, URIElement]()
        }
      }

      val candidateElements=DiffSuite.candidateStore.get(key) match {
          //todo: 老版本点击过, 新版本没点击过, 没有resDom如何做
        case Some(elementInfo) if elementInfo.getAction==Status.CLICKED && elementInfo.getResDom.nonEmpty => {
          DiffSuite.range.map(XPathUtil.getNodeListByXPath(_, elementInfo.getResDom))
            .flatten.map(m=>{
            val ele=new URIElement(m, key)
            ele.xpath->ele
          }).toMap
        }
        case _ =>{
          Map[String, URIElement]()
        }
      }


      val testcase = s"url=${key}"

      //todo: 支持结构对比, 数据对比 yaml配置
      test(testcase) {


        val allElementKeys=masterElements.keys++candidateElements.keys
        //todo: 去掉黑名单里面的字段
        val cp = new Checkpoints.Checkpoint()
        var markOnce=false
        allElementKeys.foreach(subKey => {
          val masterElement = masterElements.getOrElse(subKey, URIElement())
          val candidateElement = candidateElements.getOrElse(subKey, URIElement())
          val message =
            s"""
               |key=${subKey}
               |
               |candidate=${candidateElement.xpath}
               |
               |master=${masterElement.xpath}
               |________________
               |
               |
             """.stripMargin

          if (masterElement != candidateElement && !markOnce) {
            markOnce=true
            markup(
              s"""
                 |candidate image
                 |-------
                 |<img src='${File(DiffSuite.candidateStore.getOrElse(key, new NewElementInfo()).getResImg).name}' width='80%' />
                 |
                 |master image
                 |--------
                 |<img src='${File(DiffSuite.masterStore.getOrElse(key, new NewElementInfo()).getResImg).name}' width='80%' />
                 |
                """.stripMargin)
          }

          cp {
            withClue(message) {
              candidateElement.id should equal(masterElement.id)
              candidateElement.name should equal(masterElement.name)
              candidateElement.xpath should equal(masterElement.xpath)
              //assert(candidate == master, message)
            }
          }
        })
        cp.reportAll()

      }
    })

  }
}

object DiffSuite {
  val masterStore : scala.collection.mutable.Map[String, NewElementInfo] = Report.loadResult(s"${Report.master}/elements.yml").getNewElementStore
  val candidateStore : scala.collection.mutable.Map[String, NewElementInfo] = Report.loadResult(s"${Report.candidate}/elements.yml").getNewElementStore
  val blackList = List(".*\\.instance.*", ".*bounds.*")
  val range=List("//*[contains(name(), 'Text')]", "//*[contains(name(), 'Image')]", "//*[contains(name(), 'Button')]")
  def saveTestCase(): Unit ={
    val suites=masterStore.map(_._2.getUriElement.url)++candidateStore.map(_._2.getUriElement.url)
    suites.foreach(suite=> {
      SuiteToClass.genTestCaseClass(suite, "com.testerhome.appcrawler.DiffSuite", Map("suite"->suite, "name"->suite), Report.testcaseDir)
    })
  }
}
