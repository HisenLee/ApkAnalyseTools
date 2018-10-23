
package libchecker.app.apk;

import java.io.File;
import java.util.ArrayList;

import libchecker.app.apk.nativelib.ABI;
import libchecker.app.apk.nativelib.AppProtection;
import libchecker.app.apk.nativelib.NativeLib;
import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;
import libchecker.app.util.file.zip.ZipUtil;

public class Apk {
    public static final String TYPE_JAVA = "JAVA";

    public static final String TYPE_ARM32_NDK = "ARM 32 NDK";
    public static final String TYPE_ARM64_NDK = "ARM 64 NDK";

    public static final String TYPE_X86_NDK = "X86 NDK";
    public static final String TYPE_X64_NDK = "X64 NDK";

    // public static final String TYPE_ARM_X86_NDK = "ARM+X86 NDK";
    public static final String TYPE_ARM32_X86_NDK = "ARM32+X86 NDK";
    public static final String TYPE_ARM64_X86_NDK = "ARM64+X86 NDK";
    public static final String TYPE_ARM32_X64_NDK = "ARM32+X64 NDK";
    public static final String TYPE_ARM64_X64_NDK = "ARM64+X64 NDK";

    public static final String TYPE_ERROR = "ERROR";

    // 移动支付 libmegjb.so; libcasdkjni.so; libDexHelper_mmb.so
    public static final String CMCC_LIB_NAME = "libmegjb";
    private boolean isCMCCApp = false;
    // 联通支付
    public static final String ChinaUnicom_LIB_NAME = "libme_unipay";
    private boolean isChinaUnicom = false;
    // 电信支付 libegamepay_dr2.so; libegamepay.so
    public static final String ChinaTelecom_LIB_NAME = "libegamepay";
    private boolean isChinaTelecom = false;
    // 银联支付   libentryexstd.so; libUnionPay.so
    public static final String  ChinaUnionPay_LIB_NAME = "libentryexstd";
    private boolean isChinaUnionPay = false;
    
    // 阿里热修复 libandfix.so; libsophix.so;
    public static final String[]  AliHotFixLibs = new String[]{"libandfix", "libsophix"};
    private boolean isAlihotfix = false;
    
    // Facebook ReactNative Libs
    public static final String[]  FacebookRNLibs = new String[]{"libfb.so", "libyoga",
    												"libfolly_json", "libglog", 
    												"libreactnative", "libglog_init"};
    private boolean isFacebookRN = false;
    
    // Tencent Live Video 腾讯直播
    public static final String[]  TencentLiveVideoLibs = new String[]{"libTcVpx",
		"libtraeimp", "libhwcodec.so", "libblasV8.so", "libqavsdk", "libqav_graphics.so"};
    private boolean isTencentLiveVideo = false;

    // apk full name
    private String apkFullName = "";

    // apk short name
    private String apkShortName = null;

    // apk extracted folder
    private String apkExtDir = null;
    private String apkLibDir = null;
    private String apkAssetsDir = null;

    // all files in apk
    private ArrayList<String> allFiles = null;

    // sub apks
    private ArrayList<String> subApks = null;

    private ArrayList<NativeLib> reportedArmV5Libs = null;
    private ArrayList<NativeLib> reportedArmV7Libs = null;
    private ArrayList<NativeLib> reportedArmV8Libs = null;
    private ArrayList<NativeLib> reportedX86Libs = null;
    private ArrayList<NativeLib> reportedX64Libs = null;

    // assets libs
    private ArrayList<NativeLib> assetsAllLibs = null;

    private ArrayList<NativeLib> assetsArm32Libs = null;
    private ArrayList<NativeLib> assetsArmV5Libs = null;
    private ArrayList<NativeLib> assetsArmV7Libs = null;
    private ArrayList<NativeLib> assetsArmV8Libs = null;
    private ArrayList<NativeLib> assetsX86Libs = null;
    private ArrayList<NativeLib> assetsX64Libs = null;

    // misused x86 libs
    private ArrayList<NativeLib> misusedX86Libs = null;
    private ArrayList<NativeLib> misusedX64Libs = null;

    // misused arm v8 libs
    private ArrayList<NativeLib> misusedArmV8Libs = null;

    // mismatched x86 libs
    private ArrayList<NativeLib> mismatchedX86Libs = null;
    private ArrayList<NativeLib> mismatchedX64Libs = null;
    private ArrayList<NativeLib> mismatchedArmV8Libs = null;

