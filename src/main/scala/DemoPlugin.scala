/**
  * Created by seveniruby on 16/1/21.
  */
class DemoPlugin extends Plugin{
  override def beforeElementAction(element: UrlElement): Unit ={
    println("demo plugin before element action")
    println(element)
    println("demo plugin end")
  }
  override def afterUrlRefresh(url:String): Unit ={
    getCrawler().url=url.split('|').last
    println(s"new url=${getCrawler().url}")
    if(getCrawler().url.contains("Browser")){
      getCrawler().goBack()
    }
  }

}
