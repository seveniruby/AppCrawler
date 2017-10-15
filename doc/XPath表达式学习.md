# XPath表达式学习

# 学习渠道
w3school肯定是最好的教程
# 获取控件XPath路径的工具

| 名字 | 平台 | 介绍|
| -- | --- | --- |
| uiautomatorviewer | Android | 只能直接生成xpath, 需要自己拼凑 |
| Appium Inspector | Android iOS | 只能工作在mac上 |
| app-insecptor | Android iOS | macaca的生态工具 |

# 常见用法

# Android和iOS控件差异
tag名字是不一样的.
```
UIAXXXX
android.view.View
android.widget.XXXXX
```
关键的定位属性也不一样

iOS
```
name
label
value
```
Android
```
resource-id
content-desc
text
```

# 常见XPath表达式用法

```
//*[not(ancestor-or-self::UIATableView)]
//*[not(ancestor-or-self::UIAStatusBar)]
//*[@resource-id='com.xueqiu.android:id/action_search']/parent::*
//*[@resource-id='com.xueqiu.android:id/action_search']
//*[contains(name(), 'Text')]
//*[@resource-id!='' and not(contains(name(), 'Layout'))]
//*[../*[@selected='true']]
```
