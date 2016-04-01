# 安装Scale

[http://www.scala-lang.org/download/]()

安装后,执行`scala`命令验证是否安装成功。

# 安装SBT

[http://www.scala-sbt.org/0.13/docs/index.html]()

根据文档上的说明安装`sbt`工具。

## 编译项目



修改`build.sbt`文件中的`resolvers`部分,定位到公共的仓库,可以用下面的代码替换:

```
resolvers += "oschina" at "http://maven.oschina.net/content/groups/public/"
resolvers += Resolver.sonatypeRepo("public")
//externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)
resolvers += "snowball_public" at "http://repository.sonatype.org/content/groups/public/"
resolvers += "snowball_snapshot" at "http://repository.sonatype.org/content/repositories/snapshots/"
resolvers += "snowball_release" at "http://repository.sonatype.org/content/repositories/releases/"
//resolvers += "artifactory" at "http://repo.snowballfinance.com/artifactory/repo/"
resolvers += "spring-snapshots" at "http://repo.spring.io/snapshot"
```


在项目的根目录下执行`sbt clean compile`来编译项目。(mac下所有的jar包都在`~/.ivy2/cache`下)

可以通过下面的连接来查看不同的task的作用:

[http://www.scala-sbt.org/0.13/docs/Running.html]()


# 安装IDEA

[https://www.jetbrains.com/idea/#chooseYourEdition]()


下载`Community`版本就可以了。

# 导入到IDEA


## 下载sbt插件

Configure-plugins中安装sbt插件和scala,然后重启Idea,导入项目的时候选择sbt项目就可以了。

# 调试

`sbt stage`在`target/universal/stage/bin/appcrawler`下有可执行的脚本




