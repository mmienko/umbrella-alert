javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

name := "umbrella-alert"

version := "0.1"

scalaVersion := "2.12.8"

retrieveManaged := true

//Deps
val circeVersion = "0.10.0"
val awsLambdaCoreVersion = "1.2.0"
val awsLambdaEventsVersion = "2.2.5"
val awsSnsVersion = "1.11.500"

libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % awsLambdaCoreVersion
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % awsLambdaEventsVersion
libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.5.9"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-sns" % awsSnsVersion
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % Test
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

fork in Test := true

// Assembly
test in assembly := {} //skip tests on assembly
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
