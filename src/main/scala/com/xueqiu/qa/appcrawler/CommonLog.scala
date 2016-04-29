package com.xueqiu.qa.appcrawler

import java.io.OutputStreamWriter

import org.apache.log4j._

/**
  * Created by seveniruby on 16/3/31.
  */
trait CommonLog {
  BasicConfigurator.configure()
  Logger.getRootLogger.setLevel(Level.INFO)
  var logLevel=Level.INFO
  val layout=new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p [%c{1}.%M.%L] %m%n")
  val log = initLog()

  def initLog(): Logger ={
    val log = Logger.getLogger(this.getClass.getName)
    //val log=Logger.getRootLogger
    val console=new ConsoleAppender()
    console.setWriter(new OutputStreamWriter(System.out))
    console.setLayout(layout)
    log.addAppender(console)
    log.setLevel(Level.INFO)
    log.setAdditivity(false)
    log
  }
}
