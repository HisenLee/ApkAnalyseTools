
package libchecker.app.apk.nativelib;

import java.util.ArrayList;

import libchecker.app.apk.Apk;
import libchecker.app.apk.AppInfo;
import libchecker.app.util.Env;
import libchecker.app.util.excel.ExcelOperation;

public class AppProtection {
    // Bangcle
    // Citrix
    // Ijiami
    // Nagain
    // Nqshield
    // Payegis
    // Qihoo
    // Tencent
    // V-Key
    // Wellbia
	
	// CMCC --CMCC不包括在加固中

    // Bangcle
    public final static String BANGCLE = "Bangcle";
    public final static int RES_BANGCLE = 1;
    public final static String[] BANGCLE_LIBS = {
            "libsecexe.so", "libsecexe.x86.so", "libsecmain.so", "libsecmain.x86.so",
            "libDexHelper.so", "libDexHelper-x86.so", "libsecpreload.so",
            "libsecpreload.x86.so"
    };
    private ArrayList<AppInfo> BANGCLE_LIST = new ArrayList<AppInfo>();

    // Citrix
    public final static String CITRIX = "Citrix XenMobile";
    public final static int RES_CITRIX = 2;
    public final static String[] CITRIX_LIBS = {
            "libCtxTFE.so"
    };
    private ArrayList<AppInfo> CITRIX_LIST = new ArrayList<AppInfo>();

    // Ijiami
    public final static String IJIAMI = "Ijiami";
    public final static int RES_IJIAMI = 4;
    public final static String[] IJIAMI_LIBS = {
            "libexecmain.so", "libexec.so"
    };
    private ArrayList<AppInfo> IJIAMI_LIST = new ArrayList<AppInfo>();

    // Nagain
    public final static String NAGAIN = "Nagain";
    public final static int RES_NAGAIN = 5;
    public final static String[] NAGAIN_LIBS = {
            "libchaosvmp.so", "libfdog.so", "libddog.so", "libhdog.so"
    };
    private ArrayList<AppInfo> NAGAIN_LIST = new ArrayList<AppInfo>();

    // Nqshield
    public final static String NQSHIELD = "Nqshield";
    public final static int RES_NQSHIELD = 6;
    public final static String[] NQSHIELD_LIBS = {
            "libnqshield.so", "libnqshieldx86.so"
    };
    private ArrayList<AppInfo> NQSHIELD_LIST = new ArrayList<AppInfo>();

    // Payegis
    public final static String PAYEGIS = "Payegis";
    public final static int RES_PAYEGIS = 7;
    public final static String[] PAYEGIS_LIBS = {
            "libegis.so", "libegis-x86.so", "libegis_security.so"
    };
    private ArrayList<AppInfo> PAYEGIS_LIST = new ArrayList<AppInfo>();

    // Qihoo
    public final static String QIHOO = "Qihoo 360";
    public final static int RES_QIHOO = 8;
    public final static String[] QIHOO_LIBS = {
            "libdejavu-1.0.1.so", "libprotectClass.so", "libprotectClass_x86.so",
            "libjiagu_art.so", "libjiagu.so"
    };
    private ArrayList<AppInfo> QIHOO_LIST = new ArrayList<AppInfo>();

    // Tencent
    public final static String TENCENT = "Tencent";
    public final static int RES_TENCENT = 9;
    public final static String[] TENCENT_LIBS = {
            "libshell.so", "libmain.so"
    };
    private ArrayList<AppInfo> TENCENT_LIST = new ArrayList<AppInfo>();

    // V-Key
    public final static String VKEY = "V-Key";
    public final static int RES_VKEY = 10;
    public final static String[] VKEY_LIBS = {
            "libVosWrapper.so"
    };
    private ArrayList<AppInfo> VKEY_LIST = new ArrayList<AppInfo>();

    // Wellbia
    public final static String WELLBIA = "Wellbia";
    public final static int RES_WELLBIA = 11;
    public final static String[] WELLBIA_LIBS = {
            "libxigncode.so", "", "libgabriel.so", "libpickle.so"
    };
    private ArrayList<AppInfo> WELLBIA_LIST = new ArrayList<AppInfo>();

