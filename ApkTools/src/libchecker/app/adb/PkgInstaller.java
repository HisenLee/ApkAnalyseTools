
package libchecker.app.adb;

import libchecker.app.util.Env;
import libchecker.app.util.file.FileOperation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PkgInstaller {
    // block until install finish
    public static final boolean installApk(String deviceSerialNo, String apkFile) {
        //System.out.println("cp apk file...");
        try {
            FileOperation.copyFile(apkFile, "./tmpApkFile.apk");
        } catch (IOException e1) {
            System.out.println("exe!!! " + e1.getLocalizedMessage());
            return false;
        }
        ProcessBuilder b = null;
        if (deviceSerialNo == null) {
            b = new ProcessBuilder("adb", "install", "-r",
                    "."+Env.slash()+"tmpApkFile.apk");
        } else {
            b = new ProcessBuilder("adb", "-s", deviceSerialNo, "install", "-r",
                    "."+Env.slash()+"tmpApkFile.apk");
        }
        Process p = null;
        try {
            //System.out.println("installing apk...");
            p = b.start();
        } catch (IOException e) {
            System.out.println("exe!!! " + e.getLocalizedMessage());
            return false;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException e) {
                System.out.println("exe!!! " + e.getLocalizedMessage());
                break;
            }
            //System.out.println("Line: " + line);
            if (line == null || line.length() < 0) {
                break;
            } else if (line.contains("Success")) {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    // ignore
                }
                System.out.println("installApk OK: "+deviceSerialNo +" apkFile: "+apkFile);
                return true;
            }
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            // ignore
        }
        return false;
    }

    // block until uninstall finish
    public static final boolean uninstallApp(String deviceSerialNo, String packageName) {

        ProcessBuilder b = null;
        if (deviceSerialNo == null) {
            b = new ProcessBuilder("adb", "uninstall", packageName);
        } else {
            b = new ProcessBuilder("adb", "-s", deviceSerialNo, "uninstall", packageName);
        }
        Process p = null;
        try {
            //System.out.println("uninstalling apk...");
            p = b.start();
        } catch (IOException e) {
            System.out.println("exe: " + e.getLocalizedMessage());
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
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("uninstallApp OK: "+deviceSerialNo +" packageName: "+packageName);
        return true;
    }
}
