package com.testerhome.appcrawler

import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/8/25.
  */
class DataRecord extends CommonLog {
  val record=ListBuffer[(Long, Any)]()
  //todo: 暂时只用2个就足够了
  private val size=10
  def append(any: Any): Unit ={
    log.info(s"append ${any}")
    record.append(System.currentTimeMillis()->any)
  }
  def intervalMS(): Long ={
    if(record.size<2){
      return 0
    }else {
      val lastRecords = record.takeRight(2)
      lastRecords.last._1 - lastRecords.head._1
    }
  }
  def isDiff(): Boolean ={
    if(record.size<2){
      log.info("just only record return false")
      return false
    }else {
      val lastRecords = record.takeRight(2)
      lastRecords.last._2 != lastRecords.head._2
    }
  }
  def last(count: Int): List[Any] ={
    record.takeRight(count).map(_._2).toList
  }
  def pre(): Any ={
    record.takeRight(2).head._2
  }
  def last(): Any ={
    record.last._2
  }
  def pop(): Unit ={
    record.remove(record.size-1)
  }

}
