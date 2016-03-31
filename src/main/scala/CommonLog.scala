import java.io.OutputStreamWriter

import org.apache.log4j.spi.LoggerFactory
import org.apache.log4j._

/**
  * Created by seveniruby on 16/3/31.
  */
trait CommonLog {
  BasicConfigurator.configure()
  val log = Logger.getLogger(this.getClass)
  //val log=Logger.getRootLogger

  val layout=new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p [%.20c.%M] %m%n")
  val console=new ConsoleAppender()
  console.setWriter(new OutputStreamWriter(System.out))
  console.setLayout(layout)
  console.setName("AppCrawler")

  log.addAppender(console)
  log.setLevel(Level.INFO)
  log.setAdditivity(false)

  def add(fileName:String): Unit ={
    log.addAppender(new FileAppender(layout, fileName, false))
  }
}
