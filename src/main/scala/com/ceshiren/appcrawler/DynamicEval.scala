package com.ceshiren.appcrawler

import com.ceshiren.appcrawler.plugin.Plugin
import org.apache.commons.io.FileUtils

import java.io.File
import java.nio.charset.Charset
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader
import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.interpreter.shell.{ReplReporterImpl, ShellConfig}
import scala.tools.nsc.{Global, Settings}
import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 16/8/13.
  */
class DynamicEval(val outputDir:String="") extends CommonLog{
  //todo: scala的执行引擎，替换为bean shell
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

  val config = ShellConfig(settingsEval)

  val flusher = new ReplReporterImpl(config, settingsEval)
  val interpreter = new IMain(settingsEval, flusher)

  def compile(fileNames:List[String]): Unit ={
    run.compile(fileNames)
  }

  def eval(code:String)={
    interpreter.interpret(code)
  }
  def reset(): Unit ={

  }



}

object DynamicEval extends CommonLog{
  var instance=new DynamicEval()
  var isLoaded=false
  def apply(): Unit ={

  }

  def dsl(command: String): Unit = {
    log.info(s"eval ${command}")
    Try(DynamicEval.eval(command)) match {
      case Success(v) => log.info(v)
      case Failure(e) => log.warn(e.getMessage)
    }
  }

  def shell(command:String): Unit ={
    log.info(s"shell ${command}")
    val file=File.createTempFile(System.currentTimeMillis().toString, ".sh")
    FileUtils.writeStringToFile(file, command, Charset.defaultCharset())
    log.debug(file.getCanonicalPath)
    dsl("\"bash "+file.getCanonicalPath+"\"!!")
  }


  private def eval(code:String): Unit ={
    if(isLoaded==false){
      log.debug("first import")
      instance.eval("import sys.process._")
      instance.eval("val driver=com.ceshiren.appcrawler.AppCrawler.crawler.driver")
      instance.eval("def crawl(depth:Int)=com.ceshiren.appcrawler.AppCrawler.crawler.crawlWithRetry(depth)")
      isLoaded=true
    }
    log.info(code)
    log.info(instance.eval(code))
  }

  def compile(fileNames:List[String]): Unit ={
    instance.compile(fileNames)
    isLoaded=false
  }
  def init(classDir:String=""): Unit ={
    instance=new DynamicEval(classDir)
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
    val runtimes=new DynamicEval(pluginDir)
    runtimes.compile(pluginFiles.map(pluginDirFile.getCanonicalPath+File.separator+_))
    val urls=Seq(pluginDirFile.toURI.toURL, getClass.getProtectionDomain.getCodeSource.getLocation)
    val loader=new URLClassLoader(urls, Thread.currentThread().getContextClassLoader)
    pluginClassNames.map(loader.loadClass(_).newInstance().asInstanceOf[Plugin])
  }
}