package io.taig.objectfit;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

public final class ObjectFit {
  public enum Mode {
    CONTAIN, COVER, FILL
  }

  public static class Options {
    public final int width;
    public final int height;
    public final ObjectFit.Mode mode;
    public final String format;
    public final boolean autoRotateJpg;
    public final boolean scaleUp;
    public final int imageType;

    private Options(int width, int height, Mode mode, String format, boolean autoRotateJpg, boolean scaleUp, int imageType) {
      this.width = width;
      this.height = height;
      this.mode = mode;
      this.format = format;
      this.autoRotateJpg = autoRotateJpg;
      this.scaleUp = scaleUp;
      this.imageType = imageType;
    }

    /**
     * Set width and height independently
     *
     * @param width maximum width of the rendered image (default: {@code 500})
     * @param height maximum height of the rendered image (default: {@code 500})
     * @see #size(int)
     */
    public Options size(int width, int height) {
      return new Options(width, height, mode, format, autoRotateJpg, scaleUp, imageType);
    }

    /**
     * Set width and height to the same dimension to render a square image
     *
     * @param dimension equal width and height (default: {@code 500})
     * @see #size(int, int)
     */
    public Options size(int dimension) {
      return size(dimension, dimension);
    }

    /**
     * Set the scaling mode, inspired by CSS `object-fit`
     *
     * @param mode scaling mode (default: {@code CONTAIN})
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/object-fit">developer.mozilla.org</a>
     */
    public Options mode(ObjectFit.Mode mode) {
      return new Options(width, height, mode, format, autoRotateJpg, scaleUp, imageType);
    }

    /**
     * Set the output image format
     *
     * @param format Image format, such as {@code "png"} or {@code "jpg"} (default: {@code "png"})
     */
    public Options format(String format) {
      return new Options(width, height, mode, format, autoRotateJpg, scaleUp, imageType);
    }

    public Options autoRotateJpg(boolean enabled) {
      return new Options(width, height, mode, format, enabled, scaleUp, imageType);
    }

    public Options autoRotateJpg() {
      return autoRotateJpg(true);
    }

    /**
     * Whether images that are smaller than the given dimensions should be up-scaled
     *
     * @param enabled (default: {@code true})
     */
    public Options scaleUp(boolean enabled) {
      return new Options(width, height, mode, format, autoRotateJpg, enabled, imageType);
    }

    /**
     * @see #scaleUp(boolean)
     */
    public Options scaleUp() {
      return scaleUp(true);
    }

    public Options imageType(int imageType) {
      return new Options(width, height, mode, format, autoRotateJpg, scaleUp, imageType);
    }
  }

  private ObjectFit() {}

  public static ObjectFit.Options options() {
    return new Options(500, 500, Mode.CONTAIN, "png", true, false, BufferedImage.TYPE_INT_RGB);
  }

  public static BufferedImage toBufferedImage(InputStream input, Options options) throws IOException {
    MetadataInputStream metadataInput = new MetadataInputStream(input);

    Metadata metadata;

    try {
      metadata = options.autoRotateJpg ? metadataInput.getJpegMetadata() : null;
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

    switch (options.mode) {
      case CONTAIN: return Awts.contain(rotatedImage, options.width, options.height, options.imageType, options.scaleUp);
      case COVER: return Awts.cover(rotatedImage, options.width, options.height, options.imageType, options.scaleUp);
      default: return Awts.fill(rotatedImage, options.width, options.height, options.imageType, options.scaleUp);
    }
  }

  public static byte[] toBytes(InputStream input, Options options) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream(1024 * 8);
    ImageIO.write(toBufferedImage(input, options), options.format, output);
    return output.toByteArray();
  }

  public static void write(InputStream input, Options options, OutputStream output) throws IOException {
    ImageIO.write(toBufferedImage(input, options), options.format, output);
  }

  public static void write(InputStream input, Options options, ImageOutputStream output) throws IOException {
    ImageIO.write(toBufferedImage(input, options), options.format, output);
  }
  
  public static void write(InputStream input, Options options, File file) throws IOException {
    ImageIO.write(toBufferedImage(input, options), options.format, file);
  }
}
