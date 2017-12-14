package com.testerhome.appcrawler

import org.apache.commons.lang3.StringEscapeUtils
import javassist.{ClassPool, CtConstructor}

import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 2017/4/15.
  */
object SuiteToClass extends CommonLog {

  var index=0

  //todo: 特殊字符处理
  def format(name:String): String ={
    name
      .replaceAllLiterally("\\", "\\\\")
      .replaceAllLiterally("\"", "\\\"")
      .replaceAllLiterally("#", "")
      .replaceAllLiterally("&", "")
      .replaceAll("[#;/]", "")
  }
  /**
    * 生成用例对应的class文件，用于调用scalatest执行
    *
    */
  def genTestCaseClass(className: String, superClassName: String, fields: Map[String, Any], directory: String): Unit = {
    val pool = ClassPool.getDefault
    val classNameFormat = format(className)
    Try(pool.makeClass(classNameFormat)) match {
      case Success(classNew) => {
        classNew.setSuperclass(pool.get(superClassName))
        val init = new CtConstructor(null, classNew)
        val body = fields.map(field => {
          s"${field._1}_$$eq(${'"' + format(field._2.toString) + '"'}); "
        }).mkString("\n")
        init.setBody(s"{ ${body}\naddTestCase(); }")
        classNew.addConstructor(init)
        classNew.writeFile(directory)
      }
      case Failure(e) => {
        log.error(s"makeClass error with ${className}")
      }
    }
  }

}

