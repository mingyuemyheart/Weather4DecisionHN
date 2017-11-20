package com.pmsc.weather4decision.phone.hainan;

import android.app.Application;

import com.igexin.sdk.PushManager;
import com.pmsc.weather4decision.phone.hainan.service.DemoIntentService;
import com.pmsc.weather4decision.phone.hainan.service.DemoPushService;


/**
 * Depiction: 软件入口
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月11日 下午6:05:58
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class HNApp extends Application {
	public final static String HOST_LOCAL = "http://59.50.130.88:8888/decision-api/api/Json";
	public final static String HOST_CLOUD = "http://hnjc.tianqi.cn:8080/decision-api/api/Json";
	private static HNApp       appContext;

	public static HNApp getInstance() {
		return appContext;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;

		//初始化个推
		PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
	}
	
}
