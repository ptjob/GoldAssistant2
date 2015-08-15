package com.parttime.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.easemob.chat.EMChatManager;
import com.parttime.IM.activitysetting.GroupResumeSettingActivity;
import com.parttime.addresslist.userdetail.UserDetailActivity;
import com.parttime.common.Image.ImageShowActivity;
import com.parttime.common.activity.ChooseListActivity;
import com.parttime.constants.ActivityExtraAndKeys;
import com.parttime.main.MainTabActivity;
import com.parttime.mine.RechargeActivity;
import com.parttime.net.GroupSettingRequest;
import com.parttime.pojo.PartJob;
import com.parttime.publish.JobBrokerDetailActivity;
import com.parttime.publish.JobDetailActivity;
import com.parttime.publish.JobManageActivity;
import com.parttime.publish.JobPlazaActivity;
import com.parttime.publish.WriteJobActivity;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.jianzhidaren.BaseActivity;

import java.util.ArrayList;

/**
 * Intent启动辅助类
 */
public class IntentManager {

    /**
     * 活动群跳转到用户详情界面
     */
    public static void toUserDetailFromActivityGroup(Activity activity,
                                                     int isEnd,
                                                     String groupId,
                                                     GroupSettingRequest.UserVO userVO,
                                                     ArrayList<String> userIds,
                                                     String groupOwner){
        Intent intent = new Intent(activity, UserDetailActivity.class);
        intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPID , groupId);
        if(userVO != null) {
            intent.putExtra(ActivityExtraAndKeys.UserDetail.SELECTED_USER_ID, String.valueOf(userVO.userId));
            if(isEnd == GroupSettingRequest.AppliantResult.NO_END) {
                intent.putExtra(ActivityExtraAndKeys.UserDetail.FROM_AND_STATUS, UserDetailActivity.FromAndStatus.FROM_ACTIVITY_GROUP_AND_NOT_FINISH);
            }else{
                intent.putExtra(ActivityExtraAndKeys.UserDetail.FROM_AND_STATUS, UserDetailActivity.FromAndStatus.FROM_ACTIVITY_GROUP_AND_IS_FINISH);
            }
        }
        if(userIds != null){
            userIds.remove(EMChatManager.getInstance().getCurrentUser());
            intent.putStringArrayListExtra(ActivityExtraAndKeys.USER_ID, userIds);
        }
        if(EMChatManager.getInstance().getCurrentUser()
                .equals(groupOwner)){
            intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPOWNER, true);
        }

        activity.startActivity(intent);
    }

    /**
     * 普通群跳转到联系人详情
     * @param activity Activity
     * @param username String
     * @param groupId String
     * @param objects ArrayList<String>
     * @param groupOwner String
     */
    public static void intentToUseDetail(Activity activity, String username,
                                   String groupId, ArrayList<String> objects,String groupOwner) {
        Intent intent = new Intent(activity,UserDetailActivity.class);
        intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPID , groupId);
        if(objects != null){
            objects.remove(EMChatManager.getInstance().getCurrentUser());
        }
        intent.putStringArrayListExtra(ActivityExtraAndKeys.USER_ID, objects);
        intent.putExtra(ActivityExtraAndKeys.UserDetail.SELECTED_USER_ID,username );
        if(EMChatManager.getInstance().getCurrentUser()
                .equals(groupOwner)){
            intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPOWNER, true);
        }
        intent.putExtra(ActivityExtraAndKeys.UserDetail.FROM_AND_STATUS, UserDetailActivity.FromAndStatus.FROM_NORMAL_GROUP_AND_FRIEND);
        activity.startActivity(intent);
    }


    /**
     * 打开公共选择列表界面
     *
     * @param data 列表显示的内容
     */
    public static void openChoooseListActivity(Activity activity, String title, String[] data, int requestCode) {
        Intent intent = new Intent(activity, ChooseListActivity.class);
        intent.putExtra(ChooseListActivity.EXTRA_TITLE, title);
        intent.putExtra(ChooseListActivity.EXTRA_DATA, data);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openJobDetailActivity(Context context, PartJob partJob) {
        Intent intent = new Intent(context, JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.EXTRA_PART_JOB, partJob);
        context.startActivity(intent);
    }

    /**
     * 跳往活动详情页面
     * @param context
     * @param jobId 活动ID （二个传一个，不传时带 <= 0 的值）
     * @param groupId 群组ID （二个传一个，不传时带""或者null ）
     */
    public static void openJobDetailActivity(Context context, int jobId, String groupId) {
        Intent intent = new Intent(context, JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.EXTRA_ID, jobId);
        intent.putExtra(JobDetailActivity.EXTRA_GROUP_ID, groupId);
        context.startActivity(intent);
    }

    /**
     * 跳往活动详情页面
     * @param context
     * @param requestCode 请求Code
     * @param jobId 活动ID （二个传一个，不传时带 <= 0 的值）
     * @param groupId 群组ID （二个传一个，不传时带""或者null ）
     */
    public static void openJobDetailActivity(BaseActivity context, int requestCode,  int jobId, String groupId) {
        Intent intent = new Intent(context, JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.EXTRA_ID, jobId);
        intent.putExtra(JobDetailActivity.EXTRA_GROUP_ID, groupId);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开经纪人详情页面
     * @param context
     * @param companyId
     */
    public static void openBrokerDetailActivity(Context context, int companyId) {
        Intent intent = new Intent(context, JobBrokerDetailActivity.class);
        intent.putExtra(JobBrokerDetailActivity.EXTRA_COMPANY_ID, companyId);
        context.startActivity(intent);
    }

    /**
     * 跳往主TAB页
     * @param context
     */
    public static void goToMainTab(Context context, int resId){
        Intent intent = new Intent(context, MainTabActivity.class);
        intent.putExtra("resId", resId);
        context.startActivity(intent);
    }

    /**
     * 跳往兼职管理列表
     * @param context
     */
    public static void goToJobManage(Context context)  {
        Intent intent = new Intent(context, JobManageActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳往活动列表
     * @param context
     */
    public static void goToJobPlaza(Context context) {
        Intent intent = new Intent(context, JobPlazaActivity.class);
        context.startActivity(intent);
    }

    public static void intentToImageShow(Context context,ArrayList<String> pictures, ArrayList<String> userIds) {
        Intent intent = new Intent(context, ImageShowActivity.class);
        intent.putStringArrayListExtra(ActivityExtraAndKeys.USER_ID,userIds);
        intent.putStringArrayListExtra(ActivityExtraAndKeys.ImageShow.PICTURES,pictures);
        context.startActivity(intent);
    }

    /**
     * 充值页面
     * @param context
     */
    public static void intentToRecharge(Context context) {
        context.startActivity(new Intent(context, RechargeActivity.class));
    }

    /**
     * 编辑活动
     * @param partJob 编辑对象
     */
    public static void intentToEditJob(BaseActivity context, int requestCode, PartJob partJob) {
        Intent intent = new Intent(context, WriteJobActivity.class);
        intent.putExtra(WriteJobActivity.EXTRA_PART_JOB, partJob);
        context.startActivityForResult(intent, requestCode);
    }
}
