package com.pmsc.weather4decision.phone.hainan.act;

import android.os.Bundle;
import android.text.TextUtils;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.CONST;
import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 登录和闪屏的父类，便于实现自动登录
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
		String json = param.toString();

		final RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url;
				if (TextUtils.equals(CONST.SERVER_SWITHER, "0")) {
					url = HNApp.HOST_CLOUD;
				}else {
					url = HNApp.HOST_LOCAL;
				}
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {

					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									cancelLoadingDialog();
									JsonMap data = JsonMap.parseJson(result);
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
							}
						});
					}
				});
			}
		}).start();

	}

	public void onLoginFail() {
	}

}
