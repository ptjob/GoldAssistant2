package com.parttime.constants;

import com.parttime.login.FindPJLoginActivity;
import com.quark.jianzhidaren.ApplicationControl;

/**
 * SharedPreference 文件key常量
 * Created by wyw on 2015/7/15.
 */
public class SharedPreferenceConstants {
    public static final String CITY = "city";
    public static final String DINGWEICITY = "dingweicity";
    public static final String USER_ID = "user_id";
    public static final String INIT_CITY = "init_city";

    public static final String USER_TYPE = "user_type";
    public static final String COMPANY_ID = "userId";
    public static final String COMPANY_NAME = "IM_NIKENAME";
    public static final String REMEMBERED_TEL = FindPJLoginActivity.EXTRA_REMEMBER_TELE;
    public static final String REMEMBERED_PWD = FindPJLoginActivity.EXTRA_REMEMBER_PWD;
    public static final String CITY_DATABASE_VARSION = "city_database_version";
    public static final String ALLOW_COMPANY_SHARE = "allow_company_share";
    public static final String COMPANY_SHARE_URL = "company_share_url";
    public static final String FIRST_LOCATION = "firstdingwei";

    //群通知
    public static final String GROUP_NOTICE_CONFIGGURE = "group_notice_configure" + ApplicationControl.getInstance().getUserName();

    //免扰设置
    public static final String DISTURB_CONFIGGURE = "disturb_configure" + ApplicationControl.getInstance().getUserName();

    //禁言设置
    public static final String GAG_CONFIGGURE = "gag_configure" + ApplicationControl.getInstance().getUserName();

    public static final String BANNERS_INFO = "banners_info";
}
