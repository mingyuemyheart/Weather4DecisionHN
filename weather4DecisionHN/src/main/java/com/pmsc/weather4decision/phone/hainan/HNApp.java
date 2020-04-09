package com.pmsc.weather4decision.phone.hainan;

import android.app.Application;

import com.igexin.sdk.PushManager;
import com.pmsc.weather4decision.phone.hainan.service.DemoIntentService;
import com.pmsc.weather4decision.phone.hainan.service.DemoPushService;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

public class HNApp extends Application {

	public final static String HOST_LOCAL = "http://59.50.130.88:8888/decision-api/api/Json";
	public final static String HOST_CLOUD = "http://hnjc.tianqi.cn:8080/decision-api/api/Json";
	private static HNApp appContext;

	public static HNApp getInstance() {
		return appContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;

		//umeng分享的平台注册
		UMConfigure.init(this, "58aba143677baa01fe0003ab", "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
		PlatformConfig.setWeixin("wx326e03490187f8db", "faf3d4de00910be263a6eb4ed223d3d0");
		PlatformConfig.setQQZone("1107769691", "VU9E4fgQfDGcRorR");
		UMConfigure.setLogEnabled(true);

		//初始化个推
		PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
	}

}
