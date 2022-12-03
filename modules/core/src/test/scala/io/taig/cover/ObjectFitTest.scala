package io.taig.cover

import munit.FunSuite

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

final class ObjectFitTest extends FunSuite {
  override def beforeAll(): Unit = {
    super.beforeAll()
    System.setProperty("java.awt.headless", "true")
    ImageIO.scanForPlugins()
    ()
  }

  def loadImage(path: String): Array[Byte] = {
    val input = getClass.getResourceAsStream(path)
    val result = input.readAllBytes()
    input.close()
    result
  }

  test("jpg (rotated)") {
    val image = ObjectFit
      .of(getClass.getResourceAsStream("/Landscape_3.jpg"))
      .mode(ObjectFit.Mode.COVER)
      .size(300)
      .format("jpg")
      .toBytes

    assertEquals(obtained = image.toSeq, expected = loadImage("/Landscape_3.expected.jpg").toSeq)
  }

  test("webp") {
    val image = ObjectFit
      .of(getClass.getResourceAsStream("/river.webp"))
      .mode(ObjectFit.Mode.COVER)
      .size(500, 300)
      .format("webp")
      .imageType(BufferedImage.TYPE_INT_ARGB)
      .toBytes

    assertEquals(obtained = image.toSeq, expected = loadImage("/river.expected.webp").toSeq)
  }
}
