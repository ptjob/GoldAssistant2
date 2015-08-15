package com.parttime.publish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.parttime.common.head.ActivityHead;
import com.parttime.main.MainTabActivity;
import com.parttime.net.DefaultCallback;
import com.parttime.net.ErrorHandler;
import com.parttime.net.PublishRequest;
import com.parttime.publish.adapter.JobManageListAdapter;
import com.parttime.publish.vo.PublishActivityListVo;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.IntentManager;
import com.parttime.widget.BaseXListView;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.utils.Logger;

import me.maxwin.view.XListView;

/**
 * 兼职管理界面
 * Created by wyw on 2015/7/26.
 */
public class JobManageActivity extends BaseActivity implements AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, XListView.IXListViewListener {


    public static final int PAGE_COUNT = 20;
    private static final int REQUEST_JOB_DETAIL = 0x1000;

    private BaseXListView mListViewMain;
    private RadioButton mRadioRecruit;
    private RadioButton mRadioAuditing;
    private RadioButton mRadioUndercarriage;
    private JobManageListAdapter mAdapterRecruit;
    private JobManageListAdapter mAdapterAuditing;
    private JobManageListAdapter mAdapterUndercarriag;
    private JobManageListAdapter mCurrentAdapter;
    private PublishActivityListVo mCurrentVo;
    private int currentType;

    private DefaultCallback mDefaultCallback = new DefaultCallback() {
        @Override
        public void success(Object obj) {
            if (CheckUtils.isSafe(JobManageActivity.this)) {
                showWait(false);
                mCurrentVo = (PublishActivityListVo) obj;
                if (mCurrentVo.type != null && mCurrentVo.type == currentType) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mCurrentVo.pageNumber == 1) {
                                mCurrentAdapter.setAll(mCurrentVo.jobManageListVoList);
                                mListViewMain.updateRefreshTime();
                            } else {
                                mCurrentAdapter.addAll(mCurrentVo.jobManageListVoList);
                            }

                            mListViewMain.setLoadOver(mCurrentVo.jobManageListVoList.size(), PAGE_COUNT);
                            mListViewMain.stopRefresh();
                            mListViewMain.stopLoadMore();
                        }
                    });
                }
            }
        }

        @Override
        public void failed(Object obj) {
            showWait(false);
            new ErrorHandler(JobManageActivity.this, obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_manage);
        initControls();
        bindListener();
        bindData();
    }

    @Override
    public void finish() {
        IntentManager.goToMainTab(this, R.id.tv3);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void bindData() {
        mAdapterRecruit = new JobManageListAdapter(this);
        mAdapterAuditing = new JobManageListAdapter(this);
        mAdapterUndercarriag = new JobManageListAdapter(this);
        mListViewMain.setAdapter(mAdapterRecruit);
        mCurrentAdapter = mAdapterRecruit;

        mRadioRecruit.setChecked(true);
        refreshFirstPage();
    }

    private void bindListener() {
        mListViewMain.setOnItemClickListener(this);
        mRadioRecruit.setOnCheckedChangeListener(this);
        mRadioAuditing.setOnCheckedChangeListener(this);
        mRadioUndercarriage.setOnCheckedChangeListener(this);

        mListViewMain.setXListViewListener(this);
    }

    private void initControls() {
        mListViewMain = (BaseXListView) findViewById(R.id.listview_main);
        mRadioRecruit = (RadioButton) findViewById(R.id.radio_recruit);
        mRadioAuditing = (RadioButton) findViewById(R.id.radio_auditing);
        mRadioUndercarriage = (RadioButton) findViewById(R.id.radio_undercarriage);

        ActivityHead activityHead = new ActivityHead(this);
        activityHead.setCenterTxt1(R.string.job_manage_title);

    }

    @Override
    public void setBackButton() {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int position = i - 1;
        if (position < mCurrentAdapter.getCount()) {
            long jobId = mCurrentAdapter.getItemId(position);
            IntentManager.openJobDetailActivity(this, REQUEST_JOB_DETAIL, (int) jobId, "");
        } else {
            showToast(R.string.error_date_and_refresh);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        refreshFirstPage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.i("onActivityResult");
        switch (requestCode) {
            case REQUEST_JOB_DETAIL:
                if (resultCode == RESULT_OK && data != null) {
                    boolean shouldRefresh = data.getBooleanExtra(JobDetailActivity.EXTRA_SHOULD_REFRESH, false);
                    if (shouldRefresh) {
                        refreshFirstPage();
                    }
                }
                break;
        }
    }

    // 刷新第一页
    private void refreshFirstPage() {
        mCurrentVo = null;
        showWait(true);
        refreshListView();
    }

    private void loadMore() {
        refreshListView();
    }

    private void refreshListView() {
        int nextPageNumber = getNextPageNumber();

        if (mRadioRecruit.isChecked()) {
            // 招人中
            currentType = PublishRequest.PUBLISH_ACTIVITY_LIST_TYPE_RECRUIT;
            if (nextPageNumber == 1) {
                mListViewMain.setAdapter(mAdapterRecruit);
                mCurrentAdapter = mAdapterRecruit;
            }
        } else if (mRadioAuditing.isChecked()) {
            // 待审核
            currentType = PublishRequest.PUBLISH_ACTIVITY_LIST_TYPE_AUDITING;
            if (nextPageNumber == 1) {
                mListViewMain.setAdapter(mAdapterAuditing);
                mCurrentAdapter = mAdapterAuditing;
            }
        } else {
            // 已下架
            currentType = PublishRequest.PUBLISH_ACTIVITY_LIST_TYPE_UNDERCARRIAGE;
            if (nextPageNumber == 1) {
                mListViewMain.setAdapter(mAdapterUndercarriag);
                mCurrentAdapter = mAdapterUndercarriag;
            }
        }

        if (nextPageNumber == 1) {
            mCurrentAdapter.notifyDataSetChanged();
        }
        new PublishRequest().publishActivityList(nextPageNumber, PAGE_COUNT, currentType, queue, mDefaultCallback);
    }

    // 获取下一页页码
    private int getNextPageNumber() {
        return mCurrentVo == null ? 1 : mCurrentVo.pageNumber + 1;
    }

    @Override
    public void onRefresh() {
        refreshFirstPage();
    }

    @Override
    public void onLoadMore() {
        loadMore();
    }


}
