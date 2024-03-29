package com.ceshiren.appcrawler.utils

import com.ceshiren.appcrawler.plugin.Plugin
import com.ceshiren.appcrawler.utils.Log.log

import java.io.File
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader
import scala.sys.process.Process
import scala.tools.nsc.interpreter.shell.{ReplReporterImpl, ShellConfig}
import scala.tools.nsc.interpreter.{IMain, Results}
import scala.tools.nsc.{Global, Settings}
import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 16/8/13.
  */
class DynamicEval(val outputDir: String = "") {
  //todo: scala的执行引擎，替换为bean shell
  private val settingsCompile = new Settings()

  if (outputDir.nonEmpty) {
    val tempDir = new File(outputDir)
    if (!tempDir.exists()) {
      tempDir.mkdir()
    }
    settingsCompile.outputDirs.setSingleOutput(this.outputDir)
  }

  settingsCompile.deprecation.value = true // enable detailed deprecation warnings
  settingsCompile.unchecked.value = true // enable detailed unchecked warnings
  settingsCompile.usejavacp.value = true

  val global = new Global(settingsCompile)
  val run = new global.Run

  private val settingsEval = new Settings()
  settingsEval.deprecation.value = true // enable detailed deprecation warnings
  settingsEval.unchecked.value = true // enable detailed unchecked warnings
  settingsEval.usejavacp.value = true

  val config = ShellConfig(settingsEval)

  val flusher = new ReplReporterImpl(config, settingsEval)
  val interpreter = new IMain(settingsEval, flusher)

  def compile(fileNames: List[String]): Unit = {
    run.compile(fileNames)
  }

  def eval(code: String): Results.Result = {
    interpreter.interpret(code)
  }

  def reset(): Unit = {

  }


}

object DynamicEval {
  var instance = new DynamicEval()
  var isLoaded = false

  def apply(): Unit = {

  }

  def dsl(command: String): Unit = {
    log.info(s"eval ${command}")
    Try(DynamicEval.eval(command)) match {
      case Success(v) => log.info(v)
      case Failure(e) => {
        log.error(e.getMessage)
        e.printStackTrace()
      }
    }
  }

  def shell(command: String): Unit = {
    log.info(s"shell: ${command}")
    var buffer="";
    Try{buffer = Process(command).!!} match {
      case Success(v) => {
        log.debug(v)
        log.debug(buffer)
      }
      case Failure(exception) => {
        log.error(exception.getMessage)
        log.error(buffer)
      }
    }
  }


  private def eval(code: String): Unit = {
    log.info(code)
    log.info(instance.eval(code))
    log.info("eval finish")
  }

  def compile(fileNames: List[String]): Unit = {
    instance.compile(fileNames)
    isLoaded = false
  }

  def init(classDir: String = ""): Unit = {
    instance = new DynamicEval(classDir)
  }

  def load(): Unit ={
    if (!isLoaded) {
      log.debug("first import")
      instance.eval("import sys.process._")
      instance.eval("val driver=com.ceshiren.appcrawler.AppCrawler.crawler.driver")
      instance.eval("def crawl(depth:Int)=com.ceshiren.appcrawler.AppCrawler.crawler.crawlWithRetry(depth)")
      isLoaded = true
    }
  }

  def loadPlugins(pluginDir: String = ""): List[Plugin] = {
    val pluginDirFile = new java.io.File(pluginDir)
    if (pluginDirFile.exists() == false) {
      log.warn(s"no ${pluginDir} directory, skip")
      return Nil
    }
    val pluginFiles = pluginDirFile.list().filter(_.endsWith(".scala")).toList
    val pluginClassNames = pluginFiles.map(_.split(".scala").head)
    log.info(s"find plugins in ${pluginDir}")
    log.info(pluginFiles)
    log.info(pluginClassNames)
    val runtimes = new DynamicEval(pluginDir)
    runtimes.compile(pluginFiles.map(pluginDirFile.getCanonicalPath + File.separator + _))
    val urls = Seq(pluginDirFile.toURI.toURL, getClass.getProtectionDomain.getCodeSource.getLocation)
    val loader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader)
    pluginClassNames.map(loader.loadClass(_).newInstance().asInstanceOf[Plugin])
  }
}