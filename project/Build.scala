import sbt._
import Keys._

object ProjectBuild extends Build {

  val projectSettings = Defaults.defaultSettings ++ Seq(
      name := "edan70-unknown",
      version := "1.0",
      organization := "se.lth.cs",
      scalaVersion := "2.11.2",
      libraryDependencies += "org.apache.lucene" % "lucene-core" % "4.10.2",
      libraryDependencies += "org.openrdf.sesame" % "sesame-runtime" % "2.7.0",
      libraryDependencies += "com.sparkjava" % "spark-core" % "2.0.0",
      libraryDependencies += "org.json" % "json" % "20141113"

  )

  val myProject = Project("edan70-unknown", file("."), settings = projectSettings)

}

