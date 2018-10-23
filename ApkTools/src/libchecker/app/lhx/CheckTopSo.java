package libchecker.app.lhx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import libchecker.app.apk.AppInfo;
import libchecker.app.util.excel.AppInfoExcelParser;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.file.FileOperation;
/**
 * ɨ�赱ǰ�·ݵ�����apk�е�����so�ļ�����ɨ����д��Excel
 */
public class CheckTopSo {

	public static void main(String[] args) {
//		if (args == null) {
//			Log.e("Please input directory of excel......");
//			return;
//		}
		//C:\haixing\TOOLS\ApkTool2\0511
		String top3600Excel = "C:\\7" + "\\7.xls";
		AppInfoExcelParser appParser = new AppInfoExcelParser(top3600Excel, true);
		ArrayList<AppInfo> appInfos = appParser.getAppInfo();
		List<String> soList = new ArrayList<String>();// ���Top3600�����е�so�ļ��������ظ���
		
		for (AppInfo appInfo : appInfos) {
			String appAllSo = appInfo.soUnderArmv5 + appInfo.soUnderArmv7 + appInfo.soUnderArmv8 
					+ appInfo.soUnderx86 + appInfo.soUnderx86_64 + appInfo.soUnderAssets;
			String[] soArr = appAllSo.trim().split("\\s+");// ���ո���
			for (String item : soArr) {
				if((item.trim()!=null) && (!item.trim().equals(""))) {
					soList.add(item.substring(item.lastIndexOf("\\")+1, item.length()));
				}
				
			}
			
		}
		
		// HashMap�ļ����ܴ�ȡ�ظ�ֵ
		Map<String, Integer> hMap =  new TreeMap<String, Integer>();
		for (String item : soList) {
			String soNameString = item;
			int soNum = Collections.frequency(soList, item);
			hMap.put(soNameString, soNum);
		}
		System.out.println("Top3600�й���"+soList.size()+"��so,ȥ���ظ���󣬹���"+hMap.size()+"��so");
		
		//���ｫmap.entrySet()ת����list
        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(hMap.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ��map��ֵ����
        Collections.sort(list, new Comparator<Map.Entry<String,Integer>>() {

			public int compare(Map.Entry<String,Integer> arg0, Map.Entry<String,Integer> arg1) {
				return arg1.getValue().compareTo(arg0.getValue());
			}
		});
        
        
        
        // ��ɨ����д��Excel
        ExcelOperation excel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", CheckTopSo.class
                        .getSimpleName()));
        int row = 0;
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Lib Name");// column2:LibName
        columnNames.add("Lib Count");// column3:��Lib��Top3600�г��ֵĴ���
        excel.writeRow(row++, columnNames);
        for(Map.Entry<String,Integer> mapping: list){ 
        	ArrayList<Object> libValues = new ArrayList<Object>();
            libValues.add(mapping.getKey());  
            libValues.add(mapping.getValue());
            excel.writeRow(row++, libValues);
            System.out.println(list.size()+"==>"+mapping.getKey()+":"+mapping.getValue()); 
        } 
        
        
		
	}
	

}
