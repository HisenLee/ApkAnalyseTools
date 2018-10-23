package libchecker.app.adb;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckPackageCrashDialog {
    public static boolean docheck(String packageName, String deviceid) {
        System.out.println("look for crash dialog with device id: " + deviceid);
        ProcessBuilder b;
        Process deamon = null;
        if (deviceid == null) {
            b = new ProcessBuilder();
            b.command().add("adb");
            b.command().add("shell");
            b.command().add("dumpsys");
            b.command().add("window");
            b.command().add("windows");
        } else {
            b = new ProcessBuilder();
            b.command().add("adb");
            b.command().add("-s");
            b.command().add(deviceid);
            b.command().add("shell");
            b.command().add("dumpsys");
            b.command().add("window");
            b.command().add("windows");
        }
        boolean found = false;
        try {
            deamon = b.start();
        } catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }

        if (deamon != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    deamon.getInputStream()));
            String line = null;
            while (true) {
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                }
                // System.out.println(line);
                if (line != null) {
                    if(line.contains(packageName)){
                        if(line.contains("Application Error: ") || line.contains("Application Not Responding: ")){
                            found = true;
                        }
                    }
                } else {
                    break;
                }
            }
        }
        try {
            deamon.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deamon.destroy();
        if(found){
            System.out.println("Crash/ANR dialog found for package: " + packageName);
        }else{
            //System.out.println("No crash dialog found for package: " + packageName);
        }
        return found;
    }
}
