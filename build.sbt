
name := "scalakml"

organization := "com.github.workingDog"

version := (version in ThisBuild).value

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.12.6")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
  "com.github.workingDog" %% "scalaxal" % "1.2"
)

homepage := Some(url("https://github.com/workingDog/scalakml"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xlint" // Enable recommended additional warnings.
)

