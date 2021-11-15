package com.ceshiren.appcrawler.plugin.report

import com.ceshiren.appcrawler.model.URIElementStore
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.TData

import scala.io.Source
/**
  * Created by seveniruby on 16/8/15.
  */
abstract class Report  {


  def genTestCase(resultDir: String): Unit = {
  }


  //todo: 用junit+allure代替
  def runTestCase(namespace: String=""): Unit = {

  }

  def changeTitle(title:String): Unit ={
  }

  def loadResult(elementsFile: String): URIElementStore ={
    val content=Source.fromFile(elementsFile).mkString
    log.info(s"${elementsFile} size = ${content.size}")
    //todo: cannot deserialize from Object value (no delegate- or property-based Creator)
    TData.fromYaml[URIElementStore](content)
  }

}