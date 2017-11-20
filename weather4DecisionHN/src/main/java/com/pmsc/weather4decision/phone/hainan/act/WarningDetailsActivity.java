package com.pmsc.weather4decision.phone.hainan.act;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction:首页的预警提示点击后跳转到本界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月1日 下午6:30:10
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class WarningDetailsActivity extends AbsDrawerActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		setTitle(R.string.warning_title);
		leftButton.setBackgroundResource(R.drawable.back);
		rightButton.setVisibility(View.INVISIBLE);
		setContentView(R.layout.activity_home_warning);
		
		TextView titleTv = (TextView) findViewById(R.id.warning_title_tv);
		TextView departTv = (TextView) findViewById(R.id.warning_depart_tv);
		TextView timeTv = (TextView) findViewById(R.id.warning_time_tv);
		TextView contentTv = (TextView) findViewById(R.id.warning_content_tv);

		String warningData;
		if (getIntent().hasExtra("parm1")) {//推送消息
			warningData = getIntent().getStringExtra("parm1");
		}else {
			warningData = getIntent().getExtras().getString("warning_data");
		}

		JsonMap data = JsonMap.parseJson(warningData);
		
		String w2 = data.getString("w2");
		String place = data.getString("w1");
		if (!TextUtils.isEmpty(w2)) {
			place = data.getString("w1") + data.getString("w2");
		}else {
			place = data.getString("w1");
		}
//		if (w2.contains("北部湾北部") || w2.contains("北部湾南部") || w2.contains("琼州海峡") || w2.contains("本岛东部") || w2.contains("本岛西部") || w2.contains("本岛南部") || w2.contains("西沙附近") || w2.contains("中沙附近") || w2.contains("南沙附近")
//				|| "北部湾北部".contains(w2) || "北部湾南部".contains(w2) || "琼州海峡".contains(w2) || "本岛东部".contains(w2) || "本岛西部".contains(w2) || "本岛南部".contains(w2) || "西沙附近".contains(w2) || "中沙附近".contains(w2) || "南沙附近".contains(w2)) {
//			place = data.getString("w1");
//		}
		String depart = getString(R.string.qixiangtai, place);
		String warning = data.getString("w5") + data.getString("w7");
		
		titleTv.setText(depart + getString(R.string.place_pub) + getString(R.string.warning_home, warning));
		departTv.setText(depart);
		timeTv.setText(data.getString("w8"));
		contentTv.setText(data.getString("w9"));
	}
	
	@Override
	public void onLeftButtonAction(View view) {
		onBackPressed();
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
