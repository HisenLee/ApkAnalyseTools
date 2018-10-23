package libchecker.app.reports;

import libchecker.app.adb.AppLauncher;
import libchecker.app.adb.GetAppProcessIDs;
import libchecker.app.adb.GetAppTotalPss;
import libchecker.app.adb.PkgInstaller;
import libchecker.app.apk.AppInfo;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.excel.ThirdPartyLibExcelParser.ThirdPartyLibInfo;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.util.ArrayList;

public class Dump_App_Pss {
    // Nexus 7
    public static final String DEVICE_NEXUS7="078effa6";
    
    // FFRD8
    public static final String DEVICE_FFRD8="Baytrail329BC304";
    
    
    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {
        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir, FileType.TYPE_APK);

        ExcelOperation excel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", Dump_App_Pss.class
                        .getSimpleName()));

        int row = 0;
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Ranking");
        columnNames.add("Apk Name");
        columnNames.add("Apk Type");
        columnNames.add("Package Name");
        columnNames.add("Nexus7");
        columnNames.add("FFRD8");
        columnNames.add("Gap(M)");
        columnNames.add("Gap(%)");
        excel.writeRow(row++, columnNames);
        
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

            int nexus7 = 0;
            int ffrd8 = 0;
            PkgInstaller.uninstallApp(DEVICE_NEXUS7,appInfo.package_name);
            PkgInstaller.installApk(DEVICE_NEXUS7, apkFile);
            AppLauncher.launchApp(DEVICE_NEXUS7, appInfo.package_name);
            nexus7 = GetAppTotalPss.getAppTotalPss(DEVICE_NEXUS7,GetAppProcessIDs.getPIDs(DEVICE_NEXUS7, appInfo.package_name));
            PkgInstaller.uninstallApp(DEVICE_NEXUS7,appInfo.package_name);
            
            PkgInstaller.uninstallApp(DEVICE_FFRD8,appInfo.package_name);
            PkgInstaller.installApk(DEVICE_FFRD8, apkFile);
            AppLauncher.launchApp(DEVICE_FFRD8, appInfo.package_name);
            ffrd8 = GetAppTotalPss.getAppTotalPss(DEVICE_FFRD8,GetAppProcessIDs.getPIDs(DEVICE_FFRD8, appInfo.package_name));
            PkgInstaller.uninstallApp(DEVICE_FFRD8,appInfo.package_name);
            
            ArrayList<Object> rowResult = new ArrayList<Object>();
            rowResult.add(appInfo.app_rank);
            rowResult.add(appInfo.apk_name);
            rowResult.add(appInfo.ndk);
            rowResult.add(appInfo.package_name);
            rowResult.add(nexus7);
            rowResult.add(ffrd8);
            rowResult.add(ffrd8-nexus7);
            rowResult.add(""+(float)(ffrd8-nexus7)/nexus7*100 +"%");
            excel.writeRow(row++, rowResult);
        }
    }
}
