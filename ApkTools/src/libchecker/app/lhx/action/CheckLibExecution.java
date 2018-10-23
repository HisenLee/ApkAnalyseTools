package libchecker.app.lhx.action;

import java.util.ArrayList;
import java.util.Map;

import libchecker.app.apk.Apk;
import libchecker.app.apk.nativelib.AppProtection;
import libchecker.app.apk.nativelib.NativeLib;
import libchecker.app.lhx.ApkLibCheck;
import libchecker.app.lhx.util.Utils;
import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;


public class CheckLibExecution {
	
	// getProcessCallbackListener
	private IGetProcessCallBack getProcessCallBack;
	// apkFiles
	private ArrayList<String> apkFiles;
	// protection
	private AppProtection appProtection;
	// apkReadUtils
	private Utils apkReadUtils;
	
	public interface IGetProcessCallBack {
		public void getProcess(String current);
	}
	
	public CheckLibExecution(String apkFileDir, IGetProcessCallBack getProcessCallBack) {
		this.getProcessCallBack = getProcessCallBack;
		// get apkFiles under desc Dir
		apkFiles = FileOperation.listAll(apkFileDir, FileType.TYPE_APK);
		// get protection
		appProtection = new AppProtection();
		// get apkReadUtils
		apkReadUtils =  new Utils();
	}
	
	/**
	 * generateExcel
	 */
	public void generateExcelResult() {
		ExcelOperation excel = new ExcelOperation(
				FileOperation.generateResultXlsName("./",
						ApkLibCheck.class.getSimpleName()));
		int row = 0;
		ArrayList<Object> columnNames = new ArrayList<Object>();
		columnNames.add("Apk Name");      // column1
		columnNames.add("Apk Version");   // column2
		columnNames.add("Package Name");  // column3
		columnNames.add("NDK Type");      // column4
		
		columnNames.add("X86 Lib Missed"); // column5:�Ƿ�ȱ��x86��  libMissed
        columnNames.add("X64 Lib Missed"); // column6:�Ƿ�ȱ��x64��  libMissed
        columnNames.add("Assets miss x86 lib"); // column7:assets���Ƿ�ȱ��x86�� libMissed
        columnNames.add("Assets miss x64 lib"); // column8:assets���Ƿ�ȱ��x64�� libMissed
        columnNames.add("Missed X86 Libs"); // column9:ȱ�ٵ�x86��  libMissed
        columnNames.add("Missed X64 Libs"); // column10:ȱ�ٵ�x64��  libMissed
        
        columnNames.add("X86 lib-mix");  // column11:�Ƿ��д����so�������x86����  libMix
        columnNames.add("X64 lib-mix");  // column12:�Ƿ��д����so�������x64����  libMix
        columnNames.add("Mixed X86 Libs"); // column13:x86�·��ű�Ŀ�  libMix�����libs
        columnNames.add("Mixed X64 Libs"); // column14:x64�·��ű�Ŀ�   libMix�����libs
        
        columnNames.add("ARM-V5 Folder Libs"); // column15: armv5�µ�libs
        columnNames.add("ARM-V7 Folder Libs"); // column16: armv7�µ�libs
        columnNames.add("ARM-V8 Folder Libs"); // column17: armv8�µ�libs
        columnNames.add("X86 Folder Libs"); // column18: x86�µ�libs
        columnNames.add("X64 Folder Libs"); // column19: x64�µ�libs
        columnNames.add("Assets Folder Libs"); // column20: assets�µ�libs
        
        columnNames.add("Nested Apks");// column21: Ƕ�׵�apk�ļ�
        
        columnNames.add("Is Protected"); // column22:�Ƿ�ӹ�
        columnNames.add("Protection Vendor"); // column23:�ӹ̵ĳ���
        columnNames.add("Related Libs"); // column24:�ӹ���ص�so�ļ�
        
        columnNames.add("CMCC SDK App");// column25:�Ƿ����CMCC�Ŀ�[�ƶ�֧��]
        columnNames.add("ChinaUnicom SDK App");// column26:�Ƿ����ChinaUnicom�Ŀ�[��֧ͨ��]
        columnNames.add("ChinaTelecom SDK App");// column27:�Ƿ����ChinaTelecom�Ŀ�[����֧��]
        columnNames.add("ChinaUnionPay SDK App");// column28:�Ƿ����ChinaUnionPay�Ŀ�[����֧��]
        
        columnNames.add("HotFix SDK App");// column29:�Ƿ�����������޸�HotFix�Ŀ�
        
        columnNames.add("Facebook RN App");// column30:�Ƿ����Facebook RN�Ŀ�
        
        columnNames.add("Tencent Live-Video App");// column31:�Ƿ����Tencent Live Video�Ŀ�
		
		excel.writeRow(row++, columnNames);
		
		for(int current = 0; current < apkFiles.size(); current++) {
			// get current apkDir;ApkEntity;ApkBasicInfo
			String apkFile = apkFiles.get(current);
			Apk apk = new Apk(apkFile);
			Map<String, Object> apkBasicInfoMap = apkReadUtils.readApk(apkFile);
			// output current process...
			getProcessCallBack.getProcess((current+1) + " of " + apkFiles.size() + ", processing apk: "
	                    + FileOperation.shortName(apkFile));
			ArrayList<Object> libValues = new ArrayList<Object>();
			
			// column1-column4 Add Columns about BasicInfo
			addBasicInfo(libValues, apk, apkBasicInfoMap);
			// column5-column10 Add Columns about Libmissing
			addAboutLibmissing(libValues, apk);
            // column11-column14 Add Columns about Libmixed
            addAboutLibMixed(libValues, apk);
            // column15-column20 Add Column about LibsUnderAllFolder
            addLibsUnderAllFolder(libValues, apk);
            // column21: Add Nested Apks Ƕ�׵�apk�ļ�
            addNestedApk(libValues, apk);
            // column22-column24 Add Columns about Protection
            addAboutProtection(libValues, apk);
            // column25-column28 Add Columns about Payment
            addAboutPay(libValues, apk);
            
            // column29:�Ƿ�����������޸�HotFix�Ŀ�
            addAlihotFix(libValues, apk);
            
            // column30:�Ƿ����FacebookRN�Ŀ�
            addFacebookRN(libValues, apk);
            
            // column31:�Ƿ����TencentLiveVideo�Ŀ�
            addTencentLiveVideo(libValues, apk);;
            
			excel.writeRow(row++, libValues);
	        apk.destroy();
	        
		}// end for
		
		
		
		
	}// end method generateExcelResult
	
	
	/**
	 * column1-column4 Add Columns about BasicInfo
	 * @param libValues
	 * @param apk
	 * @param apkBasicInfoMap
	 */
	private void addBasicInfo(ArrayList<Object> libValues, Apk apk, Map<String, Object> apkBasicInfoMap) {
		// add column1: Apk Name
//		String apkName = apkReadUtils.getApkName(apkFile);
		String apkName = apk.getShortName();
		libValues.add(apkName); 
		// add column2: Apk Version
		libValues.add(apkBasicInfoMap.get("versionName")); 
		// add column3: Package Name
		libValues.add(apkBasicInfoMap.get("package")); 
		// add column4: NDK Type
		libValues.add(apk.getApkType()); 
	}
	
