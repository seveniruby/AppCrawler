# iOS遍历

## 模拟器运行
启动appium
```
appium --session-override
```

开始遍历
```
java -jar appcrawler.jar \
-c  conf/xueqiu.yaml \
-a <你的app地址比如xueqiu.app>
```
xcode编译出来的app地址可通过编译过程自己查看


## 真机运行
使用xcode编译源代码. 使用开发证书才能做自动化. 编译出真机可自动化的.app或者.ipa包
```
java -jar appcrawler.jar \
-c conf/xueqiu.yaml \
-a Snowball.app
```
