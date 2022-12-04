package io.taig.objectfit

import cats.effect.{IO, IOApp}
import io.taig.objectfit.ObjectFit

import java.io.File

object Samples extends IOApp.Simple:
  override def run: IO[Unit] =
    IO(System.setProperty("java.awt.headless", "true")) *>
    one("/otter.1.jpg", "otter.1.1.result.webp") *>
    one("/otter.2.jpg", "otter.1.2.result.webp") *>
    one("/otter.3.jpg", "otter.1.3.result.webp") *>
    two("/otter.3.jpg", "otter.2.1.result.webp") *>
      three("/otter.1.jpg", "otter.3.1.result.webp") *>
      three("/otter.2.jpg", "otter.3.2.result.webp") *>
      three("/otter.3.jpg", "otter.3.3.result.webp")

  def target(name: String): File = new File(s"./modules/samples/src/main/resources/$name")

  def one(source: String, destination: String): IO[Unit] = IO.blocking {
    ObjectFit.of(getClass.getResourceAsStream(source))
      .mode(ObjectFit.Mode.COVER)
      .size(250, 150)
      .format("webp")
      .write(target(destination))
  }

  def two(source: String, destination: String): IO[Unit] = IO.blocking {
    ObjectFit.of(getClass.getResourceAsStream(source))
      .mode(ObjectFit.Mode.COVER)
      .size(250, 150)
      .format("webp")
      .scaleUp()
      .write(target(destination))
  }

  def three(source: String, destination: String): IO[Unit] = IO.blocking {
    ObjectFit.of(getClass.getResourceAsStream(source))
      .mode(ObjectFit.Mode.FILL)
      .size(250, 250)
      .format("png")
      .write(target(destination))
  }