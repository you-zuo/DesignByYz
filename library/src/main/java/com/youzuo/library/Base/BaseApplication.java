package com.youzuo.library.Base;

import android.app.Application;
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        MobSDK.init(this);
//
//        CrashReport.initCrashReport(getApplicationContext(), "5de1522f82", true);
//        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
//        // 调试时，将第三个参数改为true
//        Bugly.init(this, "8a742d8701", false);
    }
    //开发者模式
    public static boolean isDebug = true;
    public static String APP_NAME = "test";
}
