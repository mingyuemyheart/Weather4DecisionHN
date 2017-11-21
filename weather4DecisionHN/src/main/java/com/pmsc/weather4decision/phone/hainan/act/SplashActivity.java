package com.pmsc.weather4decision.phone.hainan.act;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.lib.data.CONST;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.CustomHttpClient;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;


/**
 * Depiction: 闪屏界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月13日 下午4:33:38
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class SplashActivity extends AbsLoginActivity {
	
	private String server_url = "http://hnjc.tianqi.cn:8080/hnversion/getversion";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		SharedPreferences sp = getSharedPreferences("CLOUDOR169", Context.MODE_PRIVATE);
		CONST.SERVER_SWITHER = sp.getString("flag", "0");
		
		asyncQuery(server_url);
		
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
	 * 异步请求
	 */
	private void asyncQuery(String requestUrl) {
		HttpAsyncTask task = new HttpAsyncTask();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTask() {
		}

		@Override
		protected String doInBackground(String... url) {
			String result = null;
			if (method.equalsIgnoreCase("POST")) {
				result = CustomHttpClient.post(url[0], nvpList);
			} else if (method.equalsIgnoreCase("GET")) {
				result = CustomHttpClient.get(url[0]);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String requestResult) {
			super.onPostExecute(requestResult);
			if (requestResult != null) {
				try {
					JSONObject object = new JSONObject(requestResult);
					if (object != null) {
						if (!object.isNull("result")) {
							CONST.SERVER_SWITHER = object.getString("result");
							SharedPreferences sp = getSharedPreferences("CLOUDOR169", Context.MODE_PRIVATE);
							Editor editor = sp.edit();
							editor.putString("flag", object.getString("result"));
							editor.commit();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			String account = PreferUtil.getUserName();
			String passwd = PreferUtil.getPassword();
			if (TextUtils.isEmpty(account) || TextUtils.isEmpty(passwd)) {
				enterApp(LoginActivity.class);
			} else {
				login(account, passwd);
			}
		}

		@SuppressWarnings("unused")
		private void setParams(NameValuePair nvp) {
			nvpList.add(nvp);
		}

		private void setMethod(String method) {
			this.method = method;
		}

		private void setTimeOut(int timeOut) {
			CustomHttpClient.TIME_OUT = timeOut;
		}

		/**
		 * 取消当前task
		 */
		@SuppressWarnings("unused")
		private void cancelTask() {
			CustomHttpClient.shuttdownRequest();
			this.cancel(true);
		}
	}
}