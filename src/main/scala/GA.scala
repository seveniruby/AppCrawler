import com.brsanthu.googleanalytics.{GoogleAnalytics, PageViewHit}
import org.apache.log4j.{Level, Logger, BasicConfigurator}

/**
  * Created by seveniruby on 16/2/26.
  */


object GA {
  BasicConfigurator.configure()
  Logger.getRootLogger.setLevel(Level.OFF)
  val ga = new GoogleAnalytics("UA-74406102-1")
  var appName="default"
  def setAppName(app:String): Unit ={
    appName=app
  }
  def log(action: String): Unit ={
    ga.postAsync(new PageViewHit(s"http://appcrawler.io/${appName}/${action}", "test"))
  }

}

/*

package io.gatling.commons.util

import java.net.{ URL, URLEncoder }
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID
import javax.net.ssl.HttpsURLConnection

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Properties._
import scala.util.Success

import io.gatling.commons.util.Io._

object Ga {

  def send(version: String): Unit = {
    import ExecutionContext.Implicits.global

    val whenConnected = Future {
      val url = new URL("https://ssl.google-analytics.com/collect")
      url.openConnection().asInstanceOf[HttpsURLConnection]
    }

    whenConnected.map { conn =>
      conn.connect()
      conn.setReadTimeout(1000)
      conn.setConnectTimeout(1000)
      conn.setRequestMethod("POST")
      conn.setRequestProperty("Connection", "Close")
      conn.setRequestProperty("User-Agent", s"java/$javaVersion")
      conn.setUseCaches(false)

      withCloseable(conn.getOutputStream) { os =>

        val trackingId = if (version.endsWith("SNAPSHOT")) "UA-53375088-4" else "UA-53375088-5"

        def encode(string: String) = URLEncoder.encode(string, UTF_8.name)

        val body =
          s"""tid=$trackingId&
              |dl=${encode("http://gatling.io/" + version)}&
              |de=UTF-8}&
              |ul=en-US}&
              |t=pageview&
              |dt=${encode(version)}&
              |cid=${encode(UUID.randomUUID.toString)}""".stripMargin

        os.write(body.getBytes(UTF_8))
        os.flush()
      }
      conn
    }.recoverWith {
      case _ => whenConnected
    }.onComplete {
      case Success(conn) => conn.disconnect()
      case _             =>
    }
  }
}
*/
