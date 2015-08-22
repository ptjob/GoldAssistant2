package com.parttime.guide;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.qingmu.jianzhidaren.R;

public class GuideUserDetailActivity extends GuideBaseActivity {
    private int clickTimes = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_user_detail);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (clickTimes == 0) {
                this.finish();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(clickTimes == 0){
            this.finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
