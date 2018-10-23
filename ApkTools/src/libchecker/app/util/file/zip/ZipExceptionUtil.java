package libchecker.app.util.file.zip;

import java.io.IOException;

class ZipExceptionUtil {

  /**
   * Rethrow the given exception as a runtime exception.
   */
  static ZipException rethrow(IOException e) {
    throw new ZipException(e);
  }

}