    // Alibaba
    public final static String ALIBABA = "Alibaba";
    public final static int RES_ALIBABA = 12;
    public final static String[] ALIBABA_LIBS = {
            "libmobisec.so", "", "libmobisecx.so", "libmobisecy1.so", "libmobisecz1.so"
    };

    private ArrayList<AppInfo> ALIBABA_LIST = new ArrayList<AppInfo>();

    // Baidu
    public final static String BAIDU = "Baidu";
    public final static int RES_BAIDU = 13;
    public final static String[] BAIDU_LIBS = {
            "libbaiduprotect.so", "libbaiduprotect_x86.so"
    };
    private ArrayList<AppInfo> BAIDU_LIST = new ArrayList<AppInfo>();

    public static String checkResultToString(int result) {
        switch (result) {
            case RES_BANGCLE:
                return BANGCLE;
            case RES_CITRIX:
                return CITRIX;
            case RES_IJIAMI:
                return IJIAMI;
            case RES_NAGAIN:
                return NAGAIN;
            case RES_NQSHIELD:
                return NQSHIELD;
            case RES_PAYEGIS:
                return PAYEGIS;
            case RES_QIHOO:
                return QIHOO;
            case RES_TENCENT:
                return TENCENT;
            case RES_VKEY:
                return VKEY;
            case RES_WELLBIA:
                return WELLBIA;
            case RES_ALIBABA:
                return ALIBABA;
            case RES_BAIDU:
                return BAIDU;
            default:
                return "Other_App_Protection_Vendors";
        }
    }

