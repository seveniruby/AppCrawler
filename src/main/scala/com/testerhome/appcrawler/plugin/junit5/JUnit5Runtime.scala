package com.testerhome.appcrawler.plugin.junit5

import com.testerhome.appcrawler.data.AbstractElementStore
import com.testerhome.appcrawler.plugin.scalatest.SuiteToClass
import com.testerhome.appcrawler.{Report, ReportFactory, TData}
import org.scalatest.tools.Runner

import scala.collection.JavaConversions._
import scala.io.Source

/**
  * Created by seveniruby on 16/8/15.
  */
class JUnit5Runtime extends Report {

  override def genTestCase(resultDir: String): Unit = {
    //todo:
  }


  //todo: 用junit+allure代替
  override def runTestCase(namespace: String=""): Unit = {
    //todo:
    //seveniruby demo test
  }

  override def changeTitle(title:String): Unit ={
    //todo:
    //malu test demo
    //seveniruby need merge
    //seveniruby2
    //sevenriby3
  }

}