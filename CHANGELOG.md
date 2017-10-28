# 2.2.0 [TODO]
- 支持从历史数据中寻找最优点击路径
- 支持web
- 支持游戏app遍历
- 使用节点树模型

# 2.1.2
- 跟进支持appium 1.7[完成]

# 2.1.0
### bugfix
mark图片异常的问题
### 自动化用例
只是demo. 还有很多细节需要设计的更好.  
支持given when then风格, 也支持简化的xpath action then的简单风格.  
```yaml
#设置这个跳过遍历
autoCrawl: false
#测试用例入口
testcase:
#测试用例名字
  name: demo1
  steps:
  - when:
      xpath: //*
      action: driver.swipe(0.5, 0.8, 0.5, 0.2)
  - when:
      xpath: //*
      action: driver.swipe(0.5, 0.2, 0.5, 0.8)
#简化风格. 没有when
  - xpath: 自选
    action: click
    then:
    - //*[contains(@text, "港股")]
```
所有的xpath的设置都支持如下三种形式
- xpath //*[contains(@resource-id, 'ddd')]
- regex ^确定$ 
- contains关系 取消 确定
# 2.0.0
支持macaca[完成]  
失败重试[完成]  
支持简单的测试用例[完成]  
架构重新设计[完成] 
新老版本对比报告改进[完成]  
# 1.9.0
支持遍历断言[完成]  
支持历史对比断言[完成]  
修正不支持uiautomator2的问题[完成]  
支持yaml自动化测试用例[完成]  
action支持长按[完成]  
重构用例生成方式[完成]  

# 1.8.0
对子菜单的支持, 智能判断是否有子菜单  
支持断点续传机制  
支持自动重启appium机制, 用于防止iOS遍历内存占用太大问题  
分离插件到独立项目  

# 1.7.0
android跳到其他app后自动后退[完成]  
截图复用优化提速 [完成]  
报告增加点击前后的截图 [完成]  
独立的report子命令 [完成]  
配置支持动态指令 [完成]  
配置与老版本不兼容 [重要提醒]  
支持自定义报告title [完成]  

# 1.6.0 [内测]
增加动态插件 [完成]  
支持beforeElementAction的afterElementAction配置 [完成]
修复app的http连接支持 [完成]
支持url白名单 [完成]
支持defineUrl的xpath属性提取 [完成]
未遍历控件用测试用例的cancel状态表示 [完成]
两次back之间的时间间隔设定为不低于4s防止粘连 [完成]

# 1.5.0
配置文件内容变更 此版本不再向下兼容, 推荐使用yaml配置文件
标准的html报告 [完成]
windows下中文编码问题 [完成]
windows下命令行超长问题[完成]
加入yaml配置格式支持并添加注释 [完成]
startupActions支持scala表达式 [完成]

# 1.4.0
元素点击之前开始截图并高亮要点击的控件[完成]
修复freemind文件无法打开的问题[完成]
# 1.3.1
增加最大后退尝试次数
增加跳出app的判断
bugfix:
解决文件名特殊符号问题
修复不同界面漏掉截图的问题

# 1.3.0

# 1.2.2
支持相对路径的apk地址.
android的端口指定不再使用4730而是和ios一样
# 1.2.1
url定义优化, 内容变更改进
支持自动化测试. 添加了兼容性测试的例子
# 1.2.0
兼容appium1.5去掉了不支持的findElementByName方法
对xpath元素查找进行了优化 解析dom结构时生成合适的xpath表达式
# 1.1.4
增加log和tagLimit两个插件
截图时间超过5s自动跳过
增加Android和iOS的log输出
增加scroll方向支持
# 1.1.3
自动判断页面是否变化. 界面变化才截图. 能减少大量的重复截图
导出界面dom结构用于diff分析
# 1.1.2
增加-t参数. 支持最大遍历时间
增加-o参数, 支持设定结果目录
# 1.1.1
增加每个url最大滚动的次数
# 1.1.0
增加思维导图生成
增加插件支持
清理大量的无关文件和测试用例

# 1.0.1
支持基本遍历
支持命令行运行方式
