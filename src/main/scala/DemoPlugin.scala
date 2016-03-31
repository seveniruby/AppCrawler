/**
  * Created by seveniruby on 16/1/21.
  */
class DemoPlugin extends Plugin{
  override def beforeElementAction(element: UrlElement): Unit ={
    log.info("demo plugin before element action")
    log.info(element)
    log.info("demo plugin end")
  }
  override def afterUrlRefresh(url:String): Unit ={
    getCrawler().url=url.split('|').last
    log.info(s"new url=${getCrawler().url}")
    if(getCrawler().url.contains("Browser")){
      getCrawler().goBack()
    }
  }

}
