package com.ceshiren.appcrawler.plugin

import com.ceshiren.appcrawler.AppCrawler
import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.Log.log

/**
  * Created by seveniruby on 16/1/21.
  *
  * 如果某种类型的控件点击次数太多, 就跳过. 设定一个阈值
  */
class TagLimitPlugin extends Plugin {
  private val tagLimit = scala.collection.mutable.Map[String, Int]()
  private var tagLimitMax = 3
  private var currentKey = ""

  override def start(): Unit = {
    //log.addAppender(getCrawler().fileAppender)
    tagLimitMax = getCrawler().conf.tagLimitMax
  }

  //fixed: conf.tagLimit未生效
  override def fixElementAction(element: URIElement): Unit = {
    if (element.getAction.startsWith("_")) {
      //非普通元素点击事件，不需要统计，比如back backApp 等
      return
    }

    if (getCrawler().conf.backButton.map(_.xpath).contains(element.getXpath)) {
      return
    }

    //todo: //*[@resource='xxxx'][1]  genXPath=//sssss[@dddd=xxxx and ]
    //    if (getCrawler().conf.backButton.map(_.xpath).contains(element.getXpath)) {
    //      return
    //    }
    currentKey = getAncestor(element)
    if (!tagLimit.contains(currentKey)) {
      //应用定制化的规则
      getTimesFromTagLimit(element) match {
        case Some(v) => {
          tagLimit(currentKey) = v
          log.info(s"tagLimit[${currentKey}]=${tagLimit(currentKey)} with conf.tagLimit")
        }
        case None => tagLimit(currentKey) = tagLimitMax
      }
    }

    log.info(s"tagLimit[${currentKey}]=${tagLimit(currentKey)}")
    //如果达到限制次数就退出，小于0表示无限制
    if (currentKey.nonEmpty && tagLimit(currentKey) == 0) {
      //todo: 重构action名字的定义
      element.setAction("_skip")
      log.info(s"$element need skip")
    }
  }

  override def afterElementAction(element: URIElement): Unit = {
    if (element.getAction.startsWith("_")) {
      //非普通元素点击事件，不需要统计，比如back backApp 等
      return
    }
    //todo: 因为afterElement不一定在doElement后执行，所以这个地方可能会漏掉统计，刷新报错时会发生
    if (tagLimit.contains(currentKey)) {
      tagLimit(currentKey) -= 1
      log.info(s"tagLimit[${currentKey}]=${tagLimit(currentKey)}")
    } else {
      log.trace(s"not contains ${currentKey}")
    }
  }

  def getAncestor(element: URIElement): String = {
    getCrawler().currentUrl + element.getAncestor()
  }

  def getTimesFromTagLimit(element: URIElement): Option[Int] = {
    this.getCrawler().conf.tagLimit.foreach(tag => {
      log.trace(s"find tag with ${tag}")
      val elementMatchList = getCrawler().driver.getNodeListByKey(tag.getXPath()).map(x => AppCrawler.factory.generateElement(x, getCrawler().currentUrl))
      log.trace(elementMatchList.length)
      log.trace(element)
      if (elementMatchList.contains(element)) {
        log.debug(s"${tag.getXPath()} hit")
        return Some(tag.times)
      } else {
        None
      }
    })
    None
  }

}
