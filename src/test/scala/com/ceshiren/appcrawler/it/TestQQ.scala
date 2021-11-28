package com.ceshiren.appcrawler.it

import com.ceshiren.appcrawler.AppCrawler
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement
import org.scalatest.{BeforeAndAfterAll, FunSuite}


class SampleTest extends FunSuite with BeforeAndAfterAll{
  private var driver:AndroidDriver[WebElement] = null

  test("test automation"){
    AppCrawler.main(Array("-c", "src/test/scala/com/ceshiren/appcrawler/it/qq_automation.yml",
      "-o", s"/tmp/xueqiu/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }


  test("ruqi automation"){
    AppCrawler.main(Array("-c", "src/test/scala/com/ceshiren/appcrawler/it/ruqi_automation.yml",
      "-o", s"/tmp/ruqi/${System.currentTimeMillis()}", "--verbose"
    )
    )
  }
}

