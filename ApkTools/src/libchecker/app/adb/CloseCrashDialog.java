package libchecker.app.adb;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CloseCrashDialog {
    public static void doClose(String deviceid) {
        System.out.println("closeCrashDialog with device id: "+deviceid);
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
        boolean crashed = false;
   		try {			
   			deamon= b.start();
        }catch(IOException ex)
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
                //System.out.println(line);
                if (line != null) {
                    if (line.contains("Application Error: ")) {
                        crashed = true;
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

        if (crashed) {
            sendKey(deviceid, 20);
            sendKey(deviceid, 22);
            sendKey(deviceid, 23);
        }
        if(crashed){
            crashed = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doClose(deviceid);
        }
    }

    private static void sendKey(String deviceid, int key) {
        ProcessBuilder b2;
        Process deamon2 = null;
        if (deviceid == null) {
            b2 = new ProcessBuilder();
            b2.command().add("adb");
            b2.command().add("shell");
            b2.command().add("input");
            b2.command().add("keyevent");
            b2.command().add("" + key);
        } else {
            b2 = new ProcessBuilder();
            b2.command().add("adb");
            b2.command().add("-s");
            b2.command().add(deviceid);
            b2.command().add("shell");
            b2.command().add("input");
            b2.command().add("keyevent");
            b2.command().add("" + key);
        }
        try {
            deamon2 = b2.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            deamon2.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deamon2.destroy();
    }
}
