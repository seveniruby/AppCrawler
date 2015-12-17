# 自动遍历工具

# 为什么做这个工具
* 目前市面上没有一个好用的自动遍历工具  
* 各大云市场上自动遍历功能也都是限制了时长,并且便利并不支持定制
* 自动遍历可以降低边边角角的漏测问题
* 自动遍历可以结合接口测试发现后台问题
* 自动遍历可以发现深层次的布局问题. 通过新老版本的diff可以发现每个版本的变化和变动
* 养个手工测试之外的自动遍历宠物辅助测试,解决monkey等工具太弱智不可控的缺点

# 设计目标
* 自动爬取
* 支持定制化, 可以自己设定遍历深度
* 支持插件化, 允许别人改造和增强
* 支持自动输入和滑动
* 支持自动截获接口请求
* 支持新老版本的界面对比

# 使用帮助

<pre>
  test("iOS自动遍历"){
    val appium=new IOSTraversal
    //val android=appium.setupIOS("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphoneos/Snowball.app")
    val android=appium.setupIOS("/Users/seveniruby/Library/Developer/Xcode/DerivedData/Snowball-ckpjegabufjxgxfeqyxgkmjuwmct/Build/Products/Debug-iphonesimulator/Snowball.app")

    appium.rule("请输入手机号或邮箱登录", "15600534760")
    appium.rule("密码", "hys2xueqiu")
    appium.rule("登 录", "click")
    appium.rule("不保存", "click")
    appium.rule("点此进入消息通知中心", "click")
    appium.rule("点此访问个人主页进行应用设置", "click")
    appium.rule("持仓盈亏搬到这里，改名模拟盈亏", "click")
    appium.rule("取消", "click")
    appium.rule("关闭", "click")
    appium.rule("分时", "click")
    appium.rule("5日", "click")
    appium.rule("日k", "click")
    appium.rule("月k", "click")
    appium.rule("好", "click")

    //appium.rule("自选", "click")
    appium.back("nav_icon_back")
    appium.black("message")
    appium.black("消息")
    appium.black("弹幕")
    appium.black("发射")
    appium.black("Photos")
    appium.black("地址")
    appium.black("网址")
    appium.black("发送")
    appium.black("拉黑")
    appium.black("举报")
    appium.black("camera")

    //把列表挨个点击一遍
    appium.first("//UIAWindow[1]//UIATableView//UIATableCell[@visible='true' and @enabled='true' and @valid='true' and @name!='']")
    //日k, 月k和三年等h5页面
    appium.first("//UIAWindow[1]//UIAStaticText//UIATableCell[@dom!='' and @enabled='true' and @valid='true' and @name!='']")

    //appium.rule("edit_text_name_cube", "ZuHe")
    appium.traversal()
    println("clcikedList=")
    println(appium.clickedList.mkString("\n"))
    println("elements=")
    println(appium.elements.mkString("\n"))

  }

</pre>

# 设计理念
## 设定url getUrl
每个空间, 每个screen都有一个唯一的id, 可以自己来定义这个id, 这个会影响遍历
## 设定规则rule
遇到什么控件触发什么操作, 用来做引导
## 后退标记back
默认是back键. 可以自己制定
## 黑名单black
## 优先遍历first
