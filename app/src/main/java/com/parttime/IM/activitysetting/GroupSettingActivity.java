package com.parttime.IM.activitysetting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.carson.constant.ConstantForSaveList;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.db.MessageSetDao;
import com.easemob.exceptions.EaseMobException;
import com.parttime.common.head.ActivityHead;
import com.parttime.constants.ActionConstants;
import com.parttime.constants.ActivityExtraAndKeys;
import com.parttime.net.GroupSettingRequest;
import com.parttime.pojo.BaseUser;
import com.parttime.pojo.MessageSet;
import com.parttime.widget.SetItem;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.volley.VolleySington;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class GroupSettingActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "GroupSettingActivity";

    private ActivityHead headView;
    private SetItem top, //置顶
            undisturb,   //免扰
            gag;         //禁言

    private String groupId ;
    private EMGroup group;
    private String groupType = null;
    private MessageSet messageSet;
    private boolean isDisturb;

    protected RequestQueue queue = VolleySington.getInstance().getRequestQueue();

    private MessageSetDao dao = new MessageSetDao(ApplicationControl.getInstance());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting_detail);

        initView();

        bindView();

    }

    private void initView() {
        headView = new ActivityHead(this);

        top = (SetItem)findViewById(R.id.top);
        undisturb = (SetItem)findViewById(R.id.undisturb);
        gag = (SetItem)findViewById(R.id.gag);

    }

    private void bindView() {
        groupId = getIntent().getStringExtra(ActivityExtraAndKeys.GroupSetting.GROUPID);
        if(groupId != null) {
            group = EMGroupManager.getInstance().getGroup(groupId);

            //获取群组type
            Hashtable<String, EMConversation> conversations = EMChatManager
                    .getInstance().getAllConversations();
            if(conversations != null){
                EMConversation conversation = conversations.get(groupId);
                if(conversation != null){
                    groupType = conversation.getType().name();
                }
            }else{
                groupType = EMConversation.EMConversationType.GroupChat.name();
            }
            if( groupType != null) {
                //查询置顶
                messageSet = dao.getMessageSet(groupId, groupType);
            }
        }

        headView.setCenterTxt1(R.string.group_setting);
        if(messageSet != null){
            top.setRightImage(R.drawable.settings_btn_switch_on);
        }

        List<String> disturbListGroup = EMChatManager.getInstance()
                .getChatOptions().getReceiveNoNotifyGroup();
        if(disturbListGroup != null && disturbListGroup.contains(groupId)){
            undisturb.setRightImage(R.drawable.settings_btn_switch_on);
            isDisturb = true;
        }else{
            undisturb.setRightImage(R.drawable.settings_btn_switch_off);
        }

        top.setOnClickListener(this);
        undisturb.setOnClickListener(this);
        gag.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top:
                toTop();
                break;
            case R.id.undisturb:
                disturbSet();
                break;
            case R.id.gag:
                ConstantForSaveList.userIdUserCache.clear();
                Map<String,GroupSettingRequest.AppliantResult> groupAppliantCache = ConstantForSaveList.groupAppliantCache;
                GroupSettingRequest.AppliantResult appliantResult = groupAppliantCache.get(groupId);
                if(appliantResult != null && appliantResult.userList != null && appliantResult.userList.size() > 0){
                    for(GroupSettingRequest.UserVO userVO : appliantResult.userList){
                        if(userVO != null){
                            BaseUser baseUser = new BaseUser();
                            baseUser.userId = String.valueOf(userVO.userId);
                            baseUser.name = userVO.name;
                            baseUser.picture = userVO.picture;
                            ConstantForSaveList.userIdUserCache.put(String.valueOf(userVO.userId),baseUser);
                        }
                    }
                }
                Intent intent = new Intent(this, GroupGagActivity.class);
                intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPID, groupId);
                startActivity(intent);
                break;
        }
    }

    private void toTop(){
        if(group == null){
            return;
        }

        if(messageSet == null){
            messageSet = new MessageSet();
            messageSet.name = group.getGroupId();
            messageSet.type = groupType;
            messageSet.isTop = true;
            messageSet.createTime = System.currentTimeMillis();
            long result = dao.save(messageSet);
            if(result <= 0 ){
                Log.i(TAG,"to top failed");
            }
            top.setRightImage(R.drawable.settings_btn_switch_on);
        }else{
            dao.delete(groupId, groupType);
            messageSet = null;
            top.setRightImage(R.drawable.settings_btn_switch_off);
        }
    }

    public void disturbSet(){

        if(isDisturb){
           /* new Thread(new Runnable(){

                @Override
                public void run() {
                    try {
                        EMGroupManager.getInstance().unblockGroupMessage(groupId);
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/

            List<String> pingbiListGroup = EMChatManager.getInstance()
                    .getChatOptions().getReceiveNoNotifyGroup();
            if (pingbiListGroup != null) {
                if (pingbiListGroup.contains(groupId)) {
                    pingbiListGroup.remove(groupId);
                }
            }
            EMChatManager.getInstance().getChatOptions()
                    .setReceiveNotNoifyGroup(pingbiListGroup);
            isDisturb = false;
            undisturb.setRightImage(R.drawable.settings_btn_switch_off);
        }else{
            /*new Thread(new Runnable(){

                @Override
                public void run() {
                    try {
                        EMGroupManager.getInstance().unblockGroupMessage(groupId);
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/

            List<String> pingbiListGroup = EMChatManager.getInstance()
                    .getChatOptions().getReceiveNoNotifyGroup();
            pingbiListGroup.add(groupId);
            EMChatManager.getInstance().getChatOptions()
                    .setReceiveNotNoifyGroup(pingbiListGroup);
            isDisturb = true;
            undisturb.setRightImage(R.drawable.settings_btn_switch_on);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(ActionConstants.ACTION_MESSAGE_TO_TOP));
    }
}
