# Log插件

## 作用
自动记录Android的LogCat或者iOS的syslog.
## 安装
目前是默认自带.

## 启用
在配置文件中加入插件
```
  "pluginList" : [
    "com.testerhome.appcrawler.plugin.LogPlugin"
  ],
```

## 结果
记录一次点击事件后所发生的log记录. 并保存为后缀名为.log的文件中.
