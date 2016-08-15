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
  private val settings:Settings=new Settings()
  var outputDir=""
  def init(outputDir:String="") {
    this.outputDir=outputDir
    val tempDir=new File(this.outputDir)
    if(outputDir.nonEmpty && tempDir.exists()==false){
      tempDir.mkdir()
      settings.outputDirs.setSingleOutput(this.outputDir)
    }
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings
    settings.usejavacp.value = true

    //todo:同时使用IMain和Global会导致无法编译
  }

  def compile(fileNames:List[String]): Unit ={
    val global = new Global(settings)
    val run = new global.Run
    run.compile(fileNames)
  }

  def eval(code:String): Unit ={
    if (interpreter == null) {
      init()
      interpreter = new IMain(settings)
    }
    interpreter.interpret(code)
  }

  def reset(): Unit ={

  }

}
