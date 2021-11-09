package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.AppCrawler
import com.ceshiren.appcrawler.utils.Log
import org.junit.jupiter.api.{Test, TestFactory}
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.DynamicTest

import java.util
import io.qameta.allure.Description

import scala.io.Source
import scala.jdk.CollectionConverters._

class TestJUnit5 {
  @Test
  @Description("Some detailed test description")
  def x(): Unit = {
    assertTrue(1 == 1)
  }

  @TestFactory
  def dynamicTestsFromCollection: util.Collection[DynamicTest] = {
    """|1
      |2
      |3
      |4""".stripMargin.split("\n").map(line => {
      Log.log.info(line)
      dynamicTest(line, () => {
        assertTrue(true)
      })
    }).toList.asJava
  }

}
