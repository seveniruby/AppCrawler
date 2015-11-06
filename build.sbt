name := "LBSRefresh"

version := "1.0"

scalaVersion := "2.11.7"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5"
libraryDependencies += "io.appium" % "java-client" % "3.2.0"
libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test"
libraryDependencies += "io.selendroid" % "selendroid" % "0.16.0"
libraryDependencies += "io.selendroid" % "selendroid-client" % "0.16.0"
libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
libraryDependencies += "com.propensive" %% "rapture" % "2.0.0-M1"
libraryDependencies += "com.propensive" %% "rapture-json" % "2.0.0-M1"
libraryDependencies += "com.propensive" %% "rapture-json-json4s" % "2.0.0-M1"
libraryDependencies += "org.pegdown" % "pegdown" % "1.4.2" //html report

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)


parallelExecution in Test := true
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-o", "-P2", -u", "target/test-reports", "-h", "target/test-reports")