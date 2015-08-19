package com.carson.push;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.carson.broker.JiedanActivity;
import com.carson.constant.ConstantForSaveList;
import com.google.gson.Gson;
import com.parttime.IM.activitysetting.GroupResumeSettingActivity;
import com.parttime.constants.ActivityExtraAndKeys;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.login.StartUpActivity;
import com.parttime.main.MainTabActivity;
import com.parttime.utils.SharePreferenceUtil;
import com.quark.guanli.BaomingListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String GAG = "5";
    private static final String UNGAG = "6";

	private static final String TAG = "JPush";
	private SharePreferenceUtil sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = SharePreferenceUtil.getInstance(context);
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
				+ ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// 一个唯一标识

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			int target = 0;
			Log.e(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			String myJob = null;// 我的兼职
			String myComment = null;// 兼职成就
			String comment_activity_id = null;// 花名册小红点
												// 推送过来的是活动id,若是有已录取人取消报名会有极光推送
			String todo = null;// 未处理数据
			try {
				JSONObject extrasJson = new JSONObject(extras);
				comment_activity_id = extrasJson
						.optString("comment_activity_id");
				todo = extrasJson.optString("todo");
				myJob = extrasJson.optString("myJob");
				myComment = extrasJson.optString("myComment");
                String type = extrasJson.getString("type");
                String groupId = extrasJson.getString("group_id");

                if(GAG.equals(type)){
                    if(ConstantForSaveList.gagCache != null){
                        ConstantForSaveList.gagCache.add(groupId);
                        saveGagConfigure();
                    }
                }else if(UNGAG.equals(type)){
                    if(ConstantForSaveList.gagCache != null){
                        ConstantForSaveList.gagCache.remove(groupId);
                        saveGagConfigure();
                    }
                }

				if (null != todo) {
					try {
						target = Integer.parseInt(todo);
                        sp.saveSharedPreferences(ConstantForSaveList.userId + "todo", target);
					} catch (Exception e) {
					}
				} else {

				}
				if (null != comment_activity_id) {
					try {
                        sp.saveSharedPreferences(ConstantForSaveList.userId
								+ comment_activity_id, true);
					} catch (Exception e) {

					}
				} else {

				}
				if (null != myJob) {
					try {
						target = Integer.parseInt(myJob);
                        sp.saveSharedPreferences(ConstantForSaveList.userId + "myJob", target);
					} catch (Exception e) {

					}
				} else {

				}
				if (null != myComment) {
					try {
						target = Integer.parseInt(myComment);
                        sp.saveSharedPreferences(ConstantForSaveList.userId + "myComment",
								target);
					} catch (Exception e) {

					}
				} else {

				}
			} catch (Exception e) {
				return;
			}
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			// 报名极光通知
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			int target = 0;
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			String activity_id = null;// 花名册小红点 推送过来的是活动id,若是有已录取人取消报名会有极光推送
			String todo = null;// 未处理数据
			String type="";// type:1报名极光推送 2、接单推送
			try {
				JSONObject extrasJson = new JSONObject(extras);
				activity_id = extrasJson.optString("activity_id");
				todo = extrasJson.optString("todo");
				type = extrasJson.optString("type");
				if (null != todo) {
					try {
						target = Integer.parseInt(todo);
                        sp.saveSharedPreferences(ConstantForSaveList.userId + "todo", target);
						if ("2".equals(type)) {
                            sp.saveSharedPreferences(ConstantForSaveList.userId + "type",
									true);
						} else if ("1".equals(type)) {
                            sp.saveSharedPreferences(ConstantForSaveList.userId + "type",
									false);
						}
					} catch (Exception e) {
					}
				} else {

				}
				if (null != activity_id) {
					try {
						// edt.putBoolean(
						// ConstantForSaveList.userId + activity_id, true);
					} catch (Exception e) {

					}
				} else {

				}
			} catch (Exception e) {
				return;
			}
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			// 用户点击报名,点击通知弹出用户详情界面
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
			if (!"".equals(sp.loadStringSharedPreference("userId", ""))) {
				String activity_id, titile, groupId = null;
				String female_count, male_count;
				String extrassss = bundle.getString(JPushInterface.EXTRA_EXTRA);
				JSONObject extrasJson;
				String type;// type 1:之前的报名通知；2:接单推送
				try {
					extrasJson = new JSONObject(extrassss);
					type = extrasJson.optString("type");
					activity_id = extrasJson.optString("activity_id");
					titile = extrasJson.optString("titile");
					female_count = extrasJson.optString("female_count");
					male_count = extrasJson.optString("male_count");
                    groupId = extrasJson.optString("group_id");
				} catch (JSONException e) {
					type = "";
					activity_id = "";
					titile = "";
					male_count = "";
					female_count = "";
					e.printStackTrace();
				}
				// type =1 接收到通知点击跳转到查看报名人员界面
				if ("1".equals(type)) {
					Bundle b = new Bundle();
					b.putString("activity_id", activity_id);
					b.putString("title", titile);
					b.putString("female_count", female_count);
					b.putString("male_count", male_count);
					b.putBoolean("fromNotification", true);

                    /*startActivityForResult(
                            (new Intent(this, GroupResumeSettingActivity.class).putExtra(
                                    "groupId", groupId)), REQUEST_CODE_GROUP_DETAIL);*/
                    if(! TextUtils.isEmpty(groupId)) {
                        //Intent i = new Intent(context, MainTabActivity.class);
                        Intent i = new Intent(context, GroupResumeSettingActivity.class);
                        //i.putExtras(b);
                        i.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPID, groupId);
                        //i.putExtra(ActivityExtraAndKeys.TO_ACTIVITY_SETTING,true);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                    }
				} else if ("2".equals(type)) {
					/*Intent i = new Intent(context, JiedanActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);*/
				}

			} else {
				// 商家已经退出apk了
				// CommonWidget.showAlertDialog(context, "您还没有登陆，注册登陆后才可以查看哦！",
				// "随便看看");
				Intent i = new Intent(context, StartUpActivity.class);
				// i.putExtras(bundle);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			}
			// 清除本应用的所有通知
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

    private void saveGagConfigure() {
        if(ConstantForSaveList.gagCache != null) {
            String gagCache = new Gson().toJson(ConstantForSaveList.gagCache);
            sp.saveSharedPreferences(SharedPreferenceConstants.GAG_CONFIGGURE,gagCache);
        }
    }

    // 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	public static boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {

		if (null != ConstantForSaveList.userId) {
			if (ConstantForSaveList.userId.contains("c")) {
				if (MainTabActivity.isForeground) {
					String message = bundle
							.getString(JPushInterface.EXTRA_MESSAGE);
					String extras = bundle
							.getString(JPushInterface.EXTRA_EXTRA);
					Intent msgIntent = new Intent(
							MainTabActivity.MESSAGE_RECEIVED_ACTION);
					msgIntent
							.putExtra(MainTabActivity.KEY_MESSAGE, message);
					if (!isEmpty(extras)) {
						try {
							JSONObject extraJson = new JSONObject(extras);
							if (null != extraJson && extraJson.length() > 0) {
								msgIntent.putExtra(
										MainTabActivity.KEY_EXTRAS, extras);
							}
						} catch (JSONException e) {

						}

					}
					context.sendBroadcast(msgIntent);
				}

			} else {

			}
		} else {

		}
	}
}
