package com.parttime.publish;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.parttime.common.head.ActivityHead;
import com.parttime.net.DefaultCallback;
import com.parttime.net.ErrorHandler;
import com.parttime.net.PublishRequest;
import com.parttime.net.ResponseBaseCommonError;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.IntentManager;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.BaseActivity;

/**
 * 扩招界面
 * Created by wyw on 2015/8/2.
 */
public class JobExpansionActivity extends BaseActivity {

    // EXTRA: 活动ID
    public static final String EXTRA_JOB_ID = "job_id";

    private EditText mEditExpansionNum;
    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_expansion);
        initIntent();
        initControls();
    }

    private void initIntent() {
        int jobId = getIntent().getIntExtra(EXTRA_JOB_ID, -1);
        if (jobId == -1) {
            showToast(R.string.error_operation_fail);
            finish();
            return;
        }
        this.jobId = jobId;
    }

    private void initControls() {
        ActivityHead activityHead = new ActivityHead(this);
        activityHead.initHead(this);
        activityHead.setCenterTxt1(getString(R.string.job_expantion_title));

        mEditExpansionNum = (EditText) findViewById(R.id.edittxt_expansion_num);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void expansion(View view) {
        // 扩招
        if (CheckUtils.isEmpty(mEditExpansionNum.getText().toString())) {
            // 输入为空
            showToast("扩招人数不能为空");
            return;
        }

        try {
            int num = Integer.valueOf(mEditExpansionNum.getText().toString());
            if (num > getResources().getInteger(R.integer.expansion_max_value)) {
                // 人数限制
                showToast(getString(R.string.job_expansion_num_limited));
                return ;
            }

            showWait(true);
            new PublishRequest().setUrgent(this.jobId, num, queue, new DefaultCallback() {
                @Override
                public void success(Object obj) {
                    showWait(false);
                    showToast(R.string.job_expedited_success);
                    finish();
                }

                @Override
                public void failed(Object obj) {
                    showWait(false);
                    new ErrorHandler(JobExpansionActivity.this, obj).showToast();
                }
            });

        } catch (NumberFormatException e) {
            // 输入非数字
            e.printStackTrace();
            showToast("扩招人数必须是整数");
        }
    }
}
