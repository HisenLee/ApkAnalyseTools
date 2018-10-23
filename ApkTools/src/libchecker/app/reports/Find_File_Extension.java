
package libchecker.app.reports;

import libchecker.app.apk.Apk;
import libchecker.app.apk.AppInfo;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.excel.ThirdPartyLibExcelParser.ThirdPartyLibInfo;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.util.ArrayList;

public class Find_File_Extension {
    
    // which file or file extension to find
    private static final String TARGET_EXTENSION =".bc";
    
    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {

        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir, FileType.TYPE_APK);

        ExcelOperation excel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", Find_File_Extension.class
                        .getSimpleName()));

        int row = 0;
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Ranking");
        columnNames.add("Apk Name");
        columnNames.add("Package Name");
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
            // parse the apk file
            Apk apk = new Apk(apkFile);
            if (apk.containsFile(TARGET_EXTENSION)) {
                ArrayList<Object> libValues = new ArrayList<Object>();
                libValues.add(appInfo.app_rank);
                libValues.add(appInfo.apk_name);
                libValues.add(appInfo.package_name);
                excel.writeRow(row++, libValues);
            }
            apk.destroy();
        }
    }
}
