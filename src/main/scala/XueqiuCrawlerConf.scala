import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.json4s.native.Serialization._
import org.json4s.{DefaultFormats, FieldSerializer}

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by seveniruby on 16/1/6.
  */
class XueqiuCrawlerConf extends CrawlerConf {
  saveScreen = true
  currentDriver = "android"
  capability = Map[String, String](
    "platformName" -> "",
    "platformVersion" -> "",
    "deviceName" -> "",
    "noReset" -> "false",
    "autoWebview" -> "false",
    "autoLaunch" -> "true"
  )
  androidCapability = Map[String, String](
    "appPackage" -> "com.xueqiu.android",
    "appActivity" -> ".view.WelcomeActivityAlias",
    "appium" -> "http://127.0.0.1:4730/wd/hub",
    "app" -> "http://build.snowballfinance.com/static/apps/com.xueqiu.droid.rc/20160204_165054/xueqiu.apk"
  )
  iosCapability = Map[String, String](
    "bundleId" -> "",
    "autoAcceptAlerts" -> "true",
    "platformVersion" -> "9.2",
    "deviceName" -> "iPhone 6",
    "appium" -> "http://127.0.0.1:4723/wd/hub",
    "app" -> "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphoneos/Snowball.app"
    //"app" -> "/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphonesimulator/Snowball.app"
  )

  /** 用来确定url的元素定位xpath 他的text会被取出当作url因素 */
  defineUrl = List[String](
    "//*[contains(@resource-id, '_title')]",

    "//*[contains(@name, '_title')]"
  )

  /** 设置一个起始url和maxDepth, 用来在遍历时候指定初始状态和遍历深度 */
  baseUrl = List(
    ".*MainActivity",

    ".*SNBHomeView.*"
  )

  /** 默认的最大深度10, 结合baseUrl可很好的控制遍历的范围 */
  maxDepth = 6

  /** url黑名单.用于排除某些页面 */
  blackUrlList = ListBuffer(
    "StockMoreInfo.*",
    "StockDetail.*",
    "UserProfile.*",

    "消息",
    "MyselfUser",
    ".*消息.*",
    ".*MyselfUser.*",
    ".*股市直播.*",
    ".*UserVC.*",
    ".*正文页.*"
  )

  /** 后退按钮标记, 主要用于iOS, xpath */
  backButton = ListBuffer[String](
    "//*[@resource-id='action_back']",

    "//*[@name='nav_icon_back']",
    "//UIAButton[@name='取消']",
    "//UIAButton[@name='关闭']",
    "//UIAButton[@name='首页']"
  )

  /** 优先遍历元素 */
  firstList = ListBuffer[String](
    "//android.widget.ListView//android.widget.TextView",
    "//android.widget.ListView//android.widget.Button",

    "//UIAWindow[3]//UIAButton",
    "//UIAWindow[2]//UIAButton",
    "//UIAWindow[1]//UIATableView//UIATableCell[@name!='']",
    "//UIAWindow[1]//UIAStaticText//UIATableCell[@dom!='' and @name!='']"

  )

  /** 默认遍历列表 */
  selectedList = ListBuffer[String](
    "//*[@resource-id!='' and not(contains(name(), 'Layout'))]",
    "//*[@content-desc!='' and not(contains(name(), 'Layout'))]",
    "//android.widget.TextView[@clickable='true']",
    "//android.widget.ImageView[@clickable='true']",


    "//*[contains(name(), 'Text')]",
    "//*[contains(name(), 'Image')]",
    "//*[contains(name(), 'Button')]"
  )

  /** 最后遍历列表 */
  lastList = ListBuffer[String](
    "//*[contains(@resource-id,'group_header_view')]//android.widget.TextView"
  )

  //包括backButton
  //todo: 支持正则表达式
  /** 黑名单列表 matches风格, 默认排除内容是2个数字以上的控件. */
  blackList = ListBuffer[String](
    "message", "首页", "消息", "弹幕", "发射", "Photos", "地址", "网址", "发送", "拉黑", "举报",
    "camera", "Camera", "点评", "nav_icon_home", "评论", "回复", "咨询", "分享.*", "转发.*", "comments", "comment",
    "stock_item_.*", ".*[0-9]{2}.*", "弹幕", "发送", "保存", "确定",
    "up", "user_profile_icon", "selectAll", "cut", "copy", "send", "买[0-9]*", "卖[0-9]*",
    "聊天.*", "拍照.*", "发表.*", "回复.*", "加入.*", "赞助.*", "微博.*"
  )

  /** 引导规则. name, value, times三个元素组成 */
  elementActions = ListBuffer[scala.collection.mutable.Map[String, Any]]()
  elementActions += scala.collection.mutable.Map("idOrName" -> "登 录", "action" -> "click", "times" -> 2)
  elementActions += scala.collection.mutable.Map("idOrName" -> "登录", "action" -> "click", "times" -> 2)
  elementActions += scala.collection.mutable.Map("idOrName" -> "account", "action" -> "15600534760", "times" -> 1)
  elementActions += scala.collection.mutable.Map("idOrName" -> "password", "action" -> "hys2xueqiu", "times" -> 1)
  elementActions += scala.collection.mutable.Map("idOrName" -> "button_next", "action" -> "click", "times" -> 1)
  elementActions += scala.collection.mutable.Map("idOrName" -> "点此.*", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> "不保存", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> "确定", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> "关闭", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> "取消", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> "稍后再说", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> "这里可以.*", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> ".*搬到这里.*", "action" -> "click", "times" -> 0)
  elementActions += scala.collection.mutable.Map("idOrName" -> "tip_click_position", "action" -> "click", "times" -> 0)


  elementActions += scala.collection.mutable.Map("idOrName" -> "请输入手机号或邮箱登录", "action" -> "15600534760", "times" -> 1)
  elementActions += scala.collection.mutable.Map("idOrName" -> "密码", "action" -> "hys2xueqiu", "times" -> 1)
  elementActions += scala.collection.mutable.Map("idOrName" -> "common guide icon ok", "action" -> "click", "times" -> 0)


}
