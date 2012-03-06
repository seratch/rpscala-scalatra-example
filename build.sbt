version := "0.1"

scalaVersion := "2.9.1"

resolvers += "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.0.3",
  "org.scalatra" %% "scalatra-scalate" % "2.0.3",
  "ch.qos.logback" % "logback-classic" % "1.0.0",
  "org.scalatra" %% "scalatra-specs2" % "2.0.3" % "test",
  "org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container",
  "javax.servlet" % "servlet-api" % "2.5" % "provided"
)

seq(webSettings :_*)
