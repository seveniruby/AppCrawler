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
    val conf=new TraversalConf().load(this.cm.get("conf").get.toString)
    var t:Traversal=new Traversal

    if(conf.app.trim.matches(".*\\.apk")){
      t=new AndroidTraversal
    }else{
      t=new IOSTraversal
    }
    t.conf=conf
    t.setupApp(conf.app, conf.appiumUrl)
    t.traversal()
  }
}
