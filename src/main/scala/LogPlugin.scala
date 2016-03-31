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
        println(s"read log=${l.toString}")
        try {
          val logMessage = driver.manage().logs.get(l.toString).filter(Level.ALL).toArray()
          println(s"log=${l} size=${logMessage.size}")
          println(logMessage.lift(20).foreach(println))
          println(s"log=${l} end")
          logs+=l.toString
        } catch {
          case ex: Exception => println(s"log=${l.toString} not exist")
        }
      })
    }
    println("print logs")
    logs.foreach(l => {
      println(s"read log=${l.toString}")
      try {
        val logMessage = driver.manage().logs.get(l.toString).filter(Level.ALL).toArray()
        println(s"log=${l} size=${logMessage.size}")
        println(logMessage.foreach(Console.println))
        println(s"log=${l} end")
      } catch {
        case ex: Exception => println(s"log=${l.toString} not exist")
      }
    })
  }


  override def afterUrlRefresh(url: String): Unit = {

  }

}
