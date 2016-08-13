package com.xueqiu.qa.appcrawler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.reflect.ClassTag
import scala.reflect.ClassTag
import scala.reflect._

/**
  * Created by seveniruby on 16/8/13.
  */
object DataObject {

  def toYaml(data:Any): String ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
  }

  def fromYaml[T: ClassTag](data:String): T ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(data, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }
}
