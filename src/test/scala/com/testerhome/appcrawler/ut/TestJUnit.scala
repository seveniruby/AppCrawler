package com.testerhome.appcrawler.ut;

import org.scalatest.junit.JUnitSuite

import scala.collection.mutable.ListBuffer
import org.junit.Test
import org.junit.Before
import io.qameta.allure.Allure
import io.qameta.allure.model.{Status, TestResult}

class TestJUnit extends JUnitSuite {

    var sb: StringBuilder = _
    var lb: ListBuffer[String] = _

    @Before def initialize() {
        sb = new StringBuilder("ScalaTest is ")
        lb = new ListBuffer[String]
    }

    @Test def verifyEasy() {
        sb.append("easy!")
        assert(sb.toString === "ScalaTest is easy!")
        assert(lb.isEmpty)
                lb += "sweet"
    }

    @Test def verifyFun() {
        sb.append("fun!")
        assert(sb.toString === "ScalaTest is fun!")
        assert(lb.isEmpty)

    }

    @Test
    def testAllure(): Unit = {
        println(Allure.getLifecycle.startTestCase("testcase"))
        Allure.getLifecycle.writeTestCase("uuid")
        val result=new TestResult()
        result.setUuid("testcase")
        result.setStatus(Status.PASSED)
        Allure.getLifecycle.scheduleTestCase(result)

        //Allure.addDescription("test allure")
        //Allure.addAttachment("file", "file content")
        val link = new io.qameta.allure.model.Link()
        link.setName("link demo")
        link.setUrl("http://www.baidu.com")
        //Allure.addLinks(link)

        Allure.getLifecycle.stopTestCase("testcase")
    }
}