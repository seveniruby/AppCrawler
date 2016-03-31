/**
  * Created by seveniruby on 16/1/21.
  *
  * 如果某种类型的控件点击次数太多, 就跳过. 设定一个阈值
  */
class TagLimitPlugin extends Plugin{
  private val tagLimit: scala.collection.mutable.Map[String, Int] = scala.collection.mutable.Map()
  private val tagLimitMax=10
  def getUniqueKey(element: UrlElement): String ={
    s"${element.url}_${element.tag}_${element.id}_${element.name}"
  }
  override def beforeElementAction(element: UrlElement): Unit ={
    val key=getUniqueKey(element)
    if(!tagLimit.contains(key)){
      tagLimit(key)=tagLimitMax
    }
    if(tagLimit(key)<=0){
      getCrawler().setElementAction("skip")
      log.info(s"$element need skip")
    }
  }

  override def afterElementAction(element: UrlElement): Unit ={
    val key=getUniqueKey(element)
    tagLimit(key)-=1
    log.trace(s"tagLimit[${key}]=${tagLimit(key)}")
  }


  override def afterUrlRefresh(url:String): Unit ={

  }

}
