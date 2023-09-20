
addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.5.3")
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % "0.5.3")
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % "0.5.3")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"        % "0.3.7")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.1")


// libraryDependencies ++= List(
//   "com.geirsson" %% "scalafmt-core" % "1.3.0",
//   "com.geirsson" %% "scalafmt-cli"  % "1.3.0"
// )

// Compile / unmanagedJars ++= tsec.build.SunShine.`tools.jar`.toSeq

// Compile / sources ++= {
//   if (tsec.build.SunShine.canWeUseToolsDotJar_?)
//     file("project/boiler/gensodium.scala").getAbsoluteFile :: Nil
//   else Nil
// }
