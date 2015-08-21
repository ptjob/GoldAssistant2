package com.parttime.IM.activitysetting;

import android.content.DialogInterface;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.carson.constant.ConstantForSaveList;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.parttime.IM.activitysetting.GroupResumeSettingActivity.Action;
import com.parttime.net.DefaultCallback;
import com.parttime.net.GroupSettingRequest;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.ui.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by luhua on 2015/8/1.
 */
public class GroupSettingUtils {

    public void showAlertDialog(final BaseActivity activity,
            String title,
            String message,
            final GroupResumeSettingActivity.Action action,
            final GroupSettingRequest.UserVO userVO,
            int positiveRes, int negativeRes,
            final String userId,final String groupId, final RequestQueue queue,
            final DefaultCallback callback) {

        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(positiveRes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.showWait(true);
                if(action == Action.UNRESUME) {
                    ArrayList<Object> userIds = new ArrayList<>();
                    userIds.add(userId);
                    new GroupSettingRequest().cancelResume(userIds, groupId, queue, new DefaultCallback() {
                        @Override
                        public void success(Object obj) {
                            super.success(obj);
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                            /*EMGroupManager.getInstance()
                                                    .removeUserFromGroup(groupId,
                                                            String.valueOf(userVO.userId));*/
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {

                                                GroupSettingRequest.AppliantResult appliantResult = ConstantForSaveList.groupAppliantCache.get(groupId);
                                                if (appliantResult != null) {
                                                    appliantResult.userList.remove(userVO);

                                                    appliantResult.approvedCount--;

                                                    if(userVO.sex == 1){
                                                        appliantResult.approvedMaleCount -- ;
                                                    }else{
                                                        appliantResult.approvedFemaleCount --;
                                                    }
                                                }

                                                callback.success(userId);
                                                activity.showWait(false);
                                            }
                                        });
                                    } catch (final Exception e) {
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                activity.showWait(false);
                                                Toast.makeText(ApplicationControl.getInstance(),
                                                        "退出群聊失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void failed(Object obj) {
                            super.failed(obj);
                            activity.showWait(false);
                            Toast.makeText(ApplicationControl.getInstance(),
                                    ApplicationControl.getInstance().getString(R.string.action_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }else if(action == Action.REJECT){
                    ArrayList<Object> userIds = new ArrayList<>();
                    userIds.add(userId);
                    new GroupSettingRequest().reject(userIds, groupId, queue, new DefaultCallback() {
                        @Override
                        public void success(Object obj) {
                            super.success(obj);
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                            /*EMGroupManager.getInstance()
                                                    .removeUserFromGroup(groupId,
                                                            String.valueOf(userVO.userId));*/
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {

                                                GroupSettingRequest.AppliantResult appliantResult = ConstantForSaveList.groupAppliantCache.get(groupId);
                                                if (appliantResult != null) {
                                                    appliantResult.userList.remove(userVO);
                                                    appliantResult.unApprovedCount--;
                                                }
                                                callback.success(userId);
                                                activity.showWait(false);
                                            }
                                        });
                                    } catch (final Exception e) {
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                activity.showWait(false);
                                                Toast.makeText(ApplicationControl.getInstance(),
                                                        "退出群聊失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void failed(Object obj) {
                            super.failed(obj);
                            activity.showWait(false);
                            Toast.makeText(ApplicationControl.getInstance(),
                                    ApplicationControl.getInstance().getString(R.string.action_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }else if(action == Action.RESUME){
                    approveResume(activity, userVO, groupId, queue, callback);
                }
            }

        });

        builder.setNegativeButton(negativeRes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void approveResume(final BaseActivity activity,
                               final GroupSettingRequest.UserVO userVO,
                               final String groupId, final RequestQueue queue,
                               final DefaultCallback callback) {
        activity.showWait(true);
        ArrayList<Long> userIds = new ArrayList<>();
        userIds.add(userVO.userId);
        new GroupSettingRequest().approve(userIds , groupId, queue, new DefaultCallback(){
            @Override
            public void success(Object obj) {
                super.success(obj);
                userVO.apply = GroupSettingRequest.UserVO.APPLY_OK;
                GroupSettingRequest.AppliantResult appliantResult = ConstantForSaveList.groupAppliantCache.get(groupId);
                if(appliantResult != null){
                    List<GroupSettingRequest.UserVO> userVOs =  appliantResult.userList;
                    if(userVOs != null){
                        for (GroupSettingRequest.UserVO uVO : userVOs){
                            if(userVO.userId == uVO.userId){
                                uVO.apply = GroupSettingRequest.UserVO.APPLY_OK;
                            }
                        }
                    }
                    appliantResult.approvedCount++;
                    appliantResult.unApprovedCount --;
                    if(userVO.sex == 1){
                        appliantResult.approvedMaleCount ++ ;
                    }else{
                        appliantResult.approvedFemaleCount ++;
                    }
                }
                callback.success(null);
                activity.showWait(false);
            }

            @Override
            public void failed(Object obj) {
                super.failed(obj);
                activity.showWait(false);
                callback.failed(obj);
            }
        });
    }


}