    public Apk(String fullname) {
        Log.d("Apk: " + fullname);
        apkFullName = fullname;
        if (apkFullName.indexOf(Env.slash()) >= 0) {
            apkShortName = apkFullName.substring(
                    apkFullName.lastIndexOf(Env.slash()) + 1, apkFullName.length());
            apkExtDir = apkFullName.substring(apkFullName.lastIndexOf(Env.slash()) + 1,
                    apkFullName.lastIndexOf('.'));
            try {
                ZipUtil.unpack(new File(apkFullName), new File(apkExtDir));
            } catch (Exception ex) {
                System.out.println("Error processing apk file: " + fullname);
            }
            apkLibDir = apkExtDir + Env.slash() + "lib";
            apkAssetsDir = apkExtDir + Env.slash() + "assets";
        }

        getReportedX86Libs();
        getReportedX64Libs();
        getReportedArmV5Libs();
        getReportedArmV7Libs();
        getReportedArmV8Libs();
        getSubApks();
        getAllAssertsLibs();

        getMisusedX86Libs();
        getMisusedX64Libs();

        getMismatchedX86Libs();
        getMismatchedX64Libs();
        getMismatchedArmV8Libs();

        allFiles = FileOperation.listAll(apkExtDir);
    }

    public void destroy() {
        File dir = new File(apkExtDir);
        FileOperation.deleteRecursive(dir);
    }

    public String getShortName() {
        return apkShortName;
    }

    public String getApkType() {

        final boolean hasV5 = getReportedArmV5Libs().size() > 0;
        final boolean hasV7 = getReportedArmV7Libs().size() > 0;
        final boolean hasV8 = getReportedArmV8Libs().size() > 0;
        final boolean hasX86 = getReportedX86Libs().size() > 0;
        final boolean hasX64 = getReportedX64Libs().size() > 0;

        if (!(hasV5 || hasV7 || hasV8 || hasX86 || hasX64)) {
            return TYPE_JAVA;
        }
        if (hasV8 && !hasX86 && !hasX64) {
            return TYPE_ARM64_NDK;
        }
        if ((hasV5 || hasV7) && !hasX86 && !hasX64) {
            return TYPE_ARM32_NDK;
        }

        if ((!hasV5 && !hasV7 && !hasV8) && hasX64) {
            return TYPE_X64_NDK;
        }
        if ((!hasV5 && !hasV7 && !hasV8) && hasX86) {
            return TYPE_X86_NDK;
        }
        if (hasV8) {
            if (hasX64) {
                return TYPE_ARM64_X64_NDK;
            } else if (hasX86) {
                return TYPE_ARM64_X86_NDK;
            }
        }else{
            if (hasX64) {
                return TYPE_ARM32_X64_NDK;
            } else if (hasX86) {
                return TYPE_ARM32_X86_NDK;
            }
        }
        return TYPE_ERROR;
    }
    
    /**
     * Check Some Lib exists
     * @param f
     */
    private void checkSomeLib(File f) {
    	if (f.getName().contains(CMCC_LIB_NAME)) {
            isCMCCApp = true;
        }
        if (f.getName().contains(ChinaUnicom_LIB_NAME)) {
            isChinaUnicom = true;
        }
        if (f.getName().contains(ChinaTelecom_LIB_NAME)) {
            isChinaTelecom = true;
        }
        if (f.getName().contains(ChinaUnionPay_LIB_NAME)) {
            isChinaUnionPay = true;
        }
        // exists alihotfix
        for(int i=0; i< AliHotFixLibs.length; i++) {
        	if (f.getName().contains(AliHotFixLibs[i])) {
                isAlihotfix = true;
            }
        }
        // exists facebook react native
        for(int i=0; i< FacebookRNLibs.length; i++) {
        	if (f.getName().contains(FacebookRNLibs[i])) {
                isFacebookRN = true;
            }
        }
        // exists tencent livevideo
        for(int i=0; i< TencentLiveVideoLibs.length; i++) {
        	if (f.getName().contains(TencentLiveVideoLibs[i])) {
                isTencentLiveVideo = true;
            }
        }
        
        
    }
    
