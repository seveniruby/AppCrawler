package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler._
import org.scalatest.FunSuite

import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/9/17.
  */
class TestElementStore extends FunSuite with CommonLog{
  test("save to yaml"){
    val store=new UrlElementStore

    val element_1=UrlElement("a", "b", "c", "d", "e")
    val info_1=new ElementInfo()
    info_1.element=element_1
    info_1.action=ElementStatus.Skiped


    val element_2=UrlElement("aa", "bb", "cc", "dd", "ee")
    val info_2=new ElementInfo()
    info_2.element=element_2
    info_2.action=ElementStatus.Clicked

    store.elementStore ++= scala.collection.mutable.Map(
      element_1.toString->info_1,
      element_2.toString->info_2
    )

    store.clickedElementsList.push(element_2)

    log.info(store)
    val str=DataObject.toYaml(store)
    log.info(str)
    val store2=DataObject.fromYaml(str)
    log.info(store2)

  }


}
