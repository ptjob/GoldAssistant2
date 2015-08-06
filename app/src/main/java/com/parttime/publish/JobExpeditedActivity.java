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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_expedited);
        initControls();
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
        Intent intent = new Intent(this, JobExpeditedActivity.class);
        startActivity(intent);
    }
}
