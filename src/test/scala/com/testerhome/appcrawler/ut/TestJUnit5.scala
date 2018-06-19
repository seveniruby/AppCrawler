package com.testerhome.appcrawler.ut

import org.junit.jupiter.api.{Test, TestFactory}
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.DynamicTest
import java.util

import io.qameta.allure.Description


class TestJUnit5 {
  @Test
  @Description("Some detailed test description")
  def x(): Unit ={
    assertTrue(1==2)
  }

  @TestFactory
  def dynamicTestsFromCollection: util.Collection[DynamicTest] = {
    util.Arrays.asList(
      dynamicTest("1st dynamic test", () => assertTrue(true)),
      dynamicTest("2nd dynamic test", () => assertEquals(4, 2 * 2)))
  }

}
