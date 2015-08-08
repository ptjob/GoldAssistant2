package com.parttime.publish;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.parttime.common.head.ActivityHead;
import com.parttime.net.DefaultCallback;
import com.parttime.net.ErrorHandler;
import com.parttime.net.PublishRequest;
import com.parttime.pojo.CertVo;
import com.parttime.pojo.PreCheckStatus;
import com.parttime.utils.IntentManager;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.ui.widget.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

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
        expeditedNow();
    }

    // 加急
    private void expeditedNow() {
        showWait(true);
        new PublishRequest().setUrgent(this.jobId, 0, queue, new DefaultCallback() {
            @Override
            public void success(Object obj) {
                showWait(false);
                showToast(R.string.job_expedited_success);
                IntentManager.openJobDetailActivity(JobExpeditedActivity.this, jobId, "");
                finish();
            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                new ErrorHandler(JobExpeditedActivity.this, obj).showToast();
            }
        });
    }

    public void expansion(View view) {
        Intent intent = new Intent(this, JobExpansionActivity.class);
        intent.putExtra(JobExpeditedActivity.EXTRA_JOB_ID, jobId);
        finish();
        startActivity(intent);

    }
}
