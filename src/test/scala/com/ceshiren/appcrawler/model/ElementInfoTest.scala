package com.ceshiren.appcrawler.model

import com.ceshiren.appcrawler.core.Status
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.TData
import org.junit.jupiter.api.Test


class ElementInfoTest {

  @Test
  def yaml(): Unit ={
    val info=new ElementInfo()
    info.action=Status.CLICKED
    val strYaml=TData.toYaml(info)
    log.info(strYaml)
    val strJson=TData.toJson(info)
    log.info(strJson)
    val info2=TData.fromYaml[ElementInfo](strYaml)
    log.info(info2)
  }

  @Test
  def json(): Unit ={
    val info=new ElementInfo()
    info.action=Status.CLICKED
    val strJson=TData.toJson(info)
    log.info(strJson)
    val info2=TData.fromJson[ElementInfo](strJson)
    log.info(info2)
  }

}
