# appcrawler

Appcrawler是一个基于自动遍历的App爬虫工具，支持Android和IOS，支持真机和模拟器。最大的特点是灵活性高，可通过配置来设定遍历的规则。

## quick start

```bash
-------------------------------------------------
appcrawler 全平台自动遍历测试工具
Q&A: https://ceshiren.com/c/opensource/appcrawler
author: seveniruby
-------------------------------------------------


Usage: appcrawler [options]

  -a, --app <value>        Android或者iOS的文件地址, 可以是网络地址, 赋值给appium的app选项
  -e, --encoding <value>   set encoding, such as UTF-8 GBK
  -c, --conf <value>       配置文件地址
  -p, --platform <value>   平台类型android或者ios, 默认会根据app后缀名自动判断
  -t, --maxTime <value>    最大运行时间. 单位为秒. 超过此值会退出. 默认最长运行3个小时
  -u, --appium <value>     appium的url地址
  -o, --output <value>     遍历结果的保存目录. 里面会存放遍历生成的截图, 思维导图和日志
  --capability k1=v1,k2=v2...
                           appium capability选项, 这个参数会覆盖-c指定的配置模板参数, 用于在模板配置之上的参数微调
  -y, --yaml <value>       代表配置的yaml语法，比如blackList: [ {xpath: action_night } ]，用于避免使用配置文件的情况
  -r, --report <value>     输出html和xml报告
  --template <value>       输出代码模板
  --master <value>         master的diff.yml文件地址
  --candidate <value>      candidate环境的diff.yml文件
  -v, --verbose-debug      是否展示更多debug信息
  -vv, --verbose-trace     是否展示更多trace信息
  --demo                   生成demo配置文件学习使用方法
  --help
                           示例
                           appcrawler -a xueqiu.apk
                           appcrawler -a xueqiu.apk --capability noReset=true
                           appcrawler -c conf/xueqiu.json -p android -o result/
                           appcrawler -c xueqiu.yaml --capability udid=[你的udid] -a Snowball.app
                           appcrawler -c xueqiu.yaml -a Snowball.app -u 4730
                           appcrawler -c xueqiu.yaml -a Snowball.app -u http://127.0.0.1:4730/wd/hub

                           #生成demo配置文件到当前目录下的demo.yaml
                           appcrawler --demo

                           #启动已经安装过的app
                           appcrawler --capability "appPackage=com.xueqiu.android,appActivity=.view.WelcomeActivityAlias"

                           #使用yaml参数
                           appcrawler -a xueqiu.apk -y "blackList: [ {xpath: action_night}, {xpath: '.*[0-9\\.]{2}.*'} ]"

                           #从已经结束的结果中重新生成报告
                           appcrawler --report result/

```

## 配置文件格式

### 执行参数与配置文件

- capability设置：与appium完全一致
- testcase：用于启动app后的基础测试用例
- selectedList：遍历范围设定
- triggerActions：特定条件触发执行动作的设置
- selectedList：需要被遍历的元素范围
- firstList：优先被点击
- lastList：最后被点击
- tagLimitMax：同祖先（同类型）的元素最多点击多少次
- backButton：当所有元素都被点击后默认后退控件定位
- blackList：黑名单
- maxDepth: 遍历的最大深度

### 配置的最小单元 测试用例模型

testcase的完整形态

- given：所有的先决条件
- when：先决条件成立后的行为
- then：断言集合

testcase的简写形态

- xpath：对应when里的xpath
- action：对应when的action

执行参数比配置文件优先级别高

- given 前提条件
- when 执行动作
- then 写断言

简写形态

- xpath xpath支持xpath表达式、正则、包含
- action 支持

### xpath定义

- xpath
    - //*[@resource-id=‘xxxx’]
    - //*[contains(@text, ‘密码’)]
- 正则
    - ^确定$
    - ^.*输入密码
- 包含
    - 密码
    - 输入
    - 请

### action定义

- "" 只是截图记录
- back 后退
- backApp 回退到当前的app 默认等价于back行为 可定制
- monkey 随机事件
- click
- longTap
- xxx() 执行scala或者java代码
    - Thread.sleep(3000)
    - driver.swipe(0.9, 0.5, 0.1, 0.5)
