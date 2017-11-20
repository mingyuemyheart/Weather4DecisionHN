package com.pmsc.weather4decision.phone.hainan.act;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.CONST;
import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;


/**
 * Depiction:意见反馈界面
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
public class FeedbackActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_feed_back);
		getWindow().setBackgroundDrawableResource(R.drawable.white_bg);
	}
	
	public void onLeftButtonAction(View v) {
		super.onBackPressed();
	}
	
	public void onCommitAction(View v) {
		EditText contentV = (EditText) findViewById(R.id.content_view);
		EditText mailV = (EditText) findViewById(R.id.email_view);
		EditText phoneV = (EditText) findViewById(R.id.phone_view);
		String content = contentV.getText().toString();
		String email = mailV.getText().toString();
		String phone = phoneV.getText().toString();
		if (TextUtils.isEmpty(content)) {
			showToast(R.string.input_content_tip);
			return;
		}
		
		if (TextUtils.isEmpty(email)) {
			showToast(R.string.feed_back_email);
			return;
		}
		
		if (TextUtils.isEmpty(phone)) {
			showToast(R.string.feed_back_phone);
			return;
		}
		
		commit(content, email, phone);
	}
	
	private void commit(String content, String email, String phone) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", PreferUtil.getUid());
		param.put("content", content);
		param.put("email", email);
		param.put("mobile", phone);
		
		HttpAsyncTask http = new HttpAsyncTask("feed_back") {
			@Override
			public void onStart(String taskId) {
				showLoadingDialog(R.string.commiting);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				cancelLoadingDialog();
				JsonMap data = JsonMap.parseJson(response);
				if (data != null) {
					showToast(R.string.commit_ok);
					finish();
				} else {
					showToast(R.string.commit_fail);
				}
			}
		};
		http.setDebug(false);
		if (TextUtils.equals(CONST.SERVER_SWITHER, "0")) {
			http.excute(HNApp.HOST_CLOUD + "/decision-admin/feedback/send", param);
		}else {
			http.excute(HNApp.HOST_LOCAL + "/decision-admin/feedback/send", param);
		}
//		http.excute(HNApp.HOST + "/decision-admin/feedback/send", param);
	}
}
