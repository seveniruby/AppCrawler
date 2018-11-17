package com.testerhome.appcrawler

import java.io.File
import java.lang.reflect.Field
import java.nio.charset.Charset

import com.testerhome.appcrawler.plugin.FlowDiff
import org.apache.commons.io.FileUtils
import org.apache.log4j.{FileAppender, Level}
import org.scalatest.ConfigMap

import scala.io.Source

/**
  * Created by seveniruby on 16/4/24.
  */
object AppCrawler extends CommonLog {
  val banner=
    """
      |----------------
      |AppCrawler 2.4.2 [霍格沃兹测试学院特别纪念版]
      |Appium 1.8.1 Java8 tested, author: seveniruby 思寒
      |app爬虫, 用于自动遍历测试. 支持Android和iOS, 支持真机和模拟器
      |项目地址: https://github.com/seveniruby/AppCrawler
      |移动测试技术交流: https://testerhome.com
      |视频教程: https://ke.qq.com/course/271076
      |维护者：seveniruby 霍格沃兹测试学院 北邮
      |致谢: 晓光 泉龙 杨榕 恒温 mikezhou yaming116 沐木
      |
      |--------------------------------
      |
    """.stripMargin

  var fileAppender: FileAppender = _
  var crawler = new Crawler
  val startTime = new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)
  case class Param(
                    app: String = "",
                    encoding: String="",
                    conf: File = new File(""),
                    verbose: Boolean = false,
                    mode: String = "",
                    platform: String = "",
                    appium: String = "",
                    resultDir: String = "",
                    maxTime: Int = 0,
                    report: String = "",
                    candidate: String = "",
                    master: String = "",
                    diff: Boolean = false,
                    template: String = "",
                    demo:Boolean=false,
                    capability: Map[String, String] = Map[String, String](),
                    yaml: String = ""

                  )


  def getGlobalEncoding(): Unit = {
    log.info("default Charset=" + Charset.defaultCharset())
    log.info("default file.encoding=" + System.getProperty("file.encoding"))
    //log.info("project directory=" + (new java.io.File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath)).getParentFile.getParentFile)
  }


  def setGlobalEncoding(encoding:String="UTF-8"): Unit = {
    getGlobalEncoding()
    log.info(s"set file.encoding to ${encoding}")
    System.setProperty("file.encoding", encoding)
    val charset = classOf[Charset].getDeclaredField("defaultCharset")
    charset.setAccessible(true)
    charset.set(null, null)
    getGlobalEncoding()
    log.trace("jar path =" + (new java.io.File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath)).getParentFile.getParentFile)
  }



  def main(args: Array[String]) {
    // parser.parse returns Opti on[C]
    val args_new = if (args.length == 0) {
      Array("--help")
    } else {
      log.info(banner)
      args
    }

    val parser=createParser()
    parseParams(parser, args_new)
    sys.exit()
  }

  def createParser(): scopt.OptionParser[Param] ={
    val parser = new scopt.OptionParser[Param]("appcrawler") {
      head(banner)
      opt[String]('a', "app") action { (x, c) => {
        c.copy(app = x)
      }
      } text ("Android或者iOS的文件地址, 可以是网络地址, 赋值给appium的app选项")

      opt[String]('e', "encoding") action { (x, c) => {
        c.copy(app = x)
      }
      } text ("set encoding, such as UTF-8 GBK")

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
      opt[String]('y', "yaml") action { (x, c) =>
        c.copy(yaml = x)
      } text ("代表配置的yaml语法，比如blackList: [ {xpath: action_night } ]，用于避免使用配置文件的情况")
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
      opt[Unit]('v',"verbose") action { (_, c) =>
        c.copy(verbose = true)
      } text ("是否展示更多debug信息")
      opt[Unit]("demo") action { (_, c) =>
        c.copy(demo = true)
      } text ("生成demo配置文件学习使用方法")
      help("help") text (
        """
          |示例
          |appcrawler -a xueqiu.apk
          |appcrawler -a xueqiu.apk --capability noReset=true
          |appcrawler -c conf/xueqiu.json -p android -o result/
          |appcrawler -c xueqiu.yaml --capability udid=[你的udid] -a Snowball.app
          |appcrawler -c xueqiu.yaml -a Snowball.app -u 4730
          |appcrawler -c xueqiu.yaml -a Snowball.app -u http://127.0.0.1:4730/wd/hub
          |
          |#生成demo配置文件到当前目录下的demo.yaml
          |appcrawler --demo
          |
          |#启动已经安装过的app
          |appcrawler --capability "appPackage=com.xueqiu.android,appActivity=.view.WelcomeActivityAlias"
          |
          |#从已经结束的结果中重新生成报告
          |appcrawler --report result/
          |
          |#新老版本对比
          |appcrawler --candidate result/ --master pre/ --report ./
          |
        """.stripMargin)
    }
    parser
  }

  def parseParams(parser: scopt.OptionParser[Param], args_new:Array[String]): Unit ={
    parser.parse(args_new, Param()) match {
      case Some(config) => {
        if (config.verbose) {
          GA.logLevel = Level.TRACE
          log.info(s"verbose=${config.verbose}")
          log.info(s"set global log level to ${GA.logLevel}")
          XPathUtil.initLog()
        }

        if(config.encoding.nonEmpty){
          setGlobalEncoding(config.encoding)
        }
        log.trace("config=")
        log.trace(config)
        var crawlerConf = new CrawlerConf
        //获取配置模板文件
        if (config.conf.isFile) {
          log.info(s"Find Conf ${config.conf.getAbsolutePath}")
          crawlerConf = crawlerConf.load(config.conf).get
        }else if(config.yaml.nonEmpty){
          crawlerConf = crawlerConf.loadYaml(config.yaml)
        }

        //合并capability, 命令行>特定平台的capability>通用capability
        crawlerConf.capability ++= config.capability
        //避免有人误删微信聊天记录
        if(crawlerConf.capability.contains("noReset")==false) {
          crawlerConf.capability ++= Map("noReset" -> "true")
        }

        //设定app
        if(config.app.nonEmpty){
          crawlerConf.capability ++=Map("app"-> parsePath(config.app).getOrElse(""))
        }
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

        if (config.maxTime > 0) {
          crawlerConf.maxTime = config.maxTime
        }


        config.resultDir match {
          case param if param.nonEmpty => crawlerConf.resultDir = param
          case conf if crawlerConf.resultDir.nonEmpty => log.info("use conf in config file")
          case _ =>
            crawlerConf.resultDir = s"${startTime}_${
              List(
                crawlerConf.capability.getOrElse("appPackage", "").toString,
                crawlerConf.capability.getOrElse("bundleId", "").toString,
                crawlerConf.capability.getOrElse("app", "").toString.split(File.separator).last,
                crawlerConf.capability.getOrElse("browserName", "").toString
              ).filter(_.nonEmpty).headOption.getOrElse("")
            }"
        }
        log.info(s"result directory = ${crawlerConf.resultDir}")

        Report.showCancel = crawlerConf.showCancel
        if (crawlerConf.reportTitle.nonEmpty) {
          Report.title = crawlerConf.reportTitle
        }

        log.trace("yaml config")
        log.trace(TData.toYaml(crawlerConf))

        //todo: 用switch替代
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

        //生成demo示例文件
        if(config.demo){
          val file=scala.reflect.io.File("demo.yml")
          crawlerConf.resultDir=""
          file.writeAll(crawlerConf.toYaml())
          log.info(s"you can read ${file.jfile.getCanonicalPath} for demo")
          return
        }

        addLogFile(crawlerConf)
        getGlobalEncoding()
        startCrawl(crawlerConf)

      }
      case None => {}
    }
  }

  def parsePath(app: String): Option[String] ={
    val appFile=new File(app)
    app match {
      case file if appFile.exists() => {
        //支持相对路径
        Some(appFile.getCanonicalPath)
      }
      case url if List("http", "ftp", "https", "file").contains(url.split(':').head.toLowerCase) => {
        //支持http:// https:// ftp:// file://
        Some(app)
      }
      case fileNotExist if fileNotExist.nonEmpty => {
        log.warn(s"app not exist ${appFile.getCanonicalPath}")
        System.exit(1)
        None
      }
      case _ => {
        log.info("use app in the config file")
        None
      }
    }
  }
  def startCrawl(conf: CrawlerConf): Unit = {
    crawler = new Crawler
    crawler.loadConf(conf)
    crawler.start()
  }


  //todo: 让其他的文件也支持log输出到文件
  def addLogFile(conf: CrawlerConf): Unit = {
    fileAppender = new FileAppender(layout,  conf.resultDir + "/appcrawler.log", false)
    log.addAppender(fileAppender)
    log.info(banner)

    val resultDir = new java.io.File(conf.resultDir)
    if (!resultDir.exists()) {
      FileUtils.forceMkdir(resultDir)
      log.info("result dir path = " + resultDir.getAbsolutePath)
    }

  }
}
