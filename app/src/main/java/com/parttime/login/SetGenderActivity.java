package com.parttime.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.carson.constant.ConstantForSaveList;
import com.droid.carson.CityActivity;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parttime.base.CitySelectActivity;
import com.parttime.base.IntentManager;
import com.parttime.base.WithTitleActivity;
import com.parttime.constants.ApplicationConstants;
import com.parttime.constants.ApplicationInitCache;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.main.MainTabActivity;
import com.parttime.net.BaseRequest;
import com.parttime.net.Callback;
import com.parttime.net.ErrorHandler;
import com.parttime.net.ResponseBaseCommonError;
import com.parttime.utils.SharePreferenceUtil;
import com.qingmu.jianzhidaren.BuildConfig;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.volley.VolleySington;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjz on 2015/7/29.
 */
public class SetGenderActivity extends WithTitleActivity{
    public static final String EXTRA_TELEPHONE = "extra_telephone";
    public static  final String EXTRA_CODE = "extra_code";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_PWD_ENCODED = "extra_pwd_encoded";
    public static final String EXTRA_PWD = "extra_pwd";

    private static final int REQUEST_CODE_LOCATION = 10001;

    public static String PWD_ENCODED;
    public static String PWD;
    public static String TEL;

    private  String telephone;
    private String code;
    private String name;
    private String pwdEncoded;

    private String gender;
    private boolean lock;

    @ViewInject(R.id.fl_male)
    private View viewMale;
    @ViewInject(R.id.fl_female)
    private View viewFemale;

