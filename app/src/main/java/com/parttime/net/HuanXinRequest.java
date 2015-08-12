package com.parttime.net;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.parttime.constants.ApplicationConstants;
import com.parttime.pojo.CompanyDetailVO;
import com.quark.common.JsonUtil;
import com.quark.common.Url;
import com.quark.model.HuanxinUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by luhua on 15/7/16.
 */
public class HuanXinRequest extends BaseRequest{

    /**
     * 批量获取环信用户姓名头像
     * @param reqData String 请求参数
     * @param queue RequestQueue 请求队列
     * @param callback 回调
     *                 success回调：obj 是 ArrayList<HuanxinUser>
     *                 failed 回调：obj 是 ResponseBaseCommonError or JSONException or VolleyError
     */
    public void getHuanxinUserList(final String reqData, RequestQueue queue , final DefaultCallback callback){

        Map<String, String> map = new HashMap<>();
        map.put("user_ids", reqData);

        request(Url.HUANXIN_avatars_pic,map, queue, new Callback() {
            @Override
            public void success(Object obj) {
                JSONObject js = null ;
                if(obj instanceof JSONObject){
                    js = (JSONObject)obj;
                }
                if(js == null){
                    callback.failed("");
                    return ;
                }
                try {
                    ArrayList<HuanxinUser> usersNick = new ArrayList<>();
                    JSONArray jss = js.getJSONArray("userList");
                    for (int i = 0; i < jss.length(); i++) {
                        HuanxinUser us = (HuanxinUser) JsonUtil
                                .jsonToBean(jss.getJSONObject(i),
                                        HuanxinUser.class);
                        if (us.getName() == null
                                || us.getName().equals("")) {
                            us.setName("未知好友");
                        }else {
                            usersNick.add(us);
                        }
                    }

                    callback.success(usersNick);
                } catch (JSONException e) {
                    callback.failed(e);
                }
            }

            @Override
            public void failed(Object obj) {
                callback.failed(obj);
            }
        });

    }

    /**
     * 获取环信用户姓名头像
     * @param reqData String 请求参数
     * @param queue RequestQueue 请求队列
     * @param callback 回调
     *                 success回调：obj 是 ArrayList<HuanxinUser>
     *                 failed 回调：obj 是 ResponseBaseCommonError or JSONException or VolleyError
     */
    public void getHuanxinUserDetailList(final String reqData, RequestQueue queue , final DefaultCallback callback){

        Map<String, String> map = new HashMap<>();
        map.put("id", reqData);
        request(Url.HUANXIN_user_info,map, queue, new Callback() {
            @Override
            public void success(Object obj) {
                JSONObject js = null ;
                if(obj instanceof JSONObject){
                    js = (JSONObject)obj;
                }
                if(js == null){
                    callback.failed("");
                    return ;
                }
                try {
                    ArrayList<HuanxinUser> usersNick = new ArrayList<>();
                    JSONObject jss = js.getJSONObject("userInfo");
                    HuanxinUser us = (HuanxinUser) JsonUtil
                            .jsonToBean(jss,
                                    HuanxinUser.class);
                    if (us.getName() == null
                            || us.getName().equals("")) {
                        us.setName("未知好友");
                    }else {
                        usersNick.add(us);
                    }
                    callback.success(usersNick);
                } catch (JSONException e) {
                    callback.failed(e);
                }
            }

            @Override
            public void failed(Object obj) {
                callback.failed(obj);
            }
        });

    }

    /**
     * 获取环信点击商户头像
     * @param reqData String 请求参数
     * @param queue RequestQueue 请求队列
     * @param callback 回调
     *                 success回调：obj 是 ArrayList<HuanxinUser>
     *                 failed 回调：obj 是 ResponseBaseCommonError or JSONException or VolleyError
     */
    public void getHuanxinCompanyInfoDetailList(final String reqData, RequestQueue queue , final DefaultCallback callback){

        Map<String, String> map = new HashMap<>();
        map.put("id", reqData);
        request(Url.HUANXIN_company_info,map, queue, new Callback() {
            @Override
            public void success(Object obj) {
                JSONObject js = null ;
                if(obj instanceof JSONObject){
                    js = (JSONObject)obj;
                }
                if(js == null){
                    callback.failed("");
                    return ;
                }
                try {
                    ArrayList<HuanxinUser> usersNick = new ArrayList<>();
                    JSONObject jss = js.getJSONObject("companyInfo");
                    String ci = jss.toString();
                    CompanyDetailVO cdVO =  new Gson().fromJson(ci, CompanyDetailVO.class);

                    HuanxinUser huanxinUser = null;
                    if(cdVO != null){
                        huanxinUser = new HuanxinUser();
                        huanxinUser.setUid(ApplicationConstants.SPECIAL_USER_PREFIX_CHAR + cdVO.id);
                        huanxinUser.sex = cdVO.sex;
                        huanxinUser.setName(cdVO.name);
                        huanxinUser.setAvatar(cdVO.avatar);
                        huanxinUser.setType(cdVO.type);
                        huanxinUser.introduction = cdVO.introduction;
                    }else{
                        huanxinUser = new HuanxinUser();
                    }
                    if (huanxinUser.name == null
                            || huanxinUser.name.equals("")) {
                        huanxinUser.name = "未知好友";
                    }else {
                        usersNick.add(huanxinUser);
                    }
                    callback.success(usersNick);
                } catch (JSONException e) {
                    callback.failed(e);
                }
            }

            @Override
            public void failed(Object obj) {
                callback.failed(obj);
            }
        });

    }


}
