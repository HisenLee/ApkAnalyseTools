
package libchecker.app.util;


public class Log {

    public static final int V = 0;
    public static final int D = 1;
    public static final int I = 2;
    public static final int E = 3;
    
    public static int LOG_LEVEL = I;
    
    public static void e(String content) {
        if(LOG_LEVEL <= E){
            out(content);
        }
    }
    
    public static void i(String content) {
        if(LOG_LEVEL <= I){
            out(content);
        }
    }
    
    public static void d(String content) {
        if(LOG_LEVEL <= D){
            out(content);
        }
    }
    
    public static void v(String content) {
        if(LOG_LEVEL <= V){
            out(content);
        }
    }
    
    private static void out(String content) {
        if (content != null) {
            System.out.println(content);
        } else {
            System.out.println("");
        }
    }
}
