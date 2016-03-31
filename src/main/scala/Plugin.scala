/**
  * Created by seveniruby on 16/1/7.
  */
abstract class Plugin {
  private var crawler: Crawler=_
  def getCrawler(): Crawler ={
    this.crawler
  }
  def init(crawler: Crawler): Unit ={
    this.crawler=crawler
  }
  def afterUrlRefresh(url:String): Unit ={

  }
  def beforeElementAction(element: UrlElement): Unit ={

  }
  def afterElementAction(element: UrlElement): Unit ={

  }

  def println(str:Any): Unit ={
    getCrawler().println(str)
  }

}
