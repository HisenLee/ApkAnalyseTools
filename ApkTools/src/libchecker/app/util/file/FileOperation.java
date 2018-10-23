
package libchecker.app.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import libchecker.app.util.Env;
import libchecker.app.util.Log;

public class FileOperation {

    private static long TOTAL_SIZE = 0;
    private static long START_TIME = 0;

    public static String generateLogFile() {
        return "log-" + getCurrentTimeStamp() + ".txt";
    }

    public static String getPath(String file) {
        if (file == null) {
            return Env.slash() + "";
        }
        if (!file.contains(Env.slash() + "")) {
            return "." + Env.slash();
        }
        return file.substring(0, file.lastIndexOf(Env.slash()) + 1);
    }

    public static String generateResultXlsName(String outDir, String function) {
        if (outDir.endsWith("" + Env.slash())) {

        } else {
            outDir = outDir + Env.slash();
        }
        String target = outDir + function + "-" + getCurrentTimeStamp() + ".xls";
        Log.i("Result xls file name: " + target);
        return target;
    }

    public static void copyFileList(ArrayList<String> targetList, String sourceFolder,
            String destFolder) {
        ArrayList<String> files = listAll(sourceFolder);
        if (destFolder.endsWith("" + Env.slash())) {

        } else {
            destFolder = destFolder + Env.slash();
        }
        boolean find = false;
        for (String f : targetList) {
            find = false;
            for (String s : files) {
                String s1 = shortName(s);
                if (s1.equals(f)) {
                    find = true;
                    try {
                        copyFile(s, destFolder + f);
                    } catch (IOException e) {
                        Log.e("Error to copy file: " + f + " to " + destFolder);
                    }
                }
            }
            if (!find) {
                Log.e("File: " + f + "not copied(not found)! ");
            }
        }
    }

    public static String getCopySpeed() {
        if (START_TIME <= 0 || TOTAL_SIZE <= 0) {
            return "N/A";
        }
        double size_t = TOTAL_SIZE / (1024 * 1024.0);
        long time = (System.currentTimeMillis() - START_TIME) / 1000;
        return String.format("%.2f", size_t / time) + " M/s";
    }

    public static boolean copyFile(String sourceFile, String targetFile) throws IOException {
        if (START_TIME == 0) {
            START_TIME = System.currentTimeMillis();
        }
        File source = new File(sourceFile);
        File dest = new File(targetFile);
        if (dest.exists()) {
            if (dest.length() == source.length()) {
                // Log.write("File "+ targetFile
                // +" already exist, do not copy.");
                return true;
            } else {
                // Log.write("File "+ targetFile
                // +" exist, but size not match, delete then copy.");
                dest.delete();
            }
        }
        dest.getParentFile().mkdirs();
        Log.i("file op cp " + sourceFile + " -> " + targetFile);
        TOTAL_SIZE += source.length();
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] b = new byte[1024 * 500];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } finally {
            if (inBuff != null) {
                inBuff.close();
            }
            if (outBuff != null) {
                outBuff.close();
            }
        }
        return new File(targetFile).exists();
    }

    public static ArrayList<String> listAll(String dir) {
        ArrayList<String> result = new ArrayList<String>();
        File root = new File(dir);
        if (!root.exists() || !root.isDirectory()) {
            return null;
        } else {
            File[] fs = root.listFiles();
            for (File f : fs) {
                if (f.isDirectory()) {
                    result.addAll(listAll(f.getAbsolutePath()));
                } else {
                    result.add(f.getAbsolutePath());
                }
            }
        }
        return result;
    }

    public static ArrayList<String> listAll(String dir, String suffix) {
        ArrayList<String> result = new ArrayList<String>();
        File root = new File(dir);
        if (!root.exists() || !root.isDirectory()) {
            return null;
        } else {
            File[] fs = root.listFiles();
            for (File f : fs) {
                if (f.isDirectory()) {
                    result.addAll(listAll(f.getAbsolutePath(), suffix));
                } else {
                    String path = f.getAbsolutePath();
                    if (path.endsWith(suffix)) {
                        result.add(path);
                    }
                }
            }
        }
        return result;
    }

    public static String shortName(String fullName) {
        if (fullName == null) {
            return null;
        }
        if (fullName.indexOf(Env.slash()) >= 0) {
            return fullName.substring(fullName.lastIndexOf(Env.slash()) + 1,
                    fullName.length());
        } else {
            return fullName;
        }
    }

    public static void deleteRecursive(File path) {
        if (!path.exists()) {
            return;
        }
        if (path.isFile()) {
            path.delete();
        } else if (path.isDirectory()) {
            if (!path.delete()) {
                // directory not empty
                for (File f : path.listFiles()) {
                    deleteRecursive(f);
                }
            }
            path.delete();
        }
    }

    private static String getCurrentTimeStamp() {
        Calendar c = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        tz.setRawOffset(0);
        c.setTimeZone(tz);
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-"
                + c.get(Calendar.DAY_OF_MONTH) + "-"
                + c.get(Calendar.HOUR_OF_DAY) + "-"
                + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
    }
}
