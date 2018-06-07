package com.pmsc.weather4decision.phone.hainan.act;

/**
 * 天气统计
 */

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.android.lib.app.BaseActivity;
import com.android.lib.util.RainManager;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.dto.WeatherStaticsDto;
import com.pmsc.weather4decision.phone.hainan.util.CustomHttpClient;
import com.pmsc.weather4decision.phone.hainan.view.CircularProgressBar;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StaticsActivity extends BaseActivity implements OnClickListener, OnMarkerClickListener,
        OnMapClickListener {
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private MapView mMapView = null;
	private AMap aMap = null;
	private List<WeatherStaticsDto> mList = new ArrayList<>();//省级
	private CircularProgressBar mCircularProgressBar1 = null;
	private CircularProgressBar mCircularProgressBar2 = null;
	private CircularProgressBar mCircularProgressBar3 = null;
	private CircularProgressBar mCircularProgressBar4 = null;
	private CircularProgressBar mCircularProgressBar5 = null;
	private TextView tvName = null;
	private TextView tvBar1 = null;
	private TextView tvBar2 = null;
	private TextView tvBar3 = null;
	private TextView tvBar4 = null;
	private TextView tvBar5 = null;
	private TextView tvDetail = null;
	private RelativeLayout reDetail = null;
	private RelativeLayout reContent = null;
	private ProgressBar progressBar = null;
	public final static String SANX_DATA_99 = "sanx_data_99";//加密秘钥名称
	public final static String APPID = "f63d329270a44900";//机密需要用到的AppId
	private List<Marker> markerList = new ArrayList<>();
	private LatLng leftlatlng = null;
	private LatLng rightLatlng = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statics);
		mContext = this;
		initWidget();
		initMap(savedInstanceState);
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("天气统计");
		tvName = (TextView) findViewById(R.id.tvName);
		tvBar1 = (TextView) findViewById(R.id.tvBar1);
		tvBar2 = (TextView) findViewById(R.id.tvBar2);
		tvBar3 = (TextView) findViewById(R.id.tvBar3);
		tvBar4 = (TextView) findViewById(R.id.tvBar4);
		tvBar5 = (TextView) findViewById(R.id.tvBar5);
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		mCircularProgressBar1 = (CircularProgressBar) findViewById(R.id.bar1);
		mCircularProgressBar2 = (CircularProgressBar) findViewById(R.id.bar2);
		mCircularProgressBar3 = (CircularProgressBar) findViewById(R.id.bar3);
		mCircularProgressBar4 = (CircularProgressBar) findViewById(R.id.bar4);
		mCircularProgressBar5 = (CircularProgressBar) findViewById(R.id.bar5);
		reDetail = (RelativeLayout) findViewById(R.id.reDetail);
		reContent = (RelativeLayout) findViewById(R.id.reContent);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}
	
	/**
	 * 初始化地图
	 */
	private void initMap(Bundle bundle) {
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(bundle);
		if (aMap == null) {
			aMap = mMapView.getMap();
		}
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.05, 109.83),8.0f));
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setRotateGesturesEnabled(false);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnMapClickListener(this);
		aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
			@Override
			public void onMapLoaded() {
				asyncQuery();
			}
		});

		TextView tvMapNumber = (TextView) findViewById(R.id.tvMapNumber);
		tvMapNumber.setText(aMap.getMapContentApprovalNumber());
	}

	/**
	 * 加密请求字符串
	 * @return
	 */
	private String getSecretUrl() {
		String URL = "http://scapi.weather.com.cn/weather/stationinfo";//天气统计地址
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmm");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("date=").append(sysdate);
		buffer.append("&");
		buffer.append("appid=").append(APPID);
		
		String key = RainManager.getKey(SANX_DATA_99, buffer.toString());
		buffer.delete(buffer.lastIndexOf("&"), buffer.length());
		
		buffer.append("&");
		buffer.append("appid=").append(APPID.substring(0, 6));
		buffer.append("&");
		buffer.append("key=").append(key.substring(0, key.length() - 3));
		String result = buffer.toString();
		return result;
	}
	
	/**
	 * 获取天气统计数据
	 */
	private void asyncQuery() {
		//异步请求数据
		HttpAsyncTask task = new HttpAsyncTask();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(getSecretUrl());
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
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				mList.clear();
				parseStationInfo(result, "level1", mList);
				parseStationInfo(result, "level2", mList);
				parseStationInfo(result, "level3", mList);
				addMarker(mList);
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
	
	/**
	 * 解析数据
	 */
	private void parseStationInfo(String result, String level, List<WeatherStaticsDto> list) {
		try {
			JSONObject obj = new JSONObject(result.toString());
			if (!obj.isNull(level)) {
				JSONArray array = new JSONArray(obj.getString(level));
				for (int i = 0; i < array.length(); i++) {
					WeatherStaticsDto dto = new WeatherStaticsDto();
					JSONObject itemObj = array.getJSONObject(i);
					if (!itemObj.isNull("name")) {
						dto.name = itemObj.getString("name");
					}
					if (!itemObj.isNull("stationid")) {
						dto.stationId = itemObj.getString("stationid");
					}
					if (!itemObj.isNull("level")) {
						dto.level = itemObj.getString("level");
					}
					if (!itemObj.isNull("areaid")) {
						dto.areaId = itemObj.getString("areaid");
					}
					if (!itemObj.isNull("lat")) {
						dto.latitude = itemObj.getString("lat");
					}
					if (!itemObj.isNull("lon")) {
						dto.longitude = itemObj.getString("lon");
					}
					if (!TextUtils.isEmpty(dto.areaId) && dto.areaId.startsWith("10131")) {
						list.add(dto);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加密请求字符串
	 * @return
	 */
	private String getSecretUrl2(String stationid, String areaid) {
		String URL = "http://scapi.weather.com.cn/weather/historycount";
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmm");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("stationid=").append(stationid);
		buffer.append("&");
		buffer.append("areaid=").append(areaid);
		buffer.append("&");
		buffer.append("date=").append(sysdate);
		buffer.append("&");
		buffer.append("appid=").append(APPID);
		
		String key = RainManager.getKey(SANX_DATA_99, buffer.toString());
		buffer.delete(buffer.lastIndexOf("&"), buffer.length());
		
		buffer.append("&");
		buffer.append("appid=").append(APPID.substring(0, 6));
		buffer.append("&");
		buffer.append("key=").append(key.substring(0, key.length() - 3));
		String result = buffer.toString();
		return result;
	}
	
	/**
	 * 给marker添加文字
	 * @param name 城市名称
	 * @return
	 */
	private View getTextBitmap(String name) {      
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_marker_statistic, null);
		if (view == null) {
			return null;
		}
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		if (!TextUtils.isEmpty(name) && name.length() > 2) {
			name = name.substring(0, 2)+"\n"+name.substring(2, name.length());
		}
		tvName.setText(name);
		return view;
	}
	
	private void markerExpandAnimation(Marker marker) {
		ScaleAnimation animation = new ScaleAnimation(0,1,0,1);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(300);
		marker.setAnimation(animation);
		marker.startAnimation();
	}
	
	private void markerColloseAnimation(Marker marker) {
		ScaleAnimation animation = new ScaleAnimation(1,0,1,0);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(300);
		marker.setAnimation(animation);
		marker.startAnimation();
	}
	
	private void removeMarkers() {
		for (int i = 0; i < markerList.size(); i++) {
			Marker marker = markerList.get(i);
			markerColloseAnimation(marker);
			marker.remove();
		}
		markerList.clear();
	}
	
	/**
	 * 添加marker
	 */
	private void addMarker(List<WeatherStaticsDto> list) {
		if (list.isEmpty()) {
			return;
		}
		
		for (int i = 0; i < list.size(); i++) {
			WeatherStaticsDto dto = list.get(i);
			double lat = Double.valueOf(dto.latitude);
			double lng = Double.valueOf(dto.longitude);
			if (leftlatlng == null || rightLatlng == null) {
				MarkerOptions options = new MarkerOptions();
				options.title(list.get(i).areaId);
				options.anchor(0.5f, 0.5f);
				options.position(new LatLng(lat, lng));
				options.icon(BitmapDescriptorFactory.fromView(getTextBitmap(list.get(i).name)));
				Marker marker = aMap.addMarker(options);
				markerList.add(marker);
				markerExpandAnimation(marker);
			}else {
				if (lat > leftlatlng.latitude && lat < rightLatlng.latitude && lng > leftlatlng.longitude && lng < rightLatlng.longitude) {
					MarkerOptions options = new MarkerOptions();
					options.title(list.get(i).areaId);
					options.anchor(0.5f, 0.5f);
					options.position(new LatLng(lat, lng));
					options.icon(BitmapDescriptorFactory.fromView(getTextBitmap(list.get(i).name)));
					Marker marker = aMap.addMarker(options);
					markerList.add(marker);
					markerExpandAnimation(marker);
				}
			}
		}
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		if (reDetail.getVisibility() == View.VISIBLE) {
			hideAnimation(reDetail);
		}
	}
	
	/**
	 * 向上弹出动画
	 * @param layout
	 */
	private void showAnimation(final View layout) {
		TranslateAnimation animation = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_SELF, 0, 
				TranslateAnimation.RELATIVE_TO_SELF, 0, 
				TranslateAnimation.RELATIVE_TO_SELF, 1f, 
				TranslateAnimation.RELATIVE_TO_SELF, 0);
		animation.setDuration(300);
		layout.startAnimation(animation);
		layout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 向下隐藏动画
	 * @param layout
	 */
	private void hideAnimation(final View layout) {
		TranslateAnimation animation = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_SELF, 0, 
				TranslateAnimation.RELATIVE_TO_SELF, 0, 
				TranslateAnimation.RELATIVE_TO_SELF, 0, 
				TranslateAnimation.RELATIVE_TO_SELF, 1f);
		animation.setDuration(300);
		layout.startAnimation(animation);
		layout.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		showAnimation(reDetail);
		String name = null;
		String areaId = null;
		String stationId = null;
		
		for (int i = 0; i < mList.size(); i++) {
			if (TextUtils.equals(marker.getTitle(), mList.get(i).areaId)) {
				areaId = mList.get(i).areaId;
				stationId = mList.get(i).stationId;
				name = mList.get(i).name;
				break;
			}
		}

		tvName.setText(name + " " + stationId);
		tvDetail.setText("");
		progressBar.setVisibility(View.VISIBLE);
		reContent.setVisibility(View.INVISIBLE);
		
		//异步请求数据
		HttpAsyncTask2 task = new HttpAsyncTask2();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(getSecretUrl2(stationId, areaId));
		return true;
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTask2() {
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

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.INVISIBLE);
			reContent.setVisibility(View.VISIBLE);
			if (result != null) {
				try {
					JSONObject obj = new JSONObject(result.toString());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
					try {
						String startTime = sdf2.format(sdf.parse(obj.getString("starttime")));
						String endTime = sdf2.format(sdf.parse(obj.getString("endtime")));
						String no_rain_lx = obj.getInt("no_rain_lx")+"";//连续没雨天数
						if (TextUtils.equals(no_rain_lx, "-1")) {
							no_rain_lx = getString(R.string.no_statics);
						}else {
							no_rain_lx = no_rain_lx+"天";
						}
						String mai_lx = obj.getInt("mai_lx")+"";//连续霾天数
						if (TextUtils.equals(mai_lx, "-1")) {
							mai_lx = getString(R.string.no_statics);
						}else {
							mai_lx = mai_lx+"天";
						}
						String highTemp = null;//高温
						String lowTemp = null;//低温
						String highWind = null;//最大风速
						String highRain = null;//最大降水量
						
						if (!obj.isNull("count")) {
							JSONArray array = new JSONArray(obj.getString("count"));
							JSONObject itemObj0 = array.getJSONObject(0);//温度
							JSONObject itemObj1 = array.getJSONObject(1);//降水
							JSONObject itemObj5 = array.getJSONObject(5);//风速
							
							if (!itemObj0.isNull("max") && !itemObj0.isNull("min")) {
								highTemp = itemObj0.getString("max");
								if (TextUtils.equals(highTemp, "-1.0")) {
									highTemp = getString(R.string.no_statics);
								}else {
									highTemp = highTemp+"℃";
								}
								lowTemp = itemObj0.getString("min");
								if (TextUtils.equals(lowTemp, "-1.0")) {
									lowTemp = getString(R.string.no_statics);
								}else {
									lowTemp = lowTemp+"℃";
								}
							}
							if (!itemObj1.isNull("max")) {
								highRain = itemObj1.getString("max");
								if (TextUtils.equals(highRain, "-1.0")) {
									highRain = getString(R.string.no_statics);
								}else {
									highRain = highRain+"mm";
								}
							}
							if (!itemObj5.isNull("max")) {
								highWind = itemObj5.getString("max");
								if (TextUtils.equals(highWind, "-1.0")) {
									highWind = getString(R.string.no_statics);
								}else {
									highWind = highWind+"m/s";
								}
							}
						}
						
						if (startTime != null && endTime != null && highTemp != null && lowTemp != null && highWind != null && highRain != null) {
							StringBuffer buffer = new StringBuffer();
							buffer.append(getString(R.string.from)).append(startTime);
							buffer.append(getString(R.string.to)).append(endTime);
							buffer.append("：\n");
							buffer.append(getString(R.string.highest_temp)).append(highTemp).append("，");
							buffer.append(getString(R.string.lowest_temp)).append(lowTemp).append("，");
							buffer.append(getString(R.string.max_speed)).append(highWind).append("，");
							buffer.append(getString(R.string.max_fall)).append(highRain).append("，");
							buffer.append(getString(R.string.lx_no_fall)).append(no_rain_lx).append("，");
							buffer.append(getString(R.string.lx_no_mai)).append(mai_lx).append("。");
							
							SpannableStringBuilder builder = new SpannableStringBuilder(buffer.toString());
							ForegroundColorSpan builderSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.builder));
							ForegroundColorSpan builderSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.builder));
							ForegroundColorSpan builderSpan3 = new ForegroundColorSpan(getResources().getColor(R.color.builder));
							ForegroundColorSpan builderSpan4 = new ForegroundColorSpan(getResources().getColor(R.color.builder));
							ForegroundColorSpan builderSpan5 = new ForegroundColorSpan(getResources().getColor(R.color.builder));
							ForegroundColorSpan builderSpan6 = new ForegroundColorSpan(getResources().getColor(R.color.builder));
							
							builder.setSpan(builderSpan1, 29, 29+highTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							builder.setSpan(builderSpan2, 29+highTemp.length()+6, 29+highTemp.length()+6+lowTemp.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
							builder.setSpan(builderSpan3, 29+highTemp.length()+6+lowTemp.length()+6, 29+highTemp.length()+6+lowTemp.length()+6+highWind.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							builder.setSpan(builderSpan4, 29+highTemp.length()+6+lowTemp.length()+6+highWind.length()+7, 29+highTemp.length()+6+lowTemp.length()+6+highWind.length()+7+highRain.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							builder.setSpan(builderSpan5, 29+highTemp.length()+6+lowTemp.length()+6+highWind.length()+7+highRain.length()+8, 29+highTemp.length()+6+lowTemp.length()+6+highWind.length()+7+highRain.length()+8+no_rain_lx.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							builder.setSpan(builderSpan6, 29+highTemp.length()+6+lowTemp.length()+6+highWind.length()+7+highRain.length()+8+no_rain_lx.length()+6, 29+highTemp.length()+6+lowTemp.length()+6+highWind.length()+7+highRain.length()+8+no_rain_lx.length()+6+mai_lx.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							tvDetail.setText(builder);
							
							try {
								long start = sdf2.parse(startTime).getTime();
								long end = sdf2.parse(endTime).getTime();
								float dayCount = (float) ((end - start) / (1000*60*60*24)) + 1;
								if (!obj.isNull("tqxxcount")) {
									JSONArray array = new JSONArray(obj.getString("tqxxcount"));
									for (int i = 0; i < array.length(); i++) {
										JSONObject itemObj = array.getJSONObject(i);
										String name = itemObj.getString("name");
										int value = itemObj.getInt("value");
										
										if (i == 0) {
											if (value == -1) {
												tvBar1.setText(name + "\n" + "--");
												animate(mCircularProgressBar1, null, 0, 1000);
												mCircularProgressBar1.setProgress(0);
											}else {
												tvBar1.setText(name + "\n" + value + "天");
												animate(mCircularProgressBar1, null, -value/dayCount, 1000);
												mCircularProgressBar1.setProgress(-value/dayCount);
											}
										}else if (i == 1) {
											if (value == -1) {
												tvBar2.setText(name + "\n" + "--");
												animate(mCircularProgressBar2, null, 0, 1000);
												mCircularProgressBar2.setProgress(0);
											}else {
												tvBar2.setText(name + "\n" + value + "天");
												animate(mCircularProgressBar2, null, -value/dayCount, 1000);
												mCircularProgressBar2.setProgress(-value/dayCount);
											}
										}else if (i == 2) {
											if (value == -1) {
												tvBar3.setText(name + "\n" + "--");
												animate(mCircularProgressBar3, null, 0, 1000);
												mCircularProgressBar3.setProgress(0);
											}else {
												tvBar3.setText(name + "\n" + value + "天");
												animate(mCircularProgressBar3, null, -value/dayCount, 1000);
												mCircularProgressBar3.setProgress(-value/dayCount);
											}
										}else if (i == 3) {
											if (value == -1) {
												tvBar4.setText(name + "\n" + "--");
												animate(mCircularProgressBar4, null, 0, 1000);
												mCircularProgressBar4.setProgress(0);
											}else {
												tvBar4.setText(name + "\n" + value + "天");
												animate(mCircularProgressBar4, null, -value/dayCount, 1000);
												mCircularProgressBar4.setProgress(-value/dayCount);
											}
										}else if (i == 4) {
											if (value == -1) {
												tvBar5.setText(name + "\n" + "--");
												animate(mCircularProgressBar5, null, 0, 1000);
												mCircularProgressBar5.setProgress(0);
											}else {
												tvBar5.setText(name + "\n" + value + "天");
												animate(mCircularProgressBar5, null, -value/dayCount, 1000);
												mCircularProgressBar5.setProgress(-value/dayCount);
											}
										}
									}
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
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
	
	/**
	 * 进度条动画
	 * @param progressBar
	 * @param listener
	 * @param progress
	 * @param duration
	 */
	private void animate(final CircularProgressBar progressBar, final AnimatorListener listener,final float progress, final int duration) {
		ObjectAnimator mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
		mProgressBarAnimator.setDuration(duration);
		mProgressBarAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		if (listener != null) {
			mProgressBarAnimator.addListener(listener);
		}
		mProgressBarAnimator.reverse();
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
//		progressBar.setMarkerProgress(0f);
		mProgressBarAnimator.start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (reDetail.getVisibility() == View.VISIBLE) {
				hideAnimation(reDetail);
				return false;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			if (reDetail.getVisibility() == View.VISIBLE) {
				hideAnimation(reDetail);
			} else {
				finish();
			}
			break;

		default:
			break;
		}
	}
	
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onResume() {
//		super.onResume();
//		if (mMapView != null) {
//			mMapView.onResume();
//		}
//	}
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onPause() {
//		super.onPause();
//		if (mMapView != null) {
//			mMapView.onPause();
//		}
//	}
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		if (mMapView != null) {
//			mMapView.onSaveInstanceState(outState);
//		}
//	}
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		if (mMapView != null) {
//			mMapView.onDestroy();
//		}
//	}

}
