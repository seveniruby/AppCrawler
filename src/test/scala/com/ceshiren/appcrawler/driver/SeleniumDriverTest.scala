package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.{AppCrawler, CrawlerConf}

import java.net.URL
import com.ceshiren.appcrawler.CrawlerConf
import org.jsoup.nodes.Entities.EscapeMode
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.io.Source

class SeleniumDriverTest extends FunSuite with BeforeAndAfterEach {

  override def beforeEach() {

  }

  test("testGetPageSource") {

    val selenium=new SeleniumDriver(url = "http://127.0.0.1:4444/wd/hub", configMap = Map(
      "browserName"-> "chrome"
    ))

    selenium.conf=new CrawlerConf()
    selenium.driver.get("https://www.baidu.com")
    println(selenium.getPageSource())


  }

  test("baidu"){

    AppCrawler.main(Array(
      "--capability", "automationName=selenium,browserName=chrome,app=http://www.baidu.com",
      "-u", "http://127.0.0.1:4444/wd/hub",
      "-o", s"/Volumes/ram/baidu/${new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)}",
      "--verbose"
    )
    )
  }


}
