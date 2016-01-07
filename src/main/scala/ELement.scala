/**
  * Created by seveniruby on 15/12/18.
  */
case class Element(url: String, tag: String, id: String, name: String) {
  //用来代表唯一的控件, 每个特定的命名控件只被点击一次. 所以这个element的构造决定了控件是否可被点击多次.
  //比如某个输入框被命名为url=xueqiu id=input, 那么就只能被点击一次
  //如果url修改为url=xueqiu/xxxActivity id=input 就可以被点击多次
  //定义url是遍历的关键. 这是一门艺术
  override def toString(): String = {
    if (tag.toLowerCase().contains("edit")) {
      s"${url}-${tag}-${id}-"
    } else {
      s"${url}-${tag}-${id}-${name}"
    }
  }
}
