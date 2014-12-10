import sbt._
import Keys._

object ProjectBuild extends Build {

  val projectSettings = Defaults.defaultSettings ++ Seq(
      name := "edan70-unknown",
      version := "1.0",
      organization := "se.lth.cs",
      scalaVersion := "2.11.2",
      
      libraryDependencies += "org.apache.lucene" % "lucene-core" % "4.10.2",
      libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "4.10.2",
      libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "4.10.2",
      libraryDependencies += "org.json" % "json" % "20131018",
      libraryDependencies += "org.apache.commons" % "commons-compress" % "1.9",
      libraryDependencies += "junit" % "junit" % "4.12"
  )

  val myProject = Project("edan70-unknown", file("."), settings = projectSettings)

}

