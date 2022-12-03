val Version = new {
  val CatsEffect = "3.4.2"
  val Java = "17"
  val MetadataExtractor = "2.18.0"
  val MUnit = "0.7.29"
  val Scala = "3.2.1"
  val WebpImageIo = "0.1.6"
}

inThisBuild(
  Def.settings(
    developers := List(Developer("taig", "Niklas Klein", "mail@taig.io", url("https://taig.io/"))),
    dynverVTagPrefix := false,
    homepage := Some(url("https://github.com/taig/object-fit/")),
    licenses := List("MIT" -> url("https://raw.githubusercontent.com/taig/object-fit/main/LICENSE")),
    organization := "io.taig",
    versionScheme := Some("early-semver")
  )
)

lazy val root = project
  .in(file("."))
  .enablePlugins(BlowoutYamlPlugin)
  .settings(
    blowoutGenerators ++= {
      val github = file(".github")
      val workflows = github / "workflows"

      BlowoutYamlGenerator.lzy(workflows / "main.yml", GithubActionsGenerator.main(Version.Java)) ::
        BlowoutYamlGenerator.lzy(workflows / "pull-request.yml", GithubActionsGenerator.pullRequest(Version.Java)) ::
        Nil
    },
    name := "object-fit"
  )
  .aggregate(core)

lazy val core = project
  .in(file("modules/core"))
  .settings(
    autoScalaLibrary := false,
    crossPaths := false,
    libraryDependencies ++=
      "com.drewnoakes" % "metadata-extractor" % Version.MetadataExtractor ::
        "org.scalameta" %% "munit" % Version.MUnit % "test" ::
        "org.sejda.imageio" % "webp-imageio" % Version.WebpImageIo % "test" ::
        Nil,
    name := "object-fit"
  )

lazy val samples = project
  .in(file("modules/samples"))
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++=
      "org.sejda.imageio" % "webp-imageio" % Version.WebpImageIo ::
        "org.typelevel" %% "cats-effect" % Version.CatsEffect ::
        Nil,
    name := "object-fit-samples",
    scalaVersion := Version.Scala
  )
  .dependsOn(core)
