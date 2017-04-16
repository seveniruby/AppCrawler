package com.xueqiu.qa.appcrawler

import java.io.File
import java.lang.reflect.Field
import java.nio.charset.Charset

import com.xueqiu.qa.appcrawler.plugin.FlowDiff
import org.apache.log4j.Level
import org.scalatest.ConfigMap

import scala.io.Source

/**
  * Created by seveniruby on 16/4/24.
  */
object AppCrawler extends CommonLog {
  var logPath = ""
  var crawler = new Crawler
  val startTime = new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)

  case class Param(
                    app: File = new File(""),
                    conf: File = new File(""),
                    verbose: Boolean = false,
                    mode: String = "",
                    sbt_params: Seq[String] = Seq(),
                    platform: String = "",
                    appium: String = "",
                    resultDir: String = "",
                    maxTime: Int = 0,
                    report: String = "",
                    candidate: String = "",
                    master: String = "",
                    diff: Boolean = false,
                    template: String = "",
                    capability: Map[String, String] = Map[String, String]()
                  )

  def sbt(args: Seq[String]): Unit = {
    import scala.sys.process._
    //val sbt="/usr/local/Cellar/sbt/0.13.11/libexec/sbt-launch.jar"
    val project_dir = getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath.
      split("/").dropRight(2).mkString("/")
    val launcherJar = s"${project_dir}/lib/sbt-launch.jar"
    val cmd = Seq("java", "-jar", launcherJar) ++ args // You
    log.trace(cmd)
    cmd ! ProcessLogger(stdout append _ + "\n", stderr append _ + "\n")
  }


  def setGlobalEncoding(): Unit = {
    log.info("set file.encoding to UTF-8")
    System.setProperty("file.encoding", "UTF-8");

    val charset = classOf[Charset].getDeclaredField("defaultCharset")
    charset.setAccessible(true)
    charset.set(null, null)
    log.info("Default Charset=" + Charset.defaultCharset())
    log.info("file.encoding=" + System.getProperty("file.encoding"))
    log.info("Default Charset=" + Charset.defaultCharset())
    log.info("project directory=" + (new java.io.File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath)).getParentFile.getParentFile)

  }

  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[Param]("appcrawler") {
      head(
        """
          |AppCrawler 1.8.0
          |app爬虫, 用于自动遍历测试. 支持Android和iOS, 支持真机和模拟器
          |帮助文档: http://seveniruby.gitbooks.io/appcrawler
          |移动测试技术交流: https://testerhome.com
          |感谢: 晓光 泉龙 杨榕 恒温 mikezhou yaming116
        """.stripMargin)
      opt[File]('a', "app") action { (x, c) => {
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
      opt[String]('r', "report") action { (x, c) =>
        c.copy(report = x)
      } text ("输出html和xml报告")
      opt[String]("template") action { (x, c) =>
        c.copy(template = x)
      } text ("输出代码模板")
      opt[String]("master") action { (x, c) =>
        c.copy(master = x)
      } text ("master的diff.yml文件地址")
      opt[String]("candidate") action { (x, c) =>
        c.copy(candidate = x)
      } text ("candidate环境的diff.yml文件")
      opt[Unit]("diff") action { (x, c) =>
        c.copy(diff = true)
      } text ("执行diff对比")
      opt[Unit]("verbose").abbr("vv") action { (_, c) =>
        c.copy(verbose = true)
      } text ("是否展示更多debug信息")
      help("help") text (
        """
          |示例
          |appcrawler -a xueqiu.apk
          |appcrawler -a xueqiu.apk --capability noReset=true
          |appcrawler -c conf/xueqiu.json -p android -o result/
          |appcrawler -c xueqiu.json --capability udid=[你的udid] -a Snowball.app
          |appcrawler -c xueqiu.json -a Snowball.app -u 4730
          |appcrawler -c xueqiu.json -a Snowball.app -u http://127.0.0.1:4730/wd/hub
          |
          |#启动已经安装过的app
          |appcrawler --capability appPackage=com.xueqiu.android,appActivity=.welcomeActivity
          |
          |#从已经结束的结果中重新生成报告
          |appcrawler --report result/
          |
          |#新老版本对比
          |appcrawler --candidate result/ --master pre/ --report ./
          |
          |#自动生成Page Object代码模板文件
          |appcrawler --template PageObjectDemo.ssp --output result/
          |
          |#根据wda的inspector生成测试用例代码
          |appcrawler --template PageObjectDemo.ssp -u http://localhost:8100
          |
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
        if (config.verbose) {
          GA.logLevel = Level.TRACE
          log.info(s"verbose=${config.verbose}")
          log.info(s"set global log level to ${GA.logLevel}")
          RichData.initLog()
        }
        log.trace("config=")
        log.trace(config)
        if (config.sbt_params.nonEmpty) {
          sbt(config.sbt_params)
          return ()
        }
        var crawlerConf = new CrawlerConf
        //获取配置模板文件
        if (config.conf.isFile) {
          log.info(s"Find Conf ${config.conf.getAbsolutePath}")
          crawlerConf = crawlerConf.load(config.conf).get
        }

        //判断平台
        config.app.getName match {
          case androidApp if androidApp.matches(".*\\.apk$") => {
            crawlerConf.currentDriver = "Android"
          }
          case iosApp if iosApp.matches(".*\\.ipa$") || iosApp.matches(".*\\.app$") => {
            crawlerConf.currentDriver = "iOS"
          }
          case ios if config.platform.toLowerCase == "ios" =>
            crawlerConf.currentDriver = "iOS"
          case android if config.platform.toLowerCase == "android" =>
            crawlerConf.currentDriver = "Android"
          case _ =>
            log.warn("can not know what platform, will use default android, please use -p to set the platform")
        }
        log.info(s"Set Platform=${crawlerConf.currentDriver}")

        //合并capability, 命令行>特定平台的capability>通用capability
        crawlerConf.currentDriver.toLowerCase match {
          case "android" => {
            crawlerConf.capability ++= crawlerConf.androidCapability
          }
          case "ios" => {
            crawlerConf.capability ++= crawlerConf.iosCapability
          }
        }
        crawlerConf.capability ++= config.capability

        //设定app
        config.app match {
          case file if file.exists() => {
            //支持相对路径
            crawlerConf.capability ++= Map("app" -> config.app.getCanonicalPath)
          }
          case fileNotExist if fileNotExist.getPath.nonEmpty && fileNotExist.exists() == false && fileNotExist.getPath.contains(":/") == false => {
            log.warn(s"app not exist ${fileNotExist.getPath}")
            System.exit(1)
          }
          case network if network.getPath.contains(":/") => {
            //支持http:// https:// ftp:// file://
            crawlerConf.capability ++= Map("app" -> config.app.getPath.replace(":/", "://"))
          }
          case _ => {
            log.info("use app in the config file")
          }
        }
        log.info(s"app path = ${crawlerConf.capability("app")}")

        //设定appium的端口
        config.appium match {
          case port if port.matches("[0-9]+") =>
            crawlerConf.capability ++= Map("appium" -> s"http://127.0.0.1:${config.appium}/wd/hub")
          case url if url.contains(":/") =>
            crawlerConf.capability ++= Map("appium" -> config.appium)
          case _ => {
            if (!crawlerConf.capability.contains("appium")) {
              log.info("use default appium address 4723")
              crawlerConf.capability ++= Map("appium" -> s"http://127.0.0.1:4723/wd/hub")
            } else {
              log.info(s"use appium in the config file ${crawlerConf.capability("appium")}")
            }
          }
        }
        log.info(s"appium address = ${crawlerConf.capability.get("appium")}")

        if (config.maxTime > 0) {
          crawlerConf.maxTime = config.maxTime
        }

        config.resultDir match {
          case param if param.nonEmpty => crawlerConf.resultDir = param
          case conf if crawlerConf.resultDir.nonEmpty => log.info("use conf in config file")
          case _ =>
            crawlerConf.resultDir = s"${crawlerConf.currentDriver}_${startTime}"
        }
        log.info(s"result directory = ${crawlerConf.resultDir}")

        Report.showCancel = crawlerConf.showCancel
        if (crawlerConf.reportTitle.nonEmpty) {
          Report.title = crawlerConf.reportTitle
        }

        setGlobalEncoding()

        log.trace("yaml config")
        log.trace(DataObject.toYaml(crawlerConf))

        if (config.report != "" && config.candidate.isEmpty && config.template=="") {
          val store = Report.loadResult(s"${config.report}/elements.yml")
          Report.saveTestCase(store, config.report)

          Report.store=store
          crawler.conf=crawlerConf
          Report.runTestCase()
          return
        } else if (config.candidate.nonEmpty) {
          Report.candidate = config.candidate
          Report.master = config.master
          Report.reportDir = config.report
          Report.reportPath = config.report
          Report.testcaseDir = config.report+"/tmp/"
          DiffSuite.saveTestCase()
          Report.runTestCase()
          return
        }

        if(config.template!=""){
          val template=new Template
          if(config.appium.nonEmpty){
            template.getPageSource(config.appium)
          }else {
            template.read(s"${crawlerConf.resultDir}/elements.yml")
          }
          template.write(config.template, crawlerConf.resultDir+"/template/")
          return
        }

        startCrawl(crawlerConf)
      }
      case None => {}
    }
  }


  def startCrawl(conf: CrawlerConf): Unit = {
    conf.currentDriver.toLowerCase match {
      case "android" => {
        crawler = new AndroidCrawler
      }
      case "ios" => {
        crawler = new IOSCrawler
      }
      case _ => {
        log.info("请指定currentDriver为Android或者iOS")
      }
    }
    crawler.loadConf(conf)
    crawler.start()
  }
}
