import java.util.logging.Level

import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 16/1/21.
  *
  * 如果某种类型的控件点击次数太多, 就跳过. 设定一个阈值
  */
class LogPlugin extends Plugin {
  private var logs=ListBuffer[String]()
  override def afterElementAction(element: UrlElement): Unit = {
    val driver = getCrawler().driver
    if(logs.isEmpty){
      driver.manage().logs().getAvailableLogTypes.toArray().foreach(l => {
        log.info(s"read log=${l.toString}")
        try {
          val logMessage = driver.manage().logs.get(l.toString).filter(Level.ALL).toArray()
          log.info(s"log=${l} size=${logMessage.size}")
          logMessage.lift(20).foreach(log.info)
          log.info(s"log=${l} end")
          logs+=l.toString
        } catch {
          case ex: Exception => log.warn(s"log=${l.toString} not exist")
        }
      })
    }
    log.trace("print logs")
    logs.foreach(l => {
      log.trace(s"read log=${l.toString}")
      try {
        val logMessage = driver.manage().logs.get(l.toString).filter(Level.ALL).toArray()
        log.info(s"log=${l} size=${logMessage.size}")
        log.info(
          s"""
            |${logMessage.mkString("\n")}
            |
          """.stripMargin)
        log.info(s"log=${l} end")
      } catch {
        case ex: Exception => log.warn(s"log=${l.toString} not exist")
      }
    })
  }


  override def afterUrlRefresh(url: String): Unit = {

  }

}
