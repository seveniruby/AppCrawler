package com.testerhome.appcrawler


case class ReactTestCase(name: String = "", steps: List[Step] = List[Step]())

//given表示多个条件满足 when表示执行多个动作，then表示多个断言，xpath和action为when的简写
/**
  * 当given先决条件满足时，在when的find指定内容上执行action操作。并断言then里面的所有表达式
  * @param given
  * @param when
  * @param then
  * @param find
  * @param action
  * @param actions
  * @param times
  */
case class Step(given: List[String]=List[String](),
                var when: When=null,
                //todo: testcase和trigger 遍历都支持断言和报告输出
                then: List[String]=List[String](),
                find: String="//*",
                action: String=null,
                actions: List[String]=List[String](),
                var times:Int = 0
               ){
  def use(): Int ={
    times-=1
    times
  }
  def getXPath(): String ={
    if(when==null){
      if(this.find==null){
        "/*"
      }else{
        this.find
      }
    }else{
      if(when.xpath==null){
        "/*"
      }else{
        when.xpath
      }
    }
  }

  def getAction(): String ={
    if(when==null){
      action
    }else{
      when.action
    }
  }
}
case class When(xpath: String=null,
                action: String=null,
                actions: List[String]=List[String]()
               )

