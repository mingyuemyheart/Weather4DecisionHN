package com.pmsc.weather4decision.phone.hainan.act;

import android.os.Bundle;
import android.text.TextUtils;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.CONST;
import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.umeng.analytics.MobclickAgent;


/**
 * Depiction: 登录和闪屏的父类，便于实现自动登录
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月7日 下午4:45:07
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsLoginActivity extends BaseActivity {
	
	protected void login(final String account, final String passwd) {
		JsonMap param = new JsonMap();
		param.put("command", "6001");
		
		JsonMap object = new JsonMap();
		object.put("username", account);
		object.put("password", passwd);
		object.put("type", "1");
		param.put("object", object);
		
		HttpAsyncTask http = new HttpAsyncTask("login") {
			@Override
			public void onStart(String taskId) {
				showLoadingDialog(R.string.loading);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				cancelLoadingDialog();
				JsonMap data = JsonMap.parseJson(response);
				if (data != null) {
					if (!data.getBoolean("status")) {
						showToast(R.string.passwd_error_tip);
						onLoginFail();
						return;
					}
					
					PreferUtil.saveUserName(data.getMap("object").getString("username"));
					PreferUtil.savePassword(passwd);
					PreferUtil.saveRole(data.getMap("object").getString("role"));
					PreferUtil.saveUserGroup(data.getMap("object").getString("userGroup"));
					PreferUtil.saveUid(data.getMap("object").getString("uid"));
					
					MobclickAgent.onProfileSignIn(PreferUtil.getUid());
					
					Bundle bundle = new Bundle();
					bundle.putString("data", data.getMap("object").toString());
					openActivity(MainActivity.class, bundle);
					finish();
				} else {
					showToast(R.string.login_fail);
					onLoginFail();
				}
			}
		};
		http.setDebug(false);
		
		if (TextUtils.equals(CONST.SERVER_SWITHER, "0")) {
			http.excute(HNApp.HOST_CLOUD, param.toString(), "POST");
		}else {
			http.excute(HNApp.HOST_LOCAL, param.toString(), "POST");
		}
	}
	
	public void onLoginFail() {
	}
}
