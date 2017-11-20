package com.android.lib.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.android.lib.http.OnHttpListener;
import com.android.lib.util.LogUtil;
import com.pmsc.weather4decision.phone.hainan.util.SystemStatusManager;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;


/**
 * Depiction:
 * <p/>
 * Author: Kevin Lynn
 * <p/>
 * Create Date：2014年6月3日 下午4:14:31
 * <p/>
 * Modify:
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class BaseActivity extends FragmentActivity {
	private Dialog              loadingDialog;
	private boolean             isShowDialog = true;
	private Toast               toast;
	
	private Map<String, String> params       = new HashMap<String, String>();
	private HttpAsyncTask       http;
	private OnHttpListener      onHttpListener;
	private boolean             isPost       = false;
	private boolean             debug        = false;
	private Handler             handler      = new Handler();
	private boolean             cancelable   = true;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setTranslucentStatus();
		loadingDialog = new Dialog(this);
		loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		loadingDialog.setCancelable(cancelable);
	}

	/**
	 * 设置状态栏背景状态
	 */
	@SuppressLint("InlinedApi")
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(0);// 状态栏无背景
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName());
		MobclickAgent.onResume(this);
		LogUtil.d(this, "onResume()-->"+getClass().getSimpleName());
	}
	
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName());
		MobclickAgent.onPause(this);
		LogUtil.d(this, "onPause()-->"+getClass().getSimpleName());
	}
	
	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		loadingDialog.setCancelable(cancelable);
	}
	
	public HttpAsyncTask getHttp() {
		return http;
	}
	
	public void showLoadingDialog(int resId) {
		showLoadingDialog(getString(resId));
	}
	
	public void showLoadingDialog(String msg) {
		LinearLayout view = new LinearLayout(getApplicationContext());
		
		int padding = dip2px(10);
		view.setPadding(padding, padding, padding, padding);
		view.setOrientation(LinearLayout.HORIZONTAL);
		view.setGravity(Gravity.CENTER_VERTICAL);
		view.setBackgroundColor(Color.BLACK);
		ProgressBar bar = new ProgressBar(getApplicationContext());
		bar.setLayoutParams(new LayoutParams(dip2px(40), dip2px(40)));
		view.addView(bar);
		
		TextView msgView = new TextView(getApplicationContext());
		msgView.setPadding(padding, 0, padding / 2, 0);
		msgView.setText(msg);
		msgView.setTextSize(20f);
		msgView.setTextColor(Color.WHITE);
		view.addView(msgView);
		loadingDialog.setContentView(view);
		
		loadingDialog.show();
	}
	
	public void cancelLoadingDialog() {
		if (loadingDialog != null) {
			loadingDialog.cancel();
		}
	}
	
	public void showToast(String msg) {
		if (toast != null) {
			toast.cancel();
		}
		try {
			toast = new Toast(getApplicationContext());
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			TextView msgView = new TextView(getApplicationContext());
			msgView.setPadding(20, 10, 20, 10);
			msgView.setText(msg);
			msgView.setTextSize(20f);
			msgView.setTextColor(Color.WHITE);
			msgView.setBackgroundResource(android.R.drawable.toast_frame);
			toast.setView(msgView);
			toast.show();
		} catch (Exception e) {
		}
	}
	
	public void showToast(int strRes) {
		showToast(getString(strRes));
	}
	
	public void dismissToast() {
		if (toast != null) {
			toast.cancel();
		}
	}
	
	/**
	 * 打开新的activity
	 *
	 * @param activity
	 * @param bundle
	 */
	public void openActivity(Class<?> activity, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, activity);
		if (null != bundle) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 *
	 * @param dpValue
	 *            dp
	 * @return px
	 */
	public int dip2px(float dpValue) {
		final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	//以下是和网络请求相关的方法
	
	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}
	
	/**
	 * @param debug
	 *            the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean isPost() {
		return isPost;
	}
	
	public void setPost(boolean isPost) {
		this.isPost = isPost;
	}
	
	/**
	 * @return the isShowDialog
	 */
	public boolean isShowDialog() {
		return isShowDialog;
	}
	
	/**
	 * @param isShowDialog
	 *            the isShowDialog to set
	 */
	public void setShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}
	
	public void put(String key, String value) {
		params.put(key, value);
	}
	
	public void request(String url) {
		request(url, "");
	}
	
	public void request(String url, String tag) {
		http = new HttpAsyncTask(tag) {
			@Override
			public void onStart(String taskId) {
				BaseActivity.this.onStart(taskId);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				BaseActivity.this.onFinish(response, taskId);
			}
		};
		http.setDebug(debug);
		http.excute(url, "", isPost ? "POST" : "GET");
	}
	
	public void request(String url, String params, String tag) {
		http = new HttpAsyncTask(tag) {
			@Override
			public void onStart(String taskId) {
				BaseActivity.this.onStart(taskId);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				BaseActivity.this.onFinish(response, taskId);
			}
		};
		http.setDebug(debug);
		http.excute(url, params, isPost ? "POST" : "GET");
	}
	
	public void request(String url, Map<String, String> params) {
		request(url, params, "");
	}
	
	public void request(String url, Map<String, String> params, String tag) {
		http = new HttpAsyncTask(tag) {
			@Override
			public void onStart(String taskId) {
				BaseActivity.this.onStart(taskId);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				BaseActivity.this.onFinish(response, taskId);
			}
		};
		http.setDebug(debug);
		http.excute(url, params, isPost ? "POST" : "GET");
	}
	
	public void cancelRequest() {
		params.clear();
		if (http != null) {
			http.cancel();
		}
	}
	
	public void onStart(String taskId) {
		if (onHttpListener != null) {
			onHttpListener.onStart(taskId);
		} else {
			if (isShowDialog) {
				showLoadingDialog("加载中....");
			}
		}
	}
	
	public void onFinish(String response, String taskId) {
		if (http != null) {
			http = null;
		}
		params.clear();
		cancelLoadingDialog();
		JsonMap data = JsonMap.parseJson(response);
		onFinish(data, taskId);
		if (onHttpListener != null) {
			onHttpListener.onFinish(data, response, taskId);
		}
	}
	
	public void onFinish(JsonMap data, String tag) {
		params.clear();
	}
	
	public String getVersionName() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0";
	}
	
	/**
	 * @return the onHttpListener
	 */
	public OnHttpListener getOnHttpListener() {
		return onHttpListener;
	}
	
	/**
	 * @param onHttpListener
	 *            the onHttpListener to set
	 */
	public void setOnHttpListener(OnHttpListener onHttpListener) {
		this.onHttpListener = onHttpListener;
	}
	
	public void post(Runnable action) {
		handler.post(action);
	}
	
	public void post(Runnable action, long delayMillis) {
		handler.postDelayed(action, delayMillis);
	}
}
