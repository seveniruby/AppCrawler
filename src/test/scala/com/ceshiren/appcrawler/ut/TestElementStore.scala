package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.core.Status
import com.ceshiren.appcrawler.model.{ElementInfo, URIElement, URIElementStore}
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.TData
import org.scalatest.{FunSuite, Matchers}
/**
  * Created by seveniruby on 16/9/17.
  */
class TestElementStore extends FunSuite with Matchers {
  test("save to yaml"){
    val store=new URIElementStore

    val element_1=URIElement("a", "b", "c", "d", "e")
    val info_1=new ElementInfo()
    info_1.element=element_1
    info_1.action=Status.SKIPPED


    val element_2=URIElement("aa", "bb", "cc", "dd", "ee")
    val info_2=new ElementInfo()
    info_2.element=element_2
    info_2.action=Status.CLICKED

    store.elementStoreMap ++= scala.collection.mutable.Map(
      element_1.toString->info_1,
      element_2.toString->info_2
    )

    store.clickedElementsList.append(element_2)

    log.info(store)
    val str=TData.toYaml(store)
    log.info(str)
    val store2=TData.fromYaml[URIElementStore](str)
    log.info(store2)
    val str2=TData.toYaml(store2)


    str should be equals str2
    store should be equals store2

  }


}
