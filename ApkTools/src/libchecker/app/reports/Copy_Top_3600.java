
package libchecker.app.reports;

import libchecker.app.apk.AppInfo;
import libchecker.app.util.Log;
import libchecker.app.util.excel.AppInfoExcelParser;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.io.IOException;
import java.util.ArrayList;

public class Copy_Top_3600 {

    public static void main(String[] args) {
        if(args == null){
            Log.e("params: top3600excel, apkSourceFolder, destinationFolder");
            return;
        }
        final long now = System.currentTimeMillis();
        func(args[0], args[1], args[2]);
        final long sec = (System.currentTimeMillis() - now) / 1000;
        Log.d("Totally " + sec / 60 + " min " + sec % 60 + "sec.");
    }

    public static void func(String top3600Excel, String sourceFolder, String destFolder) {
        AppInfoExcelParser appParser = new AppInfoExcelParser(top3600Excel);
        ArrayList<AppInfo> appInfos = appParser.getAppInfo();
        ArrayList<String> apkFiles = FileOperation.listAll(
                sourceFolder,
                FileType.TYPE_APK);
        int index = 1;
        for (AppInfo info : appInfos) {
            String targetApkName = info.apk_name + FileType.TYPE_APK;
            String targetApkFile = null;
            for (String apkFile : apkFiles) {
                if (apkFile.endsWith(targetApkName)) {
                    targetApkFile = apkFile;
                    break;
                }
            }
            if (targetApkFile == null) {
                // not found, do workaround for the bug
                try {
                    String tmp = targetApkName.split("_")[3];
                    String tmpContent = targetApkName
                            .substring(targetApkName.indexOf(tmp));
                    for (String apkFile : apkFiles) {
                        if (apkFile.endsWith(tmpContent)) {
                            targetApkFile = apkFile;
                            break;
                        }
                    }
                } catch (Exception ex) {
                }
            }
            if (targetApkFile == null) {
                Log.e("apk not found " + info.apk_name);
                continue;
            }
            try {
                boolean copyFile = FileOperation
                        .copyFile(targetApkFile, destFolder + targetApkName);
                Log.d(index++ + " of " + appInfos.size() + ": copy apk " + info.apk_name +" result:" + copyFile +" speed:"+FileOperation.getCopySpeed());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