    public static boolean isAppProtectionLib(NativeLib lib) {
        final String libName = lib.getName();
        // Bangcle
        for (String b : BANGCLE_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // Citrix
        for (String b : CITRIX_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // CMCC --
        // ---
        // Ijiami
        for (String b : IJIAMI_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // Nagain
        for (String b : NAGAIN_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // Nqshield
        for (String b : NQSHIELD_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // Payegis
        for (String b : PAYEGIS_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // Qihoo
        for (String b : QIHOO_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // Tencent
        for (String b : TENCENT_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // V-Key
        for (String b : VKEY_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        // Wellbia
        for (String b : WELLBIA_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }

        // Alibaba
        for (String b : ALIBABA_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }

        // Baidu
        for (String b : BAIDU_LIBS) {
            if (b.equals(libName)) {
                return true;
            }
        }
        return false;
    }

    public int standAloneCheck(AppInfo app, Apk apk) {
        ArrayList<NativeLib> nativeLibsAll = apk.getAllAssertsLibs();
        nativeLibsAll.addAll(apk.getReportedArmV5Libs());
        nativeLibsAll.addAll(apk.getReportedArmV7Libs());
        nativeLibsAll.addAll(apk.getReportedX86Libs());
        return check(app, nativeLibsAll, false);
    }

    public int check(AppInfo app, ArrayList<NativeLib> nativeLibsAll, boolean addToList) {

        // Bangcle
        int bangcle_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : BANGCLE_LIBS) {
                if (b.equals(l.getName())) {
                    bangcle_index++;
                    if (bangcle_index >= 2) {
                        if (addToList) {
                            BANGCLE_LIST.add(app);
                        }
                        return RES_BANGCLE;
                    }
                }
            }
        }

        // Citrix
        for (NativeLib l : nativeLibsAll) {
            for (String b : CITRIX_LIBS) {
                if (b.equals(l.getName())) {
                    if (addToList) {
                        CITRIX_LIST.add(app);
                    }
                    return RES_CITRIX;
                }
            }
        }

        // Ijiami
        int ijiami_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : IJIAMI_LIBS) {
                if (b.equals(l.getName())) {
                    ijiami_index++;
                    if (ijiami_index >= 2) {
                        if (addToList) {
                            IJIAMI_LIST.add(app);
                        }
                        return RES_IJIAMI;
                    }
                }
            }
        }

        // Nagain
        for (NativeLib l : nativeLibsAll) {
            if (l.getName().equals(NAGAIN_LIBS[0])) {
                if (addToList) {
                    NAGAIN_LIST.add(app);
                }
                return RES_NAGAIN;
            }
        }
        boolean nagain_1 = false;
        boolean nagain_2 = false;
        for (NativeLib l : nativeLibsAll) {
            if (l.getName().equals(NAGAIN_LIBS[1])) {
                nagain_1 = true;
            } else if (l.getName().equals(NAGAIN_LIBS[2])) {
                nagain_2 = true;
            }
        }
        if (nagain_1 && nagain_2) {
            if (addToList) {
                NAGAIN_LIST.add(app);
            }
            return RES_NAGAIN;
        }

        // Nqshield
        for (NativeLib l : nativeLibsAll) {
            for (String b : NQSHIELD_LIBS) {
                if (b.equals(l.getName())) {
                    if (addToList) {
                        NQSHIELD_LIST.add(app);
                    }
                    return RES_NQSHIELD;
                }
            }
        }

        // Payegis
        for (NativeLib l : nativeLibsAll) {
            for (String b : PAYEGIS_LIBS) {
                if (b.equals(l.getName())) {
                    if (addToList) {
                        PAYEGIS_LIST.add(app);
                    }
                    return RES_PAYEGIS;
                }
            }
        }

        // Qihoo
        for (NativeLib l : nativeLibsAll) {
            for (String b : QIHOO_LIBS) {
                if (b.equals(l.getName())) {
                    if (addToList) {
                        QIHOO_LIST.add(app);
                    }
                    return RES_QIHOO;
                }
            }
        }

        // Tencent
        boolean tencent0 = false;
        boolean tencent1 = false;
        for (NativeLib l : nativeLibsAll) {
            if (l.getName().equals(TENCENT_LIBS[0])) {
                tencent0 = true;
            } else if (l.getName().equals(TENCENT_LIBS[1])) {
                tencent1 = true;
            }
            if (tencent0 && tencent1) {
                if (addToList) {
                    TENCENT_LIST.add(app);
                }
                return RES_TENCENT;
            }
        }

        // V-Key
        for (NativeLib l : nativeLibsAll) {
            for (String b : VKEY_LIBS) {
                if (b.equals(l.getName())) {
                    if (addToList) {
                        VKEY_LIST.add(app);
                    }
                    return RES_VKEY;
                }
            }
        }

        // Wellbia
        int wellbia_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : WELLBIA_LIBS) {
                if (b.equals(l.getName())) {
                    wellbia_index++;
                    if (wellbia_index >= 2) {
                        if (addToList) {
                            WELLBIA_LIST.add(app);
                        }
                        return RES_WELLBIA;
                    }
                }
            }
        }

        // Alibaba
        int alibaba_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : ALIBABA_LIBS) {
                if (b.equals(l.getName())) {
                    alibaba_index++;
                    if (alibaba_index >= 2) {
                        if (addToList) {
                            ALIBABA_LIST.add(app);
                        }
                        return RES_ALIBABA;
                    }
                }
            }
        }

        // Baidu
        for (NativeLib l : nativeLibsAll) {
            for (String b : BAIDU_LIBS) {
                if (b.equals(l.getName())) {
                    if (addToList) {
                        BAIDU_LIST.add(app);
                    }
                    return RES_BAIDU;
                }
            }
        }

        return -1;
    }
    
    public int check(ArrayList<NativeLib> nativeLibsAll) {

        // Bangcle
        int bangcle_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : BANGCLE_LIBS) {
                if (b.equals(l.getName())) {
                    bangcle_index++;
                    if (bangcle_index >= 2) {
//                        if (addToList) {
//                            BANGCLE_LIST.add(app);
//                        }
                        return RES_BANGCLE;
                    }
                }
            }
        }

        // Citrix
        for (NativeLib l : nativeLibsAll) {
            for (String b : CITRIX_LIBS) {
                if (b.equals(l.getName())) {
//                    if (addToList) {
//                        CITRIX_LIST.add(app);
//                    }
                    return RES_CITRIX;
                }
            }
        }