- 非以上所有行为是输入 xx ddd

### 完整配置文件

```yaml
---
maxTimeDescription: "最大运行时间"
maxTime: 10800
maxDepthDescription: "默认的最大深度10, 结合baseUrl可很好的控制遍历的范围"
maxDepth: 10
capabilityDescription: "appium的capability通用配置，其中automationName代表自动化的驱动引擎，除了支持appium的\
  所有引擎外，额外增加了adb和selenium的支持"
capability:
  appActivity: ".ApiDemos"
  appium: "http://127.0.0.1:4723/wd/hub"
  noReset: "true"
  appPackage: "io.appium.android.apis"
  fullReset: "false"
testcaseDescription: "测试用例设置，用于遍历开始之前的一些前置操作，比如自动登录"
testcase:
  name: "AppCrawler TestCase"
  steps:
    - given: []
      when: null
      then: []
      xpath: "/*/*"
      action: "Thread.sleep(1000)"
      actions: []
      times: -1
triggerActionsDescription: "在遍历过程中需要随时处理的一些操作，比如弹框、登录等"
triggerActions:
  - given: []
    when: null
    then: []
    xpath: "permission_allow_button"
    action: ""
    actions: []
    times: 3
  - given: []
    when: null
    then: []
    xpath: "允许"
    action: ""
    actions: []
    times: 3
selectedListDescription: "默认遍历列表，只有出现在这个列表里的控件范围才会被遍历"
selectedList:
  - given: []
    when: null
    then: []
    xpath: "//*[contains(name(), 'Button')]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[contains(name(), 'Text') and @clickable='true' and string-length(@text)<10]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[@clickable='true']//*[contains(name(), 'Text') and string-length(@text)<10]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[contains(name(), 'Image') and @clickable='true']"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[@clickable='true']/*[contains(name(), 'Image')]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[contains(name(), 'Image') and @name!='']"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[contains(name(), 'Text') and @name!='' and string-length(@label)<10]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//a"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[contains(@class, 'Text') and @clickable='true' and string-length(@text)<10]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[@clickable='true']//*[contains(@class, 'Text') and string-length(@text)<10]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[contains(@class, 'Image') and @clickable='true']"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[@clickable='true']/*[contains(@class, 'Image')]"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[@clickable='true' and contains(@class, 'Button')]"
    action: ""
    actions: []
    times: -1
blackListDescription: "黑名单列表 matches风格, 默认排除内容包含2个数字的控件"
blackList:
  - given: []
    when: null
    then: []
    xpath: ".*[0-9]{2}.*"
    action: ""
    actions: []
    times: -1
firstListDescription: "优先遍历列表，同时出现在selectedList与firstList中的控件会被优先遍历"
firstList: []
lastListDescription: "最后遍历列表，同时出现在selectedList与lastList中的控件会被最后遍历"
lastList:
  - given: []
    when: null
    then: []
    xpath: "//*[@selected='true']/..//*"
    action: ""
    actions: []
    times: -1
  - given: []
    when: null
    then: []
    xpath: "//*[@selected='true']/../..//*"
    action: ""
    actions: []
    times: -1
backButtonDescription: "后退按钮列表，默认在所有控件遍历完成后，才会最后点击后退按钮。目前具备了自动判断返回按钮的能力，默认不需要配置"
backButton:
  - given: []
    when: null
    then: []
    xpath: "Navigate up"
    action: ""
    actions: []
    times: -1
xpathAttributesDescription: "在生成一个控件的唯一定位符中应该包含的关键属性"
xpathAttributes:
  - "name()"
  - "name"
  - "label"
  - "value"
  - "resource-id"
  - "content-desc"
  - "text"
  - "id"
  - "name"
  - "innerText"
  - "tag"
  - "class"
sortByAttributeDescription: "陆续根据属性进行遍历排序微调，depth表示从dom中最深层的控件开始遍历，list表示dom中列表优先，\
  selected表示菜单最后遍历，这是默认规则，一般不需要改变"
sortByAttribute:
  - "depth"
  - "list"
  - "selected"
findByDescription: "默认生成控件唯一定位符的表达式风格，可选项 default|android|id|xpath，默认会自动判断是否使用android定\
  位或者ios定位"
findBy: "xpath"
baseUrlDescription: "设置一个起始点，从这个起始点开始计算深度，比如默认从登录后的界面开始计算"
baseUrl: []
appWhiteListDescription: "app白名单，允许在这些app里进行遍历"
appWhiteList: []
urlBlackListDescription: "url黑名单，用于排除某些页面的遍历"
urlBlackList: []
urlWhiteListDescription: "url白名单，仅在这些界面内遍历"
urlWhiteList: []
beforeStartWaitDescription: "启动一个app默认等待的时间"
beforeStartWait: 6000
beforeRestart: []
beforeElementDescription: "在遍历每个控件之前默认执行的动作"
beforeElement: []
afterElementDescription: "在遍历每个控件之后默认执行的动作"
afterElement: []
afterElementWaitDescription: "在遍历每个控件之后默认等待的时间，用于等待新页面加载"
afterElementWait: 500
afterAllDescription: "在遍历完当前页面内的所有控件后，是否需要刷新或者滑动"
afterAll: []
afterAllMaxDescription: "afterAll的最大重试次数，比如连续滑动2次都没新元素即取消"
afterAllMax: 2
tagLimitMaxDescription: "相似控件最多点击几次"
tagLimitMax: 2
tagLimitDescription: "设置部分相似控件的最大遍历次数"
tagLimit:
  - given: []
    when: null
    then: []
    xpath: "确定"
    action: ""
    actions: []
    times: 1000
  - given: []
    when: null
    then: []
    xpath: "取消"
    action: ""
    actions: []
    times: 1000
  - given: []
    when: null
    then: []
    xpath: "share_comment_guide_btn_name"
    action: ""
    actions: []
    times: 1000
assertGlobalDescription: "全局断言"
assertGlobal: []
suiteNameDescription: "报告中的测试套件名字可以由列表内的控件内容替换，增强报告中关键界面的辨识度"
suiteName:
  - "//*[@selected='true']//android.widget.TextView/@text"
screenshotDescription: "是否截图"
screenshot: true
reportTitleDescription: "报告的title"
reportTitle: "AppCrawler"
resultDirDescription: "结果目录，如果为空会自动创建对应时间戳_报名的结果目录"
resultDir: ""
showCancelDescription: "是否展示跳过的控件记录"
showCancel: true
pluginListDescription: "插件列表，暂时禁用，太高级了，很多人不会用"
Description: "。在selectedList firstList lastList等很多配置中，需要填充的是测试步骤Step类型。Step类型由given（\
  满足条件）when（条件满足的行为）then（断言）三部分组成。Step可以简化为xpath（定位表达式，支持xpath 正则 包含关系）与action（点击\
  \ 输入等行为）。"
pluginList: []
```

