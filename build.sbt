name := "ReactiveWebStore"

version := "0.0.1"

lazy val `reactivewebstore` = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Jasper" at "http://jaspersoft.artifactoryonline.com/jaspersoft/repo/",
  "JasperSoft" at "http://jaspersoft.artifactoryonline.com/jaspersoft/jaspersoft-repo/",
  "Jasper3rd" at "http://jaspersoft.artifactoryonline.com/jaspersoft/jaspersoft-3rd-party/",
  "jr-ce-releases" at "http://jaspersoft.artifactoryonline.com/jaspersoft/jr-ce-releases",
  "mondrian-repo-cache" at "http://jaspersoft.artifactoryonline.com/jaspersoft/mondrian-repo-cache/",
  "spring-mil" at "http://repo.spring.io/libs-milestone",
  "spring-rel" at "http://repo.spring.io/libs-release",
  "oss" at "http://oss.sonatype.org/content/groups/public/")

scalaVersion := "2.11.11"

scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-feature",
  "-language:postfixOps"
)

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.4.4" % Test,
  "io.reactivex" %% "rxscala" % "0.23.0",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "mysql" % "mysql-connector-java" % "6.0.3",
  "net.sf.jasperreports" % "jasperreports" % "6.2.2" withSources (),
  "net.sf.jasperreports" % "jasperreports-functions" % "6.2.2",
  "net.sf.jasperreports" % "jasperreports-chart-themes" % "6.2.2"
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

parallelExecution in Test := false

fork in Test := false
