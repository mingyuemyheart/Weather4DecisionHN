package com.pmsc.weather4decision.phone.hainan.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.lib.http.HttpAsyncTask;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.act.ForecastActivity;
import com.pmsc.weather4decision.phone.hainan.dto.WeatherDto;
import com.pmsc.weather4decision.phone.hainan.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProvinceFragment extends Fragment implements OnMarkerClickListener, OnMapLoadedListener {
	
	private TextView      titleTv;
	private MapView mMapView = null;
	private AMap aMap = null;
	private List<WeatherDto> mList = new ArrayList<>();
	private SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日HH时");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_province, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
		initMap(savedInstanceState, view);
	}
	
	private void initWidget(View view){
		titleTv = (TextView) view.findViewById(R.id.title_tv);
	}
	
	private void initMap(Bundle bundle, View view) {
		mMapView = (MapView) view.findViewById(R.id.map);
		mMapView.onCreate(bundle);
		if (aMap == null) {
			aMap = mMapView.getMap();
		}
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.05, 109.83),8.0f));
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setRotateGesturesEnabled(false);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnMapLoadedListener(this);
	}

	Marker m = null;
	
	@Override
	public void onMapLoaded() {
		asyncPositions();
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		//获取城市天气信息
		Bundle bundle = new Bundle();
		bundle.putString("cityId", marker.getTitle());
		bundle.putString("cityName", marker.getSnippet());
		openActivity(ForecastActivity.class, bundle);
		return true;
	}
	
	/**
	 * 获取城市的位置信息
	 */
	private void asyncPositions() {
		String listUrl = getArguments().getString("listUrl");
		String dataUrl = getArguments().getString("dataUrl");
		String url = Utils.checkUrl(listUrl, dataUrl);
		if (url == null) {
			return;
		}
		HttpAsyncTask http = new HttpAsyncTask("province") {
			@Override
			public void onStart(String taskId) {
			}
			@Override
			public void onFinish(String taskId, String response) {
				if (!TextUtils.isEmpty(response)) {
					try {
						mList.clear();
						JSONObject obj = new JSONObject(response);
						if (!obj.isNull("list")) {
							JSONArray array = obj.getJSONArray("list");
							for (int i = 0; i < array.length(); i++) {
								JSONObject itemObj = array.getJSONObject(i);
								WeatherDto dto = new WeatherDto();
								if (!itemObj.isNull("name")) {
									dto.cityName = itemObj.getString("name");
								}
								if (!itemObj.isNull("city_id")) {
									dto.cityId = itemObj.getString("city_id");
								}
								if (!itemObj.isNull("geo")) {
									JSONArray geoArray = itemObj.getJSONArray("geo");
									dto.lat = geoArray.getDouble(1);
									dto.lng = geoArray.getDouble(0);
								}
								mList.add(dto);
							}
						}
						
						if (mList.size() > 0) {
							for (int i = 0; i < mList.size(); i++) {
								WeatherDto dto = mList.get(i);
								getWeatherInfo(dto);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			}
		};
		http.setDebug(false);
		http.excute(url, "");
	}
	
	/**
	 * 获取海南本省数据
	 */
	private void getWeatherInfo(final WeatherDto dto) {
		HttpAsyncTask http = new HttpAsyncTask(dto.cityId) {
			@Override
			public void onStart(String taskId) {
			}
			@Override
			public void onFinish(String taskId, String response) {
				if (!TextUtils.isEmpty(response)) {
					try {
						JSONArray array = new JSONArray(response);

						//实况信息
						if (TextUtils.equals(dto.cityId, "101310101")) {
							JSONObject fact = array.getJSONObject(0);
							if (!fact.isNull("l")) {
								JSONObject lObj = fact.getJSONObject("l");
								if (!lObj.isNull("l13")) {
									String time = lObj.getString("l13");
									if (time != null) {
										try {
											titleTv.setText(sdf3.format(sdf2.parse(time))+"发布的市县未来24小时预报");
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}

						//逐小时预报信息
						JSONObject hour = array.getJSONObject(3);
						if (!hour.isNull("jh")) {
							JSONArray jhArray = hour.getJSONArray("jh");
							JSONObject itemObj = jhArray.getJSONObject(0);
							dto.factPheCode = itemObj.getString("ja");
							dto.factTemp = itemObj.getString("jb");

							Message msg = new Message();
							msg.what = 101;
							msg.obj = dto;
							handler.sendMessage(msg);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			}
		};
		http.setDebug(false);
//		http.excute("http://data-fusion.tianqi.cn/datafusion/GetDate?type=HN&ID="+dto.cityId, "");
		http.excute("http://data-fusion.tianqi.cn/datafusion/test?type=HN&ID="+dto.cityId, "");
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case 101:
				WeatherDto dto = (WeatherDto) msg.obj;
				addMarkers(dto);
				break;

			default:
				break;
			}
    	};
    };
    
    private void addMarkers(WeatherDto dto) {
    	LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		MarkerOptions options = new MarkerOptions();
		options.title(dto.cityId);
		options.snippet(dto.cityName);
		options.position(new LatLng(dto.lat, dto.lng));
		View mView = inflater.inflate(R.layout.layout_travel_marker, null);
		TextView tvName = (TextView) mView.findViewById(R.id.tvName);
		TextView tvTemp = (TextView) mView.findViewById(R.id.tvTemp);
		ImageView ivPhe = (ImageView) mView.findViewById(R.id.ivPhe);
		if (dto.cityName != null) {
			tvName.setText(dto.cityName);
		}
		if (dto.factTemp != null) {
			tvTemp.setText(dto.factTemp+"℃");
		}
		if (dto.factPheCode != null) {
			String currentTime = sdf1.format(new Date().getTime());
			int hour = Integer.valueOf(currentTime);
			if (hour >= 6 && hour <= 18) {
				ivPhe.setBackground(Utils.getWeatherDrawable(true, dto.factPheCode));
			}else {
				ivPhe.setBackground(Utils.getWeatherDrawable(false, dto.factPheCode));
			}
		}
		options.icon(BitmapDescriptorFactory.fromView(mView));
		aMap.addMarker(options);
		
		LatLngBounds bounds = new LatLngBounds.Builder()
		.include(new LatLng(20.530793, 110.328859))
		.include(new LatLng(19.095361, 108.651775))
		.include(new LatLng(16.857606,112.350494))
		.include(new LatLng(19.54339,110.797648)).build();
		aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }
	
	/**
	 * 打开新的activity
	 *
	 * @param activity
	 * @param bundle
	 */
	public void openActivity(Class<?> activity, Bundle bundle) {
		Intent intent = new Intent(getActivity(), activity);
		if (null != bundle) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

}
