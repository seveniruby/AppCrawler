/**
  * Created by seveniruby on 16/1/8.
  */
class XueqiuCrawlerConf extends CrawlerConf{
  blackList++=List(
    "seveniruby", "message", "首页", "消息", "弹幕", "发射", "Photos","地址", "网址", "发送", "拉黑", "举报",
    "camera","Camera", "点评", "nav_icon_home", "评论", "发表讨论", "回复", "咨询", "分享", "转发", "comments", "comment",
    "stock_item_.*", ".*[0-9]{2}.*", "弹幕", "发送", "保存", "确定",
    "up", "user_profile_icon", "selectAll", "cut", "copy", "send", "买[0-9]*", "卖[0-9]*"
  )
  val clickRule=List("不保存", "确定", "关闭", "取消", "Cancel", "好", "稍后再说", "tip_click_position")
  clickRule.foreach(r=>elementActions += scala.collection.mutable.Map("idOrName"->r, "action"->"click"))
  blackList++=List("自选", "动态", "组合", "交易", "首页")

}
