
package libchecker.app.lhx;

import libchecker.app.apk.Apk;
import libchecker.app.apk.AppInfo;
import libchecker.app.apk.nativelib.AppProtection;
import libchecker.app.apk.nativelib.NativeLib;
import libchecker.app.util.Env;
import libchecker.app.util.Log;
import libchecker.app.util.excel.ExcelOperation;
import libchecker.app.util.excel.ThirdPartyLibExcelParser.ThirdPartyLibInfo;
import libchecker.app.util.file.FileOperation;
import libchecker.app.util.file.FileType;

import java.util.ArrayList;

public class ApkLibCheck {

    public static void generate(String apkFileDir, ArrayList<AppInfo> appInfos,
            ArrayList<ThirdPartyLibInfo> libInfos) {
    	// ����AppProtection����
    	AppProtection appProtection = new AppProtection();
    	
        boolean standAloneMode = false;
        if (appInfos == null) {
            standAloneMode = true;
            Log.i("standAloneMode: " + standAloneMode);

            ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir,
                    FileType.TYPE_APK);
            if (standAloneMode) {
                appInfos = new ArrayList<AppInfo>();
                for (String apkFile : apkFiles) {
                    AppInfo info = new AppInfo(apkFile);
                    // Log.out(Log.D, apkFile);
                    appInfos.add(info);
                }
            }
        }

        ArrayList<String> apkFiles = FileOperation.listAll(apkFileDir, FileType.TYPE_APK);

        ExcelOperation excel = new ExcelOperation(
                FileOperation.generateResultXlsName("./", ApkLibCheck.class
                        .getSimpleName()));

        int row = 0;
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Rank");// column1:����
        columnNames.add("App Name");// column2:AppName
        columnNames.add("Apk Version");// column3:ApkVersion
        columnNames.add("Package Name");// column4:PkgName
        columnNames.add("NDK Type");// column5:NDKType
        
        columnNames.add("X86 Lib Missed"); // column6:�Ƿ�ȱ��x86��  libMissed
        columnNames.add("X64 Lib Missed"); // column7:�Ƿ�ȱ��x64��  libMissed
        columnNames.add("Assets miss x86 lib"); // column8:assets���Ƿ�ȱ��x86�� libMissed
        columnNames.add("Assets miss x64 lib"); // column9:assets���Ƿ�ȱ��x64�� libMissed
        columnNames.add("Missed X86 Libs"); // column10:ȱ�ٵ�x86��  libMissed
        columnNames.add("Missed X64 Libs"); // column11:ȱ�ٵ�x64��  libMissed
        columnNames.add("Missed Libs(3rd Party)"); // column12:ȱ�ٵĵ�������  
        columnNames.add("Missed Libs(ISV)");// column13:ȱ�ٵ�ISV��
        
        columnNames.add("X86 lib-mix");  // column14:�Ƿ��д����so�������x86����  libMix
        columnNames.add("X64 lib-mix");  // column15:�Ƿ��д����so�������x64����  libMix
        columnNames.add("Mixed X86 Libs"); // column16:x86�·��ű�Ŀ�  libMix�����libs
        columnNames.add("Mixed X64 Libs"); // column17:x64�·��ű�Ŀ�   libMix�����libs
        
        columnNames.add("ARM-V5 Folder Libs"); // column18: armv5�µ�libs
        columnNames.add("ARM-V7 Folder Libs"); // column19: armv7�µ�libs
        columnNames.add("ARM-V8 Folder Libs"); // column20: armv8�µ�libs
        columnNames.add("X86 Folder Libs"); // column21: x86�µ�libs
        columnNames.add("X64 Folder Libs"); // column22: x64�µ�libs
        columnNames.add("Assets Folder Libs"); // column23: assets�µ�libs
        
        columnNames.add("Nested Apks");// column24: Ƕ�׵�apk�ļ�
        
        columnNames.add("All libs 3rd Party");// column25:�Ƿ��ǵ������� ��
        
        columnNames.add("ARM64 lib-mix"); // column26:�Ƿ��д����so�������arm64�� libMix
        columnNames.add("ARM 32 lib Missed");// column27:�Ƿ�ȱ��arm 32��  libMissed
        columnNames.add("ARM 64 lib Missed");// column28:�Ƿ�ȱ��arm 64��  libMissed
        
        columnNames.add("CMCC SDK App");// column29:�Ƿ����CMCC�Ŀ�
        columnNames.add("ChinaUnicom SDK App");// column30:�Ƿ����ChinaUnicom�Ŀ�libme_unipay.so 
        
