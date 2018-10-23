
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

import java.io.IOException;
import java.util.ArrayList;

public class App_Protection_Usage_Report {

    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {

        AppProtection appProtection = new AppProtection();

        boolean standAloneMode = false;
        if (appInfos == null) {
            standAloneMode = true;
            Log.i("standaloneMode: " + standAloneMode);
        }
        // go through all apk files
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

        int current = 0;
        int total = appInfos.size();
        for (AppInfo info : appInfos) {
            String targetApkName = info.apk_name + FileType.TYPE_APK;
            // Log.out(Log.D, "targetApkName: " + targetApkName);
            String targetApkFile = null;
            for (String apkFile : apkFiles) {
                if (apkFile.endsWith(targetApkName)) {
                    targetApkFile = apkFile;
                    // Log.out(Log.D, "targetApkFile: " + targetApkFile);
                    break;
                }
            }
            if (targetApkFile == null) {
                Log.e("apk not found " + info.apk_name);
                continue;
            }
            Log.i(++current + " of " + total + ", processing apk: "
                    + FileOperation.shortName(targetApkFile));
            Apk a = new Apk(targetApkFile);
            ArrayList<NativeLib> nativeLibsAll = a.getAllAssertsLibs();
            nativeLibsAll.addAll(a.getReportedArmV5Libs());
            nativeLibsAll.addAll(a.getReportedArmV7Libs());
            nativeLibsAll.addAll(a.getReportedX86Libs());
            final int result = appProtection.check(info, nativeLibsAll, true);
            if (result > 0 && !standAloneMode) {
                try {
                    FileOperation.copyFile(
                            targetApkFile,
                            apkFileDir
                                    + "AppProtection" + Env.slash() + AppProtection.checkResultToString(result)
                                    + Env.slash() + FileOperation.shortName(targetApkFile));
                } catch (IOException e) {
                    Log.e("Error cp app protection apk: "
                                    + FileOperation.shortName(targetApkFile));
                }
            }
            a.destroy();
        }

        // dump result to xls file
        ExcelOperation resultExcel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", App_Protection_Usage_Report.class
                        .getSimpleName()));
        appProtection.dump(resultExcel);
    }
}