    private boolean enable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set_gender);
        ViewUtils.inject(this);
        super.onCreate(savedInstanceState);
        getIntentData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        enable = true;
    }

    private void getIntentData(){
        Intent intent = getIntent();
        TEL = telephone = intent.getStringExtra(EXTRA_TELEPHONE);
        code = intent.getStringExtra(EXTRA_CODE);
        name = intent.getStringExtra(EXTRA_NAME);
        PWD_ENCODED = pwdEncoded = intent.getStringExtra(EXTRA_PWD_ENCODED);
        PWD = intent.getStringExtra(EXTRA_PWD);
    }

    @Override
    protected void initViews() {
        super.initViews();
        center(R.string.set_gender);

    }

    @Override
    public void onBackPressed() {
        if(!lock) {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.fl_male)
    public void male(View v){
//        showToast("male");
//        startActivity(new Intent(this, Activity01.class));
        if(!enable){
            return;
        }
        enable = false;
        gender = "1";
        register();
    }

    @OnClick(R.id.fl_female)
    public void female(View v){
//        showToast("female");
//        startActivity(new Intent(this, Activity01.class));
        if(!enable){
            return;
        }
        enable = false;
        gender = "0";
        register();
    }

    private void register(){
        /*showWait(true);
        Map<String, String> params = new HashMap<>();
        params.put("telephone", telephone);
        params.put("name", name);
        params.put("password", pwdEncoded);
        params.put("code", code);
        params.put("city", "深圳");
        params.put("sex", gender);
        new BaseRequest().request(Url.COMPANY_REGISTER, params, VolleySington.getInstance().getRequestQueue(), new Callback() {
            @Override
            public void success(Object obj) throws JSONException {
                showWait(false);
                IntentManager.intentToLoginActivity(SetGenderActivity.this);
            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                IntentManager.intentToLoginActivity(SetGenderActivity.this);
            }
        });*/
        Intent intent = new Intent();
        intent.setClass(this, CitySelectActivity.class);
//        startActivityForResult(intent, REQUEST_CODE_LOCATION);
        intent.putExtra(CitySelectActivity.EXTRA_DIY_ACTION, new SelectCityAction());
        intent.putExtra(CitySelectActivity.EXTRA_ACITON_EXTRA, new RegParams(telephone, code, name, pwdEncoded, gender));
        startActivity(intent);
        lock = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_LOCATION:
                    String string = data.getExtras().getString(CityActivity.EXTRA_CITY);
                    showToast(string);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected ViewGroup getLeftWrapper() {
        return null;
    }

    @Override
    protected ViewGroup getRightWrapper() {
        return null;
    }

    @Override
    protected TextView getCenter() {
        return null;
    }

    public static class RegParams implements Serializable {
        public  String telephone;
        public String code;
        public String name;
        public String pwdEncoded;

        public String gender;

        public RegParams(String telephone, String code, String name, String pwdEncoded, String gender) {
            this.telephone = telephone;
            this.code = code;
            this.name = name;
            this.pwdEncoded = pwdEncoded;
            this.gender = gender;
        }
    }

    public static class SelectCityAction implements CityActivity.DiyAction {


        private String token;
        private String user_id;
        private String loginUrl;// 登陆url
        private String IM_PASSWORD;// 环信登陆密码
        private String IM_USERID;// 环信登陆账户
        private String IM_AVATAR;// 环信头像
        private String IM_NIKENAME;// 环信昵称
        // 环信
        private boolean progressShow;

        @Override
        public void clicked(int index, String city, Serializable extra, final BaseActivity activity) {
            RegParams regParams = (RegParams) extra;
            activity.showWait(true);
            SharePreferenceUtil.getInstance(activity).saveSharedPreferences(SharedPreferenceConstants.INIT_CITY, city);
            Map<String, String> params = new HashMap<>();
            params.put("telephone", regParams.telephone);
            params.put("name", regParams.name);
            params.put("password", regParams.pwdEncoded);
            params.put("code", regParams.code);
            params.put("city", city);
            params.put("sex", regParams.gender);
            new BaseRequest().request(Url.COMPANY_REGISTER, params, VolleySington.getInstance().getRequestQueue(), new Callback() {
                @Override
                public void success(Object obj) throws JSONException {
                    activity.showWait(false);
                    /*IntentManager.intentToLoginActivity(activity);
                    activity.finish();*/
                    login(activity);
                }

                @Override
                public void failed(Object obj) {
                    activity.showWait(false);
                    new ErrorHandler(activity, obj).showToast();
//                    IntentManager.intentToLoginActivity(activity);
                }
            });

        }

        public void login(final BaseActivity activity) {
            activity.showWait(true);
            StringRequest request = new StringRequest(Request.Method.POST,
                    Url.COMPANY_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.showWait(false);
                    try {
                        JSONObject json = new JSONObject(response);
                        int status = json.getInt("status");
                        if (status == 1) {
                            // 记录用户id 环信登陆id 密码 昵称 头像
                            JSONObject jsonts = json
                                    .getJSONObject("loginResponse");
                            token = jsonts.getString("token");
                            user_id = jsonts.getString("company_id");
                            IM_PASSWORD = jsonts.getString("IM_PASSWORD");
                            IM_USERID = jsonts.getString("IM_USERID");
                            IM_AVATAR = jsonts.getString("IM_AVATAR");
                            IM_NIKENAME = jsonts.getString("IM_NIKENAME");
                            int type = jsonts.getInt("type");
                            ApplicationConstants.IM_NIKENAME = IM_NIKENAME;

                            loginIM(activity, IM_USERID, IM_PASSWORD);

                        } else {
                            ResponseBaseCommonError error = new Gson().fromJson(response, ResponseBaseCommonError.class);
                            if(error != null) {
                                activity.showToast(error.msg);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    activity.showWait(false);
                    activity.showToast(activity.getResources().getString(
                            R.string.regist_request_server_fail));
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("telephone", TEL);
                    map.put("password", PWD_ENCODED);
                    return map;
                }
            };
            RequestQueue queue = VolleySington.getInstance().getRequestQueue();
            queue.add(request);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    ConstantForSaveList.DEFAULTRETRYTIME * 1000, 1, 1.0f));
        }

        private void loginSuccess2Umeng(final BaseActivity activity, final long start) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    long costTime = System.currentTimeMillis() - start;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("status", "success");
                    MobclickAgent.onEventValue(activity, "login1",
                            params, (int) costTime);
                    MobclickAgent.onEventDuration(activity, "login1",
                            (int) costTime);
                }
            });
        }

        protected void setUserHearder(String username,
                                      com.easemob.chatuidemo.domain.User user) {
            String headerName = null;
            if (!TextUtils.isEmpty(user.getNick())) {
                headerName = user.getNick();
            } else {
                headerName = user.getUsername();
            }
            if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
                user.setHeader("");
            } else if (Character.isDigit(headerName.charAt(0))) {
                user.setHeader("#");
            } else {
                user.setHeader(HanziToPinyin.getInstance()
                        .get(headerName.substring(0, 1)).get(0).target.substring(0,
                                1).toUpperCase());
                char header = user.getHeader().toLowerCase().charAt(0);
                if (header < 'a' || header > 'z') {
                    user.setHeader("#");
                }
            }
        }

        private void loginFailure2Umeng(final BaseActivity activity, final long start, final int code,
                                        final String message) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    long costTime = System.currentTimeMillis() - start;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("status", "failure");
                    params.put("error_code", code + "");
                    params.put("error_description", message);
                    MobclickAgent.onEventValue(activity, "login1",
                            params, (int) costTime);
                    MobclickAgent.onEventDuration(activity, "login1",
                            (int) costTime);

                }
            });
        }

        // 环信 登陆
        private void loginIM(final BaseActivity activity, String userName, String passWord) {
            ApplicationControl.currentUserNick = ApplicationConstants.IM_NIKENAME;
            // 如果用户名密码都有，直接进入主页面
		/*
		 * if (DemoHXSDKHelper.getInstance().isLogined()) { autoLogin = true;
		 * startActivity(new Intent(Login.this, Main.class)); return; }
		 */
            // ApplicationControl.currentUserNick = "ydt01";
            final String username = userName;
            final String password = passWord;
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                progressShow = true;
                final ProgressDialog pd = new ProgressDialog(
                        activity);
                pd.setCanceledOnTouchOutside(false);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        progressShow = false;
                    }
                });
                pd.setMessage("正在登录...");
                pd.show();

                final long start = System.currentTimeMillis();
                // 调用sdk登陆方法登陆聊天服务器
                EMChatManager.getInstance().login(username, password,
                        new EMCallBack() {

                            @Override
                            public void onSuccess() {
                                // umeng自定义事件，开发者可以把这个删掉
                                loginSuccess2Umeng(activity, start);

                                if (!progressShow) {
                                    return;
                                }
                                // 登陆成功，保存用户名密码
                                ApplicationControl.getInstance().setUserName(
                                        username);
                                ApplicationControl.getInstance().setPassword(
                                        password);
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd.setMessage("正在获取未读信息列表...");
                                    }
                                });
                                try {
                                    // ** 第一次登录或者之前logout后，加载所有本地群和回话
                                    // ** manually load all local groups and
                                    // conversations in case we are auto login
                                    EMGroupManager.getInstance().loadAllGroups();
                                    EMChatManager.getInstance()
                                            .loadAllConversations();
                                    // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
                                    List<String> usernames = EMContactManager
                                            .getInstance().getContactUserNames();
                                    Map<String, com.easemob.chatuidemo.domain.User> userlist = new HashMap<>();
                                    for (String username : usernames) {
                                        com.easemob.chatuidemo.domain.User user = new com.easemob.chatuidemo.domain.User();
                                        user.setUsername(username);
                                        setUserHearder(username, user);
                                        userlist.put(username, user);
                                        if(BuildConfig.DEBUG) {
                                            Log.i("loginIM", "username = " + username);
                                        }
                                    }
                                    // 添加user"申请与通知"
                                    com.easemob.chatuidemo.domain.User newFriends = new com.easemob.chatuidemo.domain.User();
                                    newFriends
                                            .setUsername(Constant.NEW_FRIENDS_USERNAME);
                                    newFriends.setNick(activity.getString(R.string.apply_notify));
                                    newFriends.setHeader("");
                                    userlist.put(Constant.NEW_FRIENDS_USERNAME,
                                            newFriends);
                                    // 添加"群聊"
                                    com.easemob.chatuidemo.domain.User groupUser = new com.easemob.chatuidemo.domain.User();
                                    groupUser.setUsername(Constant.GROUP_USERNAME);
                                    groupUser.setNick(activity.getString(R.string.group_chat));
                                    groupUser.setHeader("");
                                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                                    // 添加"官方账号"
                                    com.easemob.chatuidemo.domain.User publicCount = new com.easemob.chatuidemo.domain.User();
                                    publicCount.setUsername(Constant.PUBLIC_COUNT);
                                    publicCount.setNick(activity.getString(R.string.public_count));
                                    publicCount.setHeader("");
                                    userlist.put(Constant.PUBLIC_COUNT, publicCount);
                                    // 存入内存
                                    ApplicationControl.getInstance()
                                            .setContactList(userlist);
                                    // 存入db
                                    UserDao dao = new UserDao(
                                            activity);
                                    List<com.easemob.chatuidemo.domain.User> users = new ArrayList<>(
                                            userlist.values());
                                    dao.saveContactList(users);
                                    // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
                                    EMGroupManager.getInstance()
                                            .getGroupsFromServer();

                                    ApplicationInitCache.initData(SharePreferenceUtil.getInstance(activity), new Gson());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (!activity
                                                    .isFinishing())
                                                pd.dismiss();
                                            ApplicationControl.getInstance().logout(
                                                    null);
                                            Toast.makeText(activity.getApplicationContext(),
                                                    "登录失败", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    return;
                                }
                                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                                boolean updatenick = EMChatManager.getInstance()
                                        .updateCurrentUserNick(
                                                ApplicationControl.currentUserNick);
                                if (!updatenick) {
                                    EMLog.e("LoginActivity",
                                            "update current user nick fail");
                                }

                                if (!activity.isFinishing())
                                    pd.dismiss();
                                // 进入主页面

                                SharedPreferences sp = activity.getSharedPreferences(
                                        "jrdr.setting", MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("userId", "" + user_id);
                                edit.putString("token", token);
                                edit.putString("IM_PASSWORD", IM_PASSWORD);
                                edit.putString("IM_USERID", IM_USERID);
                                edit.putString("IM_AVATAR", IM_AVATAR);
                                edit.putString("IM_NIKENAME", IM_NIKENAME);
                                // 记住密码供下次登陆
                                edit.putString("remember_tele", TEL);
                                edit.putString("remember_pwd", PWD);
                                edit.commit();


                                MainTabActivity.showAnim = true;
                                Intent intent = new Intent();
                                intent.setClass(activity,
                                        MainTabActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                activity.startActivity(intent);
                                activity.finish();

                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }

                            @Override
                            public void onError(final int code, final String message) {
                                loginFailure2Umeng(activity, start, code, message);

                                if (!progressShow) {
                                    return;
                                }
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd.dismiss();
                                        activity.showToast("登录失败");
                                        IntentManager.intentToLoginActivity(activity);
                                    }
                                });
                            }
                        });

            }




        }
    }
}
