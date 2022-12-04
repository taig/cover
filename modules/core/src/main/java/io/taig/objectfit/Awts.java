package io.taig.objectfit;

import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

class Awts {
  static BufferedImage cover(BufferedImage image, int width, int height, int imageType, boolean scaleUp) {
    final int sourceWidth = image.getWidth();
    final int sourceHeight = image.getHeight();

    int croppedWidth;
    int croppedHeight;
    final double ratioWidth = (double) width / (double) sourceWidth;
    final double ratioHeight = (double) height / (double) sourceHeight;

    if(ratioWidth > ratioHeight) {
      croppedWidth = sourceWidth;
      croppedHeight = (int) (sourceWidth * ((double) height / (double) width));
    } else if(ratioWidth < ratioHeight) {
      croppedWidth = (int) (sourceHeight * ((double) width / (double) height));
      croppedHeight = sourceHeight;
    } else {
      croppedWidth = sourceWidth;
      croppedHeight = sourceHeight;
    }

    final BufferedImage croppedImage = image.getSubimage(
      (sourceWidth - croppedWidth) / 2,
      (sourceHeight - croppedHeight) / 2,
      croppedWidth,
      croppedHeight
    );

    int scaledWidth;
    int scaledHeight;
    Image scaledImage;

    if(!scaleUp && croppedWidth <= width && croppedHeight <= height) {
      scaledWidth = croppedWidth;
      scaledHeight = croppedHeight;
      scaledImage = croppedImage;
    } else {
      final double ratio = Math.max((double) width / (double) croppedWidth, (double) height / (double) croppedHeight);
      scaledWidth = (int) (croppedWidth * ratio);
      scaledHeight = (int) (croppedHeight * ratio);
      scaledImage = croppedImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    }

    final BufferedImage output = new BufferedImage(scaledWidth, scaledHeight, imageType);
    output.getGraphics().drawImage(scaledImage, 0, 0, null);
    return output;
  }

  static BufferedImage fill(BufferedImage image, int width, int height, int imageType, boolean scaleUp) {
    final int imageWidth = image.getWidth();
    final int imageHeight = image.getHeight();

    if(imageWidth == width && imageHeight == height) return image;

    final int scaledWidth = width >= imageWidth ? scaleUp ? width : imageWidth : width;
    final int scaledHeight = height >= imageHeight ? scaleUp ? height : imageHeight : height;

    Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

    if(scaledImage instanceof BufferedImage) return (BufferedImage) scaledImage;
    else {
      final BufferedImage output = new BufferedImage(scaledWidth, scaledHeight, imageType);
      output.getGraphics().drawImage(scaledImage, 0, 0, null);
      return output;
    }
  }

  static BufferedImage exifRotateImage(BufferedImage image, Metadata metadata) {
    final ExifIFD0Directory exif = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
    final JpegDirectory jpeg = metadata.getFirstDirectoryOfType(JpegDirectory.class);

    AffineTransform transform = null;

    if(exif != null && jpeg != null) {
      try {
        final int orientation = exif.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        transform = getExifTransformation(orientation, jpeg.getImageWidth(), jpeg.getImageHeight());
      } catch(MetadataException ignored) {}
    }

    return transform == null ? image : transformImage(image, transform);
  }

  static BufferedImage transformImage(BufferedImage image, AffineTransform transform) {
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

  static AffineTransform getExifTransformation(int orientation, int width, int height) {
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
