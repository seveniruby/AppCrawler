package com.xueqiu.qa.appcrawler.ut

import java.io.File

import com.twitter.util.Eval
import com.twitter.util.Eval.EvalSettings
import com.xueqiu.qa.appcrawler.{Runtimes, CommonLog, MiniAppium}
import org.scalatest.FunSuite
import org.xml.sax.ErrorHandler

import scala.reflect.internal.settings.MutableSettings
import scala.reflect.io.AbstractFile
import scala.tools.nsc.util.BatchSourceFile
import scala.tools.nsc.{Global, Settings, GenericRunnerSettings}
import scala.tools.nsc.interpreter.IMain

/**
  * Created by seveniruby on 16/8/10.
  */
class TestEval extends FunSuite with CommonLog{
  test("eval"){
    val result:Int=new Eval().inPlace("1+2")
    assert(result == 3)
  }
  test("eval println"){
    val result=new Eval().inPlace("println(\"xxx\")")
  }

  test("eval object invoke"){
    new Eval().inPlace("com.xueqiu.qa.appcrawler.MiniAppium.hello(\"dddd\", 333)")
  }

  test("MiniAppium dsl"){
    MiniAppium.dsl("hello(\"seveniruby\", 30000)")
    MiniAppium.dsl("hello(\"ruby\", 30000)")
    MiniAppium.dsl(" hello(\"seveniruby\", 30000)")
    MiniAppium.dsl("hello(\"seveniruby\", 30000 )  ")
    MiniAppium.dsl("sleep(3)")
    MiniAppium.dsl("hello(\"xxxxx\")")
  }

  test("compile"){
    val file=new File("/Users/seveniruby/projects/LBSRefresh/src/test/scala/com/xueqiu/qa/appcrawler/temp/雪球雪球问答.scala")
    val e=new Eval(Some(file))
    log.info(e.compilerOutputDir)

  }

  test("compile by scala"){

    val fileName="/Users/seveniruby/projects/LBSRefresh/iOS_20160813132121/雪球SNBPublicTimelineView.scala"
    Runtimes.init(new File(fileName).getParent)
    Runtimes.compile(List(fileName))



  }

  test("native compile"){


    val fileName="/Users/seveniruby/projects/LBSRefresh/iOS_20160813132121/雪球SNBPublicTimelineView.scala"
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

    Runtimes.init()
    Runtimes.eval(
      """
        |import com.xueqiu.qa.appcrawler.MiniAppium
        |println("xxx")
        |println("ddd")
        |MiniAppium.hello("222")
      """.stripMargin)


  }

}

