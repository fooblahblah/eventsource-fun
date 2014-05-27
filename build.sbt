name := "eventsource-fun"

version := "1.0"

scalaVersion := "2.10.4"

fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-actor"  % "2.3.3",
  "com.typesafe.akka"  %% "akka-persistence-experimental" % "2.3.3")
