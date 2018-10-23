
package libchecker.app.reports;

import libchecker.app.util.Log;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Copy_Apk_List {
    public static void main(String[] args) {
        if (args == null || args.length != 3) {
            Log.e("args[0]: app list file, args[1]: source , args[2]: dest");
            return;
        }
        BufferedReader br = null;
        ArrayList<String> files = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(args[0]));
            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                if(line.endsWith(FileType.TYPE_APK)){
                    
                }else{
                    line = line+FileType.TYPE_APK;
                }
                files.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
        } finally {
            try {
                br.close();
            } catch (IOException e) {
            }
        }
        FileOperation.copyFileList(files, args[1], args[2]);
    }
}
