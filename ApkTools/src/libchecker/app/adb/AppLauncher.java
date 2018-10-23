
package libchecker.app.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppLauncher {
    // launch app by package name
    public static final boolean launchApp(String deviceSerialNo, String packageName) {
        ProcessBuilder b = null;
        if (deviceSerialNo == null) {
            b = new ProcessBuilder("adb", "shell", "monkey", "-p", packageName, "-c",
                    "android.intent.category.LAUNCHER", "1");
        } else {
            b = new ProcessBuilder("adb", "-s", deviceSerialNo, "shell", "monkey", "-p",
                    packageName, "-c", "android.intent.category.LAUNCHER", "1");
        }
        Process p = null;
        try {
            System.out.println("launchApp starts: "+ deviceSerialNo +" "+ packageName );
            p = b.start();
        } catch (IOException e) {
            return false;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException e) {
                break;
            }
            //System.out.println("Line: " + line);
            if (line == null || line.length() < 0) {
                break;
            }
        }
        try {
            p.waitFor();
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("launchApp OK: "+ deviceSerialNo +" "+ packageName );
        return true;
    }
}
