package com.parttime.mine.setting;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.parttime.base.WithTitleActivity;
import com.qingmu.jianzhidaren.R;

import org.w3c.dom.Text;

/**
 * Created by cjz on 2015/8/13.
 */
public class AboutActivity extends WithTitleActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        ViewUtils.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        center(R.string.about);
        left(TextView.class, R.string.back);
    }

    @Override
    protected ViewGroup getLeftWrapper() {
        return null;
    }

    @Override
    protected ViewGroup getRightWrapper() {
        return null;
    }

    @Override
    protected TextView getCenter() {
        return null;
    }
}
