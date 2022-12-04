package io.taig.objectfit

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
    val options = ObjectFit
      .options()
      .mode(ObjectFit.Mode.COVER)
      .size(300)
      .format("jpg")
    val image = ObjectFit.toBytes(getClass.getResourceAsStream("/Landscape_3.jpg"), options)

    assertEquals(obtained = image.toSeq, expected = loadImage("/Landscape_3.expected.jpg").toSeq)
  }

  test("webp") {
    val options = ObjectFit
      .options()
      .mode(ObjectFit.Mode.COVER)
      .size(500, 300)
      .format("webp")
      .imageType(BufferedImage.TYPE_INT_ARGB)
    val image = ObjectFit.toBytes(getClass.getResourceAsStream("/river.webp"), options)

    assertEquals(obtained = image.toSeq, expected = loadImage("/river.expected.webp").toSeq)
  }
}
