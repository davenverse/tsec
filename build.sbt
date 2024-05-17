import Dependencies._

ThisBuild / tlBaseVersion := "0.5"

ThisBuild / organization := "io.github.jmcardon"

// scalaVersion := crossScalaVersions.value.last,
ThisBuild / crossScalaVersions := Seq("2.12.19", "2.13.12", "3.3.0")

ThisBuild / licenses := Seq(License.MIT)

ThisBuild / developers := List(
  tlGitHubDev("jmcardon", "Jose Cardona"),
  tlGitHubDev("rsoeldner", "Robert Soeldner"),
  tlGitHubDev("hrhino", "Harrison Houghton"),
  tlGitHubDev("aeons", "Bjørn Madsen"),
  tlGitHubDev("ChristopherDavenport", "Christopher Davenport")
)

ThisBuild / tlCiReleaseBranches := Seq("main")

// true by default, set to false to publish to s01.oss.sonatype.org
ThisBuild / tlSonatypeUseLegacyHost := true

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    Libraries.cats,
    Libraries.scalaTest,
    Libraries.scalaTestPlus,
    Libraries.scalaCheck,
    Libraries.commonsCodec,
    Libraries.fs2IO
  ),

  Test / fork := true,
  run / fork := true,

  Test / parallelExecution := false,
)

lazy val passwordHasherLibs = libraryDependencies ++= Seq(
  Libraries.sCrypt
)

lazy val bouncyLib = libraryDependencies += Libraries.BC

lazy val jwtCommonLibs = libraryDependencies ++= Seq(
  Libraries.circeCore,
  Libraries.circeGeneric,
  // Libraries.circeGenericExtras,
  Libraries.circeParser
)

lazy val http4sDeps = libraryDependencies ++= Seq(
  Libraries.http4sdsl,
  Libraries.http4sServer,
  Libraries.http4sCirce
)

lazy val loggingLibs = libraryDependencies ++= Seq(
  Libraries.log4s
)

lazy val root = tlCrossRootProject
  .aggregate(
    common,
    bouncyCastle,
    bouncyHash,
    bouncyCipher,
    messageDigests,
    cipherCore,
    jwtCore,
    symmetricCipher,
    mac,
    signatures,
    jwtMac,
    jwtSig,
    passwordHashers,
    http4s,
    // microsite,
    oauth2,
    // bench,
    // examples,
    // libsodium
  )

lazy val common = Project(id = "tsec-common", base = file("common"))
  .settings(commonSettings)

lazy val bouncyCastle = Project(id = "tsec-bouncy", base = file("bouncycastle"))
  .settings(commonSettings)
  .settings(bouncyLib)

lazy val passwordHashers = Project(id = "tsec-password", base = file("password-hashers"))
  .settings(commonSettings)
  .settings(passwordHasherLibs)
  .dependsOn(common % "compile->compile;test->test")

lazy val cipherCore = Project(id = "tsec-cipher-core", base = file("cipher-core"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")

lazy val symmetricCipher = Project(id = "tsec-cipher-jca", base = file("cipher-symmetric"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(cipherCore)


lazy val mac = Project(id = "tsec-mac", base = file("mac"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")

lazy val messageDigests = Project(id = "tsec-hash-jca", base = file("message-digests"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")

lazy val bouncyHash = Project(id = "tsec-hash-bouncy", base = file("hashing-bouncy"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(bouncyCastle)

lazy val bouncyCipher = Project(id = "tsec-cipher-bouncy", base = file("cipher-bouncy"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(bouncyCastle)

lazy val signatures = Project(id = "tsec-signatures", base = file("signatures"))
  .settings(commonSettings)
  .settings(bouncyLib)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(bouncyCastle)

lazy val jwtCore = Project(id = "tsec-jwt-core", base = file("jwt-core"))
  .settings(commonSettings)
  .settings(jwtCommonLibs)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(mac)
  .dependsOn(signatures)

lazy val jwtMac = Project(id = "tsec-jwt-mac", base = file("jwt-mac"))
  .settings(commonSettings)
  .settings(jwtCommonLibs)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(mac)
  .dependsOn(jwtCore)

lazy val jwtSig = Project(id = "tsec-jwt-sig", base = file("jwt-sig"))
  .settings(commonSettings)
  .settings(jwtCommonLibs)
  .settings(bouncyLib)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(jwtCore)
  .dependsOn(signatures)
  .dependsOn(messageDigests)
  .dependsOn(bouncyCastle)

lazy val bench = Project(id = "tsec-bench", base = file("bench"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(cipherCore)
  .dependsOn(symmetricCipher)
  .dependsOn(libsodium)
  .dependsOn(bouncyCipher)
  .dependsOn(bouncyHash)
  .dependsOn(mac)
  .enablePlugins(JmhPlugin)
  .enablePlugins(NoPublishPlugin)

lazy val examples = Project(id = "tsec-examples", base = file("examples"))
  .settings(commonSettings)
  .settings(jwtCommonLibs)
  .settings(bouncyLib)
  .settings(passwordHasherLibs)
  .settings(http4sDeps)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(
    symmetricCipher,
    mac,
    messageDigests,
    signatures,
    jwtMac,
    jwtSig,
    passwordHashers,
    // http4s,
    bouncyHash,
    bouncyCipher,
    libsodium
  ).enablePlugins(NoPublishPlugin)

lazy val oauth2 = Project(id = "tsec-oauth2", base = file("oauth2"))
  .settings(commonSettings)
  .dependsOn(common % "compile->compile;test->test")
  .enablePlugins(NoPublishPlugin)

lazy val http4s = Project(id = "tsec-http4s", base = file("tsec-http4s"))
  .settings(commonSettings)
  .settings(jwtCommonLibs)
  .settings(passwordHasherLibs)
  .settings(http4sDeps)
  .settings(loggingLibs)
  .dependsOn(common % "compile->compile;test->test")
  .dependsOn(
    symmetricCipher,
    mac,
    messageDigests,
    passwordHashers,
    jwtMac
  )

lazy val libsodium = Project(id = "tsec-libsodium", base = file("tsec-libsodium"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Libraries.fs2IO
    )
  )
  .settings(loggingLibs)
  .dependsOn(common % "compile->compile;test->test")
/*
lazy val microsite = Project(id = "microsite", base = file("docs"))
  .settings(commonSettings, noPublishSettings)
  .settings(micrositeSettings)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(TutPlugin)
  .dependsOn(
    common,
    messageDigests,
    cipherCore,
    jwtCore,
    symmetricCipher,
    mac,
    signatures,
    jwtMac,
    jwtSig,
    passwordHashers,
    http4s,
    examples
  )
*/