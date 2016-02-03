import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/2/2.
  */
class Demo extends FunSuite{
  test("xueqiu"){
    val xueqiu=new AndroidCrawler
    xueqiu.conf.capability++=Map("app"->"http://xqfile.imedao.com/android-release/xueqiu_730_01191600.apk")
    xueqiu.conf.androidCapability++=Map("appPackage"->"com.xueqiu.android")
    xueqiu.conf.androidCapability++=Map("appActivity"->".view.WelcomeActivityAlias")
    xueqiu.conf.androidCapability++=Map("autoLaunch"->"true")
    xueqiu.setupApp()
    xueqiu.start()
  }
}
