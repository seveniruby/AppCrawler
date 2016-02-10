import org.scalatest.{ConfigMap, BeforeAndAfterAllConfigMap, FunSuite}

/**
  * Created by seveniruby on 16/1/7.
  */
class AppCrawlerTestCase extends FunSuite with BeforeAndAfterAllConfigMap{
  var cm=ConfigMap()
  override def beforeAll(cm: ConfigMap): Unit ={
    println(cm)
    this.cm=cm
  }
  test("App Crawler"){
    val conf=this.cm.get("conf").get.asInstanceOf[CrawlerConf]
    var crawler:Crawler=new Crawler

    conf.currentDriver.toLowerCase match {
      case "android"=>{
        crawler=new AndroidCrawler
      }
      case "ios" => {
        crawler=new IOSCrawler
      }
      case _ =>{
        println("请指定currentDriver为Android或者iOS")
      }
    }
    crawler.conf=conf

    println(crawler)
    println(crawler.conf)

    crawler.setupApp()
    crawler.start()
  }
}
