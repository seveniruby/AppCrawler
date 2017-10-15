# Android遍历

## 在android上运行

## 启动appium
```
appium --session-override
```

## 简单的启动遍历
```
java -jar appcrawler.jar \
-a ~/Downloads/xueqiu.apk
```

## 定制文件运行方式
```bash
java -jar appcrawler.jar \
-a ~/Downloads/xueqiu.apk \
-c conf/xueqiu.yaml
```

## 跳过重新安装app

```bash
java -jar appcrawler.jar \
-a ~/Downloads/xueqiu.apk \
-c conf/xueqiu.yaml \
--capability \
appPackage=com.xueqiu.android,appActivity=.view.WelcomeActivityAlias
```
