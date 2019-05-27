lazy val root = (project in file(".")).
  settings(
    name := "Tutorial7_Actors",
    version := "1.0",
    scalaVersion := "2.12.8"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.3",
  "com.typesafe.play" %% "play-ws-standalone-json" % "2.0.3",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"

)

