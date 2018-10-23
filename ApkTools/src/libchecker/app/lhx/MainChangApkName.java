package libchecker.app.lhx;

import java.io.File;
/**
 * 此类暂时不需要
 */
public class MainChangApkName {

	/**
	 * C:\haixing\TOOLS\ApkTool2\0517 C:\haixing\TOOLS\ApkTool2\0517\test
	 */
	public static void main(String[] args) {
		
		
		if (args == null) {
            System.out.println("Please input correct path of the 360apks");
            return;
        }
		
//		File root = new File("C:\\haixing\\TOOLS\\ApkTool2\\0517\\test");
		File root = new File(args[0]);
		try {
			renameFile(root);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 更改apk名称
	 * 
	 * @param src 更改前：2017_4_28_ 快的打车司机版_com.funcity.taxi.driver_.apk
	 * @return  更改后：快的打车司机版
	 */
	final static String changeApkName(String src) {
		String descName = "";
		if (src == null || src.equals("")) {
			descName = src + " rename error";
		} else {
			String[] splitArr = src.split("_");
			if(splitArr != null && splitArr.length>3) {
				descName = splitArr[3].trim();
			} else{
				descName =  src + " rename error";
			}
			
		}
		return descName;

	}

	/**
	 * 遍历某个路径下的所有文件并更名
	 * 
	 * @param dir
	 * @throws Exception
	 */
	final static void renameFile(File dir) throws Exception {
		File[] fs = dir.listFiles();

		for (int i = 0; i < fs.length; i++) {
			
			System.out.println("=====start rename apk file " + i + "==============");
			
			// 打印全路径名称
			System.out.println(fs[i].getAbsolutePath());

			// 判断文件是否存在
			if (!fs[i].exists()) {
				fs[i].createNewFile();
			}
			
			// 修改前的文件名称
			String srcName = fs[i].getName();
			System.out.println("src name：" + srcName);
			
			// 修改前的文件跟路径
			String rootPath = fs[i].getParent();
//			System.out.println("根路径是：" + rootPath);
			
			// 修改后的文件全路径名
//			String descPath = rootPath + File.separator + UUID.randomUUID().toString() + ".apk";
			String descPath = rootPath + File.separator + changeApkName(srcName) + ".apk";
			// 用新的名字去创建文件
			File newFile = new File(descPath);
			// 修改后的文件名称
			String descName = newFile.getName();
			System.out.println("desc name：" + descName);
			
			// 调用renameTo方法来实现重命名
			if (fs[i].renameTo(newFile)) {
				System.out.println(srcName + " rename success");
			} else {
				System.out.println(srcName + " rename fail");
			}

		}

	}

}
