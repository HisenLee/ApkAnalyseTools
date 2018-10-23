package libchecker.app.reports;

import libchecker.app.apk.AppInfo;
import libchecker.app.apk.DecompiledApk;
import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.excel.ThirdPartyLibExcelParser.ThirdPartyLibInfo;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.util.ArrayList;

public class Summarize_App_Self_Permissions {
    
    private static ArrayList<SelfPermission> mPermissionDatabase = new ArrayList<SelfPermission>();
    
    private static int mSucceedApp = 0;
    private static int mFailedApp = 0;
    private static ArrayList<String> mFailedAppList = new ArrayList<String>();
    
    public static void doSummarize(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {

        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir, FileType.TYPE_APK);

        ExcelOperation excel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", Summarize_App_Self_Permissions.class
                        .getSimpleName()));

        int row = 0;
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("PermissionName");
        columnNames.add("Usage");
        columnNames.add("Apks");
        excel.writeRow(row++, columnNames);

        int current = 0;
        int total = appInfos.size();
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
            Log.i(++current + " of "+total +", processing apk: "+FileOperation.shortName(apkFile));
            DecompiledApk apk = new DecompiledApk(apkFile);
            ArrayList<String> selfPermissions = apk.getSelfDeclaredPermission();
            if(selfPermissions == null){
                mFailedApp ++;
                mFailedAppList.add(apkName);
            }else{
                mSucceedApp++;
            }
            if(selfPermissions!= null && selfPermissions.size() > 0){
                for(String selfpermission : selfPermissions){
                    boolean find = false;
                    for(SelfPermission permission :  mPermissionDatabase){
                        if(selfpermission.equals(permission.name)){
                            permission.regApk(appInfo.apk_name);
                            find = true;
                        }
                    }
                    if(!find){
                        SelfPermission p = new SelfPermission(selfpermission);
                        p.regApk(appInfo.apk_name);
                        mPermissionDatabase.add(p);
                    }
                }
            }
            apk.destroy();
        }
        
        // dump to excel
        for (SelfPermission p : mPermissionDatabase) {
            ArrayList<Object> libValues = new ArrayList<Object>();
            libValues.add(p.name);
            libValues.add(p.usage);
            libValues.add(p.apksString());
            excel.writeRow(row++, libValues);
        }
        
        ArrayList<Object> libValues = new ArrayList<Object>();
        libValues.add("");
        excel.writeRow(row++, libValues);
            
        libValues = new ArrayList<Object>();
        libValues.add("Total Apps: Success/Fail "+mSucceedApp+"+"+mFailedApp+" = "+(mSucceedApp+mFailedApp));
        libValues.add("Failed List: ");
        libValues.add(arrayToStringLine(mFailedAppList));
        excel.writeRow(row++, libValues);
    }
    static class SelfPermission{
        public String name = null;
        public ArrayList<String> includedApps = new ArrayList<String>();
        public int usage = 0;
        
        public SelfPermission(String n){
            name = n;
        }
        public void regApk(String apk){
            includedApps.add(apk);
            usage++;
        }
        public String apksString(){
            return arrayToStringLine(includedApps);
        }
    }
    
    private static String arrayToStringLine(ArrayList<String> target){
        if(target == null || target.size() <=0){
            return "";
        }
        String out = target.get(0);;
        if(target.size() > 1){
            out = out + Env.lineSeparator();
            for(int index = 1; index < target.size() -1; index ++){
                out = out + target.get(index);
                out = out + Env.lineSeparator();
            }
            out = out + target.get(target.size() -1);
        }
        return out;
    }
}
