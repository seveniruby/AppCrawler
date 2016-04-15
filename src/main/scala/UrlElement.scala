/**
  * Created by seveniruby on 15/12/18.
  */
case class UrlElement(url: String, tag: String, id: String, name: String, loc:String="") {
  //用来代表唯一的控件, 每个特定的命名控件只被点击一次. 所以这个element的构造决定了控件是否可被点击多次.
  //比如某个输入框被命名为url=xueqiu id=input, 那么就只能被点击一次
  //如果url修改为url=xueqiu/xxxActivity id=input 就可以被点击多次
  //定义url是遍历的关键. 这是一门艺术
  def toFileName(): String ={
    //url_[parent id]-tag-id
    s"${url}_${"\"([^/0-9][^\" =]*)\"".r.findAllMatchIn(loc).map(_.subgroups).toList.flatten.
      map(_.split("/").lastOption.getOrElse("")).mkString("-")}".take(200)
  }

  def toLoc(): String ={
    s"${url}\t${loc}\t${tag}\t${id}\t${name}"
  }

  def toTagPath(): String ={
    //相同url下的相同元素类型控制点击额度
    //s"${element.url}_${element.tag}_${element.loc}".replaceAll("@index=[^ ]*", "") //replaceAll("\\[[^\\[]*$", "")
    s"${url}-${"(/[a-zA-Z][a-zA-Z\\.]*)".r.findAllMatchIn(loc.replaceAll(":id/", "").replaceAll("android\\.[a-z]*\\.", "")).map(_.subgroups).toList.flatten.mkString("")}"
  }
}