## 金牌赞助商（Gold Sponsor）

![logo霍格沃兹测试学院](https://ceshiren.com/uploads/default/original/3X/a/2/a270cdc0bf8cb41110ce5999d5278515a0e7cf0f.png)

[霍格沃兹测试开发学社](https://ceshiren.com/)
是 [测吧（北京）科技有限公司](http://qrcode.testing-studio.com/f?from=appcrawler&url=https://ceshiren.com/t/topic/14814)
旗下业界领先的测试开发技术高端教育品牌。 学院课程均由名企一线测试大牛设计，提供实战驱动的系列课程。涵盖移动app自动化测试、接口自动化测试、性能测试、持续集成/持续交付/DevOps 、测试左移、测试右移、测试管理等课程。
[点击学习!](http://qrcode.testing-studio.com/f?from=appcrawler&url=https://testerh.ke.qq.com?flowToken=1040391)

[测吧（北京）科技有限公司](http://qrcode.testing-studio.com/f?from=appcrawler&url=https://ceshiren.com/t/topic/14814)
是一家服务于测试领域的高科技公司，为企业提供全方位的自动化测试技术支持、测试平台开发定制、测试效能提升等咨询与科研合作服务。 先后服务于华为、工信部、信通院等知名企业与机构。

## 编译

```bash
mvn clean package -DskipTests
```

## 技术交流

由霍格沃兹测试开发学社维护，技术交流与issue提交请移步 https://ceshiren.com/c/opensource/appcrawler/ 交流
