
name := "cassandra-phantom"

version := "1.0"

scalaVersion := "2.12.4"

lazy val Versions = new {
  val phantom = "2.14.5"
  val util = "0.30.1"
  val scalatest = "3.0.4"
}


resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.bintrayRepo("websudos", "oss-releases")
)



resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "com.outworkers" %% "phantom-dsl" % "2.20.2",
"com.outworkers"  %%  "phantom-streams"   % "2.20.2",
  "com.outworkers"  %%  "util-testing"      % Versions.util % Test,
  "org.scalatest"   %%  "scalatest"         % Versions.scalatest % Test,

  "com.eed3si9n" % "sbt-assembly" % "0.14.5"
)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

exportJars := true

mainClass in Compile := Some("com.cassandra.phantom.modeling.mainClass")
mainClass in(Compile, run) := Some("com.cassandra.phantom.modeling.mainClass")
mainClass in(Compile, packageBin) := Some("com.cassandra.phantom.modeling.mainClass")
mainClass in assembly := Some("com.cassandra.phantom.modeling.mainClass")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}