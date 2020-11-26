ThisBuild / scalaVersion := "2.12.12"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "flink-playgrounds-scala",
    libraryDependencies ++= Seq(
      //"org.scalatest" %% "scalatest" % "3.2.2" % Test,
      "org.apache.flink" %% "flink-scala" % "1.11.2",
      "org.apache.flink" %% "flink-streaming-scala" % "1.11.2" % "provided",
      "org.apache.flink" % "flink-table" % "1.11.2" % "provided" pomOnly(),
      "org.apache.flink" %% "flink-table-planner-blink" % "1.11.2" % "provided",
      "org.apache.flink" %% "flink-table-api-scala-bridge" % "1.11.2",
      "org.apache.flink" %% "flink-clients" % "1.11.2",
      "org.apache.kafka" % "kafka-clients" % "2.2.0",
      //added
      "org.apache.flink" %% "flink-sql-connector-kafka" % "1.11.2",
      "org.slf4j" % "slf4j-simple" % "1.8.0-alpha2"
      //"org.apache.flink" %% "flink-connector-jdbc" % "1.11.2",
      //"org.apache.flink" % "flink-csv" % "1.11.2" % Test,
      //"mysql" % "mysql-connector-java" % "8.0.22"
    )
  ).settings(
  assemblyJarName in assembly := "flink-playgrounds-scala.jar",
  mainClass in assembly := Some("example.SpendReport")
)

