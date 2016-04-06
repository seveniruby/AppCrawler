name := "AppCrawler"
version := "1.2.0"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  //"org.scala-lang" % "scala-compiler" % "2.11.7",
  "org.scala-lang" % "scala-library" % "2.11.7",
  //"org.scala-lang" % "scala-reflect" % "2.11.7",
  "org.scalatest" %% "scalatest" % "2.2.5",
  "io.appium" % "java-client" % "3.2.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
  //"io.selendroid" % "selendroid" % "0.16.0",
  "io.selendroid" % "selendroid-client" % "0.16.0",
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  //"com.propensive" %% "rapture" % "2.0.0-M1",
  //"com.propensive" %% "rapture-json" % "2.0.0-M1",
  //"com.propensive" %% "rapture-json-json4s" % "2.0.0-M1",
  //"org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.json4s" %% "json4s-native" % "3.3.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.3",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "com.brsanthu" % "google-analytics-java" % "1.1.2",
  "org.slf4j" % "slf4j-api" % "1.7.18",
  "org.slf4j" % "slf4j-log4j12" % "1.7.18",
  "org.apache.logging.log4j" % "log4j" % "2.5",
  //"com.android.tools.ddms" % "ddmlib" % "24.5.0",
  //"org.lucee" % "xml-xerces" % "2.11.0",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.5.4",
  "org.pegdown" % "pegdown" % "1.6.0" //html report
)

enablePlugins(JavaAppPackaging)

assemblyJarName in assembly := "appcrawler-"+version.value+".jar"
test in assembly := {}
mainClass in assembly := Some("AppCrawler")
assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case x =>  MergeStrategy.first
}



resolvers += "oschina" at "http://maven.oschina.net/content/groups/public/"
resolvers += Resolver.sonatypeRepo("public")
resolvers += "spring-snapshots" at "http://repo.spring.io/snapshot"
externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)


parallelExecution in Test := false
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o", "-u", "target/test-reports", "-h", "target/test-reports")
testOptions in Test += Tests.Setup(() => {
  println("List All TestCases")
  (definedTests in Test).value.map(println(_))
})
testOptions in Test += Tests.Cleanup(() => {
  println("Finish")
}
)
