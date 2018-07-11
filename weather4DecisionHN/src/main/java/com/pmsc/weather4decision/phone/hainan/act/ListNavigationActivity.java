package com.pmsc.weather4decision.phone.hainan.act;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.lib.app.BaseFragment;
import com.android.lib.data.CONST;
import com.android.lib.data.JsonMap;
import com.android.lib.util.AssetFile;
import com.android.lib.view.PagerSlidingTabStrip;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.ListNavigationPageAdapter;
import com.pmsc.weather4decision.phone.hainan.util.CommonUtil;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.pmsc.weather4decision.phone.hainan.util.StatisticUtil;
import com.pmsc.weather4decision.phone.hainan.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 带水平导航栏的列表界面,主要用来显示:灾害预警,综合服务。
 */
public class ListNavigationActivity extends AbsDrawerActivity implements OnPageChangeListener, OnMapClickListener,
OnMarkerClickListener, InfoWindowAdapter, OnClickListener {
	private PagerSlidingTabStrip      tabView;
	private ViewPager                 viewpager;
	private ListNavigationPageAdapter adapter;
	
	private List<JsonMap>             sonChannels;
	private boolean                   hasGrandSon; //是否有第三级栏目
	
	private ImageView ivExpand = null;
	private boolean isExpand = false;
	private MapView mapView = null;//高德地图
	private AMap aMap = null;//高德地图
    private List<JsonMap> dataList = new ArrayList<>();
    private Marker selectMarker = null;
    private String channelId = null;//灾害预警的id
	private TextView tvMapNumber;
	                                               
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (channelData != null && channelData.size() > 0) {
			sonChannels = channelData.getListMap("child");
			channelId = channelData.getString("id");
		}
		
		checkGrandSon();
		setContentView(R.layout.activity_listview_with_navigation);
		
		if (TextUtils.equals(channelId, "586")) {
			initAmap(savedInstanceState);
			
			ivExpand = (ImageView) findViewById(R.id.ivExpand);
			ivExpand.setVisibility(View.VISIBLE);
			ivExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					LayoutParams params = mapView.getLayoutParams();
					if (isExpand == false) {
						ivExpand.setImageResource(R.drawable.iv_collose);
						params.width = LayoutParams.MATCH_PARENT;
						params.height = LayoutParams.MATCH_PARENT;
						isExpand = true;
					}else {
						ivExpand.setImageResource(R.drawable.iv_expand);
						params.width = LayoutParams.MATCH_PARENT;
						params.height = (int) CommonUtil.dip2px(getApplicationContext(), 300);
						isExpand = false;
					}
					mapView.setLayoutParams(params);
				}
			});
		}else if (TextUtils.equals(channelId, "613")) {//实况资料
			rightButton.setOnClickListener(this);
			rightButton.setVisibility(View.VISIBLE);
			rightButton.setBackgroundColor(Color.TRANSPARENT);
			rightButton.setText("雨情");
			rightButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		}
		
		tabView = (PagerSlidingTabStrip) findViewById(R.id.tab_view);
		tabView.setOnPageChangeListener(this);

		viewpager = (ViewPager) findViewById(R.id.viewpager);
		viewpager.setOnPageChangeListener(this);
		if (!hasGrandSon) {
			//没有第三级栏目，直接加载第二级栏目数据
			if (sonChannels == null) {
				sonChannels = new ArrayList<JsonMap>();
				sonChannels.add(channelData);
			}
			adapter = new ListNavigationPageAdapter(getSupportFragmentManager(), sonChannels);
			viewpager.setAdapter(adapter);
			
			int size = adapter.getCount();
			if (size >= 2) {
				tabView.setViewPager(viewpager);
			}
			int visibility = size < 2 ? View.GONE : View.VISIBLE;
			tabView.setVisibility(visibility);
			
			if (size == 1) {
				setTitle(sonChannels.get(0).getString("title"));
			}
		} else {
			//有第三级栏目，右上角弹出菜单，菜单中加载第二级栏目.水平导航栏加载第三级栏目数据,默认显示第一条二级栏目下的三级数据
			setTitle(sonChannels.get(0).getString("title"));
			int size = sonChannels != null ? sonChannels.size() : 0;
			if (size == 1) {
				//二级栏目仅有一条时，不显示右上角“更多”按钮，直接加载其子栏目
				rightButton.setVisibility(View.INVISIBLE);
			}
			adapter = new ListNavigationPageAdapter(getSupportFragmentManager(), sonChannels.get(0).getListMap("child"));
			viewpager.setAdapter(adapter);
			tabView.setViewPager(viewpager);
		}
		
		int childIndex = getIntent().getIntExtra(CHILD_INDEX_KEY, 0);
		viewpager.setCurrentItem(childIndex);

		if (getIntent().hasExtra("columnId")) {
			String columnId = getIntent().getStringExtra("columnId");
			StatisticUtil.statisticClickCount(columnId);
		}
	}
	
	/**
	 * 初始化高德地图
	 */
	private void initAmap(Bundle bundle) {
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setVisibility(View.VISIBLE);
		mapView.onCreate(bundle);
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.05, 109.83), 7.4f));
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setOnMapClickListener(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setInfoWindowAdapter(this);

		tvMapNumber = (TextView) findViewById(R.id.tvMapNumber);
		tvMapNumber.setText(aMap.getMapContentApprovalNumber());
		
		if (sonChannels.size() > 0) {
			for (int i = 0; i < sonChannels.size(); i++) {
				String listUrl = sonChannels.get(i).getString("listUrl");
				String dataUrl = sonChannels.get(i).getString("dataUrl");
				
				String url = Utils.checkUrl(listUrl, dataUrl);
				if (!TextUtils.isEmpty(url)) {
					OkHttpWarning(url);
				}
			}
		}
	}
	
	/**
	 * 获取预警信息
	 */
	private void OkHttpWarning(final String url) {
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
										JSONObject object = new JSONObject(result);
										if (object != null) {
											if (!object.isNull("w")) {
												JSONArray jsonArray = object.getJSONArray("w");
												for (int i = 0; i < jsonArray.length(); i++) {
													JSONObject itemObj = jsonArray.getJSONObject(i);
													final JsonMap dto = new JsonMap();
													if (!itemObj.isNull("w1")) {
														dto.put("w1", itemObj.getString("w1"));
													}
													if (!itemObj.isNull("w2")) {
														dto.put("w2", itemObj.getString("w2"));
													}
													if (!itemObj.isNull("w3")) {
														dto.put("w3", itemObj.getString("w3"));
													}
													if (!itemObj.isNull("w4")) {
														dto.put("w4", itemObj.getString("w4"));
													}
													if (!itemObj.isNull("w5")) {
														dto.put("w5", itemObj.getString("w5"));
													}
													if (!itemObj.isNull("w6")) {
														dto.put("w6", itemObj.getString("w6"));
													}
													if (!itemObj.isNull("w7")) {
														dto.put("w7", itemObj.getString("w7"));
													}
													if (!itemObj.isNull("w8")) {
														dto.put("w8", itemObj.getString("w8"));
													}
													if (!itemObj.isNull("w9")) {
														dto.put("w9", itemObj.getString("w9"));
													}
													if (!itemObj.isNull("w10")) {
														dto.put("w10", itemObj.getString("w10"));
													}
													if (!itemObj.isNull("w11")) {
														dto.put("w11", itemObj.getString("w11"));
													}

													String[] names = getResources().getStringArray(R.array.district_name);
													for (int j = 0; j < names.length; j++) {
														String[] itemArray = names[j].split(",");
														String w2 = dto.getString("w2");
														String w11 = dto.getString("w11");
														String value = w2;
														if (!TextUtils.isEmpty(w2)) {
															value = w2;
														}else {
															value = w11;
														}
														if (value.contains(itemArray[0]) || itemArray[0].contains(value)) {
															if (!TextUtils.isEmpty(itemArray[2]) && !TextUtils.isEmpty(itemArray[1])) {
																dto.put("latLng", new LatLng(Double.valueOf(itemArray[2]), Double.valueOf(itemArray[1])));
																break;
															}
														}
													}

													dataList.add(dto);
												}
												addMarkerAndDrawDistrict(dataList);
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
	
	private void addMarkerAndDrawDistrict(List<JsonMap> list) {
		for (int i = 0; i < list.size(); i++) {
			JsonMap dto = list.get(i);
			MarkerOptions options = new MarkerOptions();
			if (!TextUtils.isEmpty(dto.getString("w2"))) {
				options.title(dto.getString("w2"));
			}else {
				options.title(dto.getString("w11"));
			}
			options.position((LatLng) dto.get("latLng"));
//			options.icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_marker));
			
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View markerView = inflater.inflate(R.layout.marker_view, null);
			ImageView ivMarker = (ImageView) markerView.findViewById(R.id.ivMarker);
			
			LayoutParams params = ivMarker.getLayoutParams();
			if ("琼州海峡".contains(options.getTitle()) || "本岛西部".contains(options.getTitle()) || "本岛南部".contains(options.getTitle())
					|| "本岛东部".contains(options.getTitle()) || "北部湾北部".contains(options.getTitle()) || "北部湾南部".contains(options.getTitle())
					|| "中沙附近".contains(options.getTitle()) || "西沙附近".contains(options.getTitle()) || "南沙附近".contains(options.getTitle())) {
				Drawable warnIcon = new AssetFile(getApplicationContext()).getDrawable("warning/icon_warning_" + dto.getString("w4") + dto.getString("w6") + ".png");
				ivMarker.setImageDrawable(warnIcon);
				params.width = (int) CommonUtil.dip2px(getApplicationContext(), 30);
				params.height = (int) CommonUtil.dip2px(getApplicationContext(), 30);
				ivMarker.setLayoutParams(params);
			}else {
				ivMarker.setImageResource(R.drawable.iv_marker);
				params.width = (int) CommonUtil.dip2px(getApplicationContext(), 20);
				params.height = (int) CommonUtil.dip2px(getApplicationContext(), 20);
				ivMarker.setLayoutParams(params);
			}
			options.icon(BitmapDescriptorFactory.fromView(markerView));
			aMap.addMarker(options);
		}
		
		Collections.sort(list, new Comparator<JsonMap>() {
			@Override
			public int compare(JsonMap a, JsonMap b) {
				return a.getString("w6").compareTo(b.getString("w6"));
			}
		});
		
		Map<String, JsonMap> map = new HashMap<>();
		int color = 0;
		for (int i = 0; i < list.size(); i++) {
			JsonMap data = list.get(i);
			String name = data.getString("w2");
			if (TextUtils.isEmpty(data.getString("w2"))) {
				name = data.getString("w11");
			}
			
			if (map.containsKey(name)) {
				String c = data.getString("w6");
				if (!TextUtils.isEmpty(c)) {
					if (color <= Integer.valueOf(c)) {
						color = Integer.valueOf(c);
						map.put(name, data);
					}
				}
			}else {
				map.put(name, data);
				color = 0;
			}
		}
		
		for (Entry<String, JsonMap> entry : map.entrySet()) {
//			JsonMap dto = map.get(map.keySet().iterator().next());
			JsonMap dto = entry.getValue();
			String c = dto.getString("w6");
			if (!TextUtils.isEmpty(c)) {
				int color2 = 0;
				if (TextUtils.equals(c, "01")) {
					color2 = getResources().getColor(R.color.blue);
				}else if (TextUtils.equals(c, "02")) {
					color2 = getResources().getColor(R.color.yellow);
				}else if (TextUtils.equals(c, "03")) {
					color2 = getResources().getColor(R.color.orange);
				}else if (TextUtils.equals(c, "04")) {
					color2 = getResources().getColor(R.color.red);
				}
				String districtName = dto.getString("w2");
				if (!TextUtils.isEmpty(districtName)) {
					if (districtName.contains("陵水")) {
						districtName = "陵水黎族自治县";
					}else if (districtName.contains("昌江")) {
						districtName = "昌江黎族自治县";
					}else if (districtName.contains("白沙")) {
						districtName = "白沙黎族自治县";
					}else if (districtName.contains("琼中")) {
						districtName = "琼中黎族苗族自治县";
					}else if (districtName.contains("乐东")) {
						districtName = "乐东黎族自治县";
					}else if (districtName.contains("保亭")) {
						districtName = "保亭黎族苗族自治县";
					}
					CommonUtil.drawDistrict(getApplicationContext(), aMap, districtName, color2);
				}
			}
		}
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		if (selectMarker != null) {
			selectMarker.hideInfoWindow();
		}
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		selectMarker = marker;
		marker.showInfoWindow();
		return true;
	}
	
	@Override
	public View getInfoContents(final Marker marker) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.marker_info, null);
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		tvName.setText(marker.getTitle());
		
		LinearLayout llContainer = (LinearLayout) view.findViewById(R.id.llContainer);
		
		final List<JsonMap> tempList = new ArrayList<>();
		for (int i = 0; i < dataList.size(); i++) {
			JsonMap dto = dataList.get(i);
			String title = dto.getString("w2");
			if (TextUtils.isEmpty(title)) {
				title = dto.getString("w11");
			}
			if (title.contains(marker.getTitle()) || marker.getTitle().contains(title)) {
				tempList.add(dto);
			}
		}
		
		for (int i = 0; i < tempList.size(); i++) {
			JsonMap data = tempList.get(i);
			ImageView imageView = new ImageView(getApplicationContext());
			Drawable warnIcon = new AssetFile(getApplicationContext()).getDrawable("warning/icon_warning_" + data.getString("w4") + data.getString("w6") + ".png");
			imageView.setImageDrawable(warnIcon);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)CommonUtil.dip2px(getApplicationContext(), 30), (int)CommonUtil.dip2px(getApplicationContext(), 30));
			params.setMargins(0, 0, 15, 0);
			imageView.setLayoutParams(params);
			imageView.setTag(i+"");
			llContainer.addView(imageView);
			
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					if (TextUtils.equals(String.valueOf(v.getTag()), "0")) {
						bundle.putString("warning_data", tempList.get(0).toString());
					}else if (TextUtils.equals(String.valueOf(v.getTag()), "1")) {
						bundle.putString("warning_data", tempList.get(1).toString());
					}else if (TextUtils.equals(String.valueOf(v.getTag()), "2")) {
						bundle.putString("warning_data", tempList.get(2).toString());
					}else if (TextUtils.equals(String.valueOf(v.getTag()), "3")) {
						bundle.putString("warning_data", tempList.get(3).toString());
					}
					openActivity(WarningDetailsActivity.class, bundle);
				}
			});
		}
		
		return view;
	}
	
	@Override
	public View getInfoWindow(Marker arg0) {
		return null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		boolean isFromMain = getIntent().getBooleanExtra(IS_FROM_MAIN, false);
		if (isFromMain) {
			showDrawer();
		}
	}
	
	private void checkGrandSon() {
		int size = sonChannels != null ? sonChannels.size() : 0;
		for (int i = 0; i < size; i++) {
			if (sonChannels.get(i).containsKey("child")) {
				List<JsonMap> grandSons = sonChannels.get(i).getListMap("child");
				if (grandSons != null && grandSons.size() > 0) {
					hasGrandSon = true;
				}
			}
		}
		
		if (hasGrandSon) {
			//有第三级别栏目
		}
	}
	
	public void onRightButtonAction(View view) {
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}
	
	@Override
	public void onPageSelected(final int position) {
		post(new Runnable() {
			@Override
			public void run() {
				adapter.getItem(position).loadData(null);
			}
		}, 300);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction(CONST.BROADCAST_STOPLOAD);
		sendBroadcast(intent);
		
		if (adapter == null || viewpager == null) {
			super.onBackPressed();
		}
		BaseFragment frag = adapter.getItem(viewpager.getCurrentItem());
		if (frag == null) {
			super.onBackPressed();
		}
		if (!frag.isOpenedPop()) {
			super.onBackPressed();
		} else {
			frag.closePop();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_btn:
			startActivity(new Intent(getApplicationContext(), ShawnRainActivity.class));
			break;

		default:
			break;
		}
	}

}
