package com.testerhome.appcrawler.ut

import org.junit.jupiter.api.{Test, TestFactory}
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.DynamicTest
import java.util

import com.testerhome.appcrawler.AppCrawler
import io.qameta.allure.Description

import scala.io.Source
import scala.collection.JavaConverters._

class TestJUnit5 {
  @Test
  @Description("Some detailed test description")
  def x(): Unit = {
    println("xxxxxxxx")
    assertTrue(1 == 2)
  }

  @TestFactory
  def dynamicTestsFromCollection: util.Collection[DynamicTest] = {
    Source.fromFile("/tmp/1.data").mkString.split("\n").map(line => {
      dynamicTest(line, () => {
        println(line)
        println(AppCrawler.crawler.conf.resultDir)
        println("xpath")
        println("screenshot")
        println("after clicked")
        println("screenshot2")
        assertTrue(true)
      })
    }).toList.asJava
  }

}
