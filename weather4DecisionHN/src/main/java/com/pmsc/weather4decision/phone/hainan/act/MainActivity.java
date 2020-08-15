package com.pmsc.weather4decision.phone.hainan.act;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.android.lib.util.AssetFile;
import com.android.lib.util.AuthorityUtil;
import com.android.lib.util.DeviceInfo;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.MainAdapter;
import com.pmsc.weather4decision.phone.hainan.http.FetchWeather;
import com.pmsc.weather4decision.phone.hainan.http.FetchWeather.OnFetchWeatherListener;
import com.pmsc.weather4decision.phone.hainan.util.AutoUpdateUtil;
import com.pmsc.weather4decision.phone.hainan.util.CodeParse;
import com.pmsc.weather4decision.phone.hainan.util.CommonUtil;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.pmsc.weather4decision.phone.hainan.util.SecretUrlUtil;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.pmsc.weather4decision.phone.hainan.util.WeatherUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AbsDrawerActivity implements AMapLocationListener, OnClickListener, OnFetchWeatherListener {

	private Context mContext = null;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private GridView gridView;
	private MainAdapter mainAdapter;
	private TextView tempView,windView,shiduView,aqiView,weatherView,daysView,pubTimeView;
	private String cityId, cityName;
	private RelativeLayout reFact;
	private LinearLayout llContainer, llContainer2;
	private int height = 0;
	private double lat = 0, lng = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initChannelData();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		checkMultiAuthority();
	}

	private void init() {
		if (!CommonUtil.isLocationOpen(mContext)) {
			locationDialog(mContext);
		}else {
			commonControl();
		}
	}

	/**
	 * 判断navigation是否显示，重新计算页面布局
	 */
	private void onLayoutMeasure() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;

		int statusBarHeight = -1;//状态栏高度
		//获取status_bar_height资源的ID
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			statusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}
		titleBar.measure(0, 0);
		int height1 = titleBar.getMeasuredHeight();
		reFact.measure(0, 0);
		int height2 = reFact.getMeasuredHeight();
		int height3 = 0;

		if (mainAdapter != null) {
			mainAdapter.height = height-statusBarHeight-height1-height2-height3;
			mainAdapter.notifyDataSetChanged();
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
        AutoUpdateUtil.checkUpdate(MainActivity.this, MainActivity.this, "39", getString(R.string.app_name), true);
        SettingActivity.clearCache(mContext);

		rightButton.setBackgroundResource(R.drawable.icon_my);
		rightButton.setVisibility(View.VISIBLE);
		setTitle(PreferUtil.getCurrentCity());
		reFact = (RelativeLayout) findViewById(R.id.reFact);
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

		initGridView();
		OkHttpNewsCount("http://59.50.130.88:8888/decision-admin/push/getpushcount?type=2&uid="+PreferUtil.getUid());
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
	private void initGridView() {
		mainAdapter = new MainAdapter(mContext, allChannelDataList);
		gridView.setAdapter(mainAdapter);
		onLayoutMeasure();
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
			bundle.putDouble("lat", lat);
			bundle.putDouble("lng", lng);
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
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
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
			lat = amapLocation.getLatitude();
			lng = amapLocation.getLongitude();
			if (amapLocation.getDistrict().contains("洋浦") || amapLocation.getAddress().contains("洋浦")) {
				setTitle("洋浦经济开发区");
				cityName = "洋浦经济开发区";
				cityId = "101310205_y";
				if (!TextUtils.isEmpty(cityId)) {
					PreferUtil.saveCurrentCityId(cityId);
					getAllWeather();
					setPushTags();
					registerDevice(lat, lng);
				}
			} else {
				setTitle(amapLocation.getDistrict());
				cityName = amapLocation.getDistrict();
				OkHttpGeo(lng, lat);
			}
        }
	}

	/**
	 * 获取城市id
	 * @param lng
	 * @param lat
	 */
	private void OkHttpGeo(final double lng, final double lat) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.geo(lng, lat)).build(), new Callback() {
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
								cancelLoadingDialog();
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("geo")) {
											JSONObject geoObj = obj.getJSONObject("geo");
											if (!geoObj.isNull("id")) {
												cityId = geoObj.getString("id");
												if (!TextUtils.isEmpty(cityId)) {
													PreferUtil.saveCurrentCityId(cityId);
													getAllWeather();
													setPushTags();
													registerDevice(lat, lng);
												}
											}
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
	
	//获取所有天气信息
	private void getAllWeather() {
		String cityId = PreferUtil.getCurrentCityId();
		FetchWeather fetch = new FetchWeather();
		fetch.setOnFetchWeatherListener(this);
		fetch.perform(cityId, "all");
	}
	
	@Override
	public void onFetchWeather(final String result) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cancelLoadingDialog();

				List<JsonMap> datas = JsonMap.parseJsonArray(result);
				if (datas != null && datas.size() > 0) {
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
							aqiView.setText("空气质量" + " "+ WeatherUtil.getAqi(mContext, Integer.valueOf(aqi)) + " " + aqi);
						}
					}

					//加载七天预报信息
					JsonMap forecast = datas.get(1).getMap("f");

					if(forecast==null){
						return;
					}

					//七天预报信息的发布时间
					time_7 = forecast.getString("f0");
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm");
					SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
					int index = 0;
					try {
						String f0 = sdf3.format(sdf2.parse(time_7));
						long time = sdf3.parse(f0).getTime();
						long currentDate = sdf3.parse(sdf3.format(new Date())).getTime();
						if (currentDate > time) {
							index = 1;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

					List<JsonMap> forecastList = forecast.getListMap("f1");
					if(forecastList==null){
						return;
					}

					daysView.setTag(forecastList.toString());
					daysView.setEnabled(true);

					JsonMap fisrtDay = forecastList.get(index);
					String todayW = CodeParse.parseWeatherCode(fisrtDay.getString("fa"));//	今天白天天气
					String tonightW = CodeParse.parseWeatherCode(fisrtDay.getString("fb"));//今天夜晚天气
					String todayT = fisrtDay.getString("fc") + "°C";//今天白天温度
					String tonightT = fisrtDay.getString("fd") + "°C";//今天夜晚温度

					String weather = "";
					if (TextUtils.equals(todayW, tonightW)) {
						weather = todayW;
					}else {
						weather = todayW+"转"+tonightW;
					}
					String temperature = todayT+"~"+tonightT;
					weatherView.setText(weather + " " + temperature);

//			//加载预警信息
//			List<JsonMap> warnList = datas.get(2).getListMap("w");
//			if (warnList == null || warnList.size() == 0) {
//				//表示没有预警
//				warningView.setVisibility(View.GONE);
//				return;
//			}
//			//加载预警信息
//			loadWarningData(warnList);
				}

				getHNWarning();
			}
		});
	}

	private void setPushTags() {
		String user = "decisionUser" + PreferUtil.getUid();
		String group = "decisionUser" + PreferUtil.getUserGroup();
		String five = "weatherCity" + PreferUtil.getCurrentCityId().substring(0, 5);
		String seven = "weatherCity" + PreferUtil.getCurrentCityId().substring(0, 7);
		String nine = "weatherCity" + PreferUtil.getCurrentCityId();

//		String user = PreferUtil.getUid();
//		if (!TextUtils.isEmpty(user) && user.length() > 20) {
//			user = user.substring(user.length()-20, user.length());
//		}
//		String group = PreferUtil.getUserGroup();
//		if (!TextUtils.isEmpty(group) && group.length() > 20) {
//			group = group.substring(group.length()-20, group.length());
//		}
//		String five = PreferUtil.getCurrentCityId().substring(0, 5);
//		String seven = PreferUtil.getCurrentCityId().substring(0, 7);
//		String nine = PreferUtil.getCurrentCityId();
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
						String result = response.body().string();
						if (!TextUtils.isEmpty(result)) {
							JsonMap data = JsonMap.parseJson(result);
							if (data != null) {
								if (!data.getBoolean("status")) {
									//失败，重试
									return;
								}
							} else {
								//失败，重试
							}
						}
					}
				});
			}
		}).start();
	}
	
	private void getHNWarning() {
		String cityId = PreferUtil.getCurrentCityId();
		if (!cityId.startsWith("10131")) {
			//不是海南的城市，直接返回
			llContainer.setVisibility(View.VISIBLE);
			llContainer2.setVisibility(View.GONE);
			return;
		}
		final String url = "http://59.50.130.88:8888/decision-admin/alarm/cityAlarm/"+cityId;
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {

					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						if (!TextUtils.isEmpty(result)) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									JsonMap data = JsonMap.parseJson(result);
									if (data != null && data.containsKey("w") && data.getListMap("w") != null && data.getListMap("w").size() > 0) {
										loadWarningData(data.getListMap("w"));
									}
								}
							});
						}
					}
				});
			}
		}).start();
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
	private void OkHttpNewsCount(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
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
						});
					}
				});
			}
		}).start();
	}

	/**
	 * 申请定位权限
	 */
	private void checkMultiAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			init();
		}else {
			AuthorityUtil.deniedList.clear();
			for (String permission : AuthorityUtil.allPermissions) {
				if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
					AuthorityUtil.deniedList.add(permission);
				}
			}
			if (AuthorityUtil.deniedList.isEmpty()) {//所有权限都授予
				init();
			}else {
				String[] permissions = AuthorityUtil.deniedList.toArray(new String[AuthorityUtil.deniedList.size()]);//将list转成数组
				ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_MULTI);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_MULTI:
				if (grantResults.length > 0) {
					boolean isAllGranted = true;//是否全部授权
					for (int gResult : grantResults) {
						if (gResult != PackageManager.PERMISSION_GRANTED) {
							isAllGranted = false;
							break;
						}
					}
					if (isAllGranted) {//所有权限都授予
						init();
					}else {//只要有一个没有授权，就提示进入设置界面设置
						AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的位置权限、设备信息权限、存储权限，是否前往设置？");
					}
				}else {
					for (String permission : permissions) {
						if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
							AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的位置权限、设备信息权限、存储权限，是否前往设置？");
							break;
						}
					}
				}
				break;
		}
	}

}
