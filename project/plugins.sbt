
addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.4.19")
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % "0.4.19")
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % "0.4.19")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"        % "0.3.7")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.27")


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
