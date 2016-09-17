import com.xueqiu.qa.appcrawler._

//继承Plugin类
class DynamicPlugin extends Plugin{
  //重载start方法, 启动时执行
  override def start(): Unit ={
    log.info("hello from seveniruby")
  }
  //在每个element的动作执行前进行针对性的处理. 比如跳过
  override def beforeElementAction(element: UrlElement): Unit ={
    log.info("you can add some logic in here")
    log.info(element)
  }
  //当进入新页面会回调此接口
  override def afterUrlRefresh(url:String): Unit ={
    log.info(s"url=${getCrawler().currentUrl}")
  }

}
