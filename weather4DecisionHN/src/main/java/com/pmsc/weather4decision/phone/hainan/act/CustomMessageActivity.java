package com.pmsc.weather4decision.phone.hainan.act;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction:自定义消息详情界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月3日 下午3:22:38
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class CustomMessageActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_custom_msg);
		TextView titleV = (TextView) findViewById(R.id.title_tv);
		TextView timeV = (TextView) findViewById(R.id.time_tv);
		TextView contentV = (TextView) findViewById(R.id.content_tv);
		
		JsonMap data = JsonMap.parseJson(getIntent().getStringExtra("custom_data"));
		if (data != null) {
			titleV.setText(data.getString("title"));
			timeV.setText(data.getString("pubDate"));
			contentV.setText(data.getString("content"));
		}
	}
	
	public void onLeftButtonAction(View v) {
		super.onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		boolean is_push = getIntent().getBooleanExtra("is_push", false);
		if (is_push) {
			openActivity(SplashActivity.class, null);
		}
	}
}
