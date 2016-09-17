package com.xueqiu.qa.appcrawler

import com.fasterxml.jackson.databind.{DeserializationFeature, SerializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.reflect.ClassTag
import scala.reflect.ClassTag
import scala.reflect._

/**
  * Created by seveniruby on 16/8/13.
  */
trait DataObject {

  def toYaml(data:Any): String ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
  }

  def fromYaml[T: ClassTag](data:String): T ={
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.readValue(data, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }


  def toJson(data:Any): String ={
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
  }


  def fromJson[T: ClassTag](str :String): T ={
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(str, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }


}

object DataObject extends DataObject
