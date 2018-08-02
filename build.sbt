name := """play-twitter-app"""
organization := "com.dragos"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies ++= Seq(
	"org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  	"com.typesafe.play" %% "play-slick" % "3.0.0",
  	"com.typesafe.play" %% "play-slick-evolutions" % "3.0.0"
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.dragos.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.dragos.binders._"
