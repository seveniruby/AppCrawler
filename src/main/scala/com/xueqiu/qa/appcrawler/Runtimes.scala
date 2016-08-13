package com.xueqiu.qa.appcrawler

import java.io.File

import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.io.AbstractFile
import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.{Global, Settings}

/**
  * Created by seveniruby on 16/8/13.
  */
object Runtimes extends CommonLog{

  private var interpreter:IMain=_
  private var settings:Settings=_
  var outputDir=""
  def init(outputDir:String="target") {
    this.outputDir=outputDir

    settings = new Settings()
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings
    val tempDir=new File(this.outputDir)
    if(tempDir.exists()==false){
      tempDir.mkdir()
    }
    settings.outputDirs.setSingleOutput(this.outputDir)
    settings.usejavacp.value = true
    log.info(settings)

    //todo:同时使用IMain和Global会导致无法编译
    //interpreter = new IMain(settings)



  }

  def compile(fileNames:List[String]): Unit ={
    val global = new Global(settings)
    val run = new global.Run
    run.compile(fileNames)
  }

  def eval(code:String): Unit ={
    interpreter = new IMain(settings)
    interpreter.interpret(code)
  }

}
