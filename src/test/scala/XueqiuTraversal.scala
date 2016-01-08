import org.scalatest.FunSuite

/**
  * Created by seveniruby on 15/12/19.
  */
abstract class XueqiuTraversal extends FunSuite{
  val blackList=List(
    "seveniruby", "message", "首页", "消息", "弹幕", "发射", "Photos","地址", "网址", "发送", "拉黑", "举报",
    "camera","Camera", "点评", "nav_icon_home", "评论", "发表讨论", "回复", "咨询", "分享", "转发", "comments", "comment",
    "stock_item_.*", ".*[0-9]{2}.*", "弹幕", "发送", "保存", "确定",
    "up", "user_profile_icon", "selectAll", "cut", "copy", "send", "买[0-9]*", "卖[0-9]*"
  )
  val clickRule=List("不保存", "确定", "关闭", "取消", "Cancel", "好", "稍后再说", "tip_click_position")
  val tabs=List("自选", "动态", "组合", "交易", "首页")
  // "分时", "五日", "日K", "周K", "月K"
  def subTraversal(appium: Crawler, tab:String): Unit ={
    println(s"enter ${tab}")
    appium.rule(tab, "click", 1)
    clickRule.foreach(appium.rule(_, "click"))
    blackList.foreach(appium.black(_))
    tabs.dropWhile(_==tab).foreach(appium.black(_))
    //appium.traversal()
    appium.start()
    println(s"finish ${tab}")
  }

  def setupAppium(): Crawler ={
    return null
  }


}
