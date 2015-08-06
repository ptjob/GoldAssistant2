package com.quark.jianzhidaren;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.parttime.login.FindPJLoginActivity;
import com.qingmu.jianzhidaren.R;
import com.umeng.analytics.MobclickAgent;

public class LaheiPageActivity extends Activity {
	public static LaheiPageActivity instance;
	private final static String telephone = "0755-23742220";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MobclickAgent.updateOnlineConfig(this);
		instance = this;
		setContentView(R.layout.activity_lahei);
		Button phone = (Button) findViewById(R.id.phone);
		phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ telephone));
				startActivity(intent);
			}
		});

		Button exit = (Button) findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(FindPJLoginActivity.instance != null){
					FindPJLoginActivity.instance.finish();
				}
				finish();
			}
		});

	}

}
