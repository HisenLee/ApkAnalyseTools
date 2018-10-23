
package libchecker.app.util.excel;

import libchecker.app.apk.AppInfoJuly15;
import libchecker.app.util.Env;

import java.util.ArrayList;

public class AppInfoExcelParserJuly15 {
    private String mExcelFile = null;
    private ExcelOperation mExcel = null;

    private ArrayList<AppInfoJuly15> mAppInfo = null;

    public static final int COLUMN_NO_APP_RANK = 0;
    public static final String COLUMN_NAME_APP_RANK = "app_ranking";

    public static final int COLUMN_NO_APP_NAME = 1;
    public static final String COLUMN_NAME_APP_NAME = "app_name";

    public static final int COLUMN_NO_APP_VERSION = 2;
    public static final String COLUMN_NAME_APP_VERSION = "app_version";

    public static final int COLUMN_NO_PACKAGE_NAME = 3;
    public static final String COLUMN_NAME_PACKAGE_NAME = "package_name";

    public static final int COLUMN_NO_MD5 = 4;
    public static final String COLUMN_NAME_MD5 = "md5";

    public static final int COLUMN_NO_NDK = 5;
    public static final String COLUMN_NAME_NDK = "NDK";

    public static final int COLUMN_NO_APK_NAME = 6;
    public static final String COLUMN_NAME_APK_NAME = "apk_name";

    public static final int COLUMN_NO_MAIN_ACTIVITY = 7;
    public static final String COLUMN_NAME_MAIN_ACTIVITY = "main_activity";

    public static final int COLUMN_NO_DOWNLOADS = 8;
    public static final String COLUMN_NAME_DOWNLOADS = "downloads";

    public static final int COLUMN_NO_SIZE = 9;
    public static final String COLUMN_NAME_SIZE = "size";

    public static final int COLUMN_NO_CATEGORY = 10;
    public static final String COLUMN_NAME_CATEGORY = "category";

    // an arbitrary number
    public static final int COLUMN_NO_ID = 11;
    public static final String COLUMN_NAME_ID = "id";

    public static final int COLUMN_NO_SCORE = 12;
    public static final String COLUMN_NAME_SCORE = "score";

    public static final int COLUMN_NO_WEIGHT = 13;
    public static final String COLUMN_NAME_WEIGHT = "weight";

    public static final int COLUMN_NO_BAIDU_RANKING = 14;
    public static final String COLUMN_NAME_BAIDU_RANKING = "baidu_ranking";

    public static final int COLUMN_NO_SANLIULING_RANKING = 15;
    public static final String COLUMN_NAME_SANLIULING_RANKING = "360_ranking";
    
    public static final int COLUMN_NO_YINGYONGBAO_RANKING = 16;
    public static final String COLUMN_NAME_YINGYONGBAO_RANKING = "yingyongbao_ranking";

    public static final int COLUMN_NO_WDJ_RANKING = 17;
    public static final String COLUMN_NAME_WDJ_RANKING = "wandoujia_ranking";

    public static final int COLUMN_NO_ANZHI_RANKING = 18;
    public static final String COLUMN_NAME_ANZHI_RANKING = "anzhi_ranking";
    
    public static final int COLUMN_NO_BAIDU_DOWNLOADS = 19;
    public static final String COLUMN_NAME_BAIDU_DOWNLOADS = "baidu_downloads";

    public static final int COLUMN_NO_SANLIULING_DOWNLOADS = 20;
    public static final String COLUMN_NAME_SANLIULING_DOWNLOADS = "360_downloads";
    
    public static final int COLUMN_NO_YINGYONGBAO_DOWNLOADS = 21;
    public static final String COLUMN_NAME_YINGYONGBAO_DOWNLOADS = "yingyongbao_downloads";

    public static final int COLUMN_NO_WDJ_DOWNLOADS = 22;
    public static final String COLUMN_NAME_WDJ_DOWNLOADS = "wandoujia_downloads";
    
    public static final int COLUMN_NO_ANZHI_DOWNLOADS = 23;
    public static final String COLUMN_NAME_ANZHI_DOWNLOADS = "anzhi_downloads";

    public static final int COLUMN_NO_TOTAL_DOWNLOADS = 24;
    public static final String COLUMN_NAME_TOTAL_DOWNLOADS = "total_downloads";

    public AppInfoExcelParserJuly15(String downloadAppExcel) {
        mExcelFile = downloadAppExcel;
        mExcel = new ExcelOperation(mExcelFile);
        mAppInfo = new ArrayList<AppInfoJuly15>();
        int rows = mExcel.getNumberOfRows();
        if (Env.NUMBER_APPS > 0) {
            rows = Env.NUMBER_APPS + 1;
        }
        for (int r = 1; r < rows; r++) {
            AppInfoJuly15 l = new AppInfoJuly15();
            l.app_rank = Integer.parseInt((String)(mExcel.readItem(r, COLUMN_NO_APP_RANK).content));
            l.app_name = (String) (mExcel.readItem(r, COLUMN_NO_APP_NAME).content);
            l.app_version = (String) (mExcel.readItem(r, COLUMN_NO_APP_VERSION).content);
            l.package_name = (String) (mExcel.readItem(r, COLUMN_NO_PACKAGE_NAME).content);
            l.md5 = (String) (mExcel.readItem(r, COLUMN_NO_MD5).content);
            l.ndk = (String) (mExcel.readItem(r, COLUMN_NO_NDK).content);
            l.apk_name = (String) (mExcel.readItem(r, COLUMN_NO_APK_NAME).content);
            l.main_activity = (String) (mExcel.readItem(r, COLUMN_NO_MAIN_ACTIVITY).content);
            l.downloads = (String) (mExcel.readItem(r, COLUMN_NO_DOWNLOADS).content);
            l.size = (String) (mExcel.readItem(r, COLUMN_NO_SIZE).content);
            l.category = (String) (mExcel.readItem(r, COLUMN_NO_CATEGORY).content);
            l.id = (String) (mExcel.readItem(r, COLUMN_NO_ID).content);
            l.score = (String) (mExcel.readItem(r, COLUMN_NO_SCORE).content);
            l.weight = (String) (mExcel.readItem(r, COLUMN_NO_WEIGHT).content);
            l.baidu_ranking = (String) (mExcel.readItem(r, COLUMN_NO_BAIDU_RANKING).content);
            l.sanliuling_ranking = (String) (mExcel.readItem(r, COLUMN_NO_SANLIULING_RANKING).content);
            l.yingyongbao_ranking = (String) (mExcel.readItem(r, COLUMN_NO_YINGYONGBAO_RANKING).content);
            l.wandoujia_ranking = (String) (mExcel.readItem(r, COLUMN_NO_WDJ_RANKING).content);
            l.anzhi_ranking = (String) (mExcel.readItem(r, COLUMN_NO_ANZHI_RANKING).content);
            l.baidu_downloads = (String) (mExcel.readItem(r, COLUMN_NO_BAIDU_DOWNLOADS).content);
            l.sanliuling_downloads = (String) (mExcel.readItem(r, COLUMN_NO_SANLIULING_DOWNLOADS).content);
            l.yingyongbao_downloads = (String) (mExcel.readItem(r, COLUMN_NO_YINGYONGBAO_DOWNLOADS).content);
            l.wandoujia_downloads = (String) (mExcel.readItem(r, COLUMN_NO_WDJ_DOWNLOADS).content);
            l.anzhi_downloads = (String) (mExcel.readItem(r, COLUMN_NO_ANZHI_DOWNLOADS).content);
            l.total_downloads = (String) (mExcel.readItem(r, COLUMN_NO_TOTAL_DOWNLOADS).content);
            mAppInfo.add(l);
        }
    }

    public ArrayList<AppInfoJuly15> getAppInfo() {
        return mAppInfo;
    }
}
