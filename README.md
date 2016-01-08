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
直接下载appcrawler解压即可

### 准备设备或者虚拟机
真机或者模拟器均可. 确保adb devices可以看到就行
### 使用默认的遍历规则遍历app.
可以使用打包好的工具, 不需要安装scala和sbt. 
<pre>
#传递配置文件路径即可
appcrawler xueqiu.conf
</pre>

### 配置文件定制化
通过修改配置文件. 可以实现细节的控制. 
Android和iOS的配置可参考conf目录下的配置文件. 如下是Android的定义  
<pre>
{
  "app":"http://build.snowballfinance.com/static/apps/com.xueqiu.droid.test/20160108_134204/xueqiu.apk",
  "appiumUrl":"http://127.0.0.1:4730/wd/hub",
  "defineUrl":"//*[contains(@resource-id, '_title')]",
  "baseUrl":".*Main.*",
  "maxDepth":3,
  "blackUrlList":[
    "StockMoreInfoActivity",
    "UserProfileActivity"
  ],
  "backButton":[
    
  ],
  "firstList":[
    "//android.widget.ListView//android.widget.TextView",
    "//android.widget.ListView//android.widget.Button"
  ],
  "selectedList":[
    "//*[@enabled='true' and @resource-id!='' and not(contains(name(), 'Layout'))]",
    "//*[@enabled='true' and @content-desc!='' and not(contains(name(), 'Layout'))]",
    "//android.widget.TextView[@enabled='true' and @clickable='true']",
    "//android.widget.ImageView[@clickable='true']",
    "//android.widget.ImageView[@enabled='true' and @clickable='true']"
  ],
  "lastList":[
    "//*[contains(@resource-id,'group_header_view')]//android.widget.TextView"
    
  ],
  "blackList":[
    "seveniruby", "message", "首页", "消息", "弹幕", "发射", "Photos","地址", "网址", "发送", "拉黑", "举报",
    "camera","Camera", "点评", "nav_icon_home", "评论", "发表讨论", "回复", "咨询", "分享", "转发", "comments", "comment",
    "stock_item_.*", ".*[0-9]{2}.*", "弹幕", "发送", "保存", "确定",
    "up", "user_profile_icon", "selectAll", "cut", "copy", "send", "买[0-9]*", "卖[0-9]*",
    "自选", "动态", "组合", "交易"
  ],
  "elementActions":[
    {
      "action":"click",
      "idOrName":"已有帐号？立即登录",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"登录",
      "times":0
    },
    {
      "action":"15600534760",
      "idOrName":"account",
      "times":0
    },
    {
      "action":"hys2xueqiu",
      "idOrName":"password",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"button_next",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"点此进入消息通知中心",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"点此访问个人主页进行应用设置",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"点此访问个人主页进行应用设置",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"不保存",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"确定",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"关闭",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"取消",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"Cancel",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"好",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"稍后再说",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"这里可以批量实盘买卖\n组合持仓股票",
      "times":0
    },
    {
      "action":"click",
      "idOrName":"tip_click_position",
      "times":0
    }
  ]
}
</pre>

# 设计理念
## 定义url
每个screen都有一个唯一的id, 控件的唯一性取决于这个url和控件自身的id name tag text属性.  
通过如下方法可以自定义这个url. 比如以标题作为url等.  
<pre>
defineUrl
</pre>
比如一个输入框id=input, 在多个页面中都出现了.
如果url为空, 那么它只会被点击一次. 
如果url设置为当前activiy的名字, 那么有多少页面包含它他就会被点击多少次.
android的url默认为当前的activity名字.  
iOS没有activity概念, 默认使用当前页面dom的md5值的后五位作为标记. 如果页面不变. 那么这个md5值也不会变.  
## 设定引导规则
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
base
</pre>

