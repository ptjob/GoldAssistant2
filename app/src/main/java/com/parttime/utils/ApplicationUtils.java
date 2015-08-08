package com.parttime.utils;

import com.parttime.constants.SharedPreferenceConstants;
import com.quark.jianzhidaren.ApplicationControl;

/**
 * 应用帮助类
 * Created by wyw on 2015/7/25.
 */
public class ApplicationUtils {
    public static String DEF_CITY = "深圳";
    private static final String DEF_CITY_VERSION = "0";

    public static String getLoginName() {
        return SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).loadStringSharedPreference(SharedPreferenceConstants.COMPANY_NAME);
    }

    public static int getLoginId() {
        return Integer.parseInt(SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).loadStringSharedPreference(SharedPreferenceConstants.COMPANY_ID));
    }

    public static String getCity() {
        String initCity = SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).loadStringSharedPreference(SharedPreferenceConstants.INIT_CITY, DEF_CITY);
        return SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).loadStringSharedPreference(SharedPreferenceConstants.CITY, initCity);
    }

    public static String getCityVersion() {
        return SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).loadStringSharedPreference(SharedPreferenceConstants.CITY_DATABASE_VARSION, "");
    }

    public static boolean allowCompanyShare() {
        return SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).loadBooleanSharedPreference(SharedPreferenceConstants.ALLOW_COMPANY_SHARE);
    }

    public static String getCompanyShareUrl(int companyId) {
        String url = SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).loadStringSharedPreference(SharedPreferenceConstants.COMPANY_SHARE_URL, "");
        if (!CheckUtils.isEmpty(url)) {
            url += companyId;
        }
        return url;
    }
}
