name := "AppCrawler"
version := "1.7.0"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  //"org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "io.appium" % "java-client" % "4.1.2",
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % "test",
  //"io.selendroid" % "selendroid" % "0.16.0",
  "io.selendroid" % "selendroid-client" % "0.16.0",
  //"com.propensive" %% "rapture" % "2.0.0-M1",
  //"com.propensive" %% "rapture-json" % "2.0.0-M1",
  //"com.propensive" %% "rapture-json-json4s" % "2.0.0-M1",
  //"org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.json4s" %% "json4s-native" % "3.3.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.3",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "com.brsanthu" % "google-analytics-java" % "1.1.2",
  "org.slf4j" % "slf4j-api" % "1.7.18",
  //"org.slf4j" % "slf4j-log4j12" % "1.7.18",
  //"org.apache.logging.log4j" % "log4j" % "2.5",
  //"com.android.tools.ddms" % "ddmlib" % "24.5.0",
  //"org.lucee" % "xml-xerces" % "2.11.0",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.5.4",
  "net.lightbody.bmp" % "browsermob-core" % "2.1.2",
  "org.lucee" % "commons-codec" % "1.10.L001",
  "com.twitter" %% "util-eval" % "6.35.0",
  "net.sourceforge.tess4j" % "tess4j" % "3.2.1" exclude("asm", "asm"),
  "org.jsoup" % "jsoup" % "1.9.2",
  "org.scalactic" %% "scalactic" % "3.0.0" ,
  "org.scalatest" %% "scalatest" % "3.0.0" ,
  "org.pegdown" % "pegdown" % "1.6.0" //html report
)

libraryDependencies += "bouncycastle" % "bctsp-jdk14" % "1.38" from "http://central.maven.org/maven2/bouncycastle/bcmail-jdk14/138/bcmail-jdk14-138.jar"

libraryDependencies ~= { _.map(_.exclude("ch.qos.logback", "logback-classic")) }



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

assemblyJarName in assembly := "appcrawler-"+version.value+".jar"
test in assembly := {}
mainClass in assembly := Some("com.xueqiu.qa.appcrawler.AppCrawler")
scriptClasspath := Seq("*")
assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case PathList("META-INF", xs @ _*)=>{
      (xs map {_.toLowerCase}) match {
        case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") => MergeStrategy.discard
        case _ => MergeStrategy.first
      }
    }
    case x if x.matches("com.xueqiu.qa.appcrawler.plugin.OCR.class")  => MergeStrategy.discard
    case x if x.matches("com.xueqiu.qa.appcrawler.plugin.AndroidTrace.class")  => MergeStrategy.discard
    case x =>  {
      println(x)
      MergeStrategy.first
    }
}

resolvers += "oschina" at "http://maven.oschina.net/content/groups/public/"
resolvers += "spring-snapshots" at "http://repo.spring.io/snapshot"
resolvers += "central" at "http://central.maven.org/maven2"
resolvers += "central2" at "http://central.maven.org"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.mavenLocal
//externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral =false)


parallelExecution in Test := false
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o", "-u", "target/test-reports", "-h", "target/test-reports")
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o")
testOptions in Test += Tests.Setup(() => {
  println("List All TestCases")
  (definedTests in Test).value.map(println(_))
})
testOptions in Test += Tests.Cleanup(() => {
  println("Finish")
}
)
