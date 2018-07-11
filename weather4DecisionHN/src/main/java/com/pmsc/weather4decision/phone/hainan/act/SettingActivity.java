package com.pmsc.weather4decision.phone.hainan.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.lib.app.MyApplication;
import com.android.lib.data.CONST;
import com.google.bitmapcache.ImageCache;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.AutoUpdateUtil;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;

import java.io.File;

public class SettingActivity extends AbsLoginActivity implements View.OnClickListener{
	
	private RelativeLayout reSwitch;
	private TextView tvUsername,tvVersion,tvSwitch,tvNews;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_setting);
		initWidget();
	}

	private void initWidget() {
		tvUsername = (TextView) findViewById(R.id.name_view);
		tvVersion = (TextView) findViewById(R.id.version_view);
		tvSwitch = (TextView) findViewById(R.id.tvSwitch);
		reSwitch = (RelativeLayout) findViewById(R.id.reSwitch);
		reSwitch.setOnClickListener(this);
		tvNews = (TextView) findViewById(R.id.tvNews);
		tvNews.setOnClickListener(this);

		tvUsername.setText(PreferUtil.getUserName());
		tvVersion.setText(getVersionName());

		if (TextUtils.equals(CONST.SERVER_SWITHER, "0")) {
			tvSwitch.setText(getString(R.string.cloud_server));
		}else {
			tvSwitch.setText(getString(R.string.local_server));
		}

	}
	
	public void onLeftButtonAction(View v) {
		super.onBackPressed();
	}
	
	public void onCityAction(View v) {
		openActivity(CityActivity.class, null);
		finish();
	}
	
	public void onCleanAction(View v) {
		//清理缓存
		showLoadingDialog(R.string.cleaning);
		clearCache(getApplicationContext());
		cancelLoadingDialog();
		showToast(R.string.clean_ok);
	}

	public static void clearCache(final Context context) {
		String http = ImageCache.getDiskCacheDir(context, "http").getAbsolutePath();
		String pdf = ImageCache.getDiskCacheDir(context, "pdf").getAbsolutePath();
		String json = ImageCache.getDiskCacheDir(context, "json").getAbsolutePath();
		final String[] dirs = {
				http,
				pdf,
				json
		};
		new Thread() {
			@Override
			public void run() {
				for (String dir : dirs) {
					File f = new File(dir);
					if (f != null && f.isDirectory()) {
						File[] files = f.listFiles();
						for (File child : files) {
							if (child.isFile()) {
								child.delete();
							}
						}
					}
				}
			}
		}.start();
	}
	
	public void onFeedbackAction(View v) {
		openActivity(FeedbackActivity.class, null);
	}
	
	public void onAboutAction(View v) {
		openActivity(AboutUsActivity.class, null);
	}
	
	public void onUpdateAction(View v) {
		AutoUpdateUtil.checkUpdate(SettingActivity.this, SettingActivity.this, "39", getString(R.string.app_name), false);
		
//		//启动友盟检查更新
//		UmengUpdateAgent.forceUpdate(getApplicationContext());
//		UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_DIALOG);
//		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//			@Override
//			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
//				switch (updateStatus) {
//					case UpdateStatus.Yes: // has update
//						UmengUpdateAgent.showUpdateDialog(getApplicationContext(), updateInfo);
//						break;
//					case UpdateStatus.No: // has no update
//						showToast(R.string.no_update);
//						break;
//					case UpdateStatus.NoneWifi: // none wifi
//						showToast(R.string.no_wifi);
//						break;
//					case UpdateStatus.Timeout: // time out
//						showToast(R.string.update_time_out);
//						break;
//				}
//			}
//		});
	}
	
	public void onLogoutAction(View v) {
		PreferUtil.saveUserName("");
		PreferUtil.savePassword("");
		
		openActivity(LoginActivity.class, null);
		finish();
		MyApplication.destoryActivity(CONST.MainActivity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.reSwitch:
				if (TextUtils.equals(CONST.SERVER_SWITHER, "0")) {
					CONST.SERVER_SWITHER = "1";
					tvSwitch.setText(R.string.local_server);
				}else {
					CONST.SERVER_SWITHER = "0";
					tvSwitch.setText(R.string.cloud_server);
				}

				MyApplication.destoryActivity(CONST.MainActivity);
				String account = PreferUtil.getUserName();
				String passwd = PreferUtil.getPassword();
				if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(passwd)) {
					login(account, passwd);
				}
				finish();
				break;
			case R.id.tvNews:
				startActivity(new Intent(SettingActivity.this, PushNewsActivity.class));
				break;
		}
	}
}
