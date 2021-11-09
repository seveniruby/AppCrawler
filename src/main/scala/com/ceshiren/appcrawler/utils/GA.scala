package com.ceshiren.appcrawler.utils

import com.brsanthu.googleanalytics.{GoogleAnalytics}

/**
  * Created by seveniruby on 16/2/26.
  */


object GA {
  val ga=GoogleAnalytics.builder().withTrackingId("UA-74406102-1").build()
  var appName = "default"

  def setAppName(app: String): Unit = {
    appName = app
  }

  def log(action: String): Unit = {
    ga.pageView(s"http://appcrawler.io/${appName}/${action}", "test").sendAsync()
  }

}
