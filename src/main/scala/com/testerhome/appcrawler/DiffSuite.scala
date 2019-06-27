package com.testerhome.appcrawler

import com.testerhome.appcrawler.data.AbstractElementStore.Status
import com.testerhome.appcrawler.data.{AbstractElement, AbstractElementInfo}
import com.testerhome.appcrawler.plugin.scalatest.SuiteToClass
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

    val allKeys = DiffSuite.masterStore.filter(_._2.getElement.getUrl==suite).keys ++
      DiffSuite.candidateStore.filter(_._2.getElement.getUrl==suite).keys
    log.debug(allKeys.size)

    allKeys.foreach(key => {
      val masterElements=DiffSuite.masterStore.get(key) match {
        case Some(elementInfo) if elementInfo.getAction==Status.CLICKED && elementInfo.getResDom.nonEmpty => {
          log.debug(elementInfo)
          log.debug(elementInfo.getResDom)
          DiffSuite.range.map(XPathUtil.getNodeListByXPath(_, elementInfo.getResDom))
            .flatten.map(m=>{
            log.info(AppCrawler.factory)
            val ele=AppCrawler.factory.generateElement(m, key)
            ele.getXpath->ele
          }).toMap
        }
        case _ =>{
          Map[String, AbstractElement]()
        }
      }

      val candidateElements=DiffSuite.candidateStore.get(key) match {
          //todo: 老版本点击过, 新版本没点击过, 没有resDom如何做
        case Some(elementInfo) if elementInfo.getAction==Status.CLICKED && elementInfo.getResDom.nonEmpty => {
          DiffSuite.range.map(XPathUtil.getNodeListByXPath(_, elementInfo.getResDom))
            .flatten.map(m=>{
            val ele=AppCrawler.factory.generateElement(m, key)
            ele.getXpath->ele
          }).toMap
        }
        case _ =>{
          Map[String, AbstractElement]()
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
          val masterElement = masterElements.getOrElse(subKey, AppCrawler.factory.generateElement)
          val candidateElement = candidateElements.getOrElse(subKey, AppCrawler.factory.generateElement)
          val message =
            s"""
               |key=${subKey}
               |
               |candidate=${candidateElement.getXpath}
               |
               |master=${masterElement.getXpath}
               |________________
               |
               |
             """.stripMargin

          if (masterElement != candidateElement && !markOnce) {
            markOnce=true
            //todo: 使用绝对路径展示图片
            markup(
              s"""
                 |candidate image
                 |-------
                 |<img src='${new java.io.File(".").getCanonicalPath+"/"+ReportFactory.candidate+"/"+File(DiffSuite.candidateStore.getOrElse(key, AppCrawler.factory.generateElementInfo()).getResImg).name}' width='80%' />
                 |
                 |master image
                 |--------
                 |<img src='${new java.io.File(".").getCanonicalPath+"/"+ReportFactory.master+"/"+File(DiffSuite.masterStore.getOrElse(key, AppCrawler.factory.generateElementInfo()).getResImg).name}' width='80%' />
                 |
                """.stripMargin)
          }

          cp {
            withClue(message) {
              candidateElement.getId should equal(masterElement.getId)
              candidateElement.getName should equal(masterElement.getName)
              candidateElement.getXpath should equal(masterElement.getXpath)
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
  val masterStore : scala.collection.mutable.Map[String, AbstractElementInfo] = ReportFactory.getInstance().loadResult(s"${ReportFactory.master}/elements.yml").getElementStoreMap
  val candidateStore : scala.collection.mutable.Map[String, AbstractElementInfo] = ReportFactory.getInstance().loadResult(s"${ReportFactory.candidate}/elements.yml").getElementStoreMap
  val blackList = List(".*\\.instance.*", ".*bounds.*")
  val range=List("//*[contains(name(), 'Text')]", "//*[contains(name(), 'Image')]", "//*[contains(name(), 'Button')]")
  def saveTestCase(): Unit ={
    val suites=masterStore.map(_._2.getElement.getUrl)++candidateStore.map(_._2.getElement.getUrl)
    suites.foreach(suite=> {
      SuiteToClass.genTestCaseClass(suite, "com.testerhome.appcrawler.DiffSuite", Map("suite"->suite, "name"->suite), ReportFactory.testcaseDir)
    })
  }
}
