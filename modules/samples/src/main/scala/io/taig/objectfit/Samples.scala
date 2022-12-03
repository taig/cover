package io.taig.objectfit

import cats.effect.{IO, IOApp}
import io.taig.objectfit.ObjectFit

import java.io.File

object Samples extends IOApp.Simple:
  override def run: IO[Unit] = otter1

  def target(name: String): File = new File(s"./modules/samples/src/main/resources/$name")

  val otter1: IO[Unit] = IO.blocking {
    ObjectFit.of(getClass.getResourceAsStream("/otter-1.jpg"))
      .mode(ObjectFit.Mode.COVER)
      .size(250)
      .format("webp")
      .write(target("otter-1.result.webp"))
  }