package com.testerhome.appcrawler.it

import java.net.URL

import org.openqa.selenium.chrome.{ChromeOptions, ChromeDriver}
import org.openqa.selenium.remote.{RemoteWebDriver, DesiredCapabilities}
import org.scalatest.FunSuite
import collection.JavaConversions._

/**
  * Created by seveniruby on 16/11/14.
  */
class TestNW extends FunSuite{
  test("test nw"){

    System.setProperty("webdriver.chrome.driver",
      "/Users/seveniruby/projects/nwjs/ics4_debug_nw0.14.7/chromedriver")
    val options=new ChromeOptions()
    options.addArguments("nwapp=/Users/seveniruby/projects/nwjs/ics4_debug_nw0.14.7/app")
    val driver=new ChromeDriver(options)
    println(driver.getPageSource)
    Thread.sleep(2000)
    driver.findElementsByXPath("//label").foreach(x=>{
      println(x.getTagName)
      println(x.getLocation)
      println(x.getText)
      println("text()="+x.getAttribute("text()"))
      println("text="+x.getAttribute("text"))
      println("value="+x.getAttribute("value"))
      println("name="+x.getAttribute("name"))
      println("id="+x.getAttribute("id"))
      println("class="+x.getAttribute("class"))
      println("type="+x.getAttribute("type"))
      println("placeholder="+x.getAttribute("placeholder"))
      println("============")
    })
    driver.findElementByXPath("//label[contains(., 'selectedRegion')]").click()

    //driver.quit()

  }

  test("test nw remote"){
    val options=new ChromeOptions()
    options.addArguments("nwapp=/Users/seveniruby/projects/nwjs/ics4_debug_nw0.14.7/app")
    val url="http://10.3.2.65:4444/wd/hub"

    val dc = DesiredCapabilities.chrome()
    dc.setCapability(ChromeOptions.CAPABILITY, options)

    val driver=new RemoteWebDriver(new URL(url), dc)
    println(driver.getPageSource)
    Thread.sleep(2000)
    driver.findElementsByXPath("//label").foreach(x=>{
      println(x.getTagName)
      println(x.getLocation)
      println(x.getText)
      println("text()="+x.getAttribute("text()"))
      println("text="+x.getAttribute("text"))
      println("value="+x.getAttribute("value"))
      println("name="+x.getAttribute("name"))
      println("id="+x.getAttribute("id"))
      println("class="+x.getAttribute("class"))
      println("type="+x.getAttribute("type"))
      println("placeholder="+x.getAttribute("placeholder"))
      println("============")
    })
    driver.findElementByXPath("//label[contains(., 'selectedRegion')]").click()

    //driver.quit()

  }

}
