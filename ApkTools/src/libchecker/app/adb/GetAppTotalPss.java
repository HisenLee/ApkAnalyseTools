
package libchecker.app.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GetAppTotalPss {
    // get app memusage i.e. PSS total
    public static final int getAppTotalPss(String deviceSerialNo, ArrayList<Integer> pids) {
        int total = 0;
        for (Integer pid : pids) {
            total = total + getPidPss(deviceSerialNo, pid);
        }
        System.out.println("GetAppTotalPss OK: "+ deviceSerialNo + " Total: " + total);
        return total;
    }

    // get pid memusage
    private static final int getPidPss(String deviceSerialNo, int pid) {
        int result = 0;
        ProcessBuilder b = null;
        if (deviceSerialNo == null) {
            b = new ProcessBuilder("adb", "shell", "dumpsys", "meminfo", "" + pid, "|", "grep",
                    "TOTAL");
        } else {
            b = new ProcessBuilder("adb", "-s", deviceSerialNo, "shell", "dumpsys", "meminfo", ""
                    + pid, "|", "grep", "TOTAL");
        }
        Process p = null;
        try {
            p = b.start();
        } catch (IOException e) {
            return result;
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
            } else {
                // for(String s : line.split(" ")){
                // System.out.println("s: " + s);
                // }
                if (line.contains("TOTAL")) {
                    //System.out.println(line);
                    String parsm = line.substring(line.indexOf("TOTAL") + 7,
                            line.indexOf("TOTAL") + 16);
                    result = Integer.parseInt(parsm.trim());
                }
            }
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("getPidPss OK: "+ deviceSerialNo + pid + " " + " PSS: " + result);
        return result;
    }
}
