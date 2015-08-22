package com.parttime.guide;

import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class GuideBaseActivity extends FragmentActivity {



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键=
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
