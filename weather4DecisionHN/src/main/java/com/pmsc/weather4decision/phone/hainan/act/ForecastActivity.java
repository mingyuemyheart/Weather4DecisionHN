package com.pmsc.weather4decision.phone.hainan.act;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants.Language;
import cn.com.weather.listener.AsyncResponseHandler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.lib.app.BaseActivity;
import com.android.lib.http.HttpAsyncTask;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.WeeklyForecastAdapter;
import com.pmsc.weather4decision.phone.hainan.dto.WeatherDto;
import com.pmsc.weather4decision.phone.hainan.util.CodeParse;
import com.pmsc.weather4decision.phone.hainan.util.CommonUtil;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.pmsc.weather4decision.phone.hainan.util.WeatherUtil;
import com.pmsc.weather4decision.phone.hainan.view.CubicView;
import com.pmsc.weather4decision.phone.hainan.view.WeeklyView;

/**
 * 天气预报
 * @author shawn_sun
 *
 */

public class ForecastActivity extends BaseActivity implements OnClickListener, AMapLocationListener {
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private TextView tvLocation = null;
	private TextView tvTime = null;//更新时间
	private ImageView ivPhe = null;//天气显现对应的图标
	private TextView tvPhe = null;
	private TextView tvTemperature = null;
	private TextView tvWind = null;
	private TextView tvHumidity = null;
	private TextView tvAqi = null;
	private TextView tvWeek = null;
	private ImageView ivList = null;
	private ScrollView scrollView = null;
	private ProgressBar progressBar = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
	private int width = 0;
	private LinearLayout llContainer1, llContainer2;
	private ListView mListView = null;//一周预报列表listview
	private WeeklyForecastAdapter mAdapter = null;
	private List<WeatherDto> weeklyList = new ArrayList<WeatherDto>();
	private HorizontalScrollView hScrollView2 = null;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_forecast);
		mContext = this;
		initWidget();
		initListView();
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("天气详情");
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvTime.setFocusable(true);
		tvTime.setFocusableInTouchMode(true);
		tvTime.requestFocus();
		tvTemperature = (TextView) findViewById(R.id.tvTemperature);
		tvWind = (TextView) findViewById(R.id.tvWind);
		tvHumidity = (TextView) findViewById(R.id.tvHumidity);
		tvAqi = (TextView) findViewById(R.id.tvAqi);
		ivPhe = (ImageView) findViewById(R.id.ivPhe);
		tvPhe = (TextView) findViewById(R.id.tvPhe);
		tvWeek = (TextView) findViewById(R.id.tvWeek);
		ivList = (ImageView) findViewById(R.id.ivList);
		ivList.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		llContainer1 = (LinearLayout) findViewById(R.id.llContainer1);
		llContainer2 = (LinearLayout) findViewById(R.id.llContainer2);
		hScrollView2 = (HorizontalScrollView) findViewById(R.id.hScrollView2);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;

		startLocation();
		
		String cityId = getIntent().getExtras().getString("cityId");
		if (!TextUtils.isEmpty(cityId)) {
			getWeatherInfo(cityId);
		}
	}

	/**
	 * 开始定位
	 */
	private void startLocation() {
		mLocationOption = new AMapLocationClientOption();//初始化定位参数
		mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
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
			if (!TextUtils.isEmpty(amapLocation.getStreet())) {
				tvLocation.setText(amapLocation.getStreet()+amapLocation.getStreetNum());
			}else {
				tvLocation.setText(amapLocation.getDistrict());
			}
		}
	}
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		mListView = (ListView) findViewById(R.id.listView);
		mAdapter = new WeeklyForecastAdapter(mContext, weeklyList);
		mListView.setAdapter(mAdapter);
	}
	
	private void getWeatherInfo(String cityId) {
		if (cityId.startsWith("10131")) {
			HttpAsyncTask http = new HttpAsyncTask(cityId) {
				@Override
				public void onStart(String taskId) {
				}
				@Override
				public void onFinish(String taskId, String response) {
					if (!TextUtils.isEmpty(response)) {
						try {
							JSONArray array = new JSONArray(response);
							JSONObject fact = array.getJSONObject(0);
							if (!fact.isNull("l")) {
								JSONObject object = fact.getJSONObject("l");
								
								//实况信息
								if (!object.isNull("l7")) {
									String time = object.getString("l7");
									if (time != null) {
										tvTime.setText(time + "发布");
									}
								}
								if (!object.isNull("l5")) {
									String l5 = object.getString("l5");
									String currentTime = sdf1.format(new Date().getTime());
									int hour = Integer.valueOf(currentTime);
									if (hour >= 6 && hour <= 18) {
										ivPhe.setBackground(Utils.getWeatherDrawable(true, l5));
									}else {
										ivPhe.setBackground(Utils.getWeatherDrawable(false, l5));
									}
									tvPhe.setText(CodeParse.parseWeatherCode(l5));
								}
								if (!object.isNull("l1")) {
									String factTemp = object.getString("l1");
									tvTemperature.setText(factTemp+"℃");
								}
								
								if (!object.isNull("l4")) {
									String windDir = object.getString("l4");
									if (!object.isNull("l3")) {
										String windForce = object.getString("l3");
										tvWind.setText(getString(WeatherUtil.getWindDirection(Integer.valueOf(windDir))) + " " + 
												WeatherUtil.getFactWindForce(Integer.valueOf(windForce)));
									}
								}
								
								if (!object.isNull("l2")) {
									String humidity = object.getString("l2");
									tvHumidity.setText("湿度" + humidity + "%");
								}
							}
							
							//城市信息
							JSONObject city = array.getJSONObject(1);
//							if (!city.isNull("c")) {
//								JSONObject itemObj = city.getJSONObject("c");
//								if (!itemObj.isNull("c3")) {
//									String cityName = itemObj.getString("c3");
//									if (cityName != null) {
//										tvLocation.setText(cityName);
//									}
//								}
//							}
							if (!city.isNull("f")) {
								JSONObject fObj = city.getJSONObject("f");
								
								int f0Hour = Integer.valueOf(fObj.getString("f0").substring(8, 10));
								String f0 = sdf3.format(sdf2.parse(fObj.getString("f0")));
								long time = sdf3.parse(f0).getTime();
								
								if (!fObj.isNull("f1")) {
									weeklyList.clear();
									String currentTime = sdf1.format(new Date().getTime());
									int hour = Integer.valueOf(currentTime);
									JSONArray f1 = fObj.getJSONArray("f1");
									for (int i = 0; i < f1.length(); i++) {
										WeatherDto dto = new WeatherDto();
										JSONObject weeklyObj = f1.getJSONObject(i);
										//晚上
										dto.lowPheCode = Integer.valueOf(weeklyObj.getString("fb"));
										dto.lowPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fb"))));
										dto.lowTemp = Integer.valueOf(weeklyObj.getString("fd"));
										
										//白天
										dto.highPheCode = Integer.valueOf(weeklyObj.getString("fa"));
										dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fa"))));
										dto.highTemp = Integer.valueOf(weeklyObj.getString("fc"));
										
										if (hour >= 6 && hour <= 18) {
											dto.windDir = Integer.valueOf(weeklyObj.getString("fe"));
											dto.windForce = Integer.valueOf(weeklyObj.getString("fg"));
											if (f0Hour >= 17 || f0Hour < 5) {
												if (i <= 6) {
													dto.windForceString = dto.windForce+"级";
												}else {
													dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
												}
											}else {
												if (i <= 2) {
													dto.windForceString = dto.windForce+"级";
												}else {
													dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
												}
											}
										}else {
											dto.windDir = Integer.valueOf(weeklyObj.getString("ff"));
											dto.windForce = Integer.valueOf(weeklyObj.getString("fh"));
											if (f0Hour >= 17 || f0Hour < 5) {
												if (i <= 6) {
													dto.windForceString = dto.windForce+"级";
												}else {
													dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
												}
											}else {
												if (i <= 2) {
													dto.windForceString = dto.windForce+"级";
												}else {
													dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
												}
											}
										}
										
										dto.week = CommonUtil.getWeek(mContext, i);//星期几
										dto.date = sdf3.format(new Date(time+1000*60*60*24*i));//日期
										if (i == 0) {
											tvWeek.setText("今天"+" "+dto.week);
										}
										
										weeklyList.add(dto);
									}
									
									if (weeklyList.size() > 0 && mAdapter != null) {
										CommonUtil.setListViewHeightBasedOnChildren(mListView);
										mAdapter.notifyDataSetChanged();
										
										//一周预报曲线
										WeeklyView weeklyView = new WeeklyView(mContext);
										weeklyView.setData(weeklyList);
										llContainer2.removeAllViews();
										llContainer2.addView(weeklyView, width*2, (int)(CommonUtil.dip2px(mContext, 360)));
									}
									
								}
							}
							
							//空气质量
							if (!array.isNull(4)) {
								JSONObject aqiObj = array.getJSONObject(4);
								if (!aqiObj.isNull("p")) {
									JSONObject itemObj = aqiObj.getJSONObject("p");
									if (!itemObj.isNull("p2")) {
										String aqi = itemObj.getString("p2");
										tvAqi.setText("空气质量" + " "+ WeatherUtil.getAqi(mContext, Integer.valueOf(aqi)) + " " + aqi);
									}
								}
							}
							
							//逐小时预报信息
							JSONObject hour = array.getJSONObject(3);
							if (!hour.isNull("jh")) {
								List<WeatherDto> hourlyList = new ArrayList<WeatherDto>();
								JSONArray jhArray = hour.getJSONArray("jh");
								for (int i = 0; i < jhArray.length(); i++) {
									JSONObject itemObj = jhArray.getJSONObject(i);
									WeatherDto dto = new WeatherDto();
									dto.hourlyCode = Integer.valueOf(itemObj.getString("ja"));
									dto.hourlyTemp = Integer.valueOf(itemObj.getString("jb"));
									dto.hourlyTime = itemObj.getString("jf");
									dto.hourlyWindDirCode = Integer.valueOf(itemObj.getString("jc"));
									dto.hourlyWindForceCode = Integer.valueOf(itemObj.getString("jd"));
									hourlyList.add(dto);
								}
								//逐小时预报信息
								CubicView cubicView = new CubicView(mContext);
								cubicView.setData(hourlyList);
								llContainer1.removeAllViews();
								llContainer1.addView(cubicView, width*2, (int)(CommonUtil.dip2px(mContext, 300)));
							}
							
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						
						progressBar.setVisibility(View.GONE);
						scrollView.setVisibility(View.VISIBLE);
					}
				}
			};
			http.setDebug(false);
			http.excute("http://data-fusion.tianqi.cn/datafusion/GetDate?type=HN&ID="+cityId, "");
		}else {
			WeatherAPI.getWeather2(mContext, cityId, Language.ZH_CN, new AsyncResponseHandler() {
				@Override
				public void onComplete(Weather content) {
					super.onComplete(content);
					if (content != null) {
						try {
							//城市信息
//							JSONObject city = content.getCityInfo();
//							if (!city.isNull("c3")) {
//								String cityName = city.getString("c3");
//								if (cityName != null) {
//									tvLocation.setText(cityName);
//								}
//							}
							
							//实况信息
							JSONObject object = content.getWeatherFactInfo();
							if (!object.isNull("l7")) {
								String time = object.getString("l7");
								if (time != null) {
									tvTime.setText(time + "发布");
								}
							}

							String currentTime = sdf1.format(new Date().getTime());
							int hour = Integer.valueOf(currentTime);
							if (!object.isNull("l5")) {
								String l5 = WeatherUtil.lastValue(object.getString("l5"));
								if (hour >= 6 && hour <= 18) {
									ivPhe.setBackground(Utils.getWeatherDrawable(true, l5));
								}else {
									ivPhe.setBackground(Utils.getWeatherDrawable(false, l5));
								}
								tvPhe.setText(CodeParse.parseWeatherCode(l5));
							}
							if (!object.isNull("l1")) {
								String factTemp = WeatherUtil.lastValue(object.getString("l1"));
								tvTemperature.setText(factTemp+"℃");
							}
							
							if (!object.isNull("l4")) {
								String windDir = WeatherUtil.lastValue(object.getString("l4"));
								if (!object.isNull("l3")) {
									String windForce = WeatherUtil.lastValue(object.getString("l3"));
									tvWind.setText(getString(WeatherUtil.getWindDirection(Integer.valueOf(windDir))) + " " + 
											WeatherUtil.getFactWindForce(Integer.valueOf(windForce)));
								}
							}
							
							if (!object.isNull("l2")) {
								String humidity = WeatherUtil.lastValue(object.getString("l2"));
								tvHumidity.setText("湿度" + humidity + "%");
							}
							
							JSONObject aqiObj = content.getAirQualityInfo();
							String aqi = WeatherUtil.lastValue(aqiObj.getString("k3"));
							tvAqi.setText("空气质量" + " "+ WeatherUtil.getAqi(mContext, Integer.valueOf(aqi)) + " " + aqi);
							
							//一周预报信息
							weeklyList.clear();
							//这里只去一周预报，默认为15天，所以遍历7次
							for (int i = 1; i <= 15; i++) {
								WeatherDto dto = new WeatherDto();
								
								JSONArray weeklyArray = content.getWeatherForecastInfo(i);
								JSONObject weeklyObj = weeklyArray.getJSONObject(0);

								//晚上
								dto.lowPheCode = Integer.valueOf(weeklyObj.getString("fb"));
								dto.lowPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fb"))));
								dto.lowTemp = Integer.valueOf(weeklyObj.getString("fd"));
								
								//白天数据缺失时，就使用晚上数据
								if (TextUtils.isEmpty(weeklyObj.getString("fa"))) {
									JSONObject secondObj = content.getWeatherForecastInfo(2).getJSONObject(0);
									dto.highPheCode = Integer.valueOf(secondObj.getString("fa"));
									dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(secondObj.getString("fa"))));
									
									int time1 = Integer.valueOf(secondObj.getString("fc"));
									int time2 = Integer.valueOf(weeklyObj.getString("fd"));
									if (time1 <= time2) {
										dto.highTemp = time2 + 2;
									}else {
										dto.highTemp = Integer.valueOf(secondObj.getString("fc"));
									}
								}else {
									//白天
									dto.highPheCode = Integer.valueOf(weeklyObj.getString("fa"));
									dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fa"))));
									dto.highTemp = Integer.valueOf(weeklyObj.getString("fc"));
								}
								
								if (TextUtils.isEmpty(weeklyObj.getString("fa"))) {
									dto.windDir = Integer.valueOf(weeklyObj.getString("ff"));
									dto.windForce = Integer.valueOf(weeklyObj.getString("fh"));
									if (hour >= 6 && hour <= 18) {
										dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
									}else {
										dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
									}
								}else {
									if (hour >= 6 && hour <= 18) {
										dto.windDir = Integer.valueOf(weeklyObj.getString("fe"));
										dto.windForce = Integer.valueOf(weeklyObj.getString("fg"));
										dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
									}else {
										dto.windDir = Integer.valueOf(weeklyObj.getString("ff"));
										dto.windForce = Integer.valueOf(weeklyObj.getString("fh"));
										dto.windForceString = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
									}
								}
								
								JSONArray timeArray =  content.getTimeInfo(i);
								JSONObject timeObj = timeArray.getJSONObject(0);
								dto.week = timeObj.getString("t4");//星期几
								dto.date = timeObj.getString("t1");//日期
								if (i == 1) {
									tvWeek.setText("今天"+" "+dto.week);
								}
								
								weeklyList.add(dto);
							}
							
							if (weeklyList.size() > 0 && mAdapter != null) {
								CommonUtil.setListViewHeightBasedOnChildren(mListView);
								mAdapter.notifyDataSetChanged();
							}
							
							//一周预报曲线
							WeeklyView weeklyView = new WeeklyView(mContext);
							weeklyView.setData(weeklyList);
							llContainer2.removeAllViews();
							llContainer2.addView(weeklyView, width*2, (int)(CommonUtil.dip2px(mContext, 360)));
							
							//逐小时预报信息
							JSONArray hourlyArray = content.getHourlyFineForecast2();
							List<WeatherDto> hourlyList = new ArrayList<WeatherDto>();
							for (int i = 0; i < hourlyArray.length(); i++) {
								JSONObject itemObj = hourlyArray.getJSONObject(i);
								WeatherDto dto = new WeatherDto();
								dto.hourlyCode = Integer.valueOf(itemObj.getString("ja"));
								dto.hourlyTemp = Integer.valueOf(itemObj.getString("jb"));
								dto.hourlyTime = itemObj.getString("jf");
								dto.hourlyWindDirCode = Integer.valueOf(itemObj.getString("jc"));
								dto.hourlyWindForceCode = Integer.valueOf(itemObj.getString("jd"));
								hourlyList.add(dto);
							}
							
							//逐小时预报信息
							CubicView cubicView = new CubicView(mContext);
							cubicView.setData(hourlyList);
							llContainer1.removeAllViews();
							llContainer1.addView(cubicView, width*2, (int)(CommonUtil.dip2px(mContext, 300)));
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
						
						progressBar.setVisibility(View.GONE);
						scrollView.setVisibility(View.VISIBLE);
					}
				}
				
				@Override
				public void onError(Throwable error, String content) {
					super.onError(error, content);
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.ivList:
			if (hScrollView2.getVisibility() == View.VISIBLE) {
				ivList.setImageResource(R.drawable.iv_list);
				mListView.setVisibility(View.VISIBLE);
				hScrollView2.setVisibility(View.GONE);
			}else {
				ivList.setImageResource(R.drawable.iv_trend);
				mListView.setVisibility(View.GONE);
				hScrollView2.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}

}