package com.xueqiu.qa.appcrawler

import com.fasterxml.jackson.databind.{DeserializationFeature, SerializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.reflect.ClassTag
import scala.reflect._

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.JavaConversions._


/**
  * Created by seveniruby on 16/8/13.
  */
trait DataObject {

  def toYaml(data: Any): String = {
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
  }

  def fromYaml[T: ClassTag](data: String): T = {
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.readValue(data, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }


  def toJson(data: Any): String = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
  }


  def fromJson[T: ClassTag](str: String): T = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue(str, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }

  def fromXML(str: String): Map[String, Any] = {
    val node=Jsoup.parse(str)
    def lift(node: Element): Map[String, Any] = node match {
      case doc: Document =>
        Map[String, Any](
          "head" -> lift(doc.head),
          "body" -> lift(doc.body)
        )

      case doc: Element => {
        val children: Elements = doc.children
        val attributes =
          doc.attributes.asList map { attribute =>
            attribute.getKey -> attribute.getValue
          } toMap

        Map(
          "tag" -> doc.tagName,
          "text" -> doc.ownText,
          "attributes" -> attributes,
          "children" -> children.map(element => lift(element))
        )

      }
    }
    lift(node)
  }


  def flatten(data: Map[String, Any]): mutable.Map[String, Any] = {
    val stack = new mutable.Stack[String]()
    val result = mutable.Map[String, Any]()
    def loop(dataKV: scala.collection.Map[String, Any]): Unit = {

      dataKV.foreach(data => {
        stack.push(data._1)
        data match {
          case (key: String, valueMap: scala.collection.Map[String, _]) => {
            val tag = valueMap.getOrElse("tag", "").toString
            val key = tag.split('.').lastOption.getOrElse(tag)
            if (tag.nonEmpty) {
              stack.push(key)
            }

            valueMap.foreach(kv => {
              loop(scala.collection.Map(kv._1 -> kv._2))
            })

            if (tag.nonEmpty) {
              stack.pop()
            }

          }
          case (key: String, values: Seq[_]) => {
            var index = 0
            values.foreach(value => {
              loop(Map(index.toString -> value))
              index += 1
            })
          }
          case (key, value: Any) => {
            result(stack.reverse.mkString(".")) = value
          }
        }
        stack.pop()
      })
    }
    loop(data)
    result
  }


}

object DataObject extends DataObject