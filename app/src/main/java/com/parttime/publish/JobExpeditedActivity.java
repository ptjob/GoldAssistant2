package com.parttime.publish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.parttime.common.head.ActivityHead;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.BaseActivity;

/**
 * 加急招人界面
 * Created by wyw on 2015/8/2.
 */
public class JobExpeditedActivity extends BaseActivity {
    public static final String EXTRA_JOB_ID = "job_id";

    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_expedited);
        initIntent();
        initControls();
    }

    private void initIntent() {
        jobId = getIntent().getIntExtra(EXTRA_JOB_ID, -1);
        if (jobId == -1) {
            showToast(R.string.error_operation_fail);
            finish();
        }
    }

    private void initControls() {
        ActivityHead activityHead = new ActivityHead(this);
        activityHead.initHead(this);
        activityHead.setCenterTxt1(getString(R.string.job_expedited_title));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void moveUp(View view) {
    }

    public void expansion(View view) {
        Intent intent = new Intent(this, JobExpansionActivity.class);
        intent.putExtra(JobExpeditedActivity.EXTRA_JOB_ID, jobId);
        startActivity(intent);
    }
}