    // 移动支付
    public boolean isCmccApp() {
        return isCMCCApp;
    }
    // 联通支付
    public boolean isChinaUnicom() {
    	return isChinaUnicom;
    }
    // 电信支付
    public boolean isChinaTelecom() {
    	return isChinaTelecom;
    }
    // 银联支付
    public boolean isChinaUnionPay() {
    	return isChinaUnionPay;
    }  
    // Alihotfix
    public boolean isAlihotfixLib() {
    	return isAlihotfix;
    }  
    // Facebook RN
    public boolean isFaceBookRNLib() {
    	return isFacebookRN;
    }  
    // Tencent Live Video
    public boolean isTencentLiveVideoLib() {
    	return isTencentLiveVideo;
    }  
    
    

    public ArrayList<NativeLib> getReportedArmV5Libs() {
        if (reportedArmV5Libs != null) {
            return reportedArmV5Libs;
        }
        reportedArmV5Libs = new ArrayList<NativeLib>();
        File dir = new File(apkLibDir + Env.slash() + ABI.ABI_ARMV5);
        if (!dir.exists() || !dir.isDirectory()) {
            return reportedArmV5Libs;
        }
        File[] fs = dir.listFiles();
        for (File f : fs) {
            reportedArmV5Libs.add(new NativeLib(f.getAbsolutePath()));
            checkSomeLib(f);
        }
        return reportedArmV5Libs;
    }

    public ArrayList<NativeLib> getReportedArmV7Libs() {
        if (reportedArmV7Libs != null) {
            return reportedArmV7Libs;
        }
        reportedArmV7Libs = new ArrayList<NativeLib>();
        File dir = new File(apkLibDir + Env.slash() + ABI.ABI_ARMV7);
        if (!dir.exists() || !dir.isDirectory()) {
            return reportedArmV7Libs;
        }
        File[] fs = dir.listFiles();
        for (File f : fs) {
            reportedArmV7Libs.add(new NativeLib(f.getAbsolutePath()));
            checkSomeLib(f);
        }
        return reportedArmV7Libs;
    }

    public ArrayList<NativeLib> getReportedArmV8Libs() {
        if (reportedArmV8Libs != null) {
            return reportedArmV8Libs;
        }
        reportedArmV8Libs = new ArrayList<NativeLib>();
        File dir = new File(apkLibDir + Env.slash() + ABI.ABI_ARMV8);
        if (!dir.exists() || !dir.isDirectory()) {
            return reportedArmV8Libs;
        }
        File[] fs = dir.listFiles();
        for (File f : fs) {
            reportedArmV8Libs.add(new NativeLib(f.getAbsolutePath()));
            checkSomeLib(f);
        }
        return reportedArmV8Libs;
    }

    public ArrayList<NativeLib> getReportedX86Libs() {
        if (reportedX86Libs != null) {
            return reportedX86Libs;
        }
        reportedX86Libs = new ArrayList<NativeLib>();
        File dir = new File(apkLibDir + Env.slash() + ABI.ABI_X86);
        if (!dir.exists() || !dir.isDirectory()) {
            return reportedX86Libs;
        }
        File[] fs = dir.listFiles();
        for (File f : fs) {
            NativeLib nl = new NativeLib(f.getAbsolutePath());
            reportedX86Libs.add(nl);
            checkSomeLib(f);
        }
        return reportedX86Libs;
    }

    public ArrayList<NativeLib> getReportedX64Libs() {
        if (reportedX64Libs != null) {
            return reportedX64Libs;
        }
        reportedX64Libs = new ArrayList<NativeLib>();
        File dir = new File(apkLibDir + Env.slash() + ABI.ABI_X64);
        if (!dir.exists() || !dir.isDirectory()) {
            return reportedX64Libs;
        }
        File[] fs = dir.listFiles();
        for (File f : fs) {
            reportedX64Libs.add(new NativeLib(f.getAbsolutePath()));
            checkSomeLib(f);
        }
        return reportedX64Libs;
    }

