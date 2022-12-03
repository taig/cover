package io.taig.objectfit;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

public class ObjectFit {
  enum Mode {
    CONTAIN, COVER, FILL
  }

  private final InputStream input;

  public final int width;
  public final int height;
  public final ObjectFit.Mode mode;
  public final String format;
  public final boolean autoRotateJpg;
  public final boolean scaleUp;
  public final int imageType;

  private ObjectFit(InputStream input, int width, int height, Mode mode, String format, boolean autoRotateJpg, boolean scaleUp, int imageType) {
    this.input = input;
    this.width = width;
    this.height = height;
    this.mode = mode;
    this.format = format;
    this.autoRotateJpg = autoRotateJpg;
    this.scaleUp = scaleUp;
    this.imageType = imageType;
  }

  public static ObjectFit of(InputStream input) {
    return new ObjectFit(input, 500, 500, Mode.CONTAIN, "png", true, false, BufferedImage.TYPE_INT_RGB);
  }

  public static ObjectFit of(File file) throws FileNotFoundException {
    return of(new FileInputStream(file));
  }

  /**
   * Set width and height independently
   *
   * @param width maximum width of the rendered image (default: 500)
   * @param height maximum height of the rendered image (default: 500)
   * @see #size(int)
   */
  public ObjectFit size(int width, int height) {
    return new ObjectFit(input, width, height, mode, format, autoRotateJpg, scaleUp, imageType);
  }

  /**
   * Set width and height to the same dimension to render a square image
   *
   * @param dimension equal width and height (default: 500)
   * @see #size(int, int)
   */
  public ObjectFit size(int dimension) {
    return size(dimension, dimension);
  }

  public ObjectFit mode(ObjectFit.Mode mode) {
    return new ObjectFit(input, width, height, mode, format, autoRotateJpg, scaleUp, imageType);
  }

  public ObjectFit format(String format) {
    return new ObjectFit(input, width, height, mode, format, autoRotateJpg, scaleUp, imageType);
  }

  public ObjectFit autoRotateJpg(boolean enabled) {
    return new ObjectFit(input, width, height, mode, format, enabled, scaleUp, imageType);
  }

  public ObjectFit autoRotateJpg() {
    return autoRotateJpg(true);
  }

  public ObjectFit scaleUp(boolean enabled) {
    return new ObjectFit(input, width, height, mode, format, autoRotateJpg, enabled, imageType);
  }

  public ObjectFit scaleUp() {
    return scaleUp(true);
  }

  public ObjectFit imageType(int imageType) {
    return new ObjectFit(input, width, height, mode, format, autoRotateJpg, scaleUp, imageType);
  }

  public BufferedImage toBufferedImage() throws IOException {
    MetadataInputStream metadataInput = new MetadataInputStream(input);

    Metadata metadata;

    try {
      metadata = autoRotateJpg ? metadataInput.getJpegMetadata() : null;
    } catch (ImageProcessingException exception) {
      metadataInput.close();
      throw new IOException("Failed to extract image metadata", exception);
    }

    BufferedImage image;

    try {
      image = ImageIO.read(metadataInput);
      metadataInput.close();
    } catch (Exception exception) {
      metadataInput.close();
      throw exception;
    }

    if(image == null) throw new IOException("No ImageReader available to decode InputStream");

    final BufferedImage rotatedImage = metadata != null ? Awts.exifRotateImage(image, metadata) : image;

    switch (mode) {
      case CONTAIN: throw new UnsupportedOperationException();
      case COVER: return Awts.cover(rotatedImage, width, height, imageType);
      default: throw new UnsupportedOperationException();
    }
  }

  public byte[] toBytes() throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream(1024 * 8);
    ImageIO.write(toBufferedImage(), format, output);
    return output.toByteArray();
  }

  public void write(OutputStream output) throws IOException {
    ImageIO.write(toBufferedImage(), format, output);
  }

  public void write(ImageOutputStream output) throws IOException {
    ImageIO.write(toBufferedImage(), format, output);
  }
  
  public void write(File file) throws IOException {
    ImageIO.write(toBufferedImage(), format, file);
  }
}
