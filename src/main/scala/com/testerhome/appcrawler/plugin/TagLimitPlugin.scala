package com.testerhome.appcrawler.plugin

import com.testerhome.appcrawler.AppCrawler
import com.testerhome.appcrawler.data.AbstractElement

import scala.collection.JavaConverters

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
  override def fixElementAction(element: AbstractElement): Unit = {
    if (element.getAction.startsWith("_")) {
      //非普通元素点击事件，不需要统计，比如back backApp 等
      return
    }

    if (element.getAction.equals("click_guess")) {
      // 用户自定义以及预测的返回键也不需要统计，并修改action
      element.setAction("click")
      return
    }

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

      //跳过具备selected=true的菜单栏
      getCrawler().driver.getNodeListByKey("//*[@selected='true']").foreach(m => {
        val selectedElement = getCrawler().getUrlElementByMap(m)
        val selectedKey = getAncestor(selectedElement)
        tagLimit(selectedKey) = 20
        log.info(s"tagLimit[${selectedKey}]=20")
      })
    }

    log.info(s"tagLimit[${currentKey}]=${tagLimit(currentKey)}")
    //如果达到限制次数就退出
    if (currentKey.nonEmpty && tagLimit(currentKey) <= 0) {
      element.setAction("_skip")
      log.info(s"$element need skip")
    }
  }

  override def afterElementAction(element: AbstractElement): Unit = {
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

  def getAncestor(element: AbstractElement): String = {
    getCrawler().currentUrl + element.getAncestor()
  }

  def getTimesFromTagLimit(element: AbstractElement): Option[Int] = {
    this.getCrawler().conf.tagLimit.foreach(tag => {
      if (getCrawler().driver.getNodeListByKey(tag.getXPath()).map(x => AppCrawler.factory.generateElement(JavaConverters.mapAsJavaMap(x),getCrawler().currentUrl))
        .contains(element)) {
        return Some(tag.times)
      } else {
        None
      }
    })
    None
  }

}
