name := "sj-wordcount-demo"

scalaVersion := Dependencies.Versions.scala
val sjVersion = "1.0-SNAPSHOT"

addCommandAlias("rebuild", ";clean; compile; package")

val commonSettings = Seq(
  version := sjVersion,
  scalaVersion := Dependencies.Versions.scala,
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature"
  ),

  resolvers += "Sonatype OSS" at "https://oss.sonatype.org/service/local/staging/deploy/maven2",
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  libraryDependencies ++= Seq(
    "com.bwsw" %% "sj-engine-core" % "1.0-SNAPSHOT",
    "mysql" % "mysql-connector-java" % "6.0.5"
  ),

  assemblyMergeStrategy in assembly := {
    case PathList("org", "apache", "commons", "logging", xs@_*) => MergeStrategy.first
    case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.concat
    case "log4j.properties" => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },

  assemblyJarName in assembly := s"${name.value}-${version.value}.jar",

  fork in run := true,
  fork in Test := true,
  parallelExecution in Test := false
)

lazy val wordCounterDemoInput = Project(id = "word-counter-input",
  base = file("./word-counter-input"))
  .settings(commonSettings: _*)

lazy val wordCounterDemoCommon = Project(id = "word-counter-common",
  base = file("./word-counter-common"))
  .settings(commonSettings: _*)

lazy val wordCounterDemoProcess = Project(id = "word-counter-process",
  base = file("./word-counter-process"))
  .settings(commonSettings: _*)
  .dependsOn(wordCounterDemoCommon)

lazy val wordCounterDemoOutput = Project(id = "word-counter-output",
  base = file("./word-counter-output"))
  .settings(commonSettings: _*)
  .dependsOn(wordCounterDemoCommon)
