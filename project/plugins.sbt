logLevel := Level.Warn
/*
def websudosPattern = {
  val pList = List("[organisation]/[module](_[scalaVersion])(_[sbtVersion])/[revision]/[artifact]-[revision](-[classifier]).[ext]")
  Patterns(pList, pList, true)
}

resolvers ++= Seq(
  Resolver.url("Maven ivy Websudos", url(Resolver.DefaultMavenRepositoryRoot))(websudosPattern)
)
*/

resolvers += Resolver.url("bintray-sbt-plugins", url("https://dl.bintray.com/eed3si9n/sbt-plugins/"))(Resolver.ivyStylePatterns)