        columnNames.add("Known Issue on IA32");  // column31:�Ƿ������е�Intel 32�ܹ�����֪����������
        columnNames.add("Known Issue on IA64");  // column32:�Ƿ������е�Intel 64�ܹ�����֪����������
        
        columnNames.add("Is Protected"); // column33:�Ƿ�ӹ�
        columnNames.add("Protection Vendor"); // column34:�ӹ̵ĳ���
        columnNames.add("Related Libs"); // column35:�ӹ���ص�so�ļ�

        excel.writeRow(row++, columnNames);
        int current = 0;
        int total = appInfos.size();
        AppProtection appPro = new AppProtection();
        // go through each apk file
        for (AppInfo appInfo : appInfos) {
//            final String apkName = appInfo.apk_name + FileType.TYPE_APK;
        	// ��Ҫ�޸�Ϊ�ļ�����apk��·����ʽ
            final String apkName = appInfo.apk_name + "_" + appInfo.package_name +"_" + FileType.TYPE_APK;
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
            // parse the apk file
            Log.i(++current + " of " + total + ", processing apk: "
                    + FileOperation.shortName(apkFile));
            Apk apk = new Apk(apkFile);

            ArrayList<Object> libValues = new ArrayList<Object>();

            // column1:���� columnNames.add("Ranking");  ���˴���Ҫ��˳�����������current���ɣ�libValues.add(current)
            libValues.add(appInfo.app_rank);  
            // column2:AppName
            libValues.add(appInfo.app_name);
            // column3:ApkVersion
            libValues.add(appInfo.app_version);
            // column4:PkgName
            libValues.add(appInfo.package_name);
            // column5:NDKType
            libValues.add(apk.getApkType());
            
            // column6:�Ƿ�ȱ��x86��  libMissed
            libValues.add(apk.isX86LibMismatchApp() ? "YES" : "NO");
            // column7:�Ƿ�ȱ��x64��  libMissed
            libValues.add(apk.isX64LibMismatchApp() ? "YES" : "NO");
            // column8:assets���Ƿ�ȱ��x86�� libMissed
            libValues.add(apk.assetsLibMissingX86Version() ? "YES" : "NO");
            // column9:assets���Ƿ�ȱ��x64�� libMissed
            libValues.add(apk.assetsLibMissingX64Version() ? "YES" : "NO");
            // column10:ȱ�ٵ�x86��  libMissed
            ArrayList<NativeLib> mismatchedX86Libs = apk.getMismatchedX86Libs();
            String mismatchedX86LibsString = "";
            for (NativeLib l : mismatchedX86Libs) {
                mismatchedX86LibsString = mismatchedX86LibsString + Env.lineSeparator()
                        + l.shortPath();
            }
            libValues.add(mismatchedX86LibsString);
            // column11:ȱ�ٵ�x64��  libMissed
            ArrayList<NativeLib> mismatchedX64Libs = apk.getMismatchedX64Libs();
            String mismatchedX64LibsString = "";
            for (NativeLib l : mismatchedX64Libs) {
                mismatchedX64LibsString = mismatchedX64LibsString + Env.lineSeparator()
                        + l.shortPath();
            }
            libValues.add(mismatchedX64LibsString);
            ArrayList<NativeLib> mismatchedLibs = new ArrayList<NativeLib>();
            mismatchedLibs.addAll(mismatchedX86Libs);
            mismatchedLibs.addAll(mismatchedX64Libs);
            String mismatchedLibsIsvString = "";
            String mismatchedLibsThirdPartyString = "";
            for (NativeLib l : mismatchedLibs) {
                boolean find = false;
                for (ThirdPartyLibInfo libInfo : libInfos) {
                    if (libInfo.getName().equals(l.getName())) {
                        find = true;
                        break;
                    }
                }
                if (find) {
                    mismatchedLibsThirdPartyString = mismatchedLibsThirdPartyString
                            + Env.lineSeparator() + l.shortPath();
                } else {
                    mismatchedLibsIsvString = mismatchedLibsIsvString + Env.lineSeparator()
                            + l.shortPath();
                }
            }
            // column12:ȱ�ٵĵ�������  
            libValues.add(mismatchedLibsThirdPartyString);
            // column13:ȱ�ٵ�ISV��  
            libValues.add(mismatchedLibsIsvString);
            
            // column14:�Ƿ��д����so�������x86����  libMix
            libValues.add(apk.getMisusedX86Libs().size() > 0 ? "YES" : "NO");
            // column15:�Ƿ��д����so�������x64����  libMix
            libValues.add(apk.getMisusedX64Libs().size() > 0 ? "YES" : "NO");
            // column16:x86�·��ű�Ŀ�  libMix�����libs (lib/x86/*.so not x86)
            ArrayList<NativeLib> misusedX86Libs = apk.getMisusedX86Libs();
            String misuedX86LibsString = "";
            for (NativeLib l : misusedX86Libs) {
                misuedX86LibsString = misuedX86LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(misuedX86LibsString);
            // column17:x64�·��ű�Ŀ�  libMix�����libs (lib/x64/*.so not x64)
            ArrayList<NativeLib> misusedX64Libs = apk.getMisusedX64Libs();
            String misuedX64LibsString = "";
            for (NativeLib l : misusedX64Libs) {
                misuedX64LibsString = misuedX64LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(misuedX64LibsString);
            
            // column18: armv5�µ�libs
            ArrayList<NativeLib> v5Libs = apk.getReportedArmV5Libs();
            String v5LibsString = "";
            for (NativeLib l : v5Libs) {
                v5LibsString = v5LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(v5LibsString);
            // column19: armv7�µ�libs
            ArrayList<NativeLib> v7Libs = apk.getReportedArmV7Libs();
            String v7LibsString = "";
            for (NativeLib l : v7Libs) {
                v7LibsString = v7LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(v7LibsString);
            // column20: armv8�µ�libs
            ArrayList<NativeLib> v8Libs = apk.getReportedArmV8Libs();
            String v8LibsString = "";
            for (NativeLib l : v8Libs) {
                v8LibsString = v8LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(v8LibsString);
            // column21: x86�µ�libs
            ArrayList<NativeLib> x86Libs = apk.getReportedX86Libs();
            String x86LibsString = "";
            for (NativeLib l : x86Libs) {
                x86LibsString = x86LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(x86LibsString);
            // column22: x64�µ�libs
            ArrayList<NativeLib> x64Libs = apk.getReportedX64Libs();
            String x64LibsString = "";
            for (NativeLib l : x64Libs) {
                x64LibsString = x64LibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(x64LibsString);
            // column23: assets�µ�libs
            ArrayList<NativeLib> assertLibs = apk.getAllAssertsLibs();
            String assertLibsString = "";
            for (NativeLib l : assertLibs) {
                assertLibsString = assertLibsString + Env.lineSeparator() + l.shortPath();
            }
            libValues.add(assertLibsString);
            
            // column24: Ƕ�׵�apk�ļ�
            ArrayList<String> subApks = apk.getSubApks();
            String subApksString = "";
            for (String sApk : subApks) {
                Log.v("sub apk: " + sApk);
                sApk = sApk.substring(sApk.indexOf("assets"));
                subApksString = subApksString + Env.lineSeparator() + sApk;
            }
            libValues.add(subApksString);
            
            //  column25:�Ƿ��ǵ������� ��
            ArrayList<NativeLib> v7 = apk.getReportedArmV7Libs();
            ArrayList<NativeLib> v5 = apk.getReportedArmV5Libs();
            if(v7.size() > 0){
                boolean all3rdparty = true;
                for(NativeLib l : v7){
                    boolean found = false;
                    for(ThirdPartyLibInfo info: libInfos){
                        if(info.getName().equals(l.getName())){
                            found = true;
                        }
                    }
                    if(!found){
                        all3rdparty = false;
                        break;
                    }
                }
                if(all3rdparty){
                    libValues.add("YES");
                }else{
                    libValues.add("NO");
                }
            }else if(v5.size() > 0){
                boolean all3rdparty = true;
                for(NativeLib l : v5){
                    boolean found = false;
                    for(ThirdPartyLibInfo info: libInfos){
                        if(info.getName().equals(l.getName())){
                            found = true;
                        }
                    }
                    if(!found){
                        all3rdparty = false;
                        break;
                    }
                }
                if(all3rdparty){
                    libValues.add("YES");
                }else{
                    libValues.add("NO");
                }
            }else{
                libValues.add("NO");
            }
            
            // column26:�Ƿ��д����so�������arm64�� libMix
            libValues.add(apk.getMisusedArmV8Libs().size() > 0 ? "YES" : "NO");
            // column27:�Ƿ�ȱ��arm 32��  libMissed
            libValues.add(apk.hasArm32LibPackagingIssue() ? "YES" : "NO");
            // column28:�Ƿ�ȱ��arm 64��  libMissed
            libValues.add(apk.hasArm64LibPackagingIssue() ? "YES" : "NO");
            
            // column29:�Ƿ����CMCC�Ŀ�
            libValues.add(apk.isCmccApp() ? "YES" : "NO");
            
            // column30:�Ƿ����ChinaUnicom�Ŀ�libme_unipay.so 
            libValues.add(apk.isChinaUnicom() ? "YES" : "NO");
            
            // column31:�Ƿ������е�Intel 32�ܹ�����֪����������
            boolean knownIssue = false;
            knownIssue = knownIssue || appPro.standAloneCheck(appInfo, apk) > 0;
            knownIssue = knownIssue || apk.isCmccApp();
            if (apk.getApkType().equals(Apk.TYPE_ARM32_NDK)) {
                knownIssue = knownIssue || apk.hasArm32LibPackagingIssue();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X64_NDK)) {
                knownIssue = knownIssue || apk.hasArm32LibPackagingIssue();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_NDK)) {
                knownIssue = knownIssue || true;
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X64_NDK)) {
                knownIssue = knownIssue || true;
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_JAVA)) {
                knownIssue = knownIssue || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_X64_NDK)) {
                knownIssue = knownIssue || true;
            } else if (apk.getApkType().equals(Apk.TYPE_X86_NDK)) {
                knownIssue = knownIssue || apk.assetsLibMissingX86Version()
                        || apk.isX86LibMisuseApp();
            }
            libValues.add(knownIssue ? "YES" : "NO");
            // column32:�Ƿ������е�Intel 64�ܹ�����֪����������
            knownIssue = false;
            knownIssue = knownIssue || appPro.standAloneCheck(appInfo, apk) > 0;
            knownIssue = knownIssue || apk.isCmccApp();
            if (apk.getApkType().equals(Apk.TYPE_ARM32_NDK)) {
                knownIssue = knownIssue || apk.hasArm32LibPackagingIssue();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X64_NDK)) {
                knownIssue = knownIssue || apk.isX64LibMismatchApp() || apk.isX64LibMisuseApp()
                        || apk.assetsLibMissingX64Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM32_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_NDK)) {
                knownIssue = knownIssue || apk.hasArm64LibPackagingIssue()
                        || apk.assetsLibMissingX64Version() || apk.isArmV8LibMisuseApp();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X64_NDK)) {
                knownIssue = knownIssue || apk.isX64LibMismatchApp() || apk.isX64LibMisuseApp()
                        || apk.assetsLibMissingX64Version();
            } else if (apk.getApkType().equals(Apk.TYPE_ARM64_X86_NDK)) {
                knownIssue = knownIssue || apk.isX86LibMismatchApp() || apk.isX86LibMisuseApp()
                        || apk.assetsLibMissingX86Version();
            } else if (apk.getApkType().equals(Apk.TYPE_JAVA)) {
                knownIssue = knownIssue || apk.assetsLibMissingX64Version();
            } else if (apk.getApkType().equals(Apk.TYPE_X64_NDK)) {
                knownIssue = knownIssue || apk.assetsLibMissingX64Version()
                        || apk.isX64LibMisuseApp();
            } else if (apk.getApkType().equals(Apk.TYPE_X86_NDK)) {
                knownIssue = knownIssue || apk.assetsLibMissingX86Version()
                        || apk.isX86LibMisuseApp();
            }
            libValues.add(knownIssue ? "YES" : "NO");
            
            // �жϼӹ����⣺
            ArrayList<NativeLib> nativeLibsAll = apk.getAllAssertsLibs();
            nativeLibsAll.addAll(apk.getReportedArmV5Libs());
            nativeLibsAll.addAll(apk.getReportedArmV7Libs());
            nativeLibsAll.addAll(apk.getReportedX86Libs());
            final int result = appProtection.check(appInfo, nativeLibsAll, true);
            // column33:�Ƿ�ӹ�
            boolean isProtected = false;
            if(result > 0) {
            	isProtected = true;
            } else{
            	isProtected = false;
            } 
            libValues.add(isProtected ? "YES" : "NO");
            // column34:�ӹ̵ĳ���
            String protectionVendor = "N/S";
            if(result > 0) {
            	protectionVendor = AppProtection.checkResultToString(result);
            } else {
            	protectionVendor = "N/S";
            }
            libValues.add(protectionVendor);
            //column35:�ӹ���ص�so�ļ�
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
           
            excel.writeRow(row++, libValues);
            apk.destroy();
        }
    }
}
