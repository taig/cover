package io.taig.cover;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Cover {
  /**
   * Fit the image input into the `width` x `height` box
   *
   * @param input An {@link InputStream} of an image source, this method takes care of closing it
   * @param width
   * @param height
   * @return A {@link BufferedImage} that never exceeds the given `width` and `height` dimensions and always obeys
   * their aspect ratio
   * @throws IOException If the image can not be decoded
   */
  public static BufferedImage fit(InputStream input, int width, int height) throws IOException {
    BufferedInputStream bufferedInput = input instanceof BufferedInputStream
      ? (BufferedInputStream) input
      : new BufferedInputStream(input);

    bufferedInput.mark(8192);

    Metadata metadata;

    try {
      metadata = ImageMetadataReader.readMetadata(bufferedInput);
    } catch (ImageProcessingException exception) {
      bufferedInput.close();
      throw new IOException("Failed to extract image metadata", exception);
    }

    bufferedInput.reset();

    BufferedImage image;

    try {
      image = ImageIO.read(bufferedInput);
      bufferedInput.close();
    } catch (Exception exception) {
      bufferedInput.close();
      throw exception;
    }

    if(image == null) throw new IOException("No ImageReader available to decode InputStream");

    final ExifIFD0Directory exif = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
    final JpegDirectory jpeg = metadata.getFirstDirectoryOfType(JpegDirectory.class);

    AffineTransform transform = null;

    if(exif != null && jpeg != null) {
      try {
        final int orientation = exif.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        transform = getExifTransformation(orientation, jpeg.getImageWidth(), jpeg.getImageHeight());
      } catch(MetadataException ignored) {}
    }

    final BufferedImage rotatedImage = transform == null ? image : transformImage(image, transform);

    final int sourceWidth = rotatedImage.getWidth();
    final int sourceHeight = rotatedImage.getHeight();

    int croppedWidth;
    int croppedHeight;
    final double ratioWidth = (double )width / (double) sourceWidth;
    final double ratioHeight = (double )height / (double) sourceHeight;

    if(ratioWidth > ratioHeight) {
      croppedWidth = sourceWidth;
      croppedHeight = (int) Math.ceil(sourceWidth * ((double) height / (double) width));
    } else if(ratioWidth < ratioHeight) {
      croppedWidth = (int) Math.ceil(sourceHeight * ((double) width / (double) height));
      croppedHeight = sourceHeight;
    } else {
      croppedWidth = sourceWidth;
      croppedHeight = sourceHeight;
    }

    final BufferedImage croppedImage = rotatedImage.getSubimage(
      (sourceWidth - croppedWidth) / 2,
      (sourceHeight - croppedHeight) / 2,
      croppedWidth,
      croppedHeight
    );

    int scaledWidth;
    int scaledHeight;
    Image scaledImage;

    if(croppedWidth <= width && croppedHeight <= height) {
      scaledWidth = croppedWidth;
      scaledHeight = croppedHeight;
      scaledImage = croppedImage;
    } else {
      final double ratio = Math.max((double) width / (double) croppedWidth, (double) height / (double) croppedHeight);
      scaledWidth = (int) Math.ceil(croppedWidth * ratio);
      scaledHeight = (int) Math.ceil(croppedHeight * ratio);
      scaledImage = croppedImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    }

    final BufferedImage output = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
    output.getGraphics().drawImage(scaledImage, 0, 0, null);
    return output;
  }

  private static BufferedImage transformImage(BufferedImage image, AffineTransform transform) {
    final AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
    var target = op.createCompatibleDestImage(
      image,
      image.getType() == BufferedImage.TYPE_BYTE_GRAY ? image.getColorModel() : null
    );
    final Graphics2D graphics = target.createGraphics();
    graphics.setBackground(Color.WHITE);
    graphics.clearRect(0, 0, target.getWidth(), target.getHeight());
    target = op.filter(image, target);
    graphics.dispose();
    return target;
  }

  private static AffineTransform getExifTransformation(int orientation, int width, int height) {
    final AffineTransform transform = new AffineTransform();

    switch(orientation) {
      case 2:
        transform.scale(-1.0, 1.0);
        transform.translate(-width, 0);
        return transform;
      case 3:
        transform.translate(width, height);
        transform.rotate(Math.PI);
        return transform;
      case 4:
        transform.scale(1.0, -1.0);
        transform.translate(0, -height);
        return transform;
      case 5:
        transform.rotate(-Math.PI / 2);
        transform.scale(-1.0, 1.0);
        return transform;
      case 6:
        transform.translate(height, 0);
        transform.rotate(Math.PI / 2);
        return transform;
      case 7:
        transform.scale(-1.0, 1.0);
        transform.translate(-height, 0);
        transform.translate(0, width);
        transform.rotate(3 * Math.PI / 2);
        return transform;
      case 8:
        transform.translate(0, width);
        transform.rotate(3 * Math.PI / 2);
        return transform;
      default: return null;
    }
  }
}