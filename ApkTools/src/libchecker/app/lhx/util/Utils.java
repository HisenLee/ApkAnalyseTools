package libchecker.app.lhx.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

import org.apkinfo.api.util.AXmlResourceParser;
import org.apkinfo.api.util.TypedValue;
import org.apkinfo.api.util.XmlPullParser;

public final class Utils {

	/**
	 * showErrorMsg
	 */
	public static final void showErrorMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", 0);//YES_NO_OPTION
	}
	
	/**
	 * getApkName
	 * @param apkPath
	 * @param aaptPath
	 * @return
	 */
	public String getApkName(String apkPath) {
		String apkName = "";
		try {
			Runtime rt = Runtime.getRuntime();
			
//			String order = aaptPath + "aapt.exe" + " d badging \"" + apkPath + "\"";
			String order = this.getClass().getResource("/aapt.exe").getPath() + " d badging \"" + apkPath + "\"";
			
			Process proc = rt.exec(order);
			InputStream inputStream = proc.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			String line = null;
			while((line = bufferedReader.readLine()) != null){
				if(line.contains("application:")){//application: label='手机管家' icon='res/drawable-hdpi/icon.png'
					String str1 = line.substring(line.indexOf("'")+1);
					String str2 = str1.substring(0, str1.indexOf("'"));
//					System.out.println(str2);
					apkName = str2;
					break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return apkName;
	}
	
	/**
	 * ReadApk
	 * 
	 * @return
	 */
	public Map<String, Object> readApk(String apkUrl) {
		ZipFile zipFile;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			zipFile = new ZipFile(apkUrl);
			Enumeration<?> enumeration = zipFile.entries();
			ZipEntry zipEntry = null;
			while (enumeration.hasMoreElements()) {
				zipEntry = (ZipEntry) enumeration.nextElement();
				if (zipEntry.isDirectory()) {

				} else {
					if ("androidmanifest.xml".equals(zipEntry.getName()
							.toLowerCase())) {
						AXmlResourceParser parser = new AXmlResourceParser();
						parser.open(zipFile.getInputStream(zipEntry));
						while (true) {
							int type = parser.next();
							if (type == XmlPullParser.END_DOCUMENT) {
								break;
							}
							String name = parser.getName();
							if (null != name
									&& name.toLowerCase().equals("manifest")) {
								for (int i = 0; i != parser.getAttributeCount(); i++) {
									if ("versionName".equals(parser
											.getAttributeName(i))) {
										String versionName = getAttributeValue(
												parser, i);
										if (null == versionName) {
											versionName = "";
										}
										map.put("versionName", versionName);
									} else if ("package".equals(parser
											.getAttributeName(i))) {
										String packageName = getAttributeValue(
												parser, i);
										if (null == packageName) {
											packageName = "";
										}
										map.put("package", packageName);
									} else if ("versionCode".equals(parser
											.getAttributeName(i))) {
										String versionCode = getAttributeValue(
												parser, i);
										if (null == versionCode) {
											versionCode = "";
										}
										map.put("versionCode", versionCode);
									}
								}
								break;
							}
						}
					}

				}
			}
			zipFile.close();
		} catch (Exception e) {
			map.put("code", "fail");
			map.put("error", "read apk error");
			
			map.put("versionName", "");
			map.put("package", "");
			map.put("versionCode", "");
		}
		return map;
	}

	private static String getAttributeValue(AXmlResourceParser parser, int index) {
		int type = parser.getAttributeValueType(index);
		int data = parser.getAttributeValueData(index);
		if (type == TypedValue.TYPE_STRING) {
			return parser.getAttributeValue(index);
		}
		if (type == TypedValue.TYPE_ATTRIBUTE) {
			return String.format("?%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_REFERENCE) {
			return String.format("@%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (type == TypedValue.TYPE_INT_HEX) {
			return String.format("0x%08X", data);
		}
		if (type == TypedValue.TYPE_INT_BOOLEAN) {
			return data != 0 ? "true" : "false";
		}
		if (type == TypedValue.TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data))
					+ DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type == TypedValue.TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))
					+ FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type >= TypedValue.TYPE_FIRST_COLOR_INT
				&& type <= TypedValue.TYPE_LAST_COLOR_INT) {
			return String.format("#%08X", data);
		}
		if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>", data, type);
	}

	private static String getPackage(int id) {
		if (id >>> 24 == 1) {
			return "android:";
		}
		return "";
	}

	public static float complexToFloat(int complex) {
		return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
	}

	private static final float RADIX_MULTS[] = { 0.00390625F, 3.051758E-005F,
			1.192093E-007F, 4.656613E-010F };

	private static final String DIMENSION_UNITS[] = { "px", "dip", "sp", "pt",
			"in", "mm", "", "" };
	private static final String FRACTION_UNITS[] = { "%", "%p", "", "", "", "",
			"", "" };
	
	
}
