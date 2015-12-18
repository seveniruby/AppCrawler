# 自动遍历工具

# 为什么做这个工具
* 各大云市场上自动遍历功能也都是限制了时长,并且便利并不支持定制
* 降低边边角角的漏测问题
* 结合接口测试发现后台问题
* 发现深层次的布局问题. 通过新老版本的diff可以发现每个版本的变化和变动
* 解决monkey等工具太弱智不可控的缺点

# 设计目标
* 自动爬取加上规则引导(完成)
* 支持定制化, 可以自己设定遍历深度(TODO)
* 支持插件化, 允许别人改造和增强(TODO)
* 支持自动输入和滑动(TODO)
* 支持自动截获接口请求(Doing)
* 支持新老版本的界面对比(TODO)

# 安装环境
* 安装appium

<pre>
npm install appium -g
</pre>
* 使用默认的遍历规则遍历app.

<pre>
#注意大小写区分
sbt "test-only iOS"
sbt "test-only Android"
</pre>
* 根据自己产品的需要编写定制化脚本
# 快速入门

<pre>
test("Android"){
    val appium=new AndroidTraversal
    val android=appium.setupApp("http://qaci.snowballfinance.com/view/Snowball-Android/job/snowball-droid-rc/lastSuccessfulBuild/artifact/snowball/build/outputs/apk/xueqiu.apk",
      "http://127.0.0.1:4730/wd/hub")

    appium.rule("account", "15600534760")
    appium.rule("password", "hys2xueqiu")
    appium.rule("button_next", "click")
    appium.rule("不保存", "click")
    appium.rule("点此进入消息通知中心", "click")
    appium.rule("点此访问个人主页进行应用设置", "click")
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click")
    appium.rule("取消", "click")
    appium.rule("关闭", "click")
    appium.rule("好", "click")

    appium.black("seveniruby", "message", "消息", "弹幕", "发射", "Photos","地址", "网址", "发送", "拉黑", "举报",
      "camera","Camera", "点评")
      
    appium.traversal()
  }
</pre>

# 设计理念
## 设定url getUrl
每个空间, 每个screen都有一个唯一的id, 可以自定义这个id, 这个会影响遍历
比如一个输入框id=input, 在多个页面中都出现了.
如果url为空, 那么它只会被点击一次. 
如果url设置为当前activiy的名字, 那么有多少页面包含它他就会被点击多少次.
## 设定规则rule
遇到什么控件触发什么操作, 用来做引导
rule("id", "action")
## 后退标记back
默认是back键. iOS上没有back键, 需要自己指定. back("nav-icon-back")
## 黑名单black
## 优先遍历first
