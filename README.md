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
screenshot: true
reportTitle: ""
resultDir: ""
showCancel: true
maxTime: 10800
maxDepth: 10
capability:
  noReset: "true"
  fullReset: "false"
  appium: "http://127.0.0.1:4723/wd/hub"
testcase:
  name: "ceshiren AppCrawler"
  steps:
    - given: [ ]
      when: null
      then: [ ]
      xpath: "/*/*"
      action: "Thread.sleep(1000)"
      actions: [ ]
      times: -1
triggerActions:
  - given: [ ]
    when: null
    then: [ ]
    xpath: "permission_allow_button"
    action: ""
    actions: [ ]
    times: 3
  - given: [ ]
    when: null
    then: [ ]
    xpath: "允许"
    action: ""
    actions: [ ]
    times: 3
selectedList:
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[contains(name(), 'Button')]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[contains(name(), 'Text') and @clickable='true' and string-length(@text)<10]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[@clickable='true']//*[contains(name(), 'Text') and string-length(@text)<10]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[contains(name(), 'Image') and @clickable='true']"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[@clickable='true']/*[contains(name(), 'Image')]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[contains(name(), 'Image') and @name!='']"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[contains(name(), 'Text') and @name!='' and string-length(@label)<10]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//a"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[contains(@class, 'Text') and @clickable='true' and string-length(@text)<10]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[@clickable='true']//*[contains(@class, 'Text') and string-length(@text)<10]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[contains(@class, 'Image') and @clickable='true']"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[@clickable='true']/*[contains(@class, 'Image')]"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[@clickable='true' and contains(@class, 'Button')]"
    action: ""
    actions: [ ]
    times: -1
firstList: [ ]
lastList:
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[@selected='true']/..//*"
    action: ""
    actions: [ ]
    times: -1
  - given: [ ]
    when: null
    then: [ ]
    xpath: "//*[@selected='true']/../..//*"
    action: ""
    actions: [ ]
    times: -1
backButton:
  - given: [ ]
    when: null
    then: [ ]
    xpath: "Navigate up"
    action: ""
    actions: [ ]
    times: -1
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
sortByAttribute:
  - "depth"
  - "list"
  - "selected"
findBy: "xpath"
suiteName:
  - "//*[@selected='true']//android.widget.TextView/@text"
baseUrl: [ ]
appWhiteList: [ ]
urlBlackList: [ ]
urlWhiteList: [ ]
blackList:
  - given: [ ]
    when: null
    then: [ ]
    xpath: ".*[0-9]{2}.*"
    action: ""
    actions: [ ]
    times: -1
beforeStartWait: 6000
beforeRestart: [ ]
beforeElement: [ ]
afterElement: [ ]
afterElementWait: 500
afterAll: [ ]
afterAllMax: 2
tagLimitMax: 2
tagLimit:
  - given: [ ]
    when: null
    then: [ ]
    xpath: "确定"
    action: ""
    actions: [ ]
    times: 1000
  - given: [ ]
    when: null
    then: [ ]
    xpath: "取消"
    action: ""
    actions: [ ]
    times: 1000
  - given: [ ]
    when: null
    then: [ ]
    xpath: "share_comment_guide_btn_name"
    action: ""
    actions: [ ]
    times: 1000
assertGlobal: [ ]
pluginList: [ ]
```

## 金牌赞助商（Gold Sponsor）

![Logo-霍格沃兹测试学院](https://ceshiren.com/uploads/default/original/2X/2/2529377efc39dffe8ffd96b5aed4b417cdef1a52.png)

[霍格沃兹测试开发学社](https://ceshiren.com/)
是 [测吧（北京）科技有限公司](http://qrcode.testing-studio.com/f?from=appcrawler&url=https://ceshiren.com/t/topic/14814)
旗下业界领先的测试开发技术高端教育品牌。 学院课程均由名企一线测试大牛设计，提供实战驱动的系列课程。涵盖移动app自动化测试、接口自动化测试、性能测试、持续集成/持续交付/DevOps 、测试左移、测试右移、测试管理等课程。
[点击学习!](http://qrcode.testing-studio.com/f?from=appcrawler&url=https://testerh.ke.qq.com?flowToken=1040391)

[测吧（北京）科技有限公司](http://qrcode.testing-studio.com/f?from=appcrawler&url=https://ceshiren.com/t/topic/14814)
是一家服务于测试领域的高科技公司，为企业提供全方位的自动化测试技术支持、测试平台开发定制、测试效能提升等咨询与科研合作服务。 先后服务于华为、工信部、信通院等知名企业与机构。

## 编译

```bash
mvn clean package assembly:single -DskipTests
```

## 技术交流

由霍格沃兹测试开发学社维护，技术交流与issue提交请移步 https://ceshiren.com/c/opensource/appcrawler/ 交流
