package com.parttime.publish;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;

import com.parttime.common.head.ActivityHead;
import com.parttime.utils.ApplicationUtils;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.share.CompanySharePopupWindow;
import com.quark.share.ShareModel;
import com.quark.utils.Logger;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;

/**
 * 经纪人详情页面
 * Created by wyw on 2015/8/6.
 */
public class JobBrokerDetailActivity extends BaseActivity implements PlatformActionListener, Handler.Callback {

    private static final String TAG = "JobBrokerDetailActivity";

    public static final String EXTRA_COMPANY_ID = "company_id";

    private int mCompanyId;
    private View mViewRoot;
    private CompanySharePopupWindow companySharePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_broker_detail);

        initIntent();
        initControls();


    }

    private void initControls() {

        mViewRoot = findViewById(R.id.ll_root);
        ActivityHead activityHead = new ActivityHead(this);
        activityHead.setCenterTxt1(R.string.job_broker_detail_title);

        if (ApplicationUtils.allowCompanyShare()) {
            activityHead.setRightTxt(R.string.share);
            activityHead.setRightTxtOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share();
                }
            });
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        JobBrokerDetailFragment jobBrokerDetailFragment = JobBrokerDetailFragment.newInstance(mCompanyId);
        fragmentTransaction.add(R.id.ll_main, jobBrokerDetailFragment);
        fragmentTransaction.commit();
    }

    private void share() {
        String companyShareUrl = ApplicationUtils.getCompanyShareUrl(mCompanyId);
        Logger.i(TAG, "[share]companyShareUrl=" + companyShareUrl);


        companySharePopupWindow = new CompanySharePopupWindow(this, true);
        ShareModel model = new ShareModel();
        model.setUrl(companyShareUrl);
        companySharePopupWindow.initShareParams(model, 0);
        companySharePopupWindow.setPlatformActionListener(this);
        String imageurl = Url.GETPIC + "pop_share_btn_jz.png";
        model.setImageUrl(imageurl);
        // 详细信息
        companySharePopupWindow.showShareWindow();
        // 显示窗口 (设置layout在PopupWindow中显示的位置)
        companySharePopupWindow.showAtLocation(mViewRoot, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void initIntent() {
        mCompanyId = getIntent().getIntExtra(EXTRA_COMPANY_ID, -1);
    }

    @Override
    public void setBackButton() {
        super.setBackButton();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = i;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Message msg = new Message();
        msg.what = 1;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Message msg = new Message();
        msg.what = 0;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.arg1) {
            case 1: {
                // 成功
                System.out.println("分享回调成功------------");
            }
            break;
            case 2: {
                // 失败
            }
            break;
            case 3: {
                // 取消
            }
            break;
        }
        if (companySharePopupWindow != null && companySharePopupWindow.isShowing()) {
            companySharePopupWindow.dismiss();
        }
        return false;
    }
}
