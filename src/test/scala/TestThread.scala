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

    import org.apache.log4j.spi.LoggerFactory
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
    log.setAdditivity(true)
    log.trace("trace")
    log.debug("debug")
    log.info("info")
    log.warn("warnning")
    log.error("error")
    log.fatal("fatal")


  }

  test("test slf4j"){

    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    val log = LoggerFactory.getLogger(classOf[TestThread])
    log.trace("trace")
    log.debug("debug")
    log.info("info")
    log.warn("warnning")
    log.error("error")
  }


}
