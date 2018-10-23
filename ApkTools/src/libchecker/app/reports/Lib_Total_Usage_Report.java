
package libchecker.app.reports;

import libchecker.app.apk.Apk;
import libchecker.app.apk.AppInfo;
import libchecker.app.apk.nativelib.NativeLib;
import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.excel.ThirdPartyLibExcelParser;
import libchecker.app.util.excel.ThirdPartyLibExcelParser.ThirdPartyLibInfo;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.io.IOException;
import java.util.ArrayList;

public class Lib_Total_Usage_Report {

    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {

        boolean standAloneMode = false;
        if(appInfos == null){
            standAloneMode = true;
            Log.i("standAloneMode: "+ standAloneMode);
            
            ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir,
                    FileType.TYPE_APK);
            if(standAloneMode){
                appInfos = new ArrayList<AppInfo>();
                for (String apkFile : apkFiles){
                    AppInfo info = new AppInfo(apkFile);
                    //Log.out(Log.D, apkFile);
                    appInfos.add(info);
                }
            }
        }
        
        // initiate counter for 3rd party libs
        ArrayList<NativeLibCounter> thirdPartyLibCounter = new ArrayList<NativeLibCounter>();
        if (libInfos != null) {
            for (ThirdPartyLibInfo l : libInfos) {
                thirdPartyLibCounter.add(new NativeLibCounter(l.getName()));
            }
        }

        // initiate counter for ISV libs
        ArrayList<NativeLibCounter> isvLibCounter = new ArrayList<NativeLibCounter>();

        // go through all apk files
        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir,
                FileType.TYPE_APK);
        int current = 0;
        int total = appInfos.size();
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
                Log.e("apk not found " + info.apk_name);
                continue;
            }
            Log.i(++current + " of "+total +", processing apk: "+FileOperation.shortName(targetApkFile));
            Apk a = new Apk(targetApkFile);
            ArrayList<NativeLib> nativeLibsAll = a.getAllAssertsLibs();
            nativeLibsAll.addAll(a.getReportedArmV5Libs());
            nativeLibsAll.addAll(a.getReportedArmV7Libs());
            nativeLibsAll.addAll(a.getReportedX86Libs());
            // trim libs with the same name
            ArrayList<NativeLib> nativeLibs = new ArrayList<NativeLib>();
            boolean find = false;
            for (NativeLib lA : nativeLibsAll) {
                find = false;
                for (NativeLib l : nativeLibs) {
                    if (l.getName().equals(lA.getName())) {
                        find = true;
                    }
                }
                if (!find) {
                    nativeLibs.add(lA);
                }
            }
            for (NativeLib l : nativeLibs) {
                String libName = l.getName();
                boolean isThirdPartyLib = false;
                // check whether is 3rd party lib
                for (NativeLibCounter thirdParty : thirdPartyLibCounter) {
                    if (thirdParty.name.equals(libName)) {
                        thirdParty.registerOccurence(info.apk_name);
                        isThirdPartyLib = true;
                        break;
                    }
                }
                if(isThirdPartyLib && l.getName().contains("libmegjb")){
                    // is cmcc related apk
                    try {
                        FileOperation.copyFile(targetApkFile, apkFileDir
                                + "CMCC" + Env.slash() + FileOperation.shortName(targetApkFile));
                    } catch (IOException e) {
                        Log.e("Error cp cmcc apk: "+FileOperation.shortName(targetApkFile));
                    }
                }
                if (isThirdPartyLib) {
                    continue;
                }

                // is ISV lib
                boolean find1 = false;
                for (NativeLibCounter isvLib : isvLibCounter) {
                    if (isvLib.name.equals(libName)) {
                        isvLib.registerOccurence(info.apk_name);
                        find1 = true;
                        break;
                    }
                }
                if (!find1) {
                    NativeLibCounter newIsvLib = new NativeLibCounter(libName);
                    newIsvLib.registerOccurence(info.apk_name);
                    isvLibCounter.add(newIsvLib);
                }
            }
            a.destroy();
        }

        // dump result to xls file
        ExcelOperation resultExcel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", Lib_Total_Usage_Report.class
                        .getSimpleName()));

        // write column names
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Native Lib");
        columnNames.add("Invoke Time");
        columnNames.add("Invoke Apk");
        columnNames.add("3rd Party");
        columnNames.add(ThirdPartyLibExcelParser.COLUMN_NAME_LIB_DESCRIPTION);
        columnNames.add(ThirdPartyLibExcelParser.COLUMN_NAME_X86_SUPPORT);
        columnNames.add("Vendor");
        columnNames.add("Category");
        resultExcel.writeRow(0, columnNames);

        int row = 1;
        ArrayList<Object> libValues = new ArrayList<Object>();

        // write 3rd party lib info
        for (NativeLibCounter c : thirdPartyLibCounter) {
            if (c.occurence <= 0) {
                continue;
            }
            libValues.clear();
            libValues.add(c.name);
            libValues.add(c.occurence);
            String apkNames = "";
            for (String apk : c.invokedApks) {
                apkNames = apkNames + Env.lineSeparator()
                        + FileOperation.shortName(apk);
            }
            libValues.add(apkNames);
            libValues.add("YES");
            for (ThirdPartyLibInfo p : libInfos) {
                if (p.getName().equals(c.name)) {
                    libValues.add(p.getDescription());
                    libValues.add(p.getX86Support());
                    libValues.add(p.getVendor());
                    libValues.add(p.getCategory());
                }
            }
            resultExcel.writeRow(row, libValues);
            row++;
        }

        // write ISV lib info
        for (NativeLibCounter c : isvLibCounter) {
            if (c.occurence <= 0) {
                // should never happen
            }
            libValues.clear();
            libValues.add(c.name);
            libValues.add(c.occurence);
            String apkNames = "";
            for (String apk : c.invokedApks) {
                apkNames = apkNames + Env.lineSeparator()
                        + FileOperation.shortName(apk);
            }
            libValues.add(apkNames);
            libValues.add("NO");
            resultExcel.writeRow(row, libValues);
            row++;
        }
    }

    public static class NativeLibCounter {
        private String name = null;
        private int occurence = 0;
        private ArrayList<String> invokedApks = new ArrayList<String>();

        public NativeLibCounter(String n) {
            name = n;
            occurence = 0;
            invokedApks.clear();
        }

        public void registerOccurence(String apk) {
            invokedApks.add(apk);
            occurence++;
        }

        public ArrayList<String> getInvolvedApks() {
            return invokedApks;
        }

        public int getOccurence() {
            return occurence;
        }

        public String getName() {
            return name;
        }
    }
}
