package com.parttime.guide;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.qingmu.jianzhidaren.R;

public class GuideChatActivity extends GuideBaseActivity {

    private int clickTimes = 0;

    private ImageView step1 , step2 , step3, step4 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_chat);

        step1 = (ImageView)findViewById(R.id.step1);
        step2 = (ImageView)findViewById(R.id.step2);
        step3 = (ImageView)findViewById(R.id.step3);
        step4 = (ImageView)findViewById(R.id.step4);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (clickTimes == 3) {
                this.finish();
                return true;
            } else if (clickTimes == 2) {
                step3.setVisibility(View.GONE);
                step4.setVisibility(View.VISIBLE);
            } else if (clickTimes == 1) {
                step2.setVisibility(View.GONE);
                step3.setVisibility(View.VISIBLE);
            } else if (clickTimes == 0) {
                step1.setVisibility(View.GONE);
                step2.setVisibility(View.VISIBLE);
            }

            clickTimes++;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(clickTimes == 3){
            this.finish();
            return true;
        }else if(clickTimes == 2){
            step3.setVisibility(View.GONE);
            step4.setVisibility(View.VISIBLE);
        }else if(clickTimes == 1){
            step2.setVisibility(View.GONE);
            step3.setVisibility(View.VISIBLE);
        }else if(clickTimes == 0){
            step1.setVisibility(View.GONE);
            step2.setVisibility(View.VISIBLE);
        }

        clickTimes ++ ;

        return super.onKeyDown(keyCode, event);
    }
}
