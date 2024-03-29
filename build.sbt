name := """ffams"""
organization := "com.pedrorijo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.mockito" % "mockito-all" % "2.0.2-beta" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.pedrorijo.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.pedrorijo.binders._"
