name := "AppCrawler"
version := "1.0.1"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % "2.11.7",
  "org.scala-lang" % "scala-library" % "2.11.7",
  "org.scala-lang" % "scala-reflect" % "2.11.7",
  "org.scalatest" %% "scalatest" % "2.2.5",
  "io.appium" % "java-client" % "3.2.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
  "io.selendroid" % "selendroid" % "0.16.0",
  "io.selendroid" % "selendroid-client" % "0.16.0",
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "com.propensive" %% "rapture" % "2.0.0-M1",
  "com.propensive" %% "rapture-json" % "2.0.0-M1",
  "com.propensive" %% "rapture-json-json4s" % "2.0.0-M1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.3",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.pegdown" % "pegdown" % "1.4.2" //html report
)

enablePlugins(JavaAppPackaging)


resolvers += "oschina" at "http://maven.oschina.net/content/groups/public/"
resolvers += Resolver.sonatypeRepo("public")
//externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)
resolvers += "snowball_public" at "http://repo.snowballfinance.com/nexus/content/groups/public/"
resolvers += "snowball_snapshot" at "http://repo.snowballfinance.com/nexus/content/repositories/snapshots/"
resolvers += "snowball_release" at "http://repo.snowballfinance.com/nexus/content/repositories/releases/"
//resolvers += "artifactory" at "http://repo.snowballfinance.com/artifactory/repo/"
resolvers += "spring-snapshots" at "http://repo.spring.io/snapshot"


parallelExecution in Test := false
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o", "-u", "target/test-reports", "-h", "target/test-reports")
testOptions in Test += Tests.Setup(() => {
  println("Setup")
  (definedTests in Test).value.map(println(_))
})
testOptions in Test += Tests.Cleanup(() => {
  println("Cleanup")
}
)

