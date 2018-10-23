package libchecker.app.util.excel;

import libchecker.app.apk.AppInfo;
import libchecker.app.util.Log;

import java.util.ArrayList;

public class XiaomiExcelParser {
    
    public ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
    
    public static void main(String[] args){
        parse("C:\\Users\\fpei\\Desktop\\padgame.xls");
    }
    public static void parse(String excel){
        
        ExcelOperation resultExcel = new ExcelOperation(
                "C:\\Users\\fpei\\Desktop\\padgame-parsed.xls");
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("ranking");
        columnNames.add("package name");
        columnNames.add("apk name");
        resultExcel.writeRow(0, columnNames);
        int row =1;
        
        ExcelOperation ex = new ExcelOperation(excel);
        final int totalCount = ex.getNumberOfRows();
        for (int i = 1; i <= totalCount-1; i++){
            String apkName = null;
            ExcelItem item = ex.readItem(i, 4);
            if(item.content != null){
                final String content = item.content.toString();
                final String[] cs = content.split(":");
                for(int ci =0; ci<cs.length;ci++){
                    if(cs[ci].contains("zh_CN")){
                        apkName = cs[ci+1].replace("\"", "").replace("}", "");
                        if(apkName.contains(",")){
                            apkName = apkName.substring(0, apkName.indexOf(","));
                        }
                        Log.d(apkName);
                    }
                }
                if(apkName == null){
                    Log.e("content apk parse error:"+content);
                    continue;
                }
            }
            
            columnNames = new ArrayList<Object>();
            columnNames.add(row);
            columnNames.add(ex.readItem(i, 3).content.toString());
            columnNames.add(apkName);
            resultExcel.writeRow(row++, columnNames);
        }
    }
}
