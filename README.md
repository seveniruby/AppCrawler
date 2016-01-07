# 自动遍历工具

# 为什么做这个工具
* 各大云市场上自动遍历功能也都是限制了时长,并且便利并不支持定制
* 降低边边角角的漏测问题
* 结合接口测试发现后台问题
* 发现深层次的布局问题. 通过新老版本的diff可以发现每个版本的变化和变动
* 解决monkey等工具太弱智不可控的缺点

# 设计目标
* 自动爬取加上规则引导(完成)
* 支持定制化, 可以自己设定遍历深度(完成)
* 支持插件化, 允许别人改造和增强(TODO)
* 支持滑动等更多动作(TODO)
* 支持自动截获接口请求(Doing)
* 支持新老版本的界面对比(Doing)

# 安装环境
### 安装appium
<pre>
npm install -g appium
</pre>

### 配置虚拟机
执行Android和iOS测试需要你自行配置好自己的各种虚拟机环境
### 使用默认的遍历规则遍历app.

可以使用打包好的工具, 不需要安装scala和sbt. 
<pre>
target/universal/stage/bin/traversal ios
target/universal/stage/bin/traversal android
</pre>

或者可以通过sbt直接运行

<pre>
#注意大小写区分
sbt "test-only iOS"
sbt "test-only Android"
#只跑android首页的case
sbt "test-only Android.首页"
</pre>
### 根据自己产品的需要编写定制化脚本
和接口测试一样, 编写scala的测试用例即可. 可以直接按照代码例子编写用例, 不需要安装scala的环境.   
测试用例演示

<pre>
test("Android"){
    val appium=new AndroidTraversal
    val android=appium.setupApp("http://qaci.snowballfinance.com/view/Snowball-Android/job/snowball-droid-rc/lastSuccessfulBuild/artifact/snowball/build/outputs/apk/xueqiu.apk",
      "http://127.0.0.1:4730/wd/hub")

    appium.rule("account", "15600534760")
    appium.rule("password", "xxxxx")
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
## 定义url
每个screen都有一个唯一的id, 控件的唯一性取决于这个url和控件自身的id name tag text属性.  
通过如下方法可以自定义这个url. 比如以标题作为url等.  
<pre>
    appium.urlXPath = "//*[contains(@resource-id, '_title')]"
</pre>
比如一个输入框id=input, 在多个页面中都出现了.
如果url为空, 那么它只会被点击一次. 
如果url设置为当前activiy的名字, 那么有多少页面包含它他就会被点击多少次.
android的url默认为当前的activity名字.  
iOS没有activity概念, 默认使用当前页面dom的md5值的后五位作为标记. 如果页面不变. 那么这个md5值也不会变.  
## 设定规则rule
遇到什么控件触发什么操作, 用来做引导输入  
rule方法有三个参数. 元素的id或者name属性.第二个参数为输入. "click"会执行点击操作. 其他都会被当成文本输入.  
第三个参数为这个规则被应用多少次. 默认是无限. 比如登录时的输入,可以设置为1次. 大部分情况默认即可.  
<pre>
rule("id", "click")
rule("text", "xxxxx", 1)
rule("id", "action", 10)
</pre>
## 后退标记back
默认是back键, 默认不需要设定.  
iOS上没有back键, 需要自己指定, 通过xpath定位方式指定遍历完所有控件应该点击什么控件返回. 
<pre>
    appium.back("//*[@name='nav_icon_back']")
</pre>
## 黑名单black
控件黑名单为black方法. 他会绕过id name或者text中包含特定关键词的控件.  
url黑名单可以绕过特定的activity或者window  

<pre>
  test("交易") {
    val t = setupAppium()
    t.black("tabhost", "tabs", "stock_item_.*", ".*message.*")
    t.blackUrlList.append("正文页", "个人首页", "动态", "消息")
    subTraversal(t, "交易")
  }
</pre>
## 遍历顺序控制first last
默认会遍历常见的按钮和带name或者id等重要属性的元素.  
可以自己通过first和last方法来控制先后被遍历的次序

<pre>
    //优先点击列表
    appium.first("//android.widget.ListView//android.widget.TextView")
    //其次是排序按钮
    appium.first("//android.widget.ListView//android.widget.Button")
    //最后是大类
    appium.last("//android.widget.TextView[@text='港股']")
    appium.last("//android.widget.TextView[@text='美股']")
    appium.last("//android.widget.TextView[@text='沪深']")
    appium.last("//android.widget.TextView[@text='持仓']")
</pre>

## 初始url和最大深度
<pre>
  test("首页 depth=2"){
    val t=setupAppium()
    t.maxDepth=2
    t.baseUrl=".*SNBHomeView.*"
    subTraversal(t, "首页")
  }

</pre>