    public ArrayList<NativeLib> getAllAssertsLibs() {
        if (assetsAllLibs != null) {
            return assetsAllLibs;
        }
        assetsAllLibs = new ArrayList<NativeLib>();
        assetsArm32Libs = new ArrayList<NativeLib>();
        assetsArmV5Libs = new ArrayList<NativeLib>();
        assetsArmV7Libs = new ArrayList<NativeLib>();
        assetsArmV8Libs = new ArrayList<NativeLib>();
        assetsX86Libs = new ArrayList<NativeLib>();
        assetsX64Libs = new ArrayList<NativeLib>();

        File dir = new File(apkAssetsDir);
        if (!dir.exists() || !dir.isDirectory()) {
            return assetsAllLibs;
        }

        ArrayList<String> all = FileOperation.listAll(apkAssetsDir,
                FileType.TYPE_SO);
        for (String l : all) {
            assetsAllLibs.add(new NativeLib(l));
        }
        for (NativeLib lib : assetsAllLibs) {
            if (lib.shortPath().contains(Env.slash() + ABI.ABI_ARMV5 + Env.slash())) {
                assetsArm32Libs.add(lib);
                assetsArmV5Libs.add(lib);
            } else if (lib.shortPath().contains(Env.slash() + ABI.ABI_ARMV7 + Env.slash())) {
                assetsArm32Libs.add(lib);
                assetsArmV7Libs.add(lib);
            } else if (lib.shortPath().contains(Env.slash() + ABI.ABI_X86 + Env.slash())) {
                assetsX86Libs.add(lib);
            } else if (lib.shortPath().contains(Env.slash() + ABI.ABI_ARMV8 + Env.slash())) {
                assetsArmV8Libs.add(lib);
            } else if (lib.shortPath().contains(Env.slash() + ABI.ABI_X64 + Env.slash())) {
                assetsX64Libs.add(lib);
            } else {
                if (lib.isArm32()) {
                    assetsArmV5Libs.add(lib);
                    assetsArmV7Libs.add(lib);
                    assetsArm32Libs.add(lib);
                } else if (lib.isArm64()) {
                    assetsArmV8Libs.add(lib);
                } else if (lib.isX86()) {
                    assetsX86Libs.add(lib);
                } else if (lib.isX64()) {
                    assetsX64Libs.add(lib);
                } else {
                }
            }
        }
        return assetsAllLibs;
    }

    public boolean assetsLibMissingX86Version() {
        final int armV7LibNum = assetsArmV7Libs.size();
        final int armV5LibNum = assetsArmV5Libs.size();
        final int x86LibNum = assetsX86Libs.size();

        ArrayList<NativeLib> target = assetsArmV7Libs;
        if(armV7LibNum < armV5LibNum){
            target = assetsArmV5Libs;
        }
        boolean appProtectionCheck = target.size() > x86LibNum;
        if (appProtectionCheck) {
            for (NativeLib l : target) {
                if (!AppProtection.isAppProtectionLib(l)) {
                    return true;
                }
            }
            Log.d("Assets arm libs are all app protection libs.");
            return false;
        }
        return appProtectionCheck;
    }

    public boolean assetsLibMissingX64Version() {
        final int armV7LibNum = assetsArmV7Libs.size();
        final int armV5LibNum = assetsArmV5Libs.size();
        final int x64LibNum = assetsX64Libs.size();

        ArrayList<NativeLib> target = assetsArmV7Libs;
        if(armV7LibNum < armV5LibNum){
            target = assetsArmV5Libs;
        }
        boolean appProtectionCheck = target.size() > x64LibNum;
        if (appProtectionCheck) {
            for (NativeLib l : target) {
                if (!AppProtection.isAppProtectionLib(l)) {
                    return true;
                }
            }
            Log.d("Assets arm libs are all app protection libs.");
            return false;
        }
        return appProtectionCheck;
    }

    public boolean isArmV8LibMisuseApp(){
        return getMisusedArmV8Libs().size() > 0;
    }
    public ArrayList<NativeLib> getMisusedArmV8Libs() {
        if (misusedArmV8Libs != null) {
            return misusedArmV8Libs;
        }
        misusedArmV8Libs = new ArrayList<NativeLib>();
        getReportedArmV8Libs();
        for (NativeLib v8 : reportedArmV8Libs) {
            if (!v8.isArm64()) {
                misusedArmV8Libs.add(v8);
            }
        }
        return misusedArmV8Libs;
    }

    public boolean isX86LibMisuseApp(){
        return getMisusedX86Libs().size() > 0;
    }
    public ArrayList<NativeLib> getMisusedX86Libs() {
        if (misusedX86Libs != null) {
            return misusedX86Libs;
        }
        misusedX86Libs = new ArrayList<NativeLib>();
        getReportedX86Libs();
        for (NativeLib x86 : reportedX86Libs) {
            if (!x86.isX86()) {
                misusedX86Libs.add(x86);
            }
        }
        return misusedX86Libs;
    }

