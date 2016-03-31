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
  val console=new ConsoleAppender()
  console.setWriter(new OutputStreamWriter(System.out))
  console.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p [%C{1}.%M] %m%n"))
  console.setName("AppCrawler")
  log.addAppender(console)
  log.setLevel(Level.INFO)
  log.setAdditivity(false)
}
