import com.xueqiu.qa.appcrawler._

/**
  * Created by seveniruby on 16/1/21.
  */
class DynamicPlugin extends Plugin{
  override def start(): Unit ={
    log.info("hello from seveniruby")
  }
  override def beforeElementAction(element: UrlElement): Unit ={
    log.info("you can add some login in here")
    log.info(element)
  }
  override def afterUrlRefresh(url:String): Unit ={
    log.info(s"url=${getCrawler().url}")
  }

}
