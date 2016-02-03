/**
  * Created by seveniruby on 16/1/8.
  */
class XueqiuIOSCrawlerConf extends XueqiuCrawlerConf {
  defineUrl = "//*[contains(@resource-id, '_title')]"
  baseUrl = "MainActivity"
  maxDepth = 3
  blackUrlList ++= List(
    "StockMoreInfoActivity",
    "UserProfileActivity"
  )
  firstList ++= List(
    "//android.widget.ListView//android.widget.TextView",
    "//android.widget.ListView//android.widget.Button"
  )
  selectedList ++= List(
    "//*[@enabled='true' and @resource-id!='' and not(contains(name(), 'Layout'))]",
    "//*[@enabled='true' and @content-desc!='' and not(contains(name(), 'Layout'))]",
    "//android.widget.TextView[@enabled='true' and @clickable='true']",
    "//android.widget.ImageView[@clickable='true']",
    "//android.widget.ImageView[@enabled='true' and @clickable='true']"
  )
  lastList ++= List(
    "//*[contains(@resource-id,'group_header_view')]//android.widget.TextView"
  )
  blackList ++= List(
    "seveniruby", "message", "首页", "消息", "弹幕", "发射", "Photos", "地址", "网址", "发送", "拉黑", "举报",
    "camera", "Camera", "点评", "nav_icon_home", "评论", "发表讨论", "回复", "咨询", "分享", "转发", "comments", "comment",
    "stock_item_.*", ".*[0-9]{2}.*", "弹幕", "发送", "保存", "确定",
    "up", "user_profile_icon", "selectAll", "cut", "copy", "send", "买[0-9]*", "卖[0-9]*",
    "自选", "动态", "组合", "交易"
  )

  List("已有帐号？立即登录", "登录").foreach(e => {
    elementActions ++= List(scala.collection.mutable.Map("action" -> "click", "idOrName" -> e, "times" -> 0))
  })
  List(
    Map("name"->"account", "action"->"15600534760"),
      Map("name"->"password", "action"->"hys2xueqiu")
  ).foreach(e => {
    elementActions ++= List(scala.collection.mutable.Map("action" ->e("action"), "idOrName" ->e("name") , "times" -> 0))
  })
  List("button_next", "tip_click_position", "不保存", "确定", "关闭", "取消", "Cancel", "好", "稍后再说").foreach(e => {
    elementActions ++= List(scala.collection.mutable.Map("action" -> "click", "idOrName" -> e, "times" -> 0))
  })


}
