package io.taig.cover

import munit.FunSuite

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

final class CoverTest extends FunSuite {
  override def beforeAll(): Unit = {
    super.beforeAll()
    System.setProperty("java.awt.headless", "true")
    ()
  }

  def toBytes(image: BufferedImage, format: String): Array[Byte] = {
    val output = new ByteArrayOutputStream()
    ImageIO.write(image, format, output)
    val bytes = output.toByteArray
    output.close()
    bytes
  }

  def loadImage(path: String): Array[Byte] = {
    val input = getClass.getResourceAsStream(path)
    val result = input.readAllBytes()
    input.close()
    result
  }

  test("landscape") {
    val image = Cover.fit(
      getClass.getResourceAsStream("/Landscape_3.jpg"),
      300,
      300,
      BufferedImage.TYPE_INT_RGB
    )

    assertEquals(obtained = toBytes(image, "jpg").toSeq, expected = loadImage("/Landscape_3.expected.jpg").toSeq)
  }
}
