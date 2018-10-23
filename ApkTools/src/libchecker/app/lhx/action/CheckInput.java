package libchecker.app.lhx.action;

import java.io.File;
import java.util.ArrayList;

import libchecker.app.lhx.util.Utils;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;


public class CheckInput {
	
	public boolean checkDir(String apkFileDir) {
		if(apkFileDir==null || "null".equals(apkFileDir) || "".equals(apkFileDir)) {
			Utils.showErrorMsg("Please select the directory where the apk file is located");
			return false;
		}
		
		File file = new File(apkFileDir);
		if(!file.isDirectory()) {
			Utils.showErrorMsg("Please select the directory where the apk file is located");
			return false;
		}
		
        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir,
                FileType.TYPE_APK);
        if(apkFiles==null || apkFiles.size()<1) {
        	Utils.showErrorMsg("There is no apk file in the directory");
			return false;
        }
		
        return true;
	}
	
	
}
