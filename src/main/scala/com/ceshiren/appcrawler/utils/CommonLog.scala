package com.ceshiren.appcrawler.utils

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.log4j._

import java.io.OutputStreamWriter

/**
  * Created by seveniruby on 16/3/31.
  */
trait CommonLog {
  BasicConfigurator.configure()
  Logger.getRootLogger.setLevel(Level.INFO)
  @JsonIgnore
  val layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p [%c{1}.%L.%M] %m%n")
  @JsonIgnore
  lazy val log = initLog()

  def initLog(): Logger = {
    val log = Logger.getLogger(this.getClass.getName)
    //val log=Logger.getRootLogger
    if (log.getAppender("console") == null) {
      val console = new ConsoleAppender()
      console.setName("console")
      console.setWriter(new OutputStreamWriter(System.out))
      console.setLayout(layout)
      log.addAppender(console)
    } else {
      log.info("already exist")
    }
    log.trace(s"set ${this} log level to ${GA.logLevel}")
    log.setLevel(GA.logLevel)
    log.setAdditivity(false)
    log
  }

}