        // Ijiami
        int ijiami_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : IJIAMI_LIBS) {
                if (b.equals(l.getName())) {
                    ijiami_index++;
                    if (ijiami_index >= 2) {
//                        if (addToList) {
//                            IJIAMI_LIST.add(app);
//                        }
                        return RES_IJIAMI;
                    }
                }
            }
        }

        // Nagain
        for (NativeLib l : nativeLibsAll) {
            if (l.getName().equals(NAGAIN_LIBS[0])) {
//                if (addToList) {
//                    NAGAIN_LIST.add(app);
//                }
                return RES_NAGAIN;
            }
        }
        boolean nagain_1 = false;
        boolean nagain_2 = false;
        for (NativeLib l : nativeLibsAll) {
            if (l.getName().equals(NAGAIN_LIBS[1])) {
                nagain_1 = true;
            } else if (l.getName().equals(NAGAIN_LIBS[2])) {
                nagain_2 = true;
            }
        }
        if (nagain_1 && nagain_2) {
//            if (addToList) {
//                NAGAIN_LIST.add(app);
//            }
            return RES_NAGAIN;
        }

        // Nqshield
        for (NativeLib l : nativeLibsAll) {
            for (String b : NQSHIELD_LIBS) {
                if (b.equals(l.getName())) {
//                    if (addToList) {
//                        NQSHIELD_LIST.add(app);
//                    }
                    return RES_NQSHIELD;
                }
            }
        }

        // Payegis
        for (NativeLib l : nativeLibsAll) {
            for (String b : PAYEGIS_LIBS) {
                if (b.equals(l.getName())) {
//                    if (addToList) {
//                        PAYEGIS_LIST.add(app);
//                    }
                    return RES_PAYEGIS;
                }
            }
        }

        // Qihoo
        for (NativeLib l : nativeLibsAll) {
            for (String b : QIHOO_LIBS) {
                if (b.equals(l.getName())) {
//                    if (addToList) {
//                        QIHOO_LIST.add(app);
//                    }
                    return RES_QIHOO;
                }
            }
        }

        // Tencent
        boolean tencent0 = false;
        boolean tencent1 = false;
        for (NativeLib l : nativeLibsAll) {
            if (l.getName().equals(TENCENT_LIBS[0])) {
                tencent0 = true;
            } else if (l.getName().equals(TENCENT_LIBS[1])) {
                tencent1 = true;
            }
            if (tencent0 && tencent1) {
//                if (addToList) {
//                    TENCENT_LIST.add(app);
//                }
                return RES_TENCENT;
            }
        }

        // V-Key
        for (NativeLib l : nativeLibsAll) {
            for (String b : VKEY_LIBS) {
                if (b.equals(l.getName())) {
//                    if (addToList) {
//                        VKEY_LIST.add(app);
//                    }
                    return RES_VKEY;
                }
            }
        }

        // Wellbia
        int wellbia_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : WELLBIA_LIBS) {
                if (b.equals(l.getName())) {
                    wellbia_index++;
                    if (wellbia_index >= 2) {
//                        if (addToList) {
//                            WELLBIA_LIST.add(app);
//                        }
                        return RES_WELLBIA;
                    }
                }
            }
        }

        // Alibaba
        int alibaba_index = 0;
        for (NativeLib l : nativeLibsAll) {
            for (String b : ALIBABA_LIBS) {
                if (b.equals(l.getName())) {
                    alibaba_index++;
                    if (alibaba_index >= 2) {
//                        if (addToList) {
//                            ALIBABA_LIST.add(app);
//                        }
                        return RES_ALIBABA;
                    }
                }
            }
        }

        // Baidu
        for (NativeLib l : nativeLibsAll) {
            for (String b : BAIDU_LIBS) {
                if (b.equals(l.getName())) {
//                    if (addToList) {
//                        BAIDU_LIST.add(app);
//                    }
                    return RES_BAIDU;
                }
            }
        }

        return -1;
    }

    public void dump(ExcelOperation excel) {
        // write column names
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("Vendor");
        columnNames.add("App Number");
        columnNames.add("App List");
        columnNames.add("Related Libs");
        excel.writeRow(0, columnNames);

        int row = 1;

        // Bangcle
        if (BANGCLE_LIST.size() > 0) {
            ArrayList<Object> bangcle = new ArrayList<Object>();
            bangcle.add(0, BANGCLE);
            bangcle.add(BANGCLE_LIST.size());

            String bangcle_apps = "";
            for (AppInfo info : BANGCLE_LIST) {
                bangcle_apps = bangcle_apps + Env.lineSeparator() + info.apk_name;
            }
            bangcle.add(bangcle_apps);

            String bangcle_so = "";
            for (String so : BANGCLE_LIBS) {
                bangcle_so = bangcle_so + Env.lineSeparator() + so;
            }
            bangcle.add(bangcle_so);
            excel.writeRow(row++, bangcle);
        }

        // Citrix
        if (CITRIX_LIST.size() > 0) {
            ArrayList<Object> cirtix = new ArrayList<Object>();
            cirtix.add(0, CITRIX);
            cirtix.add(CITRIX_LIST.size());

            String cirtix_apps = "";
            for (AppInfo info : CITRIX_LIST) {
                cirtix_apps = cirtix_apps + Env.lineSeparator() + info.apk_name;
            }
            cirtix.add(cirtix_apps);

            String cirtix_so = "";
            for (String so : CITRIX_LIBS) {
                cirtix_so = cirtix_so + Env.lineSeparator() + so;
            }
            cirtix.add(cirtix_so);
            excel.writeRow(row++, cirtix);
        }

        // Ijiami
        if (IJIAMI_LIST.size() > 0) {
            ArrayList<Object> ijiami = new ArrayList<Object>();
            ijiami.add(0, IJIAMI);
            ijiami.add(IJIAMI_LIST.size());

            String ijiami_apps = "";
            for (AppInfo info : IJIAMI_LIST) {
                ijiami_apps = ijiami_apps + Env.lineSeparator() + info.apk_name;
            }
            ijiami.add(ijiami_apps);

            String ijiami_so = "";
            for (String so : IJIAMI_LIBS) {
                ijiami_so = ijiami_so + Env.lineSeparator() + so;
            }
            ijiami.add(ijiami_so);
            excel.writeRow(row++, ijiami);
        }

        // Nagain
        if (NAGAIN_LIST.size() > 0) {
            ArrayList<Object> nagain = new ArrayList<Object>();
            nagain.add(0, NAGAIN);
            nagain.add(NAGAIN_LIST.size());

            String nagain_apps = "";
            for (AppInfo info : NAGAIN_LIST) {
                nagain_apps = nagain_apps + Env.lineSeparator() + info.apk_name;
            }
            nagain.add(nagain_apps);

            String nagain_so = "";
            for (String so : NAGAIN_LIBS) {
                nagain_so = nagain_so + Env.lineSeparator() + so;
            }
            nagain.add(nagain_so);
            excel.writeRow(row++, nagain);
        }

        // Nqshield
        if (NQSHIELD_LIST.size() > 0) {
            ArrayList<Object> nqsheild = new ArrayList<Object>();
            nqsheild.add(0, NQSHIELD);
            nqsheild.add(NQSHIELD_LIST.size());
            String nqsheild_apps = "";
            for (AppInfo info : NQSHIELD_LIST) {
                nqsheild_apps = nqsheild_apps + Env.lineSeparator() + info.apk_name;
            }
            nqsheild.add(nqsheild_apps);

            String nqsheild_so = "";
            for (String so : NQSHIELD_LIBS) {
                nqsheild_so = nqsheild_so + Env.lineSeparator() + so;
            }
            nqsheild.add(nqsheild_so);
            excel.writeRow(row++, nqsheild);
        }

        // Payegis
        if (PAYEGIS_LIST.size() > 0) {
            ArrayList<Object> payegis = new ArrayList<Object>();
            payegis.add(0, PAYEGIS);
            payegis.add(PAYEGIS_LIST.size());
            String payegis_apps = "";
            for (AppInfo info : PAYEGIS_LIST) {
                payegis_apps = payegis_apps + Env.lineSeparator() + info.apk_name;
            }
            payegis.add(payegis_apps);

            String payegis_so = "";
            for (String so : PAYEGIS_LIBS) {
                payegis_so = payegis_so + Env.lineSeparator() + so;
            }
            payegis.add(payegis_so);
            excel.writeRow(row++, payegis);
        }

        // Qihoo
        if (QIHOO_LIST.size() > 0) {
            ArrayList<Object> qihoo = new ArrayList<Object>();
            qihoo.add(0, QIHOO);
            qihoo.add(QIHOO_LIST.size());
            String qihoo_apps = "";
            for (AppInfo info : QIHOO_LIST) {
                qihoo_apps = qihoo_apps + Env.lineSeparator() + info.apk_name;
            }
            qihoo.add(qihoo_apps);

            String qihoo_so = "";
            for (String so : QIHOO_LIBS) {
                qihoo_so = qihoo_so + Env.lineSeparator() + so;
            }
            qihoo.add(qihoo_so);
            excel.writeRow(row++, qihoo);
        }

        // Tencent
        if (TENCENT_LIST.size() > 0) {
            ArrayList<Object> tencent = new ArrayList<Object>();
            tencent.add(0, TENCENT);
            tencent.add(TENCENT_LIST.size());
            String tencent_apps = "";
            for (AppInfo info : TENCENT_LIST) {
                tencent_apps = tencent_apps + Env.lineSeparator() + info.apk_name;
            }
            tencent.add(tencent_apps);

            String tencent_so = "";
            for (String so : TENCENT_LIBS) {
                tencent_so = tencent_so + Env.lineSeparator() + so;
            }
            tencent.add(tencent_so);
            excel.writeRow(row++, tencent);
        }

        // V-Key
        if (VKEY_LIST.size() > 0) {
            ArrayList<Object> vkey = new ArrayList<Object>();
            vkey.add(0, VKEY);
            vkey.add(VKEY_LIST.size());
            String vkey_apps = "";
            for (AppInfo info : VKEY_LIST) {
                vkey_apps = vkey_apps + Env.lineSeparator() + info.apk_name;
            }
            vkey.add(vkey_apps);

            String vkey_so = "";
            for (String so : VKEY_LIBS) {
                vkey_so = vkey_so + Env.lineSeparator() + so;
            }
            vkey.add(vkey_so);
            excel.writeRow(row++, vkey);
        }

        // Wellbia
        if (WELLBIA_LIST.size() > 0) {
            ArrayList<Object> wellbia = new ArrayList<Object>();
            wellbia.add(0, WELLBIA);
            wellbia.add(WELLBIA_LIST.size());
            String wellbia_apps = "";
            for (AppInfo info : WELLBIA_LIST) {
                wellbia_apps = wellbia_apps + Env.lineSeparator() + info.apk_name;
            }
            wellbia.add(wellbia_apps);

            String wellbia_so = "";
            for (String so : WELLBIA_LIBS) {
                wellbia_so = wellbia_so + Env.lineSeparator() + so;
            }
            wellbia.add(wellbia_so);
            excel.writeRow(row++, wellbia);
        }

        // Alibaba
        if (ALIBABA_LIST.size() > 0) {
            ArrayList<Object> alibaba = new ArrayList<Object>();
            alibaba.add(0, ALIBABA);
            alibaba.add(ALIBABA_LIST.size());
            String alibaba_apps = "";
            for (AppInfo info : ALIBABA_LIST) {
                alibaba_apps = alibaba_apps + Env.lineSeparator() + info.apk_name;
            }
            alibaba.add(alibaba_apps);

            String alibaba_so = "";
            for (String so : ALIBABA_LIBS) {
                alibaba_so = alibaba_so + Env.lineSeparator() + so;
            }
            alibaba.add(alibaba_so);
            excel.writeRow(row++, alibaba);
        }

        // Baidu
        if (BAIDU_LIST.size() > 0) {
            ArrayList<Object> baidu = new ArrayList<Object>();
            baidu.add(0, BAIDU);
            baidu.add(BAIDU_LIST.size());
            String baidu_apps = "";
            for (AppInfo info : BAIDU_LIST) {
                baidu_apps = baidu_apps + Env.lineSeparator() + info.apk_name;
            }
            baidu.add(baidu_apps);

            String baidu_so = "";
            for (String so : BAIDU_LIBS) {
                baidu_so = baidu_so + Env.lineSeparator() + so;
            }
            baidu.add(baidu_so);
            excel.writeRow(row++, baidu);
        }

        // total
        ArrayList<Object> total = new ArrayList<Object>();
        total.add("Total");
        total.add(BANGCLE_LIST.size() + CITRIX_LIST.size()
                + IJIAMI_LIST.size() + NAGAIN_LIST.size() + NQSHIELD_LIST.size()
                + PAYEGIS_LIST.size() + QIHOO_LIST.size() + TENCENT_LIST.size()
                + VKEY_LIST.size() + WELLBIA_LIST.size() + ALIBABA_LIST.size() + BAIDU_LIST.size());
        excel.writeRow(row++, total);
    }
}
