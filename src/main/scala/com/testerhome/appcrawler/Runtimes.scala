package com.testerhome.appcrawler

import java.io.File

import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader
import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.{Global, Settings}

/**
  * Created by seveniruby on 16/8/13.
  */
class Runtimes(val outputDir:String="") extends CommonLog{
  private val settingsCompile=new Settings()

  if(outputDir.nonEmpty){
    val tempDir=new File(outputDir)
    if(tempDir.exists()==false){
      tempDir.mkdir()
    }
    settingsCompile.outputDirs.setSingleOutput(this.outputDir)
  }

  settingsCompile.deprecation.value = true // enable detailed deprecation warnings
  settingsCompile.unchecked.value = true // enable detailed unchecked warnings
  settingsCompile.usejavacp.value = true

  val global = new Global(settingsCompile)
  val run = new global.Run

  private val settingsEval=new Settings()
  settingsEval.deprecation.value = true // enable detailed deprecation warnings
  settingsEval.unchecked.value = true // enable detailed unchecked warnings
  settingsEval.usejavacp.value = true

  val interpreter = new IMain(settingsEval)

  def compile(fileNames:List[String]): Unit ={
    run.compile(fileNames)
  }

  def eval(code:String): Unit ={
    interpreter.interpret(code)
  }
  def reset(): Unit ={

  }



}

object Runtimes extends CommonLog{
  var instance=new Runtimes()
  var isLoaded=false
  def apply(): Unit ={

  }
  def eval(code:String): Unit ={
    if(isLoaded==false){
      log.info("first import")
      instance.eval("val driver=com.testerhome.appcrawler.AppCrawler.crawler.driver")
      instance.eval("def crawl(depth:Int)=com.testerhome.appcrawler.AppCrawler.crawler.crawl(depth)")
      isLoaded=true
    }
    log.info(code)
    instance.eval(code)
    log.info("eval finish")
  }

  def compile(fileNames:List[String]): Unit ={
    instance.compile(fileNames)
    isLoaded=false
  }
  def init(classDir:String=""): Unit ={
    instance=new Runtimes(classDir)
  }
  def reset(): Unit ={

  }
  def loadPlugins(pluginDir:String=""): List[Plugin] ={
    val pluginDirFile=new java.io.File(pluginDir)
    if(pluginDirFile.exists()==false){
      log.warn(s"no ${pluginDir} directory, skip")
      return Nil
    }
    val pluginFiles=pluginDirFile.list().filter(_.endsWith(".scala")).toList
    val pluginClassNames=pluginFiles.map(_.split(".scala").head)
    log.info(s"find plugins in ${pluginDir}")
    log.info(pluginFiles)
    log.info(pluginClassNames)
    val runtimes=new Runtimes(pluginDir)
    runtimes.compile(pluginFiles.map(pluginDirFile.getCanonicalPath+File.separator+_))
    val urls=Seq(pluginDirFile.toURI.toURL, getClass.getProtectionDomain.getCodeSource.getLocation)
    val loader=new URLClassLoader(urls, Thread.currentThread().getContextClassLoader)
    pluginClassNames.map(loader.loadClass(_).newInstance().asInstanceOf[Plugin])
  }
}