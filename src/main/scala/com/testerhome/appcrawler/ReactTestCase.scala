package com.testerhome.appcrawler


case class ReactTestCase(name: String = "", steps: List[Step] = List[Step]())

//given表示多个条件满足 when表示执行多个动作，then表示多个断言，xpath和action为when的简写
case class Step(given: List[String]=List[String](),
                var when: When=null,
                then: List[String]=List[String](),
                xpath: String="//*",
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
      if(this.xpath==null){
        "/*"
      }else{
        this.xpath
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

