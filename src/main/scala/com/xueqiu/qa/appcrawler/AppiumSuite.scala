package com.xueqiu.qa.appcrawler

import java.awt.{Color, BasicStroke}
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.{IOSMobileCapabilityType, AndroidMobileCapabilityType, MobileCapabilityType}
import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import org.openqa.selenium.{OutputType, TakesScreenshot, WebElement}
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest._
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}

import scala.sys.process.ProcessLogger
import scala.util.{Failure, Success, Try}

import scala.sys.process._
/**
  * Created by seveniruby on 16/3/26.
  */
class AppiumSuite extends FunSuite
  with Matchers
  with WebBrowser
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with CommonLog {

}
