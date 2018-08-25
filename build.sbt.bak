name := "AppCrawler"
version := "2.3.1"
scalaVersion := "2.12.6"
//scalaVersion := "2.13.0-M3"

libraryDependencies ++= Seq(
  //"org.scala-lang" % "scala-compiler" % scalaVersion.value,
  //"org.scala-lang" % "scala-library" % scalaVersion.value,
  //"org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "io.appium" % "java-client" % "6.0.0",
  //"org.seleniumhq.selenium" % "selenium-java" % "2.53.1" ,
  //"io.selendroid" % "selendroid" % "0.16.0",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "com.brsanthu" % "google-analytics-java" % "1.1.2",
  "org.slf4j" % "slf4j-api" % "1.7.18",
  "org.slf4j" % "slf4j-log4j12" % "1.7.18",
  //"org.slf4j" % "slf4j-simple" % "1.7.18",
  //"org.apache.logging.log4j" % "log4j" % "2.5",
  //"com.android.tools.ddms" % "ddmlib" % "24.5.0",
  //"org.lucee" % "xml-xerces" % "2.11.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.5",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.9.5",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.9.5" ,
  "net.lightbody.bmp" % "browsermob-core" % "2.1.5"
    exclude ("com.fasterxml.jackson.core", "jackson-databind")
    exclude ("com.fasterxml.jackson.core", "jackson-annotations")
    exclude ("com.fasterxml.jackson.core", "jackson-core")
    exclude("com.fasterxml.jackson.dataformat","jackson-dataformat-yaml")
    exclude("com.fasterxml.jackson.dataformat","jackson-dataformat-xml"),
  "org.lucee" % "commons-codec" % "1.10.L001",
  "org.jsoup" % "jsoup" % "1.9.2",
  "com.jayway.jsonpath" % "json-path" % "2.2.0" ,
  "org.scalactic" %% "scalactic" % "3.0.5" ,
  "org.scalatest" %% "scalatest" % "3.0.5" exclude("org.scala-lang.modules", "scala-xml"),
  "org.apache.directory.studio" % "org.apache.commons.io" % "2.4",
  //todo:太重，两年不更新了，废弃
  "org.scalatra.scalate" %% "scalate-core" % "1.8.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "macaca.webdriver.client" % "macacaclient" % "2.0.20",
  "org.javassist" % "javassist" % "3.22.0-CR2",
  "com.github.tototoshi" %% "scala-csv" % "1.3.4" ,
  "us.codecraft" % "xsoup" % "0.3.1" ,
  "junit" % "junit" % "4.12" % "test",
  "org.junit.jupiter" % "junit-jupiter-api" % "5.2.0" % Test,
  //"org.bytedeco" % "javacpp" % "1.3.3",
  //"org.bytedeco" % "javacv-platform" % "1.3.3",
  //"org.bytedeco" % "javacv" % "1.2",
  //"com.github.vidstige" % "jadb" % "v1.0.1",

  /**
  "net.sourceforge.tess4j" % "tess4j" % "3.4.2"
    //exclude("ch.qos.logback", "logback-classic")
    exclude("ch.qos.logback", "logback-classic")
    exclude("org.slf4j", "log4j-over-slf4j")
    exclude("org.apache.logging.log4j", "log4j-core"),
    */
  "com.github.poslegm" %% "scala-phash" % "1.0.3",
  "com.github.spullara.mustache.java" % "compiler" % "0.9.5",
  "org.ow2.asm" % "asm" % "5.2",
//  "org.openimaj" % "openimaj" % "1.3.6",
  "io.qameta.allure" % "allure-junit5" % "2.6.0",
  "org.apache.commons" % "commons-text" % "1.4",
  "org.pegdown" % "pegdown" % "1.6.0" //html report
)

//libraryDependencies ~= { _.map(_.exclude("ch.qos.logback", "logback-classic")) }

enablePlugins(JavaAppPackaging)
/*
proguardSettings
ProguardKeys.proguardVersion in Proguard := "5.2.1"
inConfig(Proguard)(javaOptions in ProguardKeys.proguard := Seq("-Xmx2g"))
ProguardKeys.merge in Proguard := true
ProguardKeys.options in Proguard ++= Seq("-dontnote", "-dontwarn", "-ignorewarnings")
ProguardKeys.options in Proguard += ProguardOptions.keepMain("com.xueqiu.qa.appcrawler.AppCrawler")
ProguardKeys.mergeStrategies in Proguard += ProguardMerge.first(".*".r)
ProguardKeys.mergeStrategies in Proguard += ProguardMerge.discard("META-INF/.*".r)
*/

enablePlugins(SbtProguard)
proguardOptions in Proguard ++= Seq("-dontnote", "-dontwarn", "-ignorewarnings")
proguardOptions in Proguard += ProguardOptions.keepMain("com.testerhome.appcrawler.AppCrawler")

assemblyJarName in assembly := "appcrawler-"+version.value+".jar"
test in assembly := {}
mainClass in assembly := Some("com.testerhome.appcrawler.AppCrawler")
scriptClasspath := Seq("*")
assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case PathList("META-INF", xs @ _*)=>{
      (xs map {_.toLowerCase}) match {
        case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") => MergeStrategy.discard
        case _ => MergeStrategy.first
      }
    }
    case x =>  {
      //println(x)
      MergeStrategy.first
    }
}

// adding the tools.jar to the unmanaged-jars seq
unmanagedJars in Compile ~= {uj =>
  Seq(Attributed.blank(file(System.getProperty("java.home").dropRight(3)+"lib/tools.jar"))) ++ uj
}

//resolvers += "oschina" at "http://maven.oschina.net/content/groups/public/"

resolvers += Classpaths.typesafeReleases
resolvers += Classpaths.sbtPluginReleases
resolvers += Classpaths.sbtIvySnapshots
resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.mavenLocal
resolvers += Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
//resolvers += "spring-snapshots" at "http://repo.spring.io/snapshot/"
resolvers += "central" at "http://central.maven.org/maven2/"
//resolvers += "central2" at "http://central02.maven.org/"
resolvers += "elk" at "https://artifacts.elastic.co/maven"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases/"
resolvers += "bintray" at "http://dl.bintray.com/xudafeng/maven/"
//resolvers += "sonatype-ossrh" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "jitpack" at "https://jitpack.io"
resolvers += Resolver.sonatypeRepo("public")

//externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral =false)


parallelExecution in Test := false
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o", "-u", "target/test-reports", "-h", "target/test-reports")
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o")
