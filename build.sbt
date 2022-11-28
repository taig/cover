val Version = new {
  val Java = "17"
  val MetadataExtractor = "2.18.0"
}

enablePlugins(BlowoutYamlPlugin)

ThisBuild / dynverVTagPrefix := false

autoScalaLibrary := false

blowoutGenerators ++= {
  val github = file(".github")
  val workflows = github / "workflows"

  BlowoutYamlGenerator.lzy(workflows / "main.yml", GithubActionsGenerator.main(Version.Java)) ::
    BlowoutYamlGenerator.lzy(workflows / "pull-request.yml", GithubActionsGenerator.pullRequest(Version.Java)) ::
    Nil
}

crossPaths := false

developers := List(Developer("taig", "Niklas Klein", "mail@taig.io", url("https://taig.io/")))

homepage := Some(url("https://github.com/taig/cover/"))

libraryDependencies ++=
  "com.drewnoakes" % "metadata-extractor" % Version.MetadataExtractor ::
    Nil

licenses := List("MIT" -> url("https://raw.githubusercontent.com/taig/cover/main/LICENSE"))

name := "cover"

organization := "io.taig"

versionScheme := Some("early-semver")
