package com.parttime.IM;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carson.constant.ConstantForSaveList;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.parttime.constants.ActivityExtraAndKeys;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.pojo.GroupDescription;
import com.parttime.utils.SharePreferenceUtil;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.ApplicationControl;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 *
 * Created by luhua on 15/7/12.
 */
public class ChatActivityHelper {

    private final String TAG = "ChatActivityHelper";

    /**
     * 群聊通知
     * @param activity ChatActivity
     */
    public void showGroupNotice(final ChatActivity activity, View view){
        View popView = activity.getLayoutInflater().inflate(R.layout.activity_chat_group_notice_popup, null);

        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        final PopupWindow popupWindow = new PopupWindow(popView,
                (metric.widthPixels),
                LinearLayout.LayoutParams.WRAP_CONTENT);

        EMGroup group = EMGroupManager.getInstance().getGroup(activity.toChatUsername);
        final String description = group.getDescription();
        View edit = popView.findViewById(R.id.edit);
        String currentUser = EMChatManager.getInstance().getCurrentUser();
        if(currentUser != null && currentUser.equals(group.getOwner())) {
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ApplicationControl.getInstance(), EditGroupNoticeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ActivityExtraAndKeys.ChatGroupNotice.GROUP_NOTICE_CONTENT, description);
                    intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPID, activity.toChatUsername);
                    ApplicationControl.getInstance().startActivity(intent);
                    popupWindow.dismiss();
                }
            });
        }



        TextView content = (TextView)popView.findViewById(R.id.group_notice_content);
        Map<String,GroupDescription> cache = ConstantForSaveList.groupDescriptionMapCache;
        GroupDescription groupDescription = cache.get(activity.toChatUsername);
        GroupDescription gd = null;
        if(groupDescription != null){
            content.setText(toDBC(groupDescription.info));
        }else if(description != null) {
            try {
                String desc = URLDecoder.decode(description, HTTP.UTF_8);
                gd = new Gson().fromJson(desc, GroupDescription.class);
                if(gd != null){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(gd.info).append(" ").append("更新于").append(" ").append(gd.time);
                    content.setText(toDBC(stringBuilder.toString()));
                }
            } catch (IllegalStateException | JsonSyntaxException | UnsupportedEncodingException ignore) {
                Log.e(TAG, "description format is error , description = " + description);
            }
        }

        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        //设置popwindow显示位置
        popupWindow.showAsDropDown(view,
                0,
                (int) activity.getResources().getDimension(R.dimen.chat_activity_popup_margin) / 5);
        //获取popwindow焦点
        popupWindow.setFocusable(true);
        //设置popwindow如果点击外面区域，便关闭。
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        updateNoticeStatus(activity,gd);
    }

    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    private void updateNoticeStatus(ChatActivity activity,GroupDescription gd) {
        Map<String,GroupDescription> cache = ConstantForSaveList.groupDescriptionMapCache;
        GroupDescription groupDescription = cache.get(activity.toChatUsername);
        if(groupDescription != null){
            if(groupDescription.isNew){
                groupDescription.isNew = false;
            }else{
                return;
            }
        }else{
            if(gd != null) {
                cache.put(activity.toChatUsername, gd);
            }else{
                return;
            }
        }
        SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).saveSharedPreferences(
                SharedPreferenceConstants.GROUP_NOTICE_CONFIGGURE,
                new Gson().toJson(cache));
    }

}
