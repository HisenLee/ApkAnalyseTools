package libchecker.app.adb;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CrashListener {

    private ArrayList<CrashCallback> mCallbacks = new ArrayList<CrashCallback>();

    private String serialNo = null;
    private Process deamon = null;

    private boolean running = true;

    public CrashListener(String deviceSerialNo) {
        System.out.println("CrashLis");
        serialNo = deviceSerialNo;
    }

    public void addCallback(CrashCallback cb) {
        synchronized (mCallbacks) {
            if (mCallbacks.contains(cb)) {

            } else {
                mCallbacks.add(cb);
                System.out.println("cb added");
            }
        }
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                ProcessBuilder b;
                System.out.println("CrashLis started: "+serialNo);
                if (serialNo == null) {
                    b = new ProcessBuilder();
                    b.command().add("adb");
                    b.command().add("logcat");
                    b.command().add("*:I");
                } else {
                    b = new ProcessBuilder();
                    b.command().add("adb");
                    b.command().add("-s");
                    b.command().add(serialNo);
                    b.command().add("logcat");
                    b.command().add("*:I");
                }
                try {
                    deamon = b.start();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                running = true;
                if (deamon != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            deamon.getInputStream()));
                    String line = null;
                    while (running) {
                        try {
                            line = br.readLine();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            break;
                        }
                        if (line != null) {
                        	//System.out.println("line-> "+line);
                            if (line.contains("E/AndroidRuntime") && line.contains("Process: ") && line.contains("PID: ")) {
                                String packageName = line.substring(line.indexOf("Process: ") + 9,
                                        line.indexOf(','));
                                System.out.println("Process died: " + packageName);
                                synchronized (mCallbacks) {
                                    for (CrashCallback cb : mCallbacks) {
                                        cb.onProcessDied(packageName, serialNo);
                                    }
                                }
                            }else if(line.contains("W/ActivityManager") && line.contains("Force finishing activity ")){
                                String packageName = line.substring(line.indexOf("Force finishing activity ") + 25,
                                        line.lastIndexOf('/'));
                                System.out.println("Process died: " + packageName);
                                synchronized (mCallbacks) {
                                    for (CrashCallback cb : mCallbacks) {
                                        cb.onProcessDied(packageName, serialNo);
                                    }
                                }
                            }else if(line.contains("W/ActivityManager") && line.contains("Force removing ActivityRecord")  && line.contains("app died, no saved state")){
                                String packageName = line.substring(line.indexOf("{") + 1,
                                        line.lastIndexOf('/'));
                                packageName = packageName.substring(packageName.lastIndexOf(" ")+1, packageName.length());
                                System.out.println("Process died: " + packageName);
                                synchronized (mCallbacks) {
                                    for (CrashCallback cb : mCallbacks) {
                                        cb.onProcessDied(packageName, serialNo);
                                    }
                                }
                            }
                            
                            else if(line.contains("E/ActivityManager") && line.contains("ANR in ")){
                                String packageName = line.substring(line.indexOf('(')+1, line.lastIndexOf('/'));
                                System.out.println("ANR in process: " + packageName);
                                synchronized (mCallbacks) {
                                    for (CrashCallback cb : mCallbacks) {
                                        cb.onProcessDied(packageName, serialNo);
                                    }
                                }
                            }
                            
                            
                            //else if(line.contains("I/ActivityManager") && line.contains("Process ")  && line.contains(" has died.")){
                                //String packageName = line.substring(line.indexOf("Process ") + 8,
                                        //line.lastIndexOf('('));
                                //packageName = packageName.trim();
                                //System.out.println("Process died: " + packageName);
                                //synchronized (mCallbacks) {
                                    //for (CrashCallback cb : mCallbacks) {
                                        //cb.onProcessDied(packageName, serialNo);
                                    //}
                                //}
                            //}
                        }
                    }
                }
            }
        }).start();
    }

    public void finish() {
        running = false;
        if (deamon != null) {
            deamon.destroy();
            try {
                deamon.waitFor();
            } catch (InterruptedException e) {
            }
        }
    }
}
