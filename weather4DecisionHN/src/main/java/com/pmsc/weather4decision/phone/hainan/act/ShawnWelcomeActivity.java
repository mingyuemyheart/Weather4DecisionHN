package com.pmsc.weather4decision.phone.hainan.act;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.lib.app.MyApplication;
import com.android.lib.data.CONST;
import com.google.gson.JsonObject;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 闪屏页
 */
public class ShawnWelcomeActivity extends AbsLoginActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_welcome);
		okHttpTheme();
		OkHttpServer();
	}

	/**
	 * 获取服务器信息
	 */
	private void OkHttpServer() {
		SharedPreferences sp = getSharedPreferences("CLOUDOR169", Context.MODE_PRIVATE);
		CONST.SERVER_SWITHER = sp.getString("flag", "0");
		final String url = "http://hnjc.tianqi.cn:8080/hnversion/getversion";
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                doLogin();
                            }
                        });
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
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("result")) {
											CONST.SERVER_SWITHER = object.getString("result");
											SharedPreferences sp = getSharedPreferences("CLOUDOR169", Context.MODE_PRIVATE);
											Editor editor = sp.edit();
											editor.putString("flag", object.getString("result"));
											editor.apply();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

                                doLogin();
							}
						});
					}
				});
			}
		}).start();
	}

	private void doLogin() {
        String account = PreferUtil.getUserName();
        String passwd = PreferUtil.getPassword();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(passwd)) {
            enterApp(LoginActivity.class);
        } else {
            login(account, passwd);
        }
    }
	
	public void onLoginFail() {
		enterApp(LoginActivity.class);
	}

	private void enterApp(final Class<?> cls) {
		post(new Runnable() {
			@Override
			public void run() {
				openActivity(cls, null);
				finish();
			}
		}, 1000);
	}

	/**
	 * 获取主题
	 */
	private void okHttpTheme() {
		final String url = "http://decision-admin.tianqi.cn/Home/work2019/hn_theme_flag";
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(@NonNull Call call, @NonNull IOException e) {

					}

					@Override
					public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("top_img")) {
											MyApplication.setTop_img(obj.getString("top_img"));
										}
										if (!obj.isNull("top_img_url")) {
											MyApplication.setTop_img_url(obj.getString("top_img_url"));
										}
										if (!obj.isNull("top_img_title")) {
											MyApplication.setTop_img_title(obj.getString("top_img_title"));
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						});
					}
				});
			}
		}).start();
	}

}
