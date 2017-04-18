package com.xueqiu.qa.appcrawler

import org.scalatest.Sequential

/**
  * Created by seveniruby on 2017/4/17.
  */
class CrawlerSuite extends Sequential(
  new AutomationSuite
)