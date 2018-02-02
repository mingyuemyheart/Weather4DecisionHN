package com.pmsc.weather4decision.phone.hainan.act;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.android.lib.app.MyApplication;
import com.android.lib.data.CONST;
import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.android.lib.util.AssetFile;
import com.android.lib.util.DeviceInfo;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.MainAdapter;
import com.pmsc.weather4decision.phone.hainan.db.ParseCityTask;
import com.pmsc.weather4decision.phone.hainan.http.FetchWeather;
import com.pmsc.weather4decision.phone.hainan.http.FetchWeather.OnFetchWeatherListener;
import com.pmsc.weather4decision.phone.hainan.util.AutoUpdateUtil;
import com.pmsc.weather4decision.phone.hainan.util.CacheData;
import com.pmsc.weather4decision.phone.hainan.util.CodeParse;
import com.pmsc.weather4decision.phone.hainan.util.CommonUtil;
import com.pmsc.weather4decision.phone.hainan.util.CustomHttpClient;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.pmsc.weather4decision.phone.hainan.util.WeatherUtil;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.listener.AsyncResponseHandler;


/**
 * Depiction: 首页主界面
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
public class MainActivity extends AbsDrawerActivity implements AMapLocationListener, OnClickListener, OnFetchWeatherListener {

	private Context mContext = null;
    private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
    private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private GridView        gridView;
	private TextView        tempView;
	private TextView        windView;
	private TextView        shiduView;
	private TextView        aqiView;
	private TextView        weatherView;
	private TextView        daysView;
	private TextView        pubTimeView;
	
	private int             pubTime;      //七天预报发布时间，最后四位
	private String cityId, cityName;
	private RelativeLayout top = null;
	private int height = 0;
	private LinearLayout llContainer, llContainer2;

	private RelativeLayout reMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		new ParseCityTask().run();
		initChannelData();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;

		if (!CommonUtil.isLocationOpen(mContext)) {
			locationDialog(mContext);
		}else {
			commonControl();
		}
	}

	private void locationDialog(Context context) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_location, null);
		LinearLayout llNegative = (LinearLayout) view.findViewById(R.id.llNegative);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);

		final Dialog dialog = new Dialog(context, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.show();

		llNegative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				commonControl();
			}
		});

		llPositive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			switch (requestCode) {
				case 1:
					commonControl();
					break;

				default:
					break;
			}
		}
	}

	private void commonControl() {
		MyApplication.addDestoryActivity(MainActivity.this, CONST.MainActivity);
		showLoadingDialog(R.string.loading);
		initWidget();
	}

	private void initWidget() {
        AutoUpdateUtil.checkUpdate(MainActivity.this, "39", getString(R.string.app_name), true);
        SettingActivity.clearCache(mContext);

		reMain = (RelativeLayout) findViewById(R.id.reMain);
		reMain.setVisibility(View.VISIBLE);
		rightButton.setBackgroundResource(R.drawable.icon_my);
		rightButton.setVisibility(View.VISIBLE);
		setTitle(PreferUtil.getCurrentCity());
		top = (RelativeLayout) findViewById(R.id.top);
		gridView = (GridView) findViewById(R.id.grid_view);
		tempView = (TextView) findViewById(R.id.temp_tv);
		windView = (TextView) findViewById(R.id.wind_tv);
		shiduView = (TextView) findViewById(R.id.shidu_tv);
		aqiView = (TextView) findViewById(R.id.aqi_tv);
		weatherView = (TextView) findViewById(R.id.weather_tv);
		daysView = (TextView) findViewById(R.id.days_tv);
		pubTimeView = (TextView) findViewById(R.id.pub_time_view);
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		llContainer2 = (LinearLayout) findViewById(R.id.llContainer2);
		daysView.setOnClickListener(this);
		pubTimeView.setOnClickListener(this);

		Drawable iconRefresh = getResources().getDrawable(R.drawable.icon_refresh);
		iconRefresh.setBounds(0, 0, iconRefresh.getMinimumWidth() / 2, iconRefresh.getMinimumHeight() / 2);
		pubTimeView.setCompoundDrawables(null, null, iconRefresh, null);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;

		//加载首页天气信息缓存数据
		String homeData = CacheData.getHomeData();
		if (!TextUtils.isEmpty(homeData)) {
			onFetchWeather("cache", homeData);
		}

		asyncNewsCount("http://59.50.130.88:8888/decision-admin/push/getpushcount?type=2&uid="+PreferUtil.getUid());

		if (!CommonUtil.isLocationOpen(mContext)) {
			PreferUtil.saveCurrentProvince("海南省");
			PreferUtil.saveCurrentCity("海口市");
			PreferUtil.saveCurrentDistrict("美兰区");

			setTitle("海口");
			cityName = "海口";
			cityId = "101310101";
			if (!TextUtils.isEmpty(cityId)) {
				PreferUtil.saveCurrentCityId(cityId);
				getAllWeather();
				setPushTags();
			}
		}else {
			startLocation();
		}

		loadGridData();
	}
	
    /**
     * 强制帮用户打开GPS 
     * @param context 
     */  
	private static final void openGPS(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	
	private void initChannelData() {
		JsonMap data = JsonMap.parseJson(getIntent().getStringExtra("data"));
		allChannelDataList = data.getListMap("channels");
		if (allChannelDataList == null || allChannelDataList.size() == 0) {
			showToast(R.string.loading_fail);
			finish();
			return;
		}
		
		JsonMap home = new JsonMap();
		home.put("id", 0);
		home.put("parent_id", 0);
		home.put("columnType", Utils.HOME);
		home.put("title", getString(R.string.home));
		allChannelDataList.add(0, home);
		
		JsonMap city = new JsonMap();
		city.put("id", 1);
		city.put("parent_id", 0);
		city.put("columnType", Utils.CITY);
		city.put("title", getString(R.string.city_manager));
		allChannelDataList.add(1, city);
	}
	
	public void onRightButtonAction(View view) {
		//查看用户基本信息
		openActivity(SettingActivity.class, null);
	}
	
	/**
	 * 加载GridView数据
	 */
	private void loadGridData() {
		titleBar.measure(0, 0);
		int height1 = titleBar.getMeasuredHeight();
		top.measure(0, 0);
		int height2 = top.getMeasuredHeight();

		int statusBarHeight1 = -1;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
		}

		final MainAdapter adapter = new MainAdapter(allChannelDataList, height-height1-height2-statusBarHeight1);
		gridView.setAdapter(adapter);
//		ViewGroup.LayoutParams params = gridView.getLayoutParams();
//		params.height = (int) ((height-height1-height2-statusBarHeight1)/3*Math.ceil((double)(allChannelDataList.size()/3.0f)));
//		gridView.setLayoutParams(params);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PreferUtil.saveMenuCurrentParentIndex(position + 2);
				openChannelView(position + 2, 0, true);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.days_tv) {
			//查看七天天气信息
			Bundle bundle = new Bundle();
			bundle.putString("cityId", cityId);
			bundle.putString("cityName", cityName);
			openActivity(ForecastActivity.class, bundle);
		} else if (v.getId() == R.id.pub_time_view) {
			//刷新当前数据
			showLoadingDialog(R.string.loading);
			getAllWeather();
		}

	}

	/**
	 * 开始定位
	 */
	private void startLocation() {
		mLocationOption = new AMapLocationClientOption();//初始化定位参数
        mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
        mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setMockEnable(true);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();//启动定位
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
        	PreferUtil.saveCurrentProvince(amapLocation.getProvince());
			PreferUtil.saveCurrentCity(amapLocation.getCity());
			PreferUtil.saveCurrentDistrict(amapLocation.getDistrict());
			getWeatherInfo(amapLocation.getLongitude(), amapLocation.getLatitude());

			if (!TextUtils.isEmpty(amapLocation.getStreet())) {
				setTitle(amapLocation.getStreet()+amapLocation.getStreetNum());
				cityName = amapLocation.getStreet()+amapLocation.getStreetNum();
			}else {
				setTitle(amapLocation.getDistrict());
				cityName = amapLocation.getDistrict();
			}
        }
	}
	
	/**
	 * 获取天气数据*/
	private void getWeatherInfo(final double lng, final double lat) {
		WeatherAPI.getGeo(MainActivity.this, String.valueOf(lng), String.valueOf(lat), new AsyncResponseHandler(){
			@Override
			public void onComplete(JSONObject content) {
				super.onComplete(content);
				cancelLoadingDialog();
				if (!content.isNull("geo")) {
					try {
						JSONObject geoObj = content.getJSONObject("geo");
						if (!geoObj.isNull("id")) {
							cityId = geoObj.getString("id");
							if (!TextUtils.isEmpty(cityId)) {
								PreferUtil.saveCurrentCityId(cityId);
								getAllWeather();
								setPushTags();
								registerDevice(lat, lng);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onError(Throwable error, String content) {
				super.onError(error, content);
			}
		});
	}
	
	//获取所有天气信息
	private void getAllWeather() {
		String cityId = PreferUtil.getCurrentCityId();
		FetchWeather fetch = new FetchWeather();
		fetch.setOnFetchWeatherListener(this);
		fetch.perform(cityId, "all");
	}
	
	@Override
	public void onFetchWeather(String tag, String response) {
		if (tag.equalsIgnoreCase("all")) {
			cancelLoadingDialog();
		}
		
		List<JsonMap> datas = JsonMap.parseJsonArray(response);
		if (datas != null && datas.size() > 0) {
			//缓存数据
			if (tag.equalsIgnoreCase("all")) {
				CacheData.cacheHome(response);
			}
			
			//显示实况数据
			JsonMap live = datas.get(0).getMap("l");
			if (live == null) {
				return;
			}
			String temp = live.getString("l1");
			String shidu = live.getString("l2");
			String fl = live.getString("l3");
			String windFl = (TextUtils.isEmpty(fl) || fl.equalsIgnoreCase("0") ? getString(R.string.micro_wind) : fl + getString(R.string.fl));
			String fx = live.getString("l4");
			String dayW = live.getString("l5");
			String skpt = live.getString("l7");
			
			weatherView.setText(CodeParse.parseWeatherCode(dayW));
			tempView.setText(temp+"℃");
			windView.setText(CodeParse.parseWindfxCode(fx) + " " + windFl);
			shiduView.setText("相对湿度"+" "+shidu+"%");
			pubTimeView.setText(getString(R.string.hn_weather_tai, Utils.getDayDate() + skpt));
			
			//显示空气数据
			if (datas.get(4).containsKey("p")) {
				JsonMap air = datas.get(4).getMap("p");
				if (air != null) {
					String aqi = air.getString("p2");
					aqiView.setText("空气质量" + " "+ WeatherUtil.getAqi(this, Integer.valueOf(aqi)) + " " + aqi);
				}
			}
			
			//加载七天预报信息
			JsonMap forecast = datas.get(1).getMap("f");
			
			if(forecast==null){
				return;
			}
			
			//七天预报信息的发布时间
			time_7 = forecast.getString("f0");
			pubTime = Integer.parseInt(time_7.substring(8, 12));
			
			List<JsonMap> forecastList = forecast.getListMap("f1");
			if(forecastList==null){
				return;
			}
			
			daysView.setTag(forecastList.toString());
			daysView.setEnabled(true);
			
			JsonMap fisrtDay = forecastList.get(0);
			JsonMap secondDay = forecastList.get(1);
			String todayW = CodeParse.parseWeatherCode(fisrtDay.getString("fa"));//	今天白天天气
			String tonightW = CodeParse.parseWeatherCode(fisrtDay.getString("fb"));//今天夜晚天气
			String todayT = fisrtDay.getString("fc") + "°C";//今天白天温度
			String tonightT = fisrtDay.getString("fd") + "°C";//今天夜晚温度
			
			String tomorrowDayW = CodeParse.parseWeatherCode(secondDay.getString("fa"));//明天白天天气
//			String tomorrowNightW = CodeParse.parseWeatherCode(secondDay.getString("fb"));//明天夜晚天气
			String tomorrowDayT = secondDay.getString("fc") + "°C";//明天白天温度
//			String tomorrowNightT = secondDay.getString("fd") + "°C";//今天夜晚温度
			
			if (!TextUtils.isEmpty(cityId)) {
				if (cityId.startsWith("10131")) {
					String weather = "";
					if (TextUtils.equals(todayW, tonightW)) {
						weather = todayW;
					}else {
						weather = todayW+"转"+tonightW;
					}
					String temperature = todayT+"~"+tonightT;
					weatherView.setText(weather + " " + temperature);
				}else {
					if (pubTime < 1800) {
						//晚上十八点之前发布的显示今天白天到夜晚,两则相同则不带转字
						String weather = "";
						if (TextUtils.equals(todayW, tonightW)) {
							weather = todayW;
						}else {
							weather = todayW+"转"+tonightW;
						}
						String temperature = todayT+"~"+tonightT;
						weatherView.setText(weather + " " + temperature);
					} else {
						//晚上十八点以后,早上六点之前发的显示今晚到明天上午的天气信息,两则相同则不带转字
//				String weather = tomorrowDayW.equalsIgnoreCase(tomorrowNightW) ? tomorrowDayW : getString(R.string.zhuan, tomorrowDayW, tomorrowNightW);
//				String temperature = getString(R.string.temperature_to, tomorrowDayT, tomorrowNightT);
//				weatherView.setText(weather + " " + temperature);
						String weather = tonightW.equalsIgnoreCase(tomorrowDayW) ? tomorrowDayW : getString(R.string.zhuan, tonightW, tomorrowDayW);
						String temperature = getString(R.string.temperature_to, tonightT, tomorrowDayT);
						weatherView.setText(weather + " " + temperature);
					}
				}
			}
			
//			//加载预警信息
//			List<JsonMap> warnList = datas.get(2).getListMap("w");
//			if (warnList == null || warnList.size() == 0) {
//				//表示没有预警
//				warningView.setVisibility(View.GONE);
//				return;
//			}
//			//加载预警信息
//			loadWarningData(warnList);
		} else {
			if (tag.equalsIgnoreCase("all")) {
				showToast(R.string.loading_fail);
			}
		}

		getHNWarning();
	}

	private void setPushTags() {
//		String user = "decisionUser" + PreferUtil.getUid();
//		String group = "decisionUser" + PreferUtil.getUserGroup();
//		String five = "weatherCity" + PreferUtil.getCurrentCityId().substring(0, 5);
//		String seven = "weatherCity" + PreferUtil.getCurrentCityId().substring(0, 7);
//		String nine = "weatherCity" + PreferUtil.getCurrentCityId();

		String user = PreferUtil.getUid();
		if (!TextUtils.isEmpty(user) && user.length() > 20) {
			user = user.substring(user.length()-20, user.length());
		}
		String group = PreferUtil.getUserGroup();
		if (!TextUtils.isEmpty(group) && group.length() > 20) {
			group = group.substring(group.length()-20, group.length());
		}
		String five = PreferUtil.getCurrentCityId().substring(0, 5);
		String seven = PreferUtil.getCurrentCityId().substring(0, 7);
		String nine = PreferUtil.getCurrentCityId();
		String[] tags = new String[] {user, group, five, seven, nine};
		Tag[] tagParam = new Tag[tags.length];
		for (int i = 0; i < tags.length; i++) {
			Tag t = new Tag();
			//name 字段只支持：中文、英文字母（大小写）、数字、除英文逗号以外的其他特殊符号, 具体请看代码示例
			t.setName(tags[i]);
			tagParam[i] = t;
		}
		PushManager.getInstance().setTag(mContext, tagParam, System.currentTimeMillis() +"");
//		DemoIntentService.onReceiveCommandResult回调函数返回结果
	}
	
	//用来服务端统计用户的登录信息
	private void registerDevice(double lat, double lng) {
		JsonMap param = new JsonMap();
		param.put("command", "6005");
		DeviceInfo device = new DeviceInfo(this);
		JsonMap object = new JsonMap();
		object.put("uid", PreferUtil.getUid());
		object.put("deviceId", device.imei());
		object.put("platformType", "android");
		object.put("osVersion", device.romVersion());
		object.put("softwareVersion", getVersionName());
		object.put("mobileType", device.model());
		object.put("province", PreferUtil.getCurrentProvince());
		object.put("city", PreferUtil.getCurrentCity());
		object.put("district", PreferUtil.getCurrentDistrict());
		object.put("lat", lat+"");
		object.put("lon", lng+"");
		param.put("object", object);
		
		final HttpAsyncTask http = new HttpAsyncTask("register_device") {
			@Override
			public void onStart(String taskId) {
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				JsonMap data = JsonMap.parseJson(response);
				if (data != null) {
					if (!data.getBoolean("status")) {
						//失败，重试
						return;
					}
				} else {
					//失败，重试
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
	
	private void getHNWarning() {
//		String cityId = PreferUtil.getCurrentCity();
		String cityId = PreferUtil.getCurrentCityId();
		if (!cityId.startsWith("10131")) {
			//不是海南的城市，直接返回
			llContainer.setVisibility(View.VISIBLE);
			llContainer2.setVisibility(View.GONE);
			return;
		}
		String url = "http://59.50.130.88:8888/decision-admin/alarm/cityAlarm/";
		
		final HttpAsyncTask http = new HttpAsyncTask("get_hn_warning") {
			@Override
			public void onStart(String taskId) {
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				JsonMap data = JsonMap.parseJson(response);
				if (data != null && data.containsKey("w") && data.getListMap("w") != null && data.getListMap("w").size() > 0) {
					loadWarningData(data.getListMap("w"));
				}
			}
		};
		http.setDebug(false);
		http.excute(url + cityId, "");
	}
	
	private void loadWarningData(List<JsonMap> warnList) {
		llContainer.removeAllViews();
		llContainer2.removeAllViews();
		int size = warnList.size();
		for (int i = 0; i < size; i++) {
			JsonMap warn = warnList.get(i);
			if(warn==null){
				return;
			}

			TextView tvWarning = new TextView(mContext);
			tvWarning.setTag(warn.toString());
			tvWarning.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			tvWarning.setTextColor(Color.WHITE);
			tvWarning.setGravity(Gravity.CENTER_VERTICAL);
			tvWarning.setPadding(30, 0, 0, 0);

			String info = warn.getString("w5");
			String plevel = warn.getString("w4");
			String code = warn.getString("w6");
			String level = warn.getString("w7");

			tvWarning.setText(getString(R.string.warning_home, info + level));

			String iconCode = plevel + code;
			Drawable warnIcon = new AssetFile(this).getDrawable("warning/icon_warning_" + iconCode + ".png");
			warnIcon.setBounds(0, 0, dip2px(16), dip2px(16));
			tvWarning.setCompoundDrawables(warnIcon, null, null, null);

			if (size > 2) {
				llContainer2.addView(tvWarning);
			}else {
				llContainer.addView(tvWarning);
			}

			tvWarning.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//打开预警界面
					Bundle bundle = new Bundle();
					bundle.putString("warning_data", v.getTag().toString());
					openActivity(WarningDetailsActivity.class, bundle);
				}
			});
		}

		if (size > 2) {
			llContainer2.setVisibility(View.VISIBLE);
			llContainer.setVisibility(View.GONE);
		}else {
			llContainer2.setVisibility(View.GONE);
			llContainer.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 获取未读消息数量
	 * @param url
	 */
	private void asyncNewsCount(String url) {
		//异步请求数据
		HttpAsyncTaskNewsCount task = new HttpAsyncTaskNewsCount();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}

	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskNewsCount extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();

		public HttpAsyncTaskNewsCount() {
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
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!TextUtils.isEmpty(result)) {
				try {
					JSONObject obj = new JSONObject(result);
					if (!obj.isNull("count")) {
						int count = obj.getInt("count");
						if (count > 0) {
							rightButton.setBackgroundResource(R.drawable.icon_my_news);
						}else {
							rightButton.setBackgroundResource(R.drawable.icon_my);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
