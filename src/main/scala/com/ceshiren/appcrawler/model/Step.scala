package com.ceshiren.appcrawler.model

/**
  * 当given先决条件满足时，在when的find指定内容上执行action操作。并断言then里面的所有表达式
  *
  * @param given
  * @param when
  * @param then
  * @param xpath
  * @param action
  * @param actions
  * @param times
  */
case class Step(
                 given: List[String] = List[String](),
                 var when: When = null,
                 //todo: testcase和trigger 遍历都支持断言和报告输出
                 `then`: List[String] = List[String](),
                 xpath: String = "//*",
                 action: String = null,
                 actions: List[String] = List[String](),
                 var times: Int = -1
               ) {
  def use(): Int = {
    times -= 1
    times
  }

  //todo: selectedList也支持given结构
  def getGiven(): List[String] = {
    if (given == null) {
      List(getXPath())
    } else {
      given
    }
  }

  def getXPath(): String = {
    if (when == null) {
      if (this.xpath == null) {
        "/*"
      } else {
        this.xpath
      }
    } else {
      if (when.xpath == null) {
        "/*"
      } else {
        when.xpath
      }
    }
  }

  def getAction(): String = {
    //todo: 支持actions
    val result = if (when == null) {
      action
    } else {
      when.action
    }
    if (result == null) {
      ""
    } else {
      result
    }
  }
}
