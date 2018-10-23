
package libchecker.app.reports;

import libchecker.app.apk.Apk;
import libchecker.app.apk.AppInfo;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.excel.ThirdPartyLibExcelParser.ThirdPartyLibInfo;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.util.ArrayList;

public class Third_Party_Enabling_Report_SheetTwo {

    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfo) {

        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir, FileType.TYPE_APK);

        ExcelOperation resultExcel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", Third_Party_Enabling_Report_SheetTwo.class.getSimpleName()));

        int row = 0;
        // write column names
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Index");
        columnNames.add("App Ranking");
        columnNames.add("App Name");
        columnNames.add("Apk Name");
        //columnNames.add("App Vendor");
        columnNames.add("App AM");
        columnNames.add("App AE");
        //columnNames.add("Mismatched Lib");
        //columnNames.add("Lib Vendor");
        // columnNames.add("Lib Description");
        // columnNames.add("Lib Platform Support");
        // columnNames.add("Lib AM");
        // columnNames.add("Lib AE");
        columnNames.add("Status");
        resultExcel.writeRow(row++, columnNames);

        // go through each apk files
        for (AppInfo info : appInfos) {
            // find the apk file
            final String apkName = info.apk_name + FileType.TYPE_APK;
            String apkFile = null;
            for (String n : apkFiles) {
                if (n.endsWith(apkName)) {
                    apkFile = n;
                }
            }
            if (apkFile == null) {
                Log.e("apk " + apkName + " not found!");
                continue;
            }
            // parse the apk file
            Apk apk = new Apk(apkFile);
            if (apk.isX86LibMismatchApp()) {
                //ArrayList<NativeLib> mismatchedLibs = apk.getMismatchedLibs();
                //ArrayList<LibDescriptionMap> libDescriptionMap = new ArrayList<LibDescriptionMap>();
                //for (NativeLib l : mismatchedLibs) {
                    //for (ThirdPartyLibInfo lib : libInfo) {
                        //if (lib.getName().equals(l.getName())) {
                            //boolean find = false;
                            //for (LibDescriptionMap m : libDescriptionMap) {
                                //if (m.libDescription.equals(lib.getDescription())) {
                                    //m.addLib(lib.getName());
                                    //find = true;
                                   //break;
                                //}
                            //}
                            //if (!find) {
                                //LibDescriptionMap map = new LibDescriptionMap();
                                //map.libDescription = lib.getDescription();
                                //map.libVendor = lib.getVendor();
                                //map.libPlatformSuport = lib.getX86Support();
                                //map.addLib(lib.getName());
                                //libDescriptionMap.add(map);
                            //}
                        //}
                    //}
                //}
                // dump to excel
                //for (LibDescriptionMap m : libDescriptionMap) {
                    ArrayList<Object> libValues = new ArrayList<Object>();
                    libValues.add(row);
                    libValues.add(info.app_rank);
                    libValues.add(info.app_name);
                    libValues.add(info.apk_name);
                    //libValues.add(info.publisher);
                    libValues.add("");
                    libValues.add("");
                    //libValues.add(m.getLibs());
                    //libValues.add(m.libVendor);
                    //libValues.add(m.libDescription);
                    //libValues.add(m.libPlatformSuport);
                    //libValues.add("");
                    //libValues.add("");
                    libValues.add("");
                    resultExcel.writeRow(row++, libValues);
                //}
            }
            apk.destroy();
        }
    }

    public static class LibDescriptionMap {
        public String libDescription;
        public String libVendor;
        public String libPlatformSuport;
        public ArrayList<String> libs = new ArrayList<String>();

        public void addLib(String lib) {
            libs.add(lib);
        }

        public String getLibs() {
            String libString = "";
            for (String l : libs) {
                libString = libString + l + " ";
            }
            return libString;
        }
    }
}
