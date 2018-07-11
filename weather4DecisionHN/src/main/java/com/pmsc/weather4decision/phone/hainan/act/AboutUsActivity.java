package com.pmsc.weather4decision.phone.hainan.act;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction:关于我们界面
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
public class AboutUsActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_about);
		TextView title_view = (TextView) findViewById(R.id.title_view);
		title_view.setText("关于我们");
	}
	
	public void onLeftButtonAction(View v) {
		super.onBackPressed();
	}
}
