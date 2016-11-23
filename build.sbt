name := """dynamodb-test"""

version := "1.0"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.gu" %% "scanamo" % "0.8.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
