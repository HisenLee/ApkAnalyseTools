
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

import java.util.ArrayList;

public class Third_Party_Enabling_Report_SheetOne {

    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {

        // initiate counter for mismatched 3rd party libs
        ArrayList<NativeLibCounter> thirdPartyLibCounter = new ArrayList<NativeLibCounter>();
        if (libInfos != null) {
            for (ThirdPartyLibInfo l : libInfos) {
                thirdPartyLibCounter.add(new NativeLibCounter(l.getName()));
            }
        }

        // initiate counter for mismatched ISV libs
        ArrayList<NativeLibCounter> isvLibCounter = new ArrayList<NativeLibCounter>();

        // go through all apk files
        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir,
                FileType.TYPE_APK);
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
            Apk a = new Apk(targetApkFile);
            // we only care the x86 mismatch apps
            if (a.isX86LibMismatchApp()) {
                ArrayList<NativeLib> mismatchedLibs = a.getMismatchedX86Libs();
                for (NativeLib l : mismatchedLibs) {
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
                    if (isThirdPartyLib) {
                        continue;
                    }

                    // is ISV lib
                    boolean find = false;
                    for (NativeLibCounter isvLib : isvLibCounter) {
                        if (isvLib.name.equals(libName)) {
                            isvLib.registerOccurence(info.apk_name);
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        NativeLibCounter newIsvLib = new NativeLibCounter(libName);
                        newIsvLib.registerOccurence(info.apk_name);
                        isvLibCounter.add(newIsvLib);
                    }
                }
            }
            a.destroy();
        }

        // dump result to xls file
        ExcelOperation resultExcel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", Third_Party_Enabling_Report_SheetOne.class
                        .getSimpleName()));

        // write column names
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Index");
        columnNames.add("Mismatched Lib");
        columnNames.add(ThirdPartyLibExcelParser.COLUMN_NAME_LIB_DESCRIPTION);
        columnNames.add("AM");
        columnNames.add("AE");
        columnNames.add("Enabling Status");
        columnNames.add("Involved App Number");
        columnNames.add("Involved Apps");
        //columnNames.add("3rd Party");
        columnNames.add(ThirdPartyLibExcelParser.COLUMN_NAME_X86_SUPPORT);
        resultExcel.writeRow(0, columnNames);

        int row = 1;
        int index =1;
        ArrayList<Object> libValues = new ArrayList<Object>();

        thirdPartyLibCounter = NativeLibCounter.sort(thirdPartyLibCounter);
        // write 3rd party lib info
        for (NativeLibCounter c : thirdPartyLibCounter) {
            if (c.occurence <= 0) {
                continue;
            }
            libValues.clear();
            // index 
            libValues.add(index ++);
            // Name
            libValues.add(c.name);
            // Description
            for (ThirdPartyLibInfo p : libInfos) {
                if (p.getName().equals(c.name)) {
                    libValues.add(p.getDescription());
                    // libValues.add(p.getX86Support());
                    // libValues.add(p.getVendor());
                }
            }

            // AM
            libValues.add("");
            // AE
            libValues.add("");
            // Enabling Status
            libValues.add("");
            // Involved app number
            libValues.add(c.occurence);
            // Involved apps
            String apkNames = "";
            for (String apk : c.invokedApks) {
                apkNames = apkNames + Env.lineSeparator() + FileOperation.shortName(apk);
            }
            libValues.add(apkNames);
            // is 3rd party lib?
            //libValues.add("YES");

            // x86 support
            for (ThirdPartyLibInfo p : libInfos) {
                if (p.getName().equals(c.name)) {
                    // libValues.add(p.getDescription());
                    libValues.add(p.getX86Support());
                    // libValues.add(p.getVendor());
                }
            }
            resultExcel.writeRow(row, libValues);
            row++;
        }

        //isvLibCounter = NativeLibCounter.sort(isvLibCounter);
        // write ISV lib info
        //for (NativeLibCounter c : isvLibCounter) {
            //if (c.occurence <= 0) {
                // should never happen
            //}
            //libValues.clear();

            //libValues.add(c.name);
            //libValues.add("");
            //libValues.add("");
            //libValues.add("");
            //libValues.add("");
            //libValues.add(c.occurence);
            //String apkNames = "";
            //for (String apk : c.invokedApks) {
                //apkNames = apkNames + Env.lineSeparator() + FileOperation.shortName(apk);
            //}
            //libValues.add(apkNames);
            //libValues.add("NO");
            //libValues.add("");
            //resultExcel.writeRow(row, libValues);
            //row++;
        //}
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

        public static ArrayList<NativeLibCounter> sort(ArrayList<NativeLibCounter> orig) {
            if (orig.size() <= 0) {
                return orig;
            }

            ArrayList<NativeLibCounter> result = new ArrayList<NativeLibCounter>();
            while (true) {
                if(orig.size()<=0){
                    break;
                }
                NativeLibCounter biggest = orig.get(0);
                for (NativeLibCounter l : orig) {
                    if (l.occurence >= biggest.occurence) {
                        biggest = l;
                    }
                }
                result.add(biggest);
                orig.remove(biggest);
            }
            return result;
        }
    }
}
