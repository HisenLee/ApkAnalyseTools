
package libchecker.app.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GetAppProcessIDs {
    // get package pids
    public static final ArrayList<Integer> getPIDs(String deviceSerialNo, String packageName) {
        ProcessBuilder b = null;
        if (deviceSerialNo == null) {
            b = new ProcessBuilder("adb", "shell", "ps", "|", "grep", packageName);
        } else {
            b = new ProcessBuilder("adb", "-s", deviceSerialNo, "shell", "ps", "|", "grep",
                    packageName);
        }
        Process p = null;
        try {
            System.out.println("getPIDs starts: "+deviceSerialNo+"  "+packageName );
            p = b.start();
        } catch (IOException e) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        ArrayList<Integer> PIDs = new ArrayList<Integer>();
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException e) {
                break;
            }
            //System.out.println("Line: " + line);
            if (line == null || line.length() < 0) {
                break;
            } else {
                // for (String s: line.split(" ")){
                // System.out.println("xx: " + s);
                // }
                if (line.startsWith("u")) {
                    String parsm = line.substring(8, 16);
                    PIDs.add(Integer.parseInt(parsm.trim()));
                }
            }
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            // ignore
        }
        String Pids = "";
        for(Integer pid : PIDs){
            Pids = Pids+ pid+" ";
        }
        System.out.println("getPIDs OK: "+deviceSerialNo+"  "+packageName +"  "+Pids);
        return PIDs;
    }
}
