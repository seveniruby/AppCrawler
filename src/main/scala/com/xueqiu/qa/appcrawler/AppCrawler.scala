package com.xueqiu.qa.appcrawler

import java.io.File

import org.apache.log4j.Level
import org.scalatest.ConfigMap

/**
  * Created by seveniruby on 16/4/24.
  */
object AppCrawler extends CommonLog{
  var logPath=""
  case class Param(
                           app: File = new File(""),
                           conf: File = new File(""),
                           verbose: Boolean = false,
                           mode:String="",
                           sbt_params: Seq[String]=Seq(),
                           platform: String = "android",
                           appium:String = "http://127.0.0.1:4723/wd/hub/",
                           resultDir: String = "",
                           maxTime:Int = 0,
                           report:Boolean=false,
                           capability: Map[String, String] = Map[String, String]()
                         )
    def sbt(args: Seq[String]): Unit = {
      import scala.sys.process._
      //val sbt="/usr/local/Cellar/sbt/0.13.11/libexec/sbt-launch.jar"
      val project_dir=getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath.
        split("/").dropRight(2).mkString("/")
      val launcherJar = s"${project_dir}/lib/sbt-launch.jar"
      val cmd = Seq("java", "-jar", launcherJar)++args // You
      log.trace(cmd)
      cmd ! ProcessLogger(stdout append _ + "\n", stderr append _ + "\n")
    }


  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[Param]("appcrawler") {
      head(
        """
          |AppCrawler 1.5.0
          |app爬虫, 用于自动遍历测试. 支持Android和iOS, 支持真机和模拟器
          |移动测试技术交流 https://testerhome.com
          |感谢: 晓光 泉龙 杨榕 恒温 mikezhou
        """.stripMargin)
      opt[File]('a', "app") action { (x, c) =>{
        c.copy(app = x)
      }
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
      } text ("平台类型android或者ios, 默认会根据app后缀名自动判断")
      opt[Int]('t', "maxTime") action { (x, c) =>
        c.copy(maxTime = x)
      } text ("最大运行时间. 单位为秒. 超过此值会退出. 默认最长运行3个小时")
      opt[String]('u', "appium") action { (x, c) =>
        c.copy(appium = x)
      } text ("appium的url地址")
      opt[String]('o', "output") action { (x, c) =>
        c.copy(resultDir = x)
      } text ("遍历结果的保存目录. 里面会存放遍历生成的截图, 思维导图和日志")
      opt[Map[String, String]]("capability") valueName ("k1=v1,k2=v2...") action { (x, c) =>
        c.copy(capability = x)
      } text ("appium capability选项, 这个参数会覆盖-c指定的配置模板参数, 用于在模板配置之上的参数微调")
      opt[Unit]('r', "report") action { (_, c)=>
        c.copy(report = true)
      } text("输出html和xml报告")
      opt[Unit]("verbose").abbr("vv") action { (_, c) =>
        c.copy(verbose = true)
      } text ("是否展示更多debug信息")
      help("help") text (
        """
          |示例
          |appcrawler -a xueqiu.apk
          |appcrawler -a xueqiu.apk --capability noReset=true
          |appcrawler -c conf/xueqiu.json
          |appcrawler -c xueqiu.json --capability udid=[你的udid] -a Snowball.app
          |appcrawler -c xueqiu.json -a Snowball.app -u 4730
          |appcrawler -c xueqiu.json -a Snowball.app -u http://127.0.0.1:4730/wd/hub
          |appcrawler --report -o result/
        """.stripMargin)
    }
    // parser.parse returns Option[C]

    val args_new = if (args.length == 0) {
      Array("--help")
    } else {
      args
    }
    parser.parse(args_new, Param()) match {
      case Some(config) => {
        log.info(s"verbose=${config.verbose}")
        if(config.verbose){
          GA.logLevel=Level.TRACE
          log.info(s"set global log level to ${GA.logLevel}")
        }
        log.trace("config=")
        log.trace(config)
        if(config.sbt_params.nonEmpty){
          sbt(config.sbt_params)
          return()
        }
        var crawlerConf = new CrawlerConf
        //获取配置模板文件
        if (config.conf.isFile) {
          log.info(s"Find Conf ${config.conf.getAbsolutePath}")
          crawlerConf=crawlerConf.load(config.conf)
        }
        //判断平台
        crawlerConf.currentDriver = config.platform
        val fileName=config.app.getName
        if(fileName.matches(".*\\.apk$")){
          log.info("Set Platform=Android")
          crawlerConf.currentDriver = "Android"
        }
        if(fileName.matches(".*\\.ipa$") || fileName.matches(".*\\.app$") ){
          log.info("Set Platform=iOS")
          crawlerConf.currentDriver = "iOS"
        }

        //合并capability, 命令行>特定平台的capability>通用capability
        crawlerConf.currentDriver.toLowerCase match {
          case "android"=> {
            crawlerConf.capability=crawlerConf.capability++crawlerConf.androidCapability
          }
          case "ios" => {
            crawlerConf.capability=crawlerConf.capability++crawlerConf.iosCapability
          }
        }
        crawlerConf.capability ++= config.capability

        if(config.app.getName.nonEmpty) {
          //支持相对路径
          crawlerConf.capability ++= Map("app" -> config.app.getCanonicalPath)
          log.info(s"app path = ${crawlerConf.capability("app")}")
        }
        if(config.appium.matches("[0-9]+")){
          crawlerConf.capability++=Map("appium" -> s"http://127.0.0.1:${config.appium}/wd/hub")
        }else{
          crawlerConf.capability++=Map("appium" -> config.appium)
        }
        log.info(s"appium address = ${crawlerConf.capability.get("appium")}")

        if(config.maxTime>0){
          crawlerConf.maxTime=config.maxTime
        }
        crawlerConf.resultDir=config.resultDir

        //获得app设置
        //log.trace(s"app path=${config.app.getPath} ${config.app.getName} ${config.app.getAbsolutePath} ${config.app.getCanonicalPath}")
        log.trace("config =")
        log.trace(crawlerConf.toJson)
        new AppCrawlerTestCase().execute(configMap = ConfigMap("conf" -> crawlerConf), fullstacks = true)
      }
      case None => {}
    }
  }
}
