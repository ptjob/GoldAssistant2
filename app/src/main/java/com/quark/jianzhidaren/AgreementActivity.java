package com.quark.jianzhidaren;


import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parttime.base.WithTitleActivity;
import com.qingmu.jianzhidaren.R;

public class AgreementActivity extends WithTitleActivity {

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_agreement);
		super.onCreate(savedInstanceState);
//		setBackButton();
//		setTopTitle("注册协议");
		WebView webView = (WebView) findViewById(R.id.set);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.requestFocus();
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.loadUrl("file:///android_asset/cagreement.html");
	}

	@Override
	protected void initViews() {
		super.initViews();
		center(R.string.agreement);
		left(ImageButton.class, R.drawable.nav_btn_back);
	}
}
