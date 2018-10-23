package libchecker.app.adb;

public interface CrashCallback {
    void onProcessDied(String packageName, String serialNo);
}
