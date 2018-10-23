package libchecker.app.util.file.zip;

@SuppressWarnings("serial")
public class ZipBreakException extends RuntimeException {
  public ZipBreakException(String msg) {
    super(msg);
  }

  public ZipBreakException(Exception e) {
    super(e);
  }

  public ZipBreakException() {
    super();
  }
}
