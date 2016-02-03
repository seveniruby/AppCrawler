import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/2/2.
  */
class Demo extends FunSuite{
  test("xueqiu"){
    val futu=new AndroidCrawler
    futu.conf.capability++=Map("app"->"http://xqfile.imedao.com/android-release/xueqiu_730_01191600.apk")
    futu.conf.androidCapability++=Map("appPackage"->"com.xueqiu.android")
    futu.conf.androidCapability++=Map("appActivity"->".view.WelcomeActivityAlias")
    futu.setupApp()
    futu.start()
  }
}