    public boolean isX64LibMisuseApp(){
        return getMisusedX64Libs().size() > 0;
    }
    public ArrayList<NativeLib> getMisusedX64Libs() {
        if (misusedX64Libs != null) {
            return misusedX64Libs;
        }
        misusedX64Libs = new ArrayList<NativeLib>();
        getReportedX64Libs();
        for (NativeLib x64 : reportedX64Libs) {
            if (!x64.isX64()) {
                misusedX64Libs.add(x64);
            }
        }
        return misusedX64Libs;
    }

    public boolean hasArm32LibPackagingIssue() {
        final int v5No = reportedArmV5Libs.size();
        final int v7No = reportedArmV7Libs.size();
        return v5No > 0 && v7No > 0 && v5No > v7No;
    }

    public boolean hasArm64LibPackagingIssue() {
        final int v5No = reportedArmV5Libs.size();
        final int v7No = reportedArmV7Libs.size();
        final int v8No = reportedArmV8Libs.size();
        if (v8No <= 0) {
            return false;
        }
        if (v7No > 0) {
            return v8No < v7No;
        } else if (v5No > 0) {
            return v8No < v5No;
        } else {
            return false;
        }
    }

    public ArrayList<NativeLib> getMismatchedX86Libs() {
        if (mismatchedX86Libs != null) {
            return mismatchedX86Libs;
        }
        mismatchedX86Libs = new ArrayList<NativeLib>();

        if(getReportedX86Libs().size() <=0 ){
            return mismatchedX86Libs;
        }
        
        boolean find = false;
        String shortV7 = "";
        String shortV5 = "";
        String shortX86 = "";

        if (reportedArmV7Libs.size() > 0) {
            for (NativeLib v7 : reportedArmV7Libs) {
                find = false;
                shortV7 = FileOperation.shortName(v7.shortPath());
                for (NativeLib x86 : reportedX86Libs) {
                    shortX86 = FileOperation.shortName(x86.shortPath());
                    if (shortX86.equals(shortV7)) {
                        find = true;
                    }
                }
                if (!find) {
                    mismatchedX86Libs.add(v7);
                }
            }
        } else if (reportedArmV5Libs.size() > 0) {
            for (NativeLib v5 : reportedArmV5Libs) {
                find = false;
                shortV5 = FileOperation.shortName(v5.shortPath());
                for (NativeLib x86 : reportedX86Libs) {
                    shortX86 = FileOperation.shortName(x86.shortPath());
                    if (shortX86.equals(shortV5)) {
                        find = true;
                    }
                }
                if (!find) {
                    mismatchedX86Libs.add(v5);
                }
            }
        }
        return mismatchedX86Libs;
    }

    public ArrayList<NativeLib> getMismatchedArmV8Libs() {
        if (mismatchedArmV8Libs != null) {
            return mismatchedArmV8Libs;
        }
        mismatchedArmV8Libs = new ArrayList<NativeLib>();
        
        if(getReportedArmV8Libs().size() <=0 ){
            return mismatchedArmV8Libs;
        }

        boolean find = false;
        String shortV7 = "";
        String shortV5 = "";
        String shortV8 = "";

        if (reportedArmV7Libs.size() > 0) {
            for (NativeLib v7 : reportedArmV7Libs) {
                find = false;
                shortV7 = FileOperation.shortName(v7.shortPath());
                for (NativeLib v8 : reportedArmV8Libs) {
                    shortV8 = FileOperation.shortName(v8.shortPath());
                    if (shortV8.equals(shortV7)) {
                        find = true;
                    }
                }
                if (!find) {
                    mismatchedArmV8Libs.add(v7);
                }
            }
        } else if (reportedArmV5Libs.size() > 0) {
            for (NativeLib v5 : reportedArmV5Libs) {
                find = false;
                shortV5 = FileOperation.shortName(v5.shortPath());
                for (NativeLib V8 : reportedArmV8Libs) {
                    shortV8 = FileOperation.shortName(V8.shortPath());
                    if (shortV8.equals(shortV5)) {
                        find = true;
                    }
                }
                if (!find) {
                    mismatchedArmV8Libs.add(v5);
                }
            }
        }
        return mismatchedArmV8Libs;
    }

