import com.xueqiu.qa.appcrawler.{Plugin, UrlElement}

/**
  * Created by seveniruby on 16/1/21.
  */
class DynamicPlugin extends Plugin{
  override def beforeElementAction(element: UrlElement): Unit ={
    log.info("demo com.xueqiu.qa.appcrawler.plugin before element action")
    log.info(element)
    log.info("demo com.xueqiu.qa.appcrawler.plugin end")
  }
  override def afterUrlRefresh(url:String): Unit ={
    getCrawler().url=url.split('|').last
    log.info(s"new url=${getCrawler().url}")
    if(getCrawler().url.contains("Browser")){
      getCrawler().goBack()
    }
  }

}