	/**
	 * column5-column10 Add Columns about Libmissing
	 * @param libValues
	 * @param apk
	 */
	private void addAboutLibmissing(ArrayList<Object> libValues, Apk apk) {
		// add column5: X86 Lib Missed: �Ƿ�ȱ��x86��  libMissed
        libValues.add(apk.isX86LibMismatchApp() ? "YES" : "NO");
        // add column6: X64 Lib Missed: �Ƿ�ȱ��x64��  libMissed
        libValues.add(apk.isX64LibMismatchApp() ? "YES" : "NO");
        // add column7: assets���Ƿ�ȱ��x86�� libMissed
        libValues.add(apk.assetsLibMissingX86Version() ? "YES" : "NO");
        // add column8: assets���Ƿ�ȱ��x64�� libMissed
        libValues.add(apk.assetsLibMissingX64Version() ? "YES" : "NO");
        // add column9:ȱ�ٵ�x86��  libMissed
        ArrayList<NativeLib> mismatchedX86Libs = apk.getMismatchedX86Libs();
        String mismatchedX86LibsString = "";
        for (NativeLib l : mismatchedX86Libs) {
            mismatchedX86LibsString = mismatchedX86LibsString + Env.lineSeparator()
                    + l.shortPath();
        }
        libValues.add(mismatchedX86LibsString);
        // add column10:ȱ�ٵ�x64��  libMissed
        ArrayList<NativeLib> mismatchedX64Libs = apk.getMismatchedX64Libs();
        String mismatchedX64LibsString = "";
        for (NativeLib l : mismatchedX64Libs) {
            mismatchedX64LibsString = mismatchedX64LibsString + Env.lineSeparator()
                    + l.shortPath();
        }
        libValues.add(mismatchedX64LibsString);
	}
	
