
package libchecker.app.util;

public class Env {

    //public static final int NUMBER_3RD_LIBS = 402;
    // make it more flexible
    public static int NUMBER_APPS = -1;

    public static final String RANKING_FILE = "ranking.txt";

    private static char SLASH = 0;
    private static String LINE_SEPARATOR = null;

    public static final char slash() {
        if (SLASH != 0) {
            return SLASH;
        }
        SLASH = '/';
        boolean isWindowsHost = System.getProperty("os.name").startsWith(
                "Windows");
        if (isWindowsHost) {
            SLASH = '\\';
        }
        return SLASH;
    }

    public static final String lineSeparator() {
        if(LINE_SEPARATOR == null){
            LINE_SEPARATOR = System.getProperty("line.separator");
        }
        return LINE_SEPARATOR;
    }
}
