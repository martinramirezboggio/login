name := """login"""
organization := "com.martinramirez"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.20.11-play27",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.martin.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.martin.binders._"
play.sbt.routes.RoutesKeys.routesImport += "play.modules.reactivemongo.PathBindables._"
