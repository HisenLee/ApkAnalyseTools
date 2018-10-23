package libchecker.app.lhx;

import java.io.File;
/**
 * ������ʱ����Ҫ
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
	 * ����apk����
	 * 
	 * @param src ����ǰ��2017_4_28_ ��Ĵ�˾����_com.funcity.taxi.driver_.apk
	 * @return  ���ĺ󣺿�Ĵ�˾����
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
	 * ����ĳ��·���µ������ļ�������
	 * 
	 * @param dir
	 * @throws Exception
	 */
	final static void renameFile(File dir) throws Exception {
		File[] fs = dir.listFiles();

		for (int i = 0; i < fs.length; i++) {
			
			System.out.println("=====start rename apk file " + i + "==============");
			
			// ��ӡȫ·������
			System.out.println(fs[i].getAbsolutePath());

			// �ж��ļ��Ƿ����
			if (!fs[i].exists()) {
				fs[i].createNewFile();
			}
			
			// �޸�ǰ���ļ�����
			String srcName = fs[i].getName();
			System.out.println("src name��" + srcName);
			
			// �޸�ǰ���ļ���·��
			String rootPath = fs[i].getParent();
//			System.out.println("��·���ǣ�" + rootPath);
			
			// �޸ĺ���ļ�ȫ·����
//			String descPath = rootPath + File.separator + UUID.randomUUID().toString() + ".apk";
			String descPath = rootPath + File.separator + changeApkName(srcName) + ".apk";
			// ���µ�����ȥ�����ļ�
			File newFile = new File(descPath);
			// �޸ĺ���ļ�����
			String descName = newFile.getName();
			System.out.println("desc name��" + descName);
			
			// ����renameTo������ʵ��������
			if (fs[i].renameTo(newFile)) {
				System.out.println(srcName + " rename success");
			} else {
				System.out.println(srcName + " rename fail");
			}

		}

	}

}
