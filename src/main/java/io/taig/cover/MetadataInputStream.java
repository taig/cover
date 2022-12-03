package io.taig.cover;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import java.io.*;

/**
 * An {@link InputStream} that allows to tap into the stream, extract the image metadata and then reset to the first
 * byte
 */
public class MetadataInputStream extends InputStream {
  private final ByteArrayOutputStream metadata = new ByteArrayOutputStream(512);

  private final InputStream underlying;

  private InputStream result;

  public MetadataInputStream(InputStream input) {
    this.underlying = input;
  }

  public Metadata getMetadata() throws ImageProcessingException, IOException {
    if(result != null) {
      throw new IllegalStateException("Stream already consumed");
    }

    return ImageMetadataReader.readMetadata(new InputStream() {
      @Override
      public int read() throws IOException {
        int data = underlying.read();
        metadata.write(data);
        return data;
      }
    });
  }

  @Override
  public int read() throws IOException {
    if(result == null) {
      result = new SequenceInputStream(new ByteArrayInputStream(metadata.toByteArray()), underlying);
    }

    return result.read();
  }

  @Override
  public void close() throws IOException {
    super.close();
    underlying.close();
  }
}
