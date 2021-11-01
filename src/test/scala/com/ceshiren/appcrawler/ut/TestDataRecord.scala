package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.model.DataRecord
import com.ceshiren.appcrawler.utils.CommonLog
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/8/25.
  */
class TestDataRecord extends FunSuite with CommonLog{
  test("diff int"){
    val stringDiff=new DataRecord()
    stringDiff.append(22)
    Thread.sleep(1000)
    stringDiff.append(33333)
    log.info(stringDiff.isDiff())
    log.info(stringDiff.intervalMS())
  }

  test("test interval"){
    val diff=new DataRecord
    assert(0==diff.intervalMS(), diff)
    diff.append("0")
    Thread.sleep(500)
    diff.append("500")
    assert(diff.intervalMS()>=500, diff)
    Thread.sleep(2000)
    diff.append("2000")
    assert(diff.intervalMS()>=2000, diff)
    assert(diff.intervalMS()<=2200, diff)



  }

  test("diff first"){
    val stringDiff=new DataRecord
    assert(false==stringDiff.isDiff, stringDiff)
    stringDiff.append("xxxx")
    assert(false==stringDiff.isDiff, stringDiff)
    stringDiff.append("3333")
    assert(true==stringDiff.isDiff, stringDiff)
    stringDiff.append("3333")
    assert(false==stringDiff.isDiff, stringDiff)



  }

}
