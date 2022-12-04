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
  private final ByteArrayOutputStream marker = new ByteArrayOutputStream(4);

  private final ByteArrayOutputStream metadata = new ByteArrayOutputStream(512);

  private final InputStream underlying;

  private InputStream result;

  public MetadataInputStream(InputStream input) {
    this.underlying = input;
  }

  public Metadata getJpegMetadata() throws ImageProcessingException, IOException {
    if(result != null) {
      throw new IllegalStateException("Stream already consumed");
    }

    final InputStream markerInput = new InputStream() {
      @Override
      public int read() throws IOException {
        int data = underlying.read();
        marker.write(data);
        return data;
      }
    };

    final int marker = new DataInputStream(markerInput).readInt();

    // https://stackoverflow.com/a/15539831/1493269
    if(marker != 0xffd8ffe0) return null;

    final InputStream metadataInput = new InputStream() {
      @Override
      public int read() throws IOException {
        int data = underlying.read();
        metadata.write(data);
        return data;
      }
    };

    return ImageMetadataReader.readMetadata(
      new SequenceInputStream(new ByteArrayInputStream(this.marker.toByteArray()), metadataInput)
    );
  }

  @Override
  public int read() throws IOException {
    if(result == null) {
      result = new SequenceInputStream(
        new SequenceInputStream(
          new ByteArrayInputStream(marker.toByteArray()),
          new ByteArrayInputStream(metadata.toByteArray())
        ),
        underlying
      );
    }

    return result.read();
  }

  @Override
  public void close() throws IOException {
    super.close();
    underlying.close();
  }
}
