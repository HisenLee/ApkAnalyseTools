package libchecker.app.util.file.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class ByteArrayZipEntryTransformer implements ZipEntryTransformer {

  /**
   * Transforms the given byte array into a new one.
   */
  protected abstract byte[] transform(ZipEntry zipEntry, byte[] input) throws IOException;

  public void transform(InputStream in, ZipEntry zipEntry, ZipOutputStream out) throws IOException {
    byte[] bytes = IOUtils.toByteArray(in);
    bytes = transform(zipEntry, bytes);

    ByteSource source;

    if (preserveTimestamps()) {
      source = new ByteSource(zipEntry.getName(), bytes, zipEntry.getTime());
    }
    else {
      source = new ByteSource(zipEntry.getName(), bytes);
    }

    ZipEntrySourceZipEntryTransformer.addEntry(source, out);
  }

  /**
   * Override to return true if needed.
   */
  protected boolean preserveTimestamps() {
    return false;
  }

}