	/**
	 * column11-column14 Add Columns about Libmixed
	 * @param libValues
	 * @param apk
	 */
	private void addAboutLibMixed(ArrayList<Object> libValues, Apk apk) {
		 // add column11:X86 lib-mix:�Ƿ��д����so�������x86����  libMix
        libValues.add(apk.getMisusedX86Libs().size() > 0 ? "YES" : "NO");
        // add column12:X64 lib-mix:�Ƿ��д����so�������x64����  libMix
        libValues.add(apk.getMisusedX64Libs().size() > 0 ? "YES" : "NO");
        // add column13:Mixed X86 Libs
        ArrayList<NativeLib> misusedX86Libs = apk.getMisusedX86Libs();
        String misuedX86LibsString = "";
        for (NativeLib l : misusedX86Libs) {
            misuedX86LibsString = misuedX86LibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(misuedX86LibsString);
        // add column14:Mixed X64 Libs
        ArrayList<NativeLib> misusedX64Libs = apk.getMisusedX64Libs();
        String misuedX64LibsString = "";
        for (NativeLib l : misusedX64Libs) {
            misuedX64LibsString = misuedX64LibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(misuedX64LibsString);
	}
	
	/**
	 * column15-column20 Add Columns about LibsUnderAllFolder
	 * @param libValues
	 * @param apk
	 */
	private void addLibsUnderAllFolder(ArrayList<Object> libValues, Apk apk) {
		// add column15:ARM-V5 Folder Libs
        ArrayList<NativeLib> v5Libs = apk.getReportedArmV5Libs();
        String v5LibsString = "";
        for (NativeLib l : v5Libs) {
            v5LibsString = v5LibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(v5LibsString);
        // add column16:ARM-V7 Folder Libs
        ArrayList<NativeLib> v7Libs = apk.getReportedArmV7Libs();
        String v7LibsString = "";
        for (NativeLib l : v7Libs) {
            v7LibsString = v7LibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(v7LibsString);
        // add column17:ARM-V8 Folder Libs
        ArrayList<NativeLib> v8Libs = apk.getReportedArmV8Libs();
        String v8LibsString = "";
        for (NativeLib l : v8Libs) {
            v8LibsString = v8LibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(v8LibsString);
        // add column18:X86 Folder Libs
        ArrayList<NativeLib> x86Libs = apk.getReportedX86Libs();
        String x86LibsString = "";
        for (NativeLib l : x86Libs) {
            x86LibsString = x86LibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(x86LibsString);
        // add column19:X64 Folder Libs
        ArrayList<NativeLib> x64Libs = apk.getReportedX64Libs();
        String x64LibsString = "";
        for (NativeLib l : x64Libs) {
            x64LibsString = x64LibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(x64LibsString);
        // add column20:Assets Folder Libs
        ArrayList<NativeLib> assertLibs = apk.getAllAssertsLibs();
        String assertLibsString = "";
        for (NativeLib l : assertLibs) {
            assertLibsString = assertLibsString + Env.lineSeparator() + l.shortPath();
        }
        libValues.add(assertLibsString);
	}
	
	/**
	 * column21 Add Column about NestedApk
	 * @param libValues
	 * @param apk
	 */
	private void addNestedApk(ArrayList<Object> libValues, Apk apk) {
		ArrayList<String> subApks = apk.getSubApks();
        String subApksString = "";
        for (String sApk : subApks) {
            Log.v("sub apk: " + sApk);
            sApk = sApk.substring(sApk.indexOf("assets"));
            subApksString = subApksString + Env.lineSeparator() + sApk;
        }
        libValues.add(subApksString);
	}
	
	/**
	 * column22-column24 Add Columns about Protection
	 * @param libValues
	 * @param apk
	 */
	private void addAboutProtection(ArrayList<Object> libValues, Apk apk) {
		// add column22:Is Protected
        ArrayList<NativeLib> nativeLibsAll = apk.getAllAssertsLibs();
        nativeLibsAll.addAll(apk.getReportedArmV5Libs());
        nativeLibsAll.addAll(apk.getReportedArmV7Libs());
        nativeLibsAll.addAll(apk.getReportedX86Libs());
        final int result = appProtection.check(nativeLibsAll);
        boolean isProtected = false;
        if(result > 0) {
        	isProtected = true;
        } else{
        	isProtected = false;
        } 
        libValues.add(isProtected ? "YES" : "NO");
        // add column23:Protection Vendor
        String protectionVendor = "N/S";
        if(result > 0) {
        	protectionVendor = AppProtection.checkResultToString(result);
        } else {
        	protectionVendor = "N/S";
        }
        libValues.add(protectionVendor);
        // add column24:Related Libs
        String relatedLibs = "N/S";
        if(result > 0) {
        	switch (result) {
        	// Bangcle
			case AppProtection.RES_BANGCLE:
				String bangcle_so = "";
	            for (String so : AppProtection.BANGCLE_LIBS) {
	                bangcle_so = bangcle_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = bangcle_so;
				break;
			// Citrix
			case AppProtection.RES_CITRIX:
				String cirtix_so = "";
	            for (String so : AppProtection.CITRIX_LIBS) {
	                cirtix_so = cirtix_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = cirtix_so;
				break;
			// Ijiami
			case AppProtection.RES_IJIAMI:
				String ijiami_so = "";
	            for (String so : AppProtection.IJIAMI_LIBS) {
	                ijiami_so = ijiami_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = ijiami_so;
				break;
			// Nagain
			case AppProtection.RES_NAGAIN:
				String nagain_so = "";
	            for (String so : AppProtection.NAGAIN_LIBS) {
	                nagain_so = nagain_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = nagain_so;
				break;
			// Nqshield
			case AppProtection.RES_NQSHIELD:
				String nqsheild_so = "";
	            for (String so : AppProtection.NQSHIELD_LIBS) {
	                nqsheild_so = nqsheild_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = nqsheild_so;
				break;
			// Payegis
			case AppProtection.RES_PAYEGIS:
				String payegis_so = "";
	            for (String so : AppProtection.PAYEGIS_LIBS) {
	                payegis_so = payegis_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = payegis_so;
				break;
			// QIHOO
			case AppProtection.RES_QIHOO:
				String qihoo_so = "";
	            for (String so : AppProtection.QIHOO_LIBS) {
	                qihoo_so = qihoo_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = qihoo_so;
				break;
			// Tencent
			case AppProtection.RES_TENCENT:
				String tencent_so = "";
	            for (String so : AppProtection.TENCENT_LIBS) {
	                tencent_so = tencent_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = tencent_so;
				break;
			// VKEY
			case AppProtection.RES_VKEY:
				String vkey_so = "";
	            for (String so : AppProtection.VKEY_LIBS) {
	                vkey_so = vkey_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = vkey_so;
				break;
			// Wellbia
			case AppProtection.RES_WELLBIA:
				String wellbia_so = "";
	            for (String so : AppProtection.WELLBIA_LIBS) {
	                wellbia_so = wellbia_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = wellbia_so;
				break;
			// ALIBABA
			case AppProtection.RES_ALIBABA:
				String alibaba_so = "";
	            for (String so : AppProtection.ALIBABA_LIBS) {
	                alibaba_so = alibaba_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = alibaba_so;
				break;
			// Baidu
			case AppProtection.RES_BAIDU:
				String baidu_so = "";
	            for (String so : AppProtection.BAIDU_LIBS) {
	                baidu_so = baidu_so + Env.lineSeparator() + so + ";";
	            }
	            relatedLibs = baidu_so;
				break;
			default:
				break;
			}// end switch
        } else{
        	relatedLibs = "N/S";
        } // end result>0
        libValues.add(relatedLibs);
	}
	
	/**
	 * column25-column28 Add Columns about Payment
	 * @param libValues
	 * @param apk
	 */
	private void addAboutPay(ArrayList<Object> libValues, Apk apk) {
		// add column25:CMCC SDK App  �Ƿ����CMCC�Ŀ�  ���ƶ�֧����
        libValues.add(apk.isCmccApp() ? "YES" : "NO");
        // add column26:�Ƿ����ChinaUnicom�Ŀ�libme_unipay.so ����֧ͨ����
        libValues.add(apk.isChinaUnicom() ? "YES" : "NO");
        // add column27:�Ƿ����ChinaTelecom�Ŀ�[����֧��]
        libValues.add(apk.isChinaTelecom() ? "YES" : "NO");
        // add column28:�Ƿ����ChinaUnionPay�Ŀ�[����֧��]
        libValues.add(apk.isChinaUnionPay() ? "YES" : "NO");
	}
	
	/**
	 * column29:�Ƿ�����������޸�HotFix�Ŀ�
	 * @param libValues
	 * @param apk
	 */
	private void addAlihotFix(ArrayList<Object> libValues, Apk apk) {
		libValues.add(apk.isAlihotfixLib() ? "YES" : "NO");
	}
	
	/**
	 * column30:�Ƿ����FacebookRN�Ŀ�
	 * @param libValues
	 * @param apk
	 */
	private void addFacebookRN(ArrayList<Object> libValues, Apk apk) {
		libValues.add(apk.isFaceBookRNLib() ? "YES" : "NO");
	}
	
	/**
	 * column31:�Ƿ����Tencent Live Video�Ŀ�
	 * @param libValues
	 * @param apk
	 */
	private void addTencentLiveVideo(ArrayList<Object> libValues, Apk apk) {
		libValues.add(apk.isTencentLiveVideoLib() ? "YES" : "NO");
	}
	
	
	

}
