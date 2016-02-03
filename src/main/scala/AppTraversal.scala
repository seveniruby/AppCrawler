import org.scalatest.{ConfigMap, BeforeAndAfterAllConfigMap, FunSuite}

/**
  * Created by seveniruby on 16/1/7.
  */
class AppTraversal extends FunSuite with BeforeAndAfterAllConfigMap{
  var cm=ConfigMap()
  override def beforeAll(cm: ConfigMap): Unit ={
    println(cm)
    this.cm=cm
  }
  test("App Traversal"){
    val conf=new CrawlerConf().load(this.cm.get("conf").get.toString)
    var t:Crawler=new Crawler

    conf.currentDriver.toLowerCase match {
      case "android"=>{
        t=new AndroidCrawler
      }
      case "ios" => {
        t=new IOSCrawler
      }
      case _ =>{
        println("请指定currentDriver为Android或者iOS")
      }
    }
    t.conf=conf
    t.setupApp()
    t.start()
  }
}
