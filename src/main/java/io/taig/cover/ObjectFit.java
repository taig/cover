package io.taig.cover;

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
  public final boolean scaleUp;

  private ObjectFit(InputStream input, int width, int height, Mode mode, String format, boolean scaleUp) {
    this.input = input;
    this.width = width;
    this.height = height;
    this.mode = mode;
    this.format = format;
    this.scaleUp = scaleUp;
  }

  public static ObjectFit of(InputStream input) {
    return new ObjectFit(input, 500, 500, Mode.CONTAIN, "png", true);
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
    return new ObjectFit(input, width, height, mode, format, scaleUp);
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
    return new ObjectFit(input, width, height, mode, format, scaleUp);
  }

  public ObjectFit format(String format) {
    return new ObjectFit(input, width, height, mode, format, scaleUp);
  }

  public ObjectFit scaleUp(boolean enabled) {
    return new ObjectFit(input, width, height, mode, format, enabled);
  }

  public ObjectFit scaleUp() {
    return scaleUp(true);
  }

  public BufferedImage toBufferedImage() {
    return null;
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
