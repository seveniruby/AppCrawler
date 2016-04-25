import com.xueqiu.qa.appcrawler.plugin.AndroidTrace
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/4/24.
  */
class TestAndroidTrace extends FunSuite{
  test("list"){
    new AndroidTrace().getConnections()

  }

}
