package io.taig.objectfit;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import java.io.*;

/**
 * An {@link InputStream} that allows to tap into the stream, extract the image metadata and then reset to the first
 * byte while only buffering the metadata related bytes
 */
public class MetadataInputStream extends InputStream {
  private final InputStream underlying;

  private volatile InputStream result;

  public MetadataInputStream(InputStream input) {
    this.underlying = input;
  }

  public Metadata getJpegMetadata() throws ImageProcessingException, IOException {
    if(result != null) {
      throw new IllegalStateException("Stream already consumed");
    }

    final ByteArrayOutputStream markerBuffer = new ByteArrayOutputStream(4);

    final InputStream markerInput = new InputStream() {
      @Override
      public int read() throws IOException {
        int data = underlying.read();
        markerBuffer.write(data);
        return data;
      }
    };

    final int marker = new DataInputStream(markerInput).readInt();

    // https://stackoverflow.com/a/15539831/1493269
    if(marker != 0xffd8ffe0) {
      result = new SequenceInputStream(new ByteArrayInputStream(markerBuffer.toByteArray()), underlying);
      return null;
    }

    final ByteArrayOutputStream metadataBuffer = new ByteArrayOutputStream(512);

    final InputStream metadataInput = new InputStream() {
      @Override
      public int read() throws IOException {
        int data = underlying.read();
        metadataBuffer.write(data);
        return data;
      }
    };

    try {
      return ImageMetadataReader.readMetadata(
        new SequenceInputStream(new ByteArrayInputStream(markerBuffer.toByteArray()), metadataInput)
      );
    } finally {
      result = new SequenceInputStream(
        new SequenceInputStream(
          new ByteArrayInputStream(markerBuffer.toByteArray()),
          new ByteArrayInputStream(metadataBuffer.toByteArray())
        ),
        underlying
      );
    }
  }

  @Override
  public int read() throws IOException {
    if(result == null) {
      throw new IllegalStateException("getJpegMetadata has not been called");
    }

    return result.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    if(result == null) {
      throw new IllegalStateException("getJpegMetadata has not been called");
    }

    return result.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if(result == null) {
      throw new IllegalStateException("getJpegMetadata has not been called");
    }

    return result.read(b, off, len);
  }

  @Override
  public byte[] readAllBytes() throws IOException {
    if(result == null) {
      throw new IllegalStateException("getJpegMetadata has not been called");
    }

    return result.readAllBytes();
  }

  @Override
  public byte[] readNBytes(int len) throws IOException {
    if(result == null) {
      throw new IllegalStateException("getJpegMetadata has not been called");
    }

    return result.readNBytes(len);
  }

  @Override
  public int readNBytes(byte[] b, int off, int len) throws IOException {
    if(result == null) {
      throw new IllegalStateException("getJpegMetadata has not been called");
    }

    return result.readNBytes(b, off, len);
  }

  @Override
  public void close() throws IOException {
    underlying.close();
  }
}
