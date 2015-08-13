package com.parttime.constants;

import android.text.TextUtils;

import com.carson.constant.ConstantForSaveList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parttime.utils.SharePreferenceUtil;

import java.util.HashSet;

/**
 *
 * Created by luhua on 2015/8/13.
 */
public class ApplicationInitCache {

    /**
     * 初始化应用缓存数据
     * @param sp SharePreferenceUtil
     * @param gson Gson
     */
    public static void initData(SharePreferenceUtil sp, Gson gson) {
        //初始化免扰管理
        if(ConstantForSaveList.disturbCache == null || ConstantForSaveList.disturbCache.size() == 0){
            String disturbStr = sp.loadStringSharedPreference(SharedPreferenceConstants.DISTURB_CONFIGGURE);
            if(!TextUtils.isEmpty(disturbStr)){
                HashSet<String> data = gson.fromJson(disturbStr, new TypeToken<HashSet<String>>(){}.getType());
                if(data != null && data.size() > 0){
                    ConstantForSaveList.disturbCache.addAll(data);
                }
            }
        }
        //初始化禁言管理
        if(ConstantForSaveList.gagCache == null || ConstantForSaveList.gagCache.size() == 0){
            String disturbStr = sp.loadStringSharedPreference(SharedPreferenceConstants.GAG_CONFIGGURE);
            if(!TextUtils.isEmpty(disturbStr)){
                HashSet<String> data = gson.fromJson(disturbStr, new TypeToken<HashSet<String>>(){}.getType());
                if(data != null && data.size() > 0){
                    ConstantForSaveList.gagCache.addAll(data);
                }
            }
        }
    }

}
