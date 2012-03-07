libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.10"))

resolvers += "mpeltonen.github.com" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")

