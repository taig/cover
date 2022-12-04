import io.circe.Json
import io.circe.syntax._

object GithubActionsGenerator {
  object Step {
    def setupJava(version: String): Json = Json.obj(
      "name" := "Setup Java JDK",
      "uses" := "actions/setup-java@v3",
      "with" := Json.obj(
        "cache" := "sbt",
        "distribution" := "temurin",
        "java-version" := version
      )
    )

    val Checkout: Json = Json.obj(
      "name" := "Checkout",
      "uses" := "actions/checkout@v3",
      "with" := Json.obj(
        "fetch-depth" := 0
      )
    )
  }

  object Job {
    def test(javaVersion: String): Json = Json.obj(
      "name" := "Test",
      "runs-on" := "ubuntu-latest",
      "steps" := List(
        Step.Checkout,
        Step.setupJava(javaVersion),
        Json.obj(
          "name" := "Unit tests",
          "run" := "sbt -Dmode=ci test"
        )
      )
    )

    def lint(javaVersion: String): Json = Json.obj(
      "name" := "Lint",
      "runs-on" := "ubuntu-latest",
      "steps" := List(
        Step.Checkout,
        Step.setupJava(javaVersion),
        Json.obj(
          "name" := "Workflows",
          "run" := "sbt -Dmode=ci blowoutCheck"
        ),
        Json.obj(
          "name" := "Code formatting",
          "run" := "sbt -Dmode=ci scalafmtCheckAll"
        )
      )
    )
  }

  def main(javaVersion: String): Json = Json.obj(
    "name" := "CI & CD",
    "on" := Json.obj(
      "push" := Json.obj(
        "branches" := List("main"),
        "tags" := List("*.*.*")
      )
    ),
    "jobs" := Json.obj(
      "lint" := Job.lint(javaVersion),
      "test" := Job.test(javaVersion),
      "deploy" := Json.obj(
        "name" := "Deploy",
        "runs-on" := "ubuntu-latest",
        "needs" := List("lint", "test"),
        "steps" := List(
          Step.Checkout,
          Step.setupJava(javaVersion),
          Json.obj(
            "name" := "Release",
            "run" := "sbt ci-release",
            "env" := Json.obj(
              "PGP_PASSPHRASE" := "${{secrets.PGP_PASSPHRASE}}",
              "PGP_SECRET" := "${{secrets.PGP_SECRET}}",
              "SONATYPE_PASSWORD" := "${{secrets.SONATYPE_PASSWORD}}",
              "SONATYPE_USERNAME" := "${{secrets.SONATYPE_USERNAME}}"
            )
          )
        )
      )
    )
  )

  def pullRequest(javaVersion: String): Json = Json.obj(
    "name" := "CI",
    "on" := Json.obj(
      "pull_request" := Json.obj(
        "branches" := List("main")
      )
    ),
    "jobs" := Json.obj(
      "lint" := Job.lint(javaVersion),
      "test" := Job.test(javaVersion)
    )
  )
}
