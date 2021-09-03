lazy val packagingSettings: Seq[Def.Setting[_]] = Seq(
  name := "data-engineering",
  version := "0.0-RELEASE",
  scalaVersion := "2.12.10",

  //don't run tests in parallel - breaks spark test harness
  parallelExecution in Test := false,

  //don't run sbt test during assembly
  test in assembly := {},

  //mergeStrategy is used by assembly to resolve conflicting classpaths
  // *NOTE* there may be differences in mergeStrategy for Spark 2.X and Spark 1.X, please keep that in mind
  // this too a lot of trial-and-error to work correctly, modify on your own risk
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case PathList("javax", "servlet", xs@_*) => MergeStrategy.last
    case PathList("javax", "activation", xs@_*) => MergeStrategy.last
    case PathList("org", "apache", xs@_*) => MergeStrategy.last
    case PathList("com", "google", xs@_*) => MergeStrategy.last
    case PathList("com", "esotericsoftware", xs@_*) => MergeStrategy.last
    case PathList("com", "codahale", xs@_*) => MergeStrategy.last
    case PathList("com", "yammer", xs@_*) => MergeStrategy.last
    case "reference.conf" => MergeStrategy.concat
    case "plugin.properties" => MergeStrategy.last
    case "log4j.properties" => MergeStrategy.last
    //Handles issues with geomesa dependencies
    case "git.properties" => MergeStrategy.last
    case PathList("tec", "uom", "se", "format", "messages.properties") => MergeStrategy.last
    case PathList("com", "typesafe", "scalalogging", "Logger.class") => MergeStrategy.first
    case "module-info.class" => MergeStrategy.discard
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  assemblyJarName in assembly := "data-engineering-" + version.value + ".jar",
  publishLocal := {},
  publish := {}
)

lazy val dependenciesSetting = Seq(
  resolvers ++= Seq("Spark Packages repo" at "https://repos.spark-packages.org/"),
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "3.1.0" % "provided",
    "org.apache.spark" %% "spark-sql" % "3.1.0" % "provided",
    "joda-time" % "joda-time" % "2.10.8",
    "com.typesafe" % "config" % "1.3.1",
    "org.rogach" %% "scallop" % "4.0.2",
    "org.scalatest" %% "scalatest" % "3.2.8" % Test,
  )
)

lazy val `data-engineering` = project.in(file("."))
  .settings(packagingSettings: _*)
  .settings(dependenciesSetting: _*)