package com.seagazer.ui.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * 屏幕适配器，提供适配屏幕分辩率功能
 * 通常用于BaseActivity统一设置，需在super.onCreate(savedInstanceState)前进行调用
 */
public class ScreenAdapter {
    private static final int DEFAULT_WIDTH_DP = 960;// the default width dp is 1920*1080 for TV
    private static float sDensity;
    private static float sScaleDensity;

    /**
     * 适配屏幕
     *
     * @param activity    当前activity
     * @param application 当前application
     */
    public static void adjustDensity(Activity activity, Application application) {
        adjustDensity(activity, application, 0);
    }

    /**
     * 适配屏幕
     *
     * @param activity    当前activity
     * @param application 当前application
     * @param widthDp     当前设计稿的横向宽度dp
     */
    public static void adjustDensity(Activity activity, final Application application, int widthDp) {
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (widthDp <= 0) {
            widthDp = DEFAULT_WIDTH_DP;
        }
        if (sDensity == 0) {
            sDensity = appDisplayMetrics.density;
            sScaleDensity = appDisplayMetrics.scaledDensity;// default is the same as density unless with user custom setting
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration configuration) {
                    if (configuration != null && configuration.fontScale > 0) {
                        sScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        final float targetDensity = appDisplayMetrics.widthPixels * 1.0f / widthDp;
        final float targetScaleDensity = targetDensity * (sScaleDensity / sDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaleDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaleDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

}
