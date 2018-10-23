
package libchecker.app.reports;

import libchecker.app.apk.Apk;
import libchecker.app.apk.AppInfo;
import libchecker.app.apk.nativelib.AppProtection;
import libchecker.app.apk.nativelib.NativeLib;
import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.excel.ThirdPartyLibExcelParser.ThirdPartyLibInfo;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.util.ArrayList;

public class Lib_Check_Report {

    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {

        boolean standAloneMode = false;
        if (appInfos == null) {
            standAloneMode = true;
            Log.i("standAloneMode: " + standAloneMode);

            ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir,
                    FileType.TYPE_APK);
            if (standAloneMode) {
                appInfos = new ArrayList<AppInfo>();
                for (String apkFile : apkFiles) {
                    AppInfo info = new AppInfo(apkFile);
                    // Log.out(Log.D, apkFile);
                    appInfos.add(info);
                }
            }
        }

        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir, FileType.TYPE_APK);

        ExcelOperation excel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", Lib_Check_Report.class
                        .getSimpleName()));

        int row = 0;
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Ranking");
        columnNames.add("Apk Name");
        columnNames.add("Package Name");
        columnNames.add("Apk Type");
        columnNames.add("Misused X86 Libs"); // lib/x86/*.so not x86
        columnNames.add("Misused X64 Libs"); // lib/x64/*.so not x64
        columnNames.add("X86 Lib Mismatch"); // lib/x86/*.so mismatch
        columnNames.add("X64 Lib Mismatch"); // lib/x64/*.so mismatch
        columnNames.add("Mismatched X86 Libs");
        columnNames.add("Mismatched X64 Libs");
        columnNames.add("Mismatched Libs(3rd Party)");
        columnNames.add("Mismatched Libs(ISV)");
        columnNames.add("ARM-V5 Folder Libs");
        columnNames.add("ARM-V7 Folder Libs");
        columnNames.add("ARM-V8 Folder Libs");
        columnNames.add("X86 Folder Libs");
        columnNames.add("X64 Folder Libs");
        columnNames.add("Assets Folder Libs");
        columnNames.add("Nested Apks");
        columnNames.add("All libs 3rd Party");
        columnNames.add("Assets miss x64 lib");
        columnNames.add("Assets miss x86 lib");
        columnNames.add("X86 lib-mix");
        columnNames.add("X64 lib-mix");
        columnNames.add("ARM64 lib-mix");
        columnNames.add("ARM 32 lib mismatch");
        columnNames.add("ARM 64 lib mismatch");
        columnNames.add("CMCC SDK App");
        columnNames.add("Known Issue on IA32");
        columnNames.add("Known Issue on IA64");

        excel.writeRow(row++, columnNames);

        int current = 0;
        int total = appInfos.size();
        AppProtection appPro = new AppProtection();
        // go through each apk file
        for (AppInfo appInfo : appInfos) {
            final String apkName = appInfo.apk_name + FileType.TYPE_APK;
            String apkFile = null;
            for (String n : apkFiles) {
                if (n.endsWith(apkName)) {
                    apkFile = n;
                    break;
                }
            }
            if (apkFile == null) {
                Log.e("apk " + apkName + " not found!");
                continue;
            }
            // parse the apk file
            Log.i(++current + " of " + total + ", processing apk: "
                    + FileOperation.shortName(apkFile));
            Apk apk = new Apk(apkFile);

            ArrayList<Object> libValues = new ArrayList<Object>();

            // columnNames.add("Ranking");
            libValues.add(appInfo.app_rank);
            // columnNames.add("Apk Name");
            libValues.add(appInfo.apk_name);
            // columnNames.add("Package Name");
            libValues.add(appInfo.package_name);
            // columnNames.add("Apk Type");
            libValues.add(apk.getApkType());

            // columnNames.add("Misused X86 Libs"); // lib/x86/*.so not x86
            ArrayList<NativeLib> misusedX86Libs = apk.getMisusedX86Libs();
            String misuedX86LibsString = "";
            for (NativeLib l : misusedX86Libs) {
                misuedX86LibsString = misuedX86LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(misuedX86LibsString);

            // columnNames.add("Misused X64 Libs"); // lib/x64/*.so not x64
            ArrayList<NativeLib> misusedX64Libs = apk.getMisusedX64Libs();
            String misuedX64LibsString = "";
            for (NativeLib l : misusedX64Libs) {
                misuedX64LibsString = misuedX64LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(misuedX64LibsString);

            // columnNames.add("X86 Lib Mismatch"); // lib/x86/*.so mismatch
            libValues.add(apk.isX86LibMismatchApp() ? "YES" : "NO");

            // columnNames.add("X64 Lib Mismatch"); // lib/x64/*.so mismatch
            libValues.add(apk.isX64LibMismatchApp() ? "YES" : "NO");

            // columnNames.add("Mismatched X86 Libs");
            ArrayList<NativeLib> mismatchedX86Libs = apk.getMismatchedX86Libs();
            String mismatchedX86LibsString = "";
            for (NativeLib l : mismatchedX86Libs) {
                mismatchedX86LibsString = mismatchedX86LibsString + Env.lineSeparator()
                        + l.shortPath();
            }
            libValues.add(mismatchedX86LibsString);

            // columnNames.add("Mismatched X64 Libs");
            ArrayList<NativeLib> mismatchedX64Libs = apk.getMismatchedX64Libs();
            String mismatchedX64LibsString = "";
            for (NativeLib l : mismatchedX64Libs) {
                mismatchedX64LibsString = mismatchedX64LibsString + Env.lineSeparator()
                        + l.shortPath();
            }
            libValues.add(mismatchedX64LibsString);

            ArrayList<NativeLib> mismatchedLibs = new ArrayList<NativeLib>();
            mismatchedLibs.addAll(mismatchedX86Libs);
            mismatchedLibs.addAll(mismatchedX64Libs);
            String mismatchedLibsIsvString = "";
            String mismatchedLibsThirdPartyString = "";
            for (NativeLib l : mismatchedLibs) {
                boolean find = false;
                for (ThirdPartyLibInfo libInfo : libInfos) {
                    if (libInfo.getName().equals(l.getName())) {
                        find = true;
                        break;
                    }
                }
                if (find) {
                    mismatchedLibsThirdPartyString = mismatchedLibsThirdPartyString
                            + Env.lineSeparator() + l.shortPath();
                } else {
                    mismatchedLibsIsvString = mismatchedLibsIsvString + Env.lineSeparator()
                            + l.shortPath();
                }
            }
            // columnNames.add("Mismatched Libs(3rd Party)");
            libValues.add(mismatchedLibsThirdPartyString);
            // columnNames.add("Mismatched Libs(ISV)");
            libValues.add(mismatchedLibsIsvString);

            // columnNames.add("ARM-V5 Folder Libs");
            ArrayList<NativeLib> v5Libs = apk.getReportedArmV5Libs();
            String v5LibsString = "";
            for (NativeLib l : v5Libs) {
                v5LibsString = v5LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(v5LibsString);

            // columnNames.add("ARM-V7 Folder Libs");
            ArrayList<NativeLib> v7Libs = apk.getReportedArmV7Libs();
            String v7LibsString = "";
            for (NativeLib l : v7Libs) {
                v7LibsString = v7LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(v7LibsString);

            // columnNames.add("ARM-V8 Folder Libs");
            ArrayList<NativeLib> v8Libs = apk.getReportedArmV8Libs();
            String v8LibsString = "";
            for (NativeLib l : v8Libs) {
                v8LibsString = v8LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(v8LibsString);

            // columnNames.add("X86 Folder Libs");
            ArrayList<NativeLib> x86Libs = apk.getReportedX86Libs();
            String x86LibsString = "";
            for (NativeLib l : x86Libs) {
                x86LibsString = x86LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(x86LibsString);

            // columnNames.add("X64 Folder Libs");
            ArrayList<NativeLib> x64Libs = apk.getReportedX64Libs();
            String x64LibsString = "";
            for (NativeLib l : x64Libs) {
                x64LibsString = x64LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(x64LibsString);

            // columnNames.add("Assets Folder Libs");
            ArrayList<NativeLib> assertLibs = apk.getAllAssertsLibs();
            String assertLibsString = "";
            for (NativeLib l : assertLibs) {
                assertLibsString = assertLibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(assertLibsString);

            // columnNames.add("Nested Apks");
            ArrayList<String> subApks = apk.getSubApks();
            String subApksString = "";
            for (String sApk : subApks) {
                Log.v("sub apk: " + sApk);
                sApk = sApk.substring(sApk.indexOf("assets"));
                subApksString = subApksString + Env.lineSeparator() + sApk;
            }
            libValues.add(subApksString);

            //  columnNames.add("All libs 3rd Party");
            ArrayList<NativeLib> v7 = apk.getReportedArmV7Libs();
            ArrayList<NativeLib> v5 = apk.getReportedArmV5Libs();
            if(v7.size() > 0){
                boolean all3rdparty = true;
                for(NativeLib l : v7){
                    boolean found = false;
                    for(ThirdPartyLibInfo info: libInfos){
                        if(info.getName().equals(l.getName())){
                            found = true;
                        }
                    }
                    if(!found){
                        all3rdparty = false;
                        break;
                    }
                }
                if(all3rdparty){
                    libValues.add("YES");
                }else{
                    libValues.add("NO");
                }
            }else if(v5.size() > 0){
                boolean all3rdparty = true;
                for(NativeLib l : v5){
                    boolean found = false;
                    for(ThirdPartyLibInfo info: libInfos){
                        if(info.getName().equals(l.getName())){
                            found = true;
                        }
                    }
                    if(!found){
                        all3rdparty = false;
                        break;
                    }
                }
                if(all3rdparty){
                    libValues.add("YES");
                }else{
                    libValues.add("NO");
                }
            }else{
                libValues.add("NO");
            }
            
            // columnNames.add("Assets miss x64 lib");
            libValues.add(apk.assetsLibMissingX64Version() ? "YES" : "NO");

            // columnNames.add("Assets miss x86 lib");
            libValues.add(apk.assetsLibMissingX86Version() ? "YES" : "NO");

            // columnNames.add("X86 lib-mix");
            libValues.add(apk.getMisusedX86Libs().size() > 0 ? "YES" : "NO");

            // columnNames.add("X64 lib-mix");
            libValues.add(apk.getMisusedX64Libs().size() > 0 ? "YES" : "NO");

            // columnNames.add("ARM64 lib-mix");
            libValues.add(apk.getMisusedArmV8Libs().size() > 0 ? "YES" : "NO");

            // columnNames.add("ARM 32 lib mismatch");
            libValues.add(apk.hasArm32LibPackagingIssue() ? "YES" : "NO");

            // columnNames.add("ARM 64 lib mismatch");
            libValues.add(apk.hasArm64LibPackagingIssue() ? "YES" : "NO");

            // columnNames.add("CMCC SDK App");
            libValues.add(apk.isCmccApp() ? "YES" : "NO");

            // columnNames.add("Known Issue on IA32");
            boolean knownIssue = false;
            knownIssue = knownIssue || appPro.standAloneCheck(appInfo, apk) > 0;
            knownIssue = knownIssue || apk.isCmccApp();
            if (apk.getApkType().equals(Apk.TYPE_ARM32_NDK)) {
                knownIssue = knownIssue || apk.hasArm32LibPackagingIssue();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X64_NDK)) {
                knownIssue = knownIssue || apk.hasArm32LibPackagingIssue();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_NDK)) {
                knownIssue = knownIssue || true;
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X64_NDK)) {
                knownIssue = knownIssue || true;
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_JAVA)) {
                knownIssue = knownIssue || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_X64_NDK)) {
                knownIssue = knownIssue || true;
            } else if (apk.getApkType().equals(Apk.TYPE_X86_NDK)) {
                knownIssue = knownIssue || apk.assetsLibMissingX86Version()
                        || apk.isX86LibMisuseApp();
            }
            libValues.add(knownIssue ? "YES" : "NO");

            // columnNames.add("Known Issue on IA64");
            knownIssue = false;
            knownIssue = knownIssue || appPro.standAloneCheck(appInfo, apk) > 0;
            knownIssue = knownIssue || apk.isCmccApp();
            if (apk.getApkType().equals(Apk.TYPE_ARM32_NDK)) {
                knownIssue = knownIssue || apk.hasArm32LibPackagingIssue();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X64_NDK)) {
                knownIssue = knownIssue || apk.isX64LibMismatchApp() || apk.isX64LibMisuseApp()
                        || apk.assetsLibMissingX64Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_NDK)) {
                knownIssue = knownIssue || apk.hasArm64LibPackagingIssue()
                        || apk.assetsLibMissingX64Version() || apk.isArmV8LibMisuseApp();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X64_NDK)) {
                knownIssue = knownIssue || apk.isX64LibMismatchApp() || apk.isX64LibMisuseApp()
                        || apk.assetsLibMissingX64Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_JAVA)) {
                knownIssue = knownIssue || apk.assetsLibMissingX64Version();
            } else if (apk.getApkType().equals(Apk.TYPE_X64_NDK)) {
                knownIssue = knownIssue || apk.assetsLibMissingX64Version()
                        || apk.isX64LibMisuseApp();
            } else if (apk.getApkType().equals(Apk.TYPE_X86_NDK)) {
                knownIssue = knownIssue || apk.assetsLibMissingX86Version()
                        || apk.isX86LibMisuseApp();
            }
            libValues.add(knownIssue ? "YES" : "NO");

            excel.writeRow(row++, libValues);
            apk.destroy();
        }
    }
}
