package com.parttime.publish;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.parttime.utils.ApplicationUtils;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.share.CompanySharePopupWindow;
import com.quark.share.ShareModel;
import com.quark.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;

/**
 * 经纪人排行榜
 * Created by wyw on 2015/8/2.
 */
public class JobBrokerChartsActivity extends BaseActivity implements Handler.Callback, PlatformActionListener {
    private ArrayList<Fragment> pageViews;
    private TextView msgTv;
    private TextView contactsTv;
    private TextView mTxtShare;
    private ViewPager viewPager;
    private CompanySharePopupWindow companySharePopupWindow;
    private View mViewRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_broker_rank);

        intiControls();
        bindListener();
    }

    private void bindListener() {
        msgTv.setOnClickListener(new TabButtonClickListener(0));
        contactsTv.setOnClickListener(new TabButtonClickListener(1));
        mTxtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
    }

    private void share() {
        String companyShareUrl = ApplicationUtils.getCompanyShareUrl(ApplicationUtils.getLoginId());
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

    private void intiControls() {
        mViewRoot = findViewById(R.id.ll_root);
        pageViews = new ArrayList<>();
        Fragment jobBrokerChartsFragment = JobBrokerChartsFragment.newInstance();
        Fragment jobBrokerMeFragment = JobBrokerDetailFragment.newInstance(ApplicationUtils.getLoginId());
        pageViews.add(jobBrokerChartsFragment);
        pageViews.add(jobBrokerMeFragment);

        msgTv = (TextView) findViewById(R.id.fragment_quanzi_msg_tv);
        contactsTv = (TextView) findViewById(R.id.fragment_quanzi_contacts_tv);
        mTxtShare = (TextView) findViewById(R.id.right_txt);

        msgTv.setTextColor(getResources().getColor(R.color.guanli_common_color));

        viewPager = (ViewPager) findViewById(R.id.guidePages);
        viewPager.setAdapter(new GuidePageAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    @Override
    public void setBackButton() {
        super.setBackButton();
    }

    private class TabButtonClickListener implements View.OnClickListener {
        private int index;

        public TabButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View view) {
            if (index == 0) {
                if (mTxtShare != null) {
                    mTxtShare.setVisibility(View.GONE);
                }
            } else if (index == 1 && ApplicationUtils.allowCompanyShare()) {
                if (mTxtShare != null) {
                    mTxtShare.setVisibility(View.VISIBLE);
                }
            }
            viewPager.setCurrentItem(index, true);
        }
    }

    private class GuidePageAdapter extends FragmentPagerAdapter {
        public GuidePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return pageViews.get(i);
        }

        @Override
        public int getCount() {
            return pageViews.size();
        }
    }

    private class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            setBackStatus(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    private void setBackStatus(int position) {
        if (position == 0) {
            msgTv.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.quanzi_btn_bar_left_on));

            msgTv.setTextColor(getResources().getColor(
                    R.color.guanli_common_color));

            contactsTv.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.quanzi_btn_bar_right_off));
            contactsTv.setTextColor(getResources().getColor(
                    R.color.body_color));
            mTxtShare.setVisibility(View.GONE);
        } else if (position == 1) {
            msgTv.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.quanzi_btn_bar_left_off));
            msgTv.setTextColor(getResources().getColor(
                    R.color.body_color));
            contactsTv.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.quanzi_btn_bar_right_on));
            contactsTv.setTextColor(getResources().getColor(
                    R.color.guanli_common_color));
            if (ApplicationUtils.allowCompanyShare()) {
                mTxtShare.setVisibility(View.VISIBLE);
            }
        }

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
