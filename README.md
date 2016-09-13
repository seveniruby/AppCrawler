# AppCrawler
基于appium框架的一个自动遍历工具和app爬虫工具, 支持Android和iOS, 支持真机和模拟器

# 为什么做这个工具
- 各大云市场上自动遍历功能都是限制了时长, 企业无法自由定制.
- 解决monkey等工具可控性差的缺点
- 发现深层次的布局问题. 通过新老版本的diff可以发现每个版本的UI变动范围

# 设计目标
- 自动爬取加上规则引导(完成)
- 支持定制化, 可以自己设定遍历深度(完成)
- 支持插件化, 允许别人改造和增强(完成)
- 支持自动截获接口请求(完成)
- 支持新老版本的界面对比(Doing)
- 结合Frida (TODO)
- 支持云测服务 (Doing)

# 设计思想
- 自动遍历
- 类似ReactNative的虚拟dom机制
- Diff对比

# 安装依赖
### mac下安装appium
<pre>
brew install node
npm install -g appium
</pre>
真机或者模拟器均可. 确保adb devices可以看到就行
### 启动appium
启动appium
<pre>
appium --session-override
</pre>
### 启动appcrawler.
下载appcrawler工具, 解压. 只要有java即可  
### 快速遍历
<pre>
appcrawler --help
appcrawler -a xueqiu.apk
</pre>

### 输出结果
在当前目录下会生成一个包含输出结果的目录, 以时间命名. 包含了如下的测试结果  

- 每个步骤的截图
- 所有遍历过的控件组成的思维导图
- 一些点击和元素log
- 各种插件输出数据.比如har格式的接口数据等

# 设计理念
## 定义url
每个app的界面都会抽象一个唯一的id标记, 这样可以类比为普通的接口测试中的url.  
点击等动作抽象为触发一个接口请求. 界面内每个控件的布局抽象为接口的返回.  
这样普通的app测试就可以和接口测试一样具备可参考性了.  
android的url默认为当前的activity名字.  
iOS没有activity概念, 默认使用UIANavigationBar的label属性作为界面唯一标记.    
也可以自己指定某些特征作为url, 比如title或者某些关键控件的文本

控件的唯一性取决于这个url和控件自身的id name tag text loc等属性.  
比如一个输入框id=input, 在多个页面中都出现了.  
如果url为空, 那么它只会被点击一次. 
如果url设置为当前activiy的名字, 那么有多少页面包含它他就会被点击多少次.  

url的定义是一门艺术, 可以决定如何优雅的遍历.  
## 设定引导规则
遇到什么控件触发什么操作, 用来做引导输入, 是一种触发机制.    
rule方法有三个参数. 元素的id或者name属性.第二个参数为输入. "click"会执行点击操作. 其他都会被当成文本输入.  
第三个参数为这个规则被应用多少次. 默认是无限. 比如登录时的输入,可以设置为1次. 大部分情况默认即可.  
<pre>
  "elementActions":[
    {
      "action":"click",
      "xpath":"已有帐号？立即登录",
      "times":0
    },
    {
      "action":"click",
      "xpath":"登录",
      "times":0
    },
    {
      "action":"156005347XX",
      "xpath":"account",
      "times":0
    },
    {
      "action":"xxxxxxxx",
      "xpath":"password",
      "times":0
    }
  ]
</pre>
## 后退标记back
android默认是back键,不需要设定.  
iOS上没有back键, 需要自己指定, 通过xpath定位方式指定遍历完所有控件应该点击什么控件返回. 
<pre>
  "backButton":[
    "//*[@name='nav_icon_back']",
    "//UIAButton[@name='取消']",
    "//UIAButton[@name='Cancel']",
    "//UIAButton[@name='关闭']",
    "//*[@value='首页']",
    "//UIAButton[@name='首页']"
  ],
</pre>
## 黑名单black
控件黑名单为black方法. 他会绕过id name或者text中包含特定关键词的控件.  
url黑名单可以绕过特定的activity或者window  
<pre>
  "blackList" : [ "消息", "聊天室" ]
</pre>
## 遍历顺序控制
适用于在一些列表页或者tab页中精确的控制点击顺序  
selectedList表示默认要遍历的元素特征  
first表示优先遍历元素特征  
last表示最后应该遍历的元素特征  
统一使用XPath来表示  
<pre>
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
</pre>

## url控制
支持黑名单, 最大遍历深度, 和重命名url, 重新设定初始url  
<pre>
  "defineUrl":"//*[contains(@resource-id, '_title')]",
  "baseUrl":".*Main.*",
  "maxDepth":3,
  "blackUrlList":[
    "StockMoreInfoActivity",
    "StockDetailActivity",
    "UserProfileActivity"
  ],
</pre>

# How To Develop

- 安装scala
- 安装sbt
- 执行sbt stage打包

# 备注
此工具是由雪球测试团队开发. 用于快速的回归基本功能.  
开源的目的是希望这种自动遍历的独特设计思路可以加速自动遍历技术的应用, 并借助开源力量让这个工具发展的更快, 从而让原创团队也能收益.  
因为平时工作较忙, 暂不接受任何工具方面的咨询.  如有疑问请去testerhome测试技术社区交流. 