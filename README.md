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

# 相关文档

- [TesterHome AppCrawler专区](https://testerhome.com/topics/node83)  
- [在线帮助](https://seveniruby.gitbooks.io/appcrawler/content/)
