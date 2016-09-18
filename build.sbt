name := "AppCrawler"
version := "1.7.0"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  //"org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "2.2.6",
  "io.appium" % "java-client" % "3.4.1",
  "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
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
  "org.slf4j" % "slf4j-log4j12" % "1.7.18",
  "org.apache.logging.log4j" % "log4j" % "2.5",
  //"com.android.tools.ddms" % "ddmlib" % "24.5.0",
  //"org.lucee" % "xml-xerces" % "2.11.0",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.5.4",
  "net.lightbody.bmp" % "browsermob-core" % "2.1.2",
  "org.lucee" % "commons-codec" % "1.10.L001",
  "com.twitter" %% "util-eval" % "6.35.0",
  "org.pegdown" % "pegdown" % "1.6.0" //html report
)

enablePlugins(JavaAppPackaging)

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
    case x =>  MergeStrategy.first
}



//resolvers += "oschina" at "http://maven.oschina.net/content/groups/public/"
resolvers += Resolver.sonatypeRepo("public")
resolvers += "spring-snapshots" at "http://repo.spring.io/snapshot"
resolvers += Resolver.mavenLocal
externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = true)


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
