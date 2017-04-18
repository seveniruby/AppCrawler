package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.AppiumClient
import org.scalatest.FunSuite

import scala.sys.process.Process


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

  test("executor service default"){

    val pre=System.currentTimeMillis()
    val r=AppiumClient.asyncTask(5){
      Thread.sleep(100000)
      "xxxx"
    }
    assert(r==None)
    val now=System.currentTimeMillis()
    println((now-pre)/1000)

  }


  test("executor service expect"){

    val pre=System.currentTimeMillis()
    val r=AppiumClient.asyncTask(5){
      Thread.sleep(1000)
      "xxxx"
    }
    assert(r.get=="xxxx")
    val now=System.currentTimeMillis()
    println((now-pre)/1000)

  }

  test("executor service Int expect"){

    val pre=System.currentTimeMillis()
    val r=AppiumClient.asyncTask(5) {
      Thread.sleep(100000)
      1
    }
    assert(r==None)
    val now=System.currentTimeMillis()
    println((now-pre)/1000)

  }

  test("executor service Int"){

    val pre=System.currentTimeMillis()
    val r=AppiumClient.asyncTask(5){
      Thread.sleep(1000)
      1
    }
    assert(r.get==1)
    val now=System.currentTimeMillis()
    println((now-pre)/1000)

  }

  test("-1 async"){
    val x=AppiumClient.asyncTask(-1){
      println("start")
      Thread.sleep(6000)
      3
    }
    0 to 10 foreach{ i=>
      Thread.sleep(1000)
      println(x)
    }
  }

  test("appium start"){
    val process=Process("appium -p 4445")
    val pb=process.run()
    val x=AppiumClient.asyncTask(10){
      pb.exitValue()
    }
    println(x)
    Thread.sleep(20000)
    pb.destroy()
  }




}
