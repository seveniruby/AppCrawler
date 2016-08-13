package com.xueqiu.qa.appcrawler.ut

import org.scalatest.FunSuite


/**
  * Created by seveniruby on 16/3/30.
  */
class TestThread extends FunSuite{
  test("test start new thread and kill"){
    var a=1
    var hello = new Thread(new Runnable {
      def run() {
        println("hello world")
        for(i<- 1 to 5){
          Thread.sleep(1000)
          a+=1
          println(a)
        }
        println("thread end")
      }
    })

    hello.start()
    Thread.sleep(7000)
    println(s"a=$a")

    hello = new Thread(new Runnable {
      def run() {
        println("hello world")
        Thread.sleep(5000)
        println("thread end")
      }
    })

    hello.start()
    Thread.sleep(3000)
    hello.stop()
  }

  test("logger testing"){

    import org.apache.log4j.{BasicConfigurator, Logger}

    BasicConfigurator.configure()
    var log=Logger.getRootLogger()
    log.trace("trace")
    log.debug("debug")
    log.info("info")
    log.warn("warnning")
    log.error("error")
    log.fatal("fatal")

    log=Logger.getLogger(this.getClass)
    log.trace("trace")
    log.debug("debug")
    log.info("info")
    log.warn("warnning")
    log.error("error")
    log.fatal("fatal")

    log=Logger.getLogger("demo")
    log.trace("trace")
    log.debug("debug")
    log.info("info")
    log.warn("warnning")
    log.error("error")
    log.fatal("fatal")


  }

  test("test slf4j"){

    import org.slf4j.LoggerFactory
    val log = LoggerFactory.getLogger(classOf[TestThread])
    log.trace("trace")
    log.debug("debug")
    log.info("info")
    log.warn("warnning")
    log.error("error")
  }


/*
  test("test console"){
    import scala.tools.nsc.Settings
    import scala.tools.nsc.interpreter.ILoop

    val settings=new Settings()
    val loop = new ILoop
    settings.usejavacp.value=true
    loop.process(settings)
  }
*/


  def callbyname(count:Int =3)(callback: =>Unit): Unit ={
    1 to count foreach(x=>callback)
  }


  def callbythread(count:Int =3)(callback: =>Unit): Unit ={
    1 to count foreach(x=>{
      val thread = new Thread(new Runnable {
        override def run(): Unit = {
          callback
        }
      })
      thread.start()
      thread.join(3000)
      thread.stop()
    })
  }

  test("test by name callback"){
    println("before")
    callbythread(3){
      println("xx start")
      Thread.sleep(5000)
      println("xx stop")
    }
    println("after")

  }



}
