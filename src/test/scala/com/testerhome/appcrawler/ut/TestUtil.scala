package com.testerhome.appcrawler.ut

import java.io.File
import java.io.File
import java.util.jar.JarFile

import com.testerhome.appcrawler.CommonLog
import com.testerhome.appcrawler.plugin.DemoPlugin
import com.testerhome.appcrawler._
import com.testerhome.appcrawler.driver.AppiumClient
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
class TestUtil extends FunSuite with CommonLog{

  val fileName="/Users/seveniruby/projects/LBSRefresh/iOS_20160813203343/AppCrawler_8.scala"
  test("MiniAppium dsl"){
    Util.dsl("hello(\"seveniruby\", 30000)")
    Util.dsl("hello(\"ruby\", 30000)")
    Util.dsl(" hello(\"seveniruby\", 30000)")
    Util.dsl("hello(\"seveniruby\", 30000 )  ")
    Util.dsl("sleep(3)")
    Util.dsl("hello(\"xxxxx\")")
    Util.dsl("hello(\"xxxxx\"); hello(\"double\")")
    Util.dsl("println(com.testerhome.appcrawler.AppCrawler.crawler.driver)")

  }

  test("MiniAppium dsl re eval"){
    Util.dsl("val a=new java.util.Date")
    Util.dsl("val b=a")
    Util.dsl("val a=new java.util.Date")
    Util.dsl("println(a)")
    Util.dsl("println(b)")
  }

  test("shell"){
    Util.dsl("\"12345\"")
    //todo: not work
    Util.dsl("\"sh -c 'adb devices; echo xxx;' \" !")
    Util.dsl(" \"sh /tmp/1.sh\"!")

  }

  test("compile by scala"){
    Util.init(new File(fileName).getParent)
    Util.compile(List(fileName))

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


  test("imain"){

    Util.init()
    Util.dsl(
      """
        |import com.testerhome.appcrawler.MiniAppium
        |println("xxx")
        |println("ddd")
        |MiniAppium.hello("222")
      """.stripMargin)


  }


  test("imain q"){

    Util.init()
    Util.dsl("import com.testerhome.appcrawler.MiniAppium")
    Util.dsl(
      """
        |println("xxx")
        |println("ddd")
        |MiniAppium.hello("222")
      """.stripMargin)


  }


  test("imain with MiniAppium"){

    Util.init()
    Util.dsl("import com.testerhome.appcrawler.MiniAppium._")
    Util.dsl(
      """
        |hello("222")
        |println(driver)
      """.stripMargin)
  }

  test("compile plugin"){
    Util.init()
    Util.compile(List("src/universal/plugins/DynamicPlugin.scala"))
    val p=Class.forName("com.testerhome.appcrawler.plugin.DynamicPlugin").newInstance()
    log.info(p)


  }

  test("test classloader"){
    val classPath="target/tmp/"
    Util.init(classPath)
    Util.compile(List("/Users/seveniruby/projects/LBSRefresh/src/universal/plugins/"))
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
    val plugins=Util.loadPlugins("/Users/seveniruby/projects/LBSRefresh/src/universal/plugins/")
    plugins.foreach(log.info)

  }

  test("crawl keyword"){
    Util.dsl("def crawl(depth:Int)=com.testerhome.appcrawler.AppCrawler.crawler.crawl(depth)")
    Util.dsl("crawl(1)")
  }


}

