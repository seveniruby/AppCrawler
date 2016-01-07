import org.scalatest.ConfigMap

/**
  * Created by seveniruby on 16/1/7.
  */

object AppCrawler{
  def sbt(args: String): Unit = {
    import scala.sys.process._
    //val sbt="/usr/local/Cellar/sbt/0.13.8/libexec/sbt-launch.jar"
    val project_dir=getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath.
      split("/").dropRight(2).mkString("/")
    val sbt = s"${project_dir}/lib/sbt-launch.jar"
    val cmd = Seq("java", "-jar", sbt, args) // You
    println(cmd)
    cmd ! ProcessLogger(stdout append _ + "\n", stderr append _ + "\n")
  }

  def main(args: Array[String]) {
    if(args.length==0){
      println(
        """
          |AppiumCrawler app自动探测工具, 支持Android与iOS遍历. 这不仅仅是个遍历工具
          |appiumcrawler [conf file path]
          |
        """.stripMargin)
    }
    args.foreach(conf=>{
      new AppTraversal().execute(configMap = ConfigMap("conf"-> conf))
    })
  }
}
