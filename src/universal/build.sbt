name := "AppCrawlerDemo"
version := "0.0.1"
scalaVersion := "2.11.7"
offline := true

resolvers += "oschina" at "http://maven.oschina.net/content/groups/public/"
resolvers += Resolver.sonatypeRepo("public")
resolvers += "spring-snapshots" at "http://repo.spring.io/snapshot"
externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)


parallelExecution in Test := false
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o", "-u", "target/reports", "-h", "target/reports")
testOptions in Test += Tests.Setup(() => {
  println("Setup")
  (definedTests in Test).value.map(println(_))
})
testOptions in Test += Tests.Cleanup(() => {
  println("Cleanup")
}
)