    public ArrayList<NativeLib> getMismatchedX64Libs() {
        if (mismatchedX64Libs != null) {
            return mismatchedX64Libs;
        }
        mismatchedX64Libs = new ArrayList<NativeLib>();

        if(getReportedX64Libs().size() <=0 ){
            return mismatchedX64Libs;
        }
        
        if (getMismatchedArmV8Libs().size() > 0) {
            // arm v8 lib mismatch app
            boolean find = false;
            String shortV7 = "";
            String shortV5 = "";
            String shortX64 = "";

            if (reportedArmV7Libs.size() > 0) {
                for (NativeLib v7 : reportedArmV7Libs) {
                    find = false;
                    shortV7 = FileOperation.shortName(v7.shortPath());
                    for (NativeLib x64 : reportedX64Libs) {
                        shortX64 = FileOperation.shortName(x64.shortPath());
                        if (shortX64.equals(shortV7)) {
                            find = true;
                        }
                    }
                    if (!find) {
                        mismatchedX64Libs.add(v7);
                    }
                }
            } else if (reportedArmV5Libs.size() > 0) {
                for (NativeLib v5 : reportedArmV5Libs) {
                    find = false;
                    shortV5 = FileOperation.shortName(v5.shortPath());
                    for (NativeLib x64 : reportedX64Libs) {
                        shortX64 = FileOperation.shortName(x64.shortPath());
                        if (shortX64.equals(shortV5)) {
                            find = true;
                        }
                    }
                    if (!find) {
                        mismatchedX64Libs.add(v5);
                    }
                }
            }
        } else {
            boolean find = false;
            String shortV8 = "";
            String shortX64 = "";
            if (reportedArmV8Libs.size() > 0) {
                for (NativeLib v8 : reportedArmV8Libs) {
                    find = false;
                    shortV8 = FileOperation.shortName(v8.shortPath());
                    for (NativeLib x64 : reportedX64Libs) {
                        shortX64 = FileOperation.shortName(x64.shortPath());
                        if (shortX64.equals(shortV8)) {
                            find = true;
                        }
                    }
                    if (!find) {
                        mismatchedX64Libs.add(v8);
                    }
                }
            }
        }
        return mismatchedX64Libs;
    }

    public ArrayList<String> getSubApks() {
        if (subApks != null) {
            return subApks;
        }
        subApks = new ArrayList<String>();
        File dir = new File(apkAssetsDir);
        if (dir.exists() && dir.isDirectory()) {
            subApks.addAll(FileOperation.listAll(apkAssetsDir,
                    FileType.TYPE_APK));
        }
        return subApks;
    }
    
    
    
    public boolean isX86LibMismatchApp() {
        return getMismatchedX86Libs().size() > 0 ;
    }

    public boolean isX64LibMismatchApp() {
        return getMismatchedX64Libs().size() > 0;
    }

    public boolean containsFile(String fileName, long size) {
        for (String f : allFiles) {
            if (FileOperation.shortName(f).equals(fileName)) {
                if (new File(f).length() == size) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsFile(String fileName) {
        for (String f : allFiles) {
            if (FileOperation.shortName(f).endsWith(fileName)) {
                // if(new File(f).length() == size){
                return true;
                // }
            }
        }
        return false;
    }

    @Deprecated
    public void dump() {
        Log.v("slash: " + Env.slash());
        Log.v("apkExtDir: " + apkExtDir);
        Log.v("apkFullName: " + apkFullName);
        Log.v("apkShortName: " + apkShortName);
        Log.v("apkLibDir: " + apkLibDir);
        Log.v("apkAssetsDir: " + apkAssetsDir);

        String sub = "";
        for (String a : subApks) {
            sub = sub + "\r\n" + a;
        }
        Log.v("subApks: " + sub);

        sub = "";
        for (NativeLib a : reportedArmV5Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("reportedV5Libs: " + sub);

        sub = "";
        for (NativeLib a : reportedArmV7Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("reportedV7Libs: " + sub);

        sub = "";
        for (NativeLib a : reportedX86Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("reportedX86Libs: " + sub);

        sub = "";
        for (NativeLib a : assetsAllLibs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("assetsAllLibs: " + sub);

        sub = "";
        for (NativeLib a : assetsArm32Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("assetsArm32Libs: " + sub);

        sub = "";
        for (NativeLib a : assetsArmV5Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("assetsArmV5Libs: " + sub);
        sub = "";
        for (NativeLib a : assetsArmV7Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("assetsArmV7Libs: " + sub);
        sub = "";
        for (NativeLib a : assetsX86Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("assetsX86Libs: " + sub);

        sub = "";
        for (NativeLib a : misusedX86Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("misusedX86Libs: " + sub);

        sub = "";
        for (NativeLib a : mismatchedX86Libs) {
            sub = sub + "\r\n" + a.shortPath();
        }
        Log.v("mismatchedX86Libs: " + sub);
    }
}
