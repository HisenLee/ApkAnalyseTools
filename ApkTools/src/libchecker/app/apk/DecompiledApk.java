
package libchecker.app.apk;

import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.file.FileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DecompiledApk {
    private String mApkFullName = null;
    //private String mDecompiledRootDir = null;
    private String mDecompileCurrentDir = null;
    private ArrayList<String> mSelfDeclaredPermission = null;
    private ArrayList<String> mUsingPermission = null;

    public DecompiledApk(String fullname) {
        mApkFullName = fullname;
        System.out.println("Apk: "+ mApkFullName);
        decompileApk();
        //mDecompiledRootDir = fullname.substring(0, fullname.length() - 4);
        mDecompileCurrentDir = mApkFullName.substring(
                mApkFullName.lastIndexOf(Env.slash()) + 1, mApkFullName.length() - 4);
        parseAndroidManifest();
    }

    public ArrayList<String> getSelfDeclaredPermission() {
        return mSelfDeclaredPermission;
    }

    public ArrayList<String> getUsingPermission() {
        return mUsingPermission;
    }

    public void destroy() {
        //FileOperation.deleteRecursive(new File(mDecompiledRootDir));
        FileOperation.deleteRecursive(new File(mDecompileCurrentDir));
    }

    private boolean decompileApk() {
        ProcessBuilder b = null;
        b = new ProcessBuilder("java", "-jar", "./apktool.jar", "d", "-s", mApkFullName);
        Process p = null;
        try {
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
            System.out.println("Line: " + line);
            if (line == null || line.length() < 0) {
                break;
            }
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            // ignore
        }
        return true;
    }

    private synchronized boolean parseAndroidManifest() {
        if (mSelfDeclaredPermission != null) {
            return true;
        }
        mSelfDeclaredPermission = new ArrayList<String>();
        mUsingPermission = new ArrayList<String>();

        File manifest = new File(mDecompileCurrentDir + Env.slash() + "AndroidManifest.xml");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(manifest));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<uses-permission")) {
                    mUsingPermission.add(line.trim());
                } else if (line.contains("<permission ")) {
                    line = line.trim().substring(line.indexOf("android:name=\""));
                    mSelfDeclaredPermission.add(line.split("\"")[1]);
                } else {
                    // TBD;
                }
            }
            br.close();
        } catch (Exception e) {
            mSelfDeclaredPermission = null;
            mUsingPermission = null;
            Log.e("Error: "+e.getMessage());
            return false;
        }
        return true;
    }
}
