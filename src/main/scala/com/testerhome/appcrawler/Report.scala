package com.testerhome.appcrawler

import com.testerhome.appcrawler.data.AbstractElementStore
import com.testerhome.appcrawler.plugin.scalatest.SuiteToClass
import org.scalatest.tools.Runner

import scala.io.Source
import collection.JavaConversions._

/**
  * Created by seveniruby on 16/8/15.
  */
abstract class Report extends CommonLog {


  def saveTestCase(store: AbstractElementStore, resultDir: String): Unit = {
  }


  //todo: 用junit+allure代替
  def runTestCase(namespace: String=""): Unit = {

  }

  def changeTitle(title:String): Unit ={
  }

  def loadResult(elementsFile: String): AbstractElementStore ={
    val content=Source.fromFile(elementsFile).mkString
    log.info(s"${elementsFile} size = ${content.size}")
    //todo: cannot deserialize from Object value (no delegate- or property-based Creator)
    log.warn("一定概率失败，底层依赖库的bug")
    TData.fromYaml[AbstractElementStore](content)
  }

}