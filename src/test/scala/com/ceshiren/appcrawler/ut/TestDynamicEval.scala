package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.CommonLog

import java.io.File
import java.io.File
import java.util.jar.JarFile
import com.ceshiren.appcrawler.plugin.{DemoPlugin, Plugin}
import com.ceshiren.appcrawler._
import com.ceshiren.appcrawler.driver.AppiumClient
import org.scalatest.FunSuite
import org.xml.sax.ErrorHandler

import scala.reflect.internal.settings.MutableSettings
import scala.reflect.internal.util.ScalaClassLoader
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader
import scala.reflect.io.AbstractFile
import scala.tools.nsc.util.BatchSourceFile
import scala.tools.nsc.{GenericRunnerSettings, Global, Settings}
import scala.tools.nsc.interpreter.IMain

/**
  * Created by seveniruby on 16/8/10.
  */
class TestDynamicEval extends FunSuite with CommonLog{

  val fileName="/Users/seveniruby/projects/LBSRefresh/iOS_20160813203343/AppCrawler_8.scala"
  test("MiniAppium dsl"){
    DynamicEval.dsl("hello(\"seveniruby\", 30000)")
    DynamicEval.dsl("hello(\"ruby\", 30000)")
    DynamicEval.dsl(" hello(\"seveniruby\", 30000)")
    DynamicEval.dsl("hello(\"seveniruby\", 30000 )  ")
    DynamicEval.dsl("sleep(3)")
    DynamicEval.dsl("hello(\"xxxxx\")")
    DynamicEval.dsl("hello(\"xxxxx\"); hello(\"double\")")
    DynamicEval.dsl("println(com.ceshiren.appcrawler.AppCrawler.crawler.driver)")

  }

  test("MiniAppium dsl re eval"){
    DynamicEval.dsl("val a=new java.util.Date")
    DynamicEval.dsl("val b=a")
    DynamicEval.dsl("val a=new java.util.Date")
    DynamicEval.dsl("println(a)")
    DynamicEval.dsl("println(b)")
  }

  test("shell"){
    DynamicEval.dsl("\"12345\"")
    DynamicEval.dsl(" \"sh /tmp/1.sh\"!")
    DynamicEval.shell("adb devices; echo xxx;")

  }


  test("compile by scala"){
    DynamicEval.init(new File(fileName).getParent)
    DynamicEval.compile(List(fileName))

  }

  test("native compile"){


    val outputDir=new File(fileName).getParent

    val settings = new Settings()
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings
    settings.outputDirs.setSingleOutput(outputDir)
    settings.usejavacp.value = true

    val global = new Global(settings)
    val run = new global.Run
    run.compile(List(fileName))

  }


  test("compile plugin"){
    DynamicEval.init()
    DynamicEval.compile(List("src/universal/plugins/DynamicPlugin.scala"))
    val p=Class.forName("com.ceshiren.appcrawler.plugin.DynamicPlugin").newInstance()
    log.info(p)


  }

  test("test classloader"){
    val classPath="target/tmp/"
    DynamicEval.init(classPath)
    DynamicEval.compile(List("/Users/seveniruby/projects/LBSRefresh/src/universal/plugins/"))
    val urls=Seq(new java.io.File(classPath).toURI.toURL)
    val loader=new URLClassLoader(urls, ClassLoader.getSystemClassLoader)
    val x=loader.loadClass("AppCrawler_5").newInstance().asInstanceOf[FunSuite]
    log.info(x.testNames)
    log.info(getClass.getCanonicalName)

    log.info(getClass.getProtectionDomain.getCodeSource.getLocation.getPath)

  }

  test("load plugins"){

    val a=new DemoPlugin()
    log.info(a.asInstanceOf[Plugin])
    //getClass.getClassLoader.asInstanceOf[URLClassLoader].loadClass("DynamicPlugin")
    val plugins=DynamicEval.loadPlugins("/Users/seveniruby/projects/LBSRefresh/src/universal/plugins/")
    plugins.foreach(log.info)

  }

  test("crawl keyword"){
    DynamicEval.dsl("def crawl(depth:Int)=com.ceshiren.appcrawler.AppCrawler.crawler.crawl(depth)")
    DynamicEval.dsl("crawl(1)")
  }


}

