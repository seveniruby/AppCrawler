package com.xueqiu.qa.appcrawler.ut

import com.twitter.util.Eval
import com.xueqiu.qa.appcrawler.MiniAppium
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/8/10.
  */
class TestEval extends FunSuite{
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

}
