name := "kafka-gatling"

version := "0.1"

scalaVersion := "2.12.10"

enablePlugins(GatlingPlugin)
// https://mvnrepository.com/artifact/ru.tinkoff/gatling-kafka-plugin
libraryDependencies ++= Seq(
  "ru.tinkoff" %% "gatling-kafka-plugin" % "0.0.3",
  "io.gatling" % "gatling-maven-plugin" % "3.0.5",
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.3.1",
  "io.gatling" % "gatling-core" % "3.3.1",
  "io.gatling" % "gatling-test-framework" % "3.3.1" % "test",
  "org.apache.kafka" % "kafka-clients" % "1.1.0" % "provided",
  "com.sksamuel.avro4s" %% "avro4s-core" % "1.9.0"
)
javaOptions in Gatling := overrideDefaultJavaOptions("-Xss10m", "-Xms2G", "-Xmx8G")
