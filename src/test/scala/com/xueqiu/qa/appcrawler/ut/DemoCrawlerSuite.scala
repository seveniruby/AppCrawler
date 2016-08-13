package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.CrawlerSuite
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/8/12.
  */
class DemoCrawlerSuite extends FunSuite{
  var name="自动遍历"
  override def suiteName=name
  1 to 10 foreach(i=>{
    test(s"xxx ${i}"){
      markup("<img src='/Users/seveniruby/projects/LBSRefresh/iOS_20160811165931/141_雪球-港股聚宝盆_老熊老雄.jpg'/>")
      assert(1==i)
    }
  })

}
