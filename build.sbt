sbtPlugin := true

name := "scalakml"

organization := "com.github.workingDog"

version := (version in ThisBuild).value

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5",
  "com.github.workingDog" % "scalaxal_2.11" % "1.0"
)

homepage := Some(url("https://github.com/workingDog/scalakml"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
