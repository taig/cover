package io.taig.objectfit

import cats.effect.{IO, IOApp}
import io.taig.objectfit.ObjectFit

import java.io.File

object Samples extends IOApp.Simple:
  override def run: IO[Unit] = otter1 *> otter2 *> otter3

  def target(name: String): File = new File(s"./modules/samples/src/main/resources/$name")

  val otter1: IO[Unit] = IO.blocking {
    ObjectFit.of(getClass.getResourceAsStream("/otter-1.jpg"))
      .mode(ObjectFit.Mode.COVER)
      .size(250, 150)
      .format("webp")
      .write(target("otter-1.result.webp"))
  }

  val otter2: IO[Unit] = IO.blocking {
    ObjectFit.of(getClass.getResourceAsStream("/otter-2.jpg"))
      .mode(ObjectFit.Mode.COVER)
      .size(250, 150)
      .format("webp")
      .write(target("otter-2.result.webp"))
  }

  val otter3: IO[Unit] = IO.blocking {
    ObjectFit.of(getClass.getResourceAsStream("/otter-3.jpg"))
      .mode(ObjectFit.Mode.COVER)
      .size(250, 150)
      .format("webp")
      .write(target("otter-3.result.webp"))
  }