
package libchecker.app.apk;

import libchecker.app.util.Env;
import libchecker.app.util.file.FileOperation;

public class AppInfoJuly15 {
    public int app_rank = -1;
    public String app_name = "";
    public String app_version = "";
    public String package_name = "";
    public String md5 = "";
    public String ndk = "";
    public String apk_name = "";
    public String main_activity = "";
    public String downloads = "";
    public String size = "";
    public String category = "";
    public String id = "";
    public String score = "";
    public String weight = "";
    public String baidu_ranking = "";
    public String sanliuling_ranking = "";
    public String yingyongbao_ranking = "";
    public String wandoujia_ranking = "";
    public String anzhi_ranking = "";
    public String baidu_downloads = "";
    public String sanliuling_downloads = "";
    public String yingyongbao_downloads = "";
    public String wandoujia_downloads = "";
    public String anzhi_downloads = "";
    public String total_downloads = "";

    public AppInfoJuly15(){
        
    }
    
    public AppInfoJuly15(String apkFile) {
        app_rank = -1;
        final String apk = FileOperation.shortName(apkFile); 
        //Log.out(Log.D, "apk: "+apk);
        app_name = apk.substring(0, apk.lastIndexOf('.'));
        //Log.out(Log.D, "app_name: "+app_name);
        app_version = "";
        package_name = "";
        md5 = "";
        ndk = "";
        apk_name = apk.substring(0, apk.lastIndexOf('.'));
        main_activity = "";
        downloads = "";
        size = "";
        category = "";
        id = "";
        score = "";
        weight = "";
        baidu_ranking = "";
        sanliuling_ranking = "";
        yingyongbao_ranking = "";
        wandoujia_ranking = "";
        anzhi_ranking = "";
        baidu_downloads = "";
        sanliuling_downloads = "";
        yingyongbao_downloads = "";
        wandoujia_downloads = "";
        anzhi_downloads = "";
        total_downloads = "";
    }

    @Override
    public String toString() {
        return "app_rank:" + app_rank + Env.lineSeparator()
                + "app_name:" + app_name + Env.lineSeparator()
                + "app_version:" + app_version + Env.lineSeparator()
                + "package_name:" + package_name + Env.lineSeparator()
                + "md5:" + md5 + Env.lineSeparator()
                + "ndk:" + ndk + Env.lineSeparator()
                + "apk_name:" + apk_name + Env.lineSeparator()
                + "main_activity:" + main_activity + Env.lineSeparator()
                + "downloads:" + downloads + Env.lineSeparator()
                + "size:" + size + Env.lineSeparator()
                + "category:" + category + Env.lineSeparator()
                + "id:" + id + Env.lineSeparator()
                + "score:" + score + Env.lineSeparator()
                + "weight:" + weight + Env.lineSeparator()
                + "baidu_ranking:" + baidu_ranking + Env.lineSeparator()
                + "sanliuling_ranking:" + sanliuling_ranking + Env.lineSeparator()
                + "yingyongbao_ranking:" + yingyongbao_ranking + Env.lineSeparator()
                + "wandoujia_ranking:" + wandoujia_ranking + Env.lineSeparator()
                + "anzhi_ranking:" + anzhi_ranking + Env.lineSeparator()
                + "baidu_downloads:" + baidu_downloads + Env.lineSeparator()
                + "sanliuling_downloads:" + sanliuling_downloads + Env.lineSeparator()
                + "yingyongbao_downloads:" + yingyongbao_downloads + Env.lineSeparator()
                + "wandoujia_downloads:" + wandoujia_downloads + Env.lineSeparator()
                + "anzhi_downloads:" + anzhi_downloads + Env.lineSeparator()
                + "total_downloads:" + total_downloads + Env.lineSeparator();
    }
    
    public static AppInfoJuly15 convertFromAppInfo(AppInfo info){
        AppInfoJuly15 july15 = new AppInfoJuly15();
        july15.anzhi_downloads = info.anzhi_downloads;
        july15.anzhi_ranking = info.anzhi_ranking;
        july15.apk_name = info.apk_name;
        july15.app_name = info.app_name;
        july15.app_rank = info.app_rank;
        july15.app_version = info.app_version;
        july15.baidu_downloads = info.baidu_downloads;
        july15.baidu_ranking = info.baidu_ranking;
        july15.category = info.category;
        july15.downloads = info.downloads;
        july15.id = info.id;
        july15.main_activity = info.main_activity;
        july15.md5 = info.md5;
        july15.ndk = info.ndk;
        july15.package_name = info.package_name;
        july15.sanliuling_downloads = info.sanliuling_downloads;
        july15.sanliuling_ranking = info.sanliuling_ranking;
        july15.score = info.score;
        july15.size = info.size;
        july15.total_downloads = info.total_downloads;
        july15.wandoujia_downloads = "TBD";
        july15.wandoujia_ranking = "TBD";
        july15.weight = info.weight;
        july15.yingyongbao_downloads = info.yingyongbao_downloads;
        july15.yingyongbao_ranking = info.yingyongbao_ranking;
        return july15;
    }
}
