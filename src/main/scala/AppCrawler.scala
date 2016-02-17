import org.scalatest.ConfigMap

/**
  * Created by seveniruby on 16/1/7.
  */


import java.io.File

case class Config(
                   app: File = new File("."),
                   conf: File = new File("."),
                   verbose: Boolean = false,
                   platform: String = "android",
                   capability: Map[String, String] = Map[String, String]()
                 )


object AppCrawler {
  /*
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
  */

  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[Config]("appcrawler") {
      head("appcrawler", "1.0.1")
      opt[File]('a', "app") action { (x, c) =>
        c.copy(app = x)
      } text ("Android或者iOS的文件地址, 可以是网络地址, 赋值给appium的app选项")
      opt[File]('c', "conf") action { (x, c) =>
        c.copy(conf = x)
      } validate { x => {
        if (x.isFile) {
          success
        } else {
          failure(s"$x not exist")
        }
      }
      } text ("配置文件地址")
      opt[String]('p', "platform") action { (x, c) =>
        c.copy(platform = x)
      } text ("平台类型android或者ios")
      opt[Map[String, String]]("capability") valueName ("k1=v1,k2=v2...") action { (x, c) =>
        c.copy(capability = x)
      } text ("appium capability选项")
      opt[Unit]("verbose") action { (_, c) =>
        c.copy(verbose = true)
      } text ("是否展示更多debug信息")
      note("appcrawler app爬虫\n")
      help("help") text (
        """
          |示例
          |appcrawler -a xueqiu.apk
          |appcrawler -a xueqiu.apk --capability noReset=true
          |appcrawler -c conf/xueqiu_android.conf
        """.stripMargin)

    }
    // parser.parse returns Option[C]

    val args_new = if (args.length == 0) {
      Array("--help")
    } else {
      args
    }
    parser.parse(args_new, Config()) match {
      case Some(config) => {
        var crawlerConf = new CrawlerConf
        //获取配置模板文件
        if (config.conf.isFile) {
          println(s"Find Conf ${config.conf.getAbsolutePath}")
          crawlerConf=crawlerConf.load(config.conf)
        }
        //合并capability, 特定平台的capability>通用capability
        config.platform.toLowerCase match {
          case "android"=> {
            crawlerConf.androidCapability=crawlerConf.capability++crawlerConf.androidCapability
            crawlerConf.androidCapability ++= config.capability
            if (config.app.getName!=".") {
              crawlerConf.androidCapability ++= Map("app" -> config.app.getPath.replace(":/", "://"))
            }
          }
          case "ios" => {
            crawlerConf.iosCapability=crawlerConf.capability++crawlerConf.iosCapability
            crawlerConf.iosCapability ++= config.capability
            if (config.app.getName!=".") {
              crawlerConf.iosCapability ++= Map("app" -> config.app.getPath.replace(":/", "://"))
            }
          }
        }
        crawlerConf.currentDriver = config.platform

        //获得app设置
        //println(s"app path=${config.app.getPath} ${config.app.getName} ${config.app.getAbsolutePath} ${config.app.getCanonicalPath}")
        println(crawlerConf.toJson)
        new AppCrawlerTestCase().execute(configMap = ConfigMap("conf" -> crawlerConf))
      }
      case None => {}
    }
  }
}
