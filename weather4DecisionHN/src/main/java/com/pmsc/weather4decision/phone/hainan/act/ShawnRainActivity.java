package com.pmsc.weather4decision.phone.hainan.act;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapTouchListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.android.lib.app.BaseActivity;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.DialogDetailAdapter;
import com.pmsc.weather4decision.phone.hainan.adapter.ShawnRainAdapter;
import com.pmsc.weather4decision.phone.hainan.dto.ShawnRainDto;
import com.pmsc.weather4decision.phone.hainan.fragment.ShawnRainCheckHourFragment;
import com.pmsc.weather4decision.phone.hainan.fragment.ShawnRainCheckMinuteFragment;
import com.pmsc.weather4decision.phone.hainan.util.CommonUtil;
import com.pmsc.weather4decision.phone.hainan.util.CustomHttpClient;
import com.pmsc.weather4decision.phone.hainan.util.StatisticUtil;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.pmsc.weather4decision.phone.hainan.view.MainViewPager;

import net.tsz.afinal.FinalBitmap;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 实况资料
 * @author shawn_sun
 *
 */

public class ShawnRainActivity extends BaseActivity implements OnClickListener, OnCameraChangeListener{
	
	private Context mContext = null;
	private RelativeLayout reTitle = null;
	private TextView tvTitle = null;
	private TextView tvControl = null;
	private final static String CHANNEL_DATA = "channel_data";
	private LinearLayout llBack = null;
	private LinearLayout llContainer2 = null;
	private LinearLayout llContainer = null;
	private LinearLayout llContainer1 = null;
	private String url = null;
	private MapView mapView = null;//高德地图
	private AMap aMap = null;//高德地图
	private TextView tvLayerName = null;
	private String layerName = null;
	private String startTime = null,  endTime = null;
	private int selectId = 0;
	private TextView tvToast = null;
	private ImageView ivChart = null;
	private TextView tvDetail = null;
	private TextView tvHistory = null;
	private ProgressBar progressBar = null;
	private List<Polygon> polygons = new ArrayList<Polygon>();
	private List<Polyline> polylines = new ArrayList<Polyline>();
	private List<ShawnRainDto> times = new ArrayList<ShawnRainDto>();
	private List<ShawnRainDto> realDatas = new ArrayList<ShawnRainDto>();
	public static String childId = null;
	private String title = null;
	private String stationName = null;
	private String area = null;
	private String val = null;
	private List<ShawnRainDto> nameList = new ArrayList<ShawnRainDto>();
	private List<Text> texts = new ArrayList<Text>();//等值线
	private List<Text> cityNames = new ArrayList<Text>();
	private List<Circle> circles = new ArrayList<Circle>();
	private List<ShawnRainDto> dataList = new ArrayList<ShawnRainDto>();
	private int width = 0, height = 0;
	private float density = 0;
	private TextView tvIntro = null;
	private LinearLayout listTitle = null;
	private ListView listView = null;
	private ShawnRainAdapter mAdapter = null;
	private List<ShawnRainDto> mList = new ArrayList<ShawnRainDto>();
	private LinearLayout llBottom = null;
	private ScrollView scrollView = null;
	private LinearLayout llViewPager = null;
	private MainViewPager viewPager = null;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private LinearLayout llRainCheck = null;
	private TextView tv1, tv2, tv3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_rain);
		mContext = this;
		initListView();
		initWidget();
		initAmap(savedInstanceState);
	}
	
	private void initWidget() {
		reTitle = (RelativeLayout) findViewById(R.id.reTitle);
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvControl = (TextView) findViewById(R.id.tvControl);
		tvControl.setOnClickListener(this);
		llContainer2 = (LinearLayout) findViewById(R.id.llContainer2);
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		llContainer1 = (LinearLayout) findViewById(R.id.llContainer1);
		tvLayerName = (TextView) findViewById(R.id.tvLayerName);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		ivChart = (ImageView) findViewById(R.id.ivChart);
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		tvDetail.setOnClickListener(this);
		tvHistory = (TextView) findViewById(R.id.tvHistory);
		tvHistory.setOnClickListener(this);
		tvToast = (TextView) findViewById(R.id.tvToast);
		tvIntro = (TextView) findViewById(R.id.tvIntro);
		listTitle = (LinearLayout) findViewById(R.id.listTitle);
		llBottom = (LinearLayout) findViewById(R.id.llBottom);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		llViewPager = (LinearLayout) findViewById(R.id.llViewPager);
		llRainCheck = (LinearLayout) findViewById(R.id.llRainCheck);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		density = dm.density;
		
		String result = getIntent().getStringExtra(CHANNEL_DATA);
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject obj = new JSONObject(result);
				if (!obj.isNull("title")) {
					tvTitle.setText(obj.getString("title"));
				}
				if (!obj.isNull("child")) {
					dataList.clear();
					JSONArray array = obj.getJSONArray("child");
					for (int i = 0; i < array.length(); i++) {
						JSONObject itemObj = array.getJSONObject(i);
						ShawnRainDto dto = new ShawnRainDto();
						if (!itemObj.isNull("title")) {
							dto.title = itemObj.getString("title");
						}
						if (!itemObj.isNull("icon")) {
							dto.icon1 = itemObj.getString("icon");
						}
						if (!itemObj.isNull("icon1")) {
							dto.icon2 = itemObj.getString("icon1");
						}
						if (!itemObj.isNull("child")) {
							List<ShawnRainDto> itemList = new ArrayList<ShawnRainDto>();
							itemList.clear();
							JSONArray itemArray = itemObj.getJSONArray("child");
							for (int j = 0; j < itemArray.length(); j++) {
								JSONObject childObj = itemArray.getJSONObject(j);
								ShawnRainDto itemDto = new ShawnRainDto();
								if (!childObj.isNull("id")) {
									itemDto.id = childObj.getString("id");
								}
								if (!childObj.isNull("title")) {
									itemDto.title = childObj.getString("title");
								}
								if (!childObj.isNull("dataUrl")) {
									itemDto.dataUrl = childObj.getString("dataUrl");
								}
								itemList.add(itemDto);
							}
							dto.itemList.addAll(itemList);
						}
						dataList.add(dto);
					}
					
					llContainer2.removeAllViews();
					if (dataList.size() > 0) {
						for (int i = 0; i < dataList.size(); i++) {
							ShawnRainDto dto = dataList.get(i);
							LinearLayout llItem = new LinearLayout(mContext);
							llItem.setGravity(Gravity.CENTER);
							llItem.setOrientation(LinearLayout.HORIZONTAL);
							if (!TextUtils.isEmpty(dto.title)) {
								llItem.setTag(dto.title);
							}
							
							TextView tvItem = new TextView(mContext);
							tvItem.setGravity(Gravity.CENTER);
							tvItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
							tvItem.setPadding(0, (int)(density*10), 0, (int)(density*10));
							tvItem.setMaxLines(1);
							if (!TextUtils.isEmpty(dto.title)) {
								tvItem.setText(dto.title);
							}

							ImageView imageView = new ImageView(mContext);
							
							if (i == 0) {
								llItem.setBackgroundColor(getResources().getColor(R.color.white));
								if (!TextUtils.isEmpty(dto.icon2)) {
									FinalBitmap finalBitmap = FinalBitmap.create(mContext);
									finalBitmap.display(imageView, dto.icon2, null, 0);
									LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams((int)(CommonUtil.dip2px(mContext, 18)), (int)(CommonUtil.dip2px(mContext, 18)));
									ivParams.rightMargin = (int) CommonUtil.dip2px(mContext, 5);
									imageView.setLayoutParams(ivParams);
								}
								tvItem.setTextColor(getResources().getColor(R.color.main_color));
								switchItem(dto);
							}else {
								llItem.setBackgroundColor(getResources().getColor(R.color.gray));
								if (!TextUtils.isEmpty(dto.icon1)) {
									FinalBitmap finalBitmap = FinalBitmap.create(mContext);
									finalBitmap.display(imageView, dto.icon1, null, 0);
									LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams((int)(CommonUtil.dip2px(mContext, 18)), (int)(CommonUtil.dip2px(mContext, 18)));
									ivParams.rightMargin = (int) CommonUtil.dip2px(mContext, 5);
									imageView.setLayoutParams(ivParams);
								}
								tvItem.setTextColor(getResources().getColor(R.color.text_color3));
							}
							llItem.addView(imageView);
							llItem.addView(tvItem);
							llContainer2.addView(llItem);
							LayoutParams params = llItem.getLayoutParams();
							if (dataList.size() <= 3) {
								params.width = width/3;
							}else {
								params.width = width/4;
							}
							llItem.setLayoutParams(params);
							llItem.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if (llContainer2 != null) {
										for (int i = 0; i < llContainer2.getChildCount(); i++) {
											LinearLayout llItem = (LinearLayout) llContainer2.getChildAt(i);
											ShawnRainDto dto = dataList.get(i);
											ImageView imageView = (ImageView) llItem.getChildAt(0);
											TextView tvItem = (TextView) llItem.getChildAt(1);
											if (TextUtils.equals((String) arg0.getTag(), (String) llItem.getTag())) {
												llItem.setBackgroundColor(getResources().getColor(R.color.white));
												if (!TextUtils.isEmpty(dto.icon2)) {
													FinalBitmap finalBitmap = FinalBitmap.create(mContext);
													finalBitmap.display(imageView, dto.icon2, null, 0);
													LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams((int)(CommonUtil.dip2px(mContext, 18)), (int)(CommonUtil.dip2px(mContext, 18)));
													ivParams.rightMargin = (int) CommonUtil.dip2px(mContext, 5);
													imageView.setLayoutParams(ivParams);
												}
												tvItem.setTextColor(getResources().getColor(R.color.main_color));
												switchItem(dto);
											}else {
												llItem.setBackgroundColor(getResources().getColor(R.color.gray));
												if (!TextUtils.isEmpty(dto.icon1)) {
													FinalBitmap finalBitmap = FinalBitmap.create(mContext);
													finalBitmap.display(imageView, dto.icon1, null, 0);
													LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams((int)(CommonUtil.dip2px(mContext, 18)), (int)(CommonUtil.dip2px(mContext, 18)));
													ivParams.rightMargin = (int) CommonUtil.dip2px(mContext, 5);
													imageView.setLayoutParams(ivParams);
												}
												tvItem.setTextColor(getResources().getColor(R.color.text_color4));
											}
										}
									}
								}
							});
						}
					}
					
					reTitle.setFocusable(true);
					reTitle.setFocusableInTouchMode(true);
					reTitle.requestFocus();
					
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (getIntent().hasExtra("columnId")) {
			String columnId = getIntent().getStringExtra("columnId");
			StatisticUtil.statisticClickCount(columnId);
		}
	}
	
	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		if (viewPager != null) {
			viewPager.removeAllViewsInLayout();
			fragments.clear();
		}

		llRainCheck.removeAllViews();
		for (int i = 0; i < 2; i++) {
			TextView tvName = new TextView(mContext);
			if (i == 0) {
				tvName.setText("近7天分钟查询");
				tvName.setBackgroundColor(Color.WHITE);
				tvName.setTextColor(getResources().getColor(R.color.text_color3));
			}else {
				tvName.setText("近3个月逐时查询");
				tvName.setBackgroundColor(getResources().getColor(R.color.light_gray2));
				tvName.setTextColor(getResources().getColor(R.color.text_color4));
			}
			tvName.setOnClickListener(new MyOnClickListener(i));
			tvName.setGravity(Gravity.CENTER);
			tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			tvName.setPadding(0, (int)(density*10), 0, (int)(density*10));
			tvName.setMaxLines(1);
			tvName.setTag(i);
			llRainCheck.addView(tvName);
			LayoutParams params = tvName.getLayoutParams();
			params.width = width/2;
			tvName.setLayoutParams(params);
		}

		Bundle bundle = new Bundle();
		bundle.putString("childId", childId);
		Fragment fragment1 = new ShawnRainCheckMinuteFragment();
		fragment1.setArguments(bundle);
		fragments.add(fragment1);
		Fragment fragment2 = new ShawnRainCheckHourFragment();
		fragment2.setArguments(bundle);
		fragments.add(fragment2);

		if (viewPager == null) {
			viewPager = (MainViewPager) findViewById(R.id.viewPager);
			viewPager.setSlipping(true);//设置ViewPager是否可以滑动
			viewPager.setOffscreenPageLimit(fragments.size());
			viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		}
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			if (llRainCheck != null) {
				for (int i = 0; i < llRainCheck.getChildCount(); i++) {
					TextView tv = (TextView) llRainCheck.getChildAt(i);
					if (i == arg0) {
						tv.setBackgroundColor(Color.WHITE);
						tv.setTextColor(getResources().getColor(R.color.text_color3));
					}else {
						tv.setBackgroundColor(getResources().getColor(R.color.light_gray2));
						tv.setTextColor(getResources().getColor(R.color.text_color4));
					}
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * 头标点击监听
	 * @author shawn_sun
	 */
	private class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (viewPager != null) {
				viewPager.setCurrentItem(index, true);
			}
		}
	}

	/**
	 * @ClassName: MyPagerAdapter
	 * @Description: TODO填充ViewPager的数据适配器
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:37:47
	 *
	 */
	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
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
		
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.211397,109.795324), 7.8f));
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setOnCameraChangeListener(this);
		aMap.getUiSettings().setRotateGesturesEnabled(false);
		aMap.showMapText(false);
		
		aMap.setOnMapTouchListener(new OnMapTouchListener() {
			@Override
			public void onTouch(MotionEvent arg0) {
				if (scrollView != null) {
					if (arg0.getAction() == MotionEvent.ACTION_UP) {
						scrollView.requestDisallowInterceptTouchEvent(false);
					}else {
						scrollView.requestDisallowInterceptTouchEvent(true);
					}
				}
			}
		});
		
		LatLngBounds bounds = new LatLngBounds.Builder()
//		.include(new LatLng(57.9079, 71.9282))
//		.include(new LatLng(3.9079, 134.8656))
		.include(new LatLng(1, 66))
		.include(new LatLng(60, 153))
		.build();
		aMap.addGroundOverlay(new GroundOverlayOptions()
			.anchor(0.5f, 0.5f)
			.positionFromBounds(bounds)
			.image(BitmapDescriptorFactory.fromResource(R.drawable.empty))
			.transparency(0.0f));
		aMap.runOnDrawFrame();
		
	}
	
	private void switchItem(ShawnRainDto dto) {
		llContainer.removeAllViews();
		llContainer1.removeAllViews();
		if (dto.itemList.size() > 0) {
			for (int j = 0; j < dto.itemList.size(); j++) {
				final ShawnRainDto itemDto = dto.itemList.get(j);
				TextView tvName = new TextView(mContext);
				tvName.setGravity(Gravity.CENTER);
				tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
				tvName.setPadding(0, (int)(density*10), 0, (int)(density*10));
				tvName.setMaxLines(1);
				
				TextView tvBar = new TextView(mContext);
				tvBar.setGravity(Gravity.CENTER);
				
				if (!TextUtils.isEmpty(itemDto.title)) {
					tvName.setText(itemDto.title);
					tvName.setTag(itemDto.dataUrl+","+itemDto.id);
				}
				if (j == 0) {
					childId = itemDto.id;
					if (mAdapter != null) {
						mAdapter.childId = itemDto.id;
					}

					//648降水实况，656温度实况，657最高温，658最低温， 655极大风
					if (TextUtils.equals(itemDto.id, "648") || TextUtils.equals(itemDto.id, "656")  || TextUtils.equals(itemDto.id, "657")
							|| TextUtils.equals(itemDto.id, "658")  || TextUtils.equals(itemDto.id, "655")) {//降水查询
						llViewPager.setVisibility(View.VISIBLE);
						scrollView.setVisibility(View.GONE);
						initViewPager();
					}else {
						llViewPager.setVisibility(View.GONE);
						scrollView.setVisibility(View.VISIBLE);
						layerName = tvName.getText().toString();
						if (!TextUtils.isEmpty(itemDto.dataUrl)) {
							url = itemDto.dataUrl;
							progressBar.setVisibility(View.VISIBLE);
							asyncTask(url);
						}
					}
					tvName.setTextColor(getResources().getColor(R.color.main_color));
					tvBar.setBackgroundColor(getResources().getColor(R.color.main_color));
				}else {
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
				llContainer.addView(tvName);
				LayoutParams params = tvName.getLayoutParams();
				if (dto.itemList.size() <= 3) {
					params.width = width/3;
				}else {
					params.width = width/4;
				}
				tvName.setLayoutParams(params);
				
				llContainer1.addView(tvBar);
				LayoutParams params1 = tvBar.getLayoutParams();
				if (dto.itemList.size() <= 3) {
					params1.width = width/3;
				}else {
					params1.width = width/4;
				}
				params1.height = (int) (density*2);
				tvBar.setLayoutParams(params1);
				
				tvName.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (llContainer != null) {
							for (int i = 0; i < llContainer.getChildCount(); i++) {
								TextView tvName = (TextView) llContainer.getChildAt(i);
								TextView tvBar = (TextView) llContainer1.getChildAt(i);
								if (TextUtils.equals((String) arg0.getTag(), (String) tvName.getTag())) {
									String[] tags = ((String) arg0.getTag()).split(",");
									childId = itemDto.id;
									if (mAdapter != null) {
										mAdapter.childId = itemDto.id;
									}

									//648降水实况，656温度实况，657最高温，658最低温， 655极大风
									if (TextUtils.equals(itemDto.id, "648") || TextUtils.equals(itemDto.id, "656")  || TextUtils.equals(itemDto.id, "657")
											|| TextUtils.equals(itemDto.id, "658")  || TextUtils.equals(itemDto.id, "655")) {//降水查询
										llViewPager.setVisibility(View.VISIBLE);
										scrollView.setVisibility(View.GONE);
										initViewPager();
									} else {
										llViewPager.setVisibility(View.GONE);
										scrollView.setVisibility(View.VISIBLE);
										url = tags[0];
										layerName = tvName.getText().toString();
									}
									tvName.setTextColor(getResources().getColor(R.color.main_color));
									tvBar.setBackgroundColor(getResources().getColor(R.color.main_color));
								}else {
									tvName.setTextColor(getResources().getColor(R.color.text_color4));
									tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
								}
							}
							
							if (!TextUtils.isEmpty(url)) {
								progressBar.setVisibility(View.VISIBLE);
								asyncTask(url);
							}
						}
					}
				});
			}
		}
	}
	
	private void initListView() {
		listView = (ListView) findViewById(R.id.listView);
		mAdapter = new ShawnRainAdapter(mContext, mList);
		listView.setAdapter(mAdapter);
	}
	
	/**
     * 异步解析五中天气现象数据并绘制在地图上
     */
	private void drawCityName() {
		removeCityNames();
		if (aMap == null) {
			return;
		}
		String result = Utils.getFromAssets(mContext, "hnGeo2.json");
		if (!TextUtils.isEmpty(result)) {
			AsynLoadTaskDistrict task = new AsynLoadTaskDistrict(result);  
			task.execute();
		}
	}
	
	private class AsynLoadTaskDistrict extends AsyncTask<Void, Void, Void> {
		
		private String result = null;
		
		private AsynLoadTaskDistrict(String result) {
			this.result = result;
		}

		@Override
		protected void onPreExecute() {
			//开始执行
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//执行完毕
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				JSONObject obj = new JSONObject(result);
//				if (!obj.isNull("polyline")) {
//					String polyline = obj.getString("polyline");
//					String[] array1 = polyline.split("\\|");
//					for (int i = 0; i < 1; i++) {
//						String[] array2 = array1[i].split(";");
//						PolygonOptions polylineOption = new PolygonOptions();
//						polylineOption.strokeColor(0xff999999);
//						for (int j = 0; j < array2.length; j++) {
//							String[] array3 = array2[i].split(",");
//							if (!TextUtils.isEmpty(array3[0]) && !TextUtils.isEmpty(array3[1])) {
//								double lng = Double.valueOf(array3[0]);
//								double lat = Double.valueOf(array3[1]);
//								polylineOption.add(new LatLng(lat, lng));
//							}
//						}
//						aMap.addPolygon(polylineOption);
//					}
//				}
				if (!obj.isNull("districts")) {
					JSONArray array = obj.getJSONArray("districts");
					nameList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject itemObj = array.getJSONObject(i);
						ShawnRainDto dto = new ShawnRainDto();
						if (!itemObj.isNull("name")) {
							String name = itemObj.getString("name");
							if (name.contains("五指山")) {
								dto.cityName = name.substring(0, 3);
							}else {
								dto.cityName = name.substring(0, 2);
							}
						}
						if (!itemObj.isNull("center")) {
							String[] latLng = itemObj.getString("center").split(",");
							dto.lng = Double.valueOf(latLng[0]);
							dto.lat = Double.valueOf(latLng[1]);
						}
						nameList.add(dto);
					}
					
					Message msg = new Message();
					msg.what = 101;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Point leftPoint = new Point(0, dm.heightPixels);
		Point rightPoint = new Point(dm.widthPixels, 0);
		LatLng leftlatlng = aMap.getProjection().fromScreenLocation(leftPoint);
		LatLng rightLatlng = aMap.getProjection().fromScreenLocation(rightPoint);
		
		if (listView != null) {
			if (listView.getVisibility() == View.VISIBLE) {
				if (leftlatlng.latitude <= 3.9079 || rightLatlng.latitude >= 57.9079 || leftlatlng.longitude <= 71.9282
						|| rightLatlng.longitude >= 134.8656 || arg0.zoom < 7.8f) {
					aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.211397,109.795324), 7.8f));
				}
			}else {
				if (leftlatlng.latitude <= 3.9079 || rightLatlng.latitude >= 57.9079 || leftlatlng.longitude <= 71.9282
						|| rightLatlng.longitude >= 134.8656 || arg0.zoom < 8.2f) {
					aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.211397,109.795324), 8.2f));
				}
			}
		}else {
			if (leftlatlng.latitude <= 3.9079 || rightLatlng.latitude >= 57.9079 || leftlatlng.longitude <= 71.9282
					|| rightLatlng.longitude >= 134.8656 || arg0.zoom < 7.8f) {
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.211397,109.795324), 7.8f));
			}
		}
	}
	
	private void asyncTask(String url) {
		//异步请求数据
		HttpAsyncTaskUrl task = new HttpAsyncTaskUrl();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskUrl extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskUrl() {
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
				try {
					JSONObject obj = new JSONObject(result);
					
					if (!obj.isNull("th")) {
						JSONObject itemObj = obj.getJSONObject("th");
						if (!itemObj.isNull("stationName")) {
							stationName = itemObj.getString("stationName");
						}
						if (!itemObj.isNull("area")) {
							area = itemObj.getString("area");
						}
						if (!itemObj.isNull("val")) {
							val = itemObj.getString("val");
						}
					}

					if (!obj.isNull("zh")) {
						JSONObject itemObj = obj.getJSONObject("zh");
						if (!itemObj.isNull("stationName")) {
							tv3.setText(itemObj.getString("stationName"));
						}
						if (!itemObj.isNull("area")) {
							tv2.setText(itemObj.getString("area"));
						}
						if (!itemObj.isNull("val")) {
							tv1.setText(itemObj.getString("val"));
						}
					}
					
					if (!obj.isNull("title")) {
						title = obj.getString("title");
					}
					
					if (!obj.isNull("cutlineUrl")) {
						FinalBitmap finalBitmap = FinalBitmap.create(mContext);
						finalBitmap.display(ivChart, obj.getString("cutlineUrl"), null, 0);
					}
					
					if (!obj.isNull("times")) {
						times.clear();
						JSONArray array = new JSONArray(obj.getString("times"));
						for (int i = 0; i < array.length(); i++) {
							JSONObject itemObj = array.getJSONObject(i);
							ShawnRainDto dto = new ShawnRainDto();
							if (!itemObj.isNull("timeString")) {
								dto.timeString = itemObj.getString("timeString");
								if (i == selectId) {
									startTime = itemObj.getString("timestart");
									endTime = itemObj.getString("timeParams");
									tvLayerName.setText(dto.timeString);
									tvLayerName.setVisibility(View.VISIBLE);
									if (layerName != null) {
										tvLayerName.setText(dto.timeString+layerName);
									}
								}
							}
							if (!itemObj.isNull("timeParams")) {
								dto.timeParams = itemObj.getString("timeParams");
							}
							times.add(dto);
						}
					}
					
					if (!obj.isNull("realDatas")) {
						realDatas.clear();
						JSONArray array = new JSONArray(obj.getString("realDatas"));
						for (int i = 0; i < array.length(); i++) {
							JSONObject itemObj = array.getJSONObject(i);
							ShawnRainDto dto = new ShawnRainDto();
							if (!itemObj.isNull("stationCode")) {
								dto.stationCode = itemObj.getString("stationCode");
							}
							if (!itemObj.isNull("stationName")) {
								dto.stationName = itemObj.getString("stationName");
							}
							if (!itemObj.isNull("area")) {
								dto.area = itemObj.getString("area");
							}
							if (!itemObj.isNull("val")) {
								dto.val = itemObj.getDouble("val");
							}

							if (!TextUtils.isEmpty(dto.stationName) && !TextUtils.isEmpty(dto.area)) {
								realDatas.add(dto);
							}
						}
					}
					
					if (!obj.isNull("dataUrl")) {
						String dataUrl = obj.getString("dataUrl");
						if (!TextUtils.isEmpty(dataUrl)) {
							asyncTaskJson(dataUrl);
						}
					}
					
					if (!obj.isNull("zx")) {
						tvIntro.setText(obj.getString("zx"));
					}
					
					if (!obj.isNull("jb")) {
						mList.clear();
						JSONArray array = obj.getJSONArray("jb");
						for (int i = 0; i < array.length(); i++) {
							JSONObject itemObj = array.getJSONObject(i);
							ShawnRainDto data = new ShawnRainDto();
							if (!itemObj.isNull("lv")) {
								data.rainLevel = itemObj.getString("lv");
							}
							if (!itemObj.isNull("count")) {
								data.count = itemObj.getInt("count")+"";
							}
							if (!itemObj.isNull("xs")) {
								JSONArray xsArray = itemObj.getJSONArray("xs");
								List<ShawnRainDto> list = new ArrayList<ShawnRainDto>();
								list.clear();
								for (int j = 0; j < xsArray.length(); j++) {
									ShawnRainDto d = new ShawnRainDto();
									d.area = xsArray.getString(j);
									list.add(d);
								}
								data.areaList.addAll(list);
							}
							mList.add(data);
						}
						if (mList.size() > 0 && mAdapter != null) {
							CommonUtil.setListViewHeightBasedOnChildren(listView);
							mAdapter.startTime = startTime;
							mAdapter.endTime = endTime;
							mAdapter.notifyDataSetChanged();
							tvIntro.setVisibility(View.VISIBLE);
							listTitle.setVisibility(View.VISIBLE);
							listView.setVisibility(View.VISIBLE);
						}
					}else {
						tvIntro.setVisibility(View.GONE);
						listTitle.setVisibility(View.GONE);
						listView.setVisibility(View.GONE);
					}
					
					int statusBarHeight = -1;  
					//获取status_bar_height资源的ID  
					int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");  
					if (resourceId > 0) {  
					    //根据资源ID获取响应的尺寸值  
						statusBarHeight = getResources().getDimensionPixelSize(resourceId);  
					}  
					int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
			        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
					reTitle.measure(w, h);
					llContainer2.measure(w, h);
					llContainer.measure(w, h);
					listTitle.measure(w, h);
					llBottom.measure(w, h);
					LayoutParams mapParams = mapView.getLayoutParams();
					if (listView.getVisibility() == View.VISIBLE) {
						mapParams.height = height-statusBarHeight-reTitle.getMeasuredHeight()-llContainer2.getMeasuredHeight()
								-llContainer.getMeasuredHeight()-listTitle.getMeasuredHeight()*8;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.211397,109.795324), 7.8f));
							}
						}, 500);
					}else {
						mapParams.height = height-statusBarHeight-reTitle.getMeasuredHeight()-llContainer2.getMeasuredHeight()
								-llContainer.getMeasuredHeight()-llBottom.getMeasuredHeight();
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.211397,109.795324), 8.2f));
							}
						}, 500);
					}
					mapView.setLayoutParams(mapParams);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else {
				drawCityName();
				removePolygons();
				progressBar.setVisibility(View.GONE);
				tvToast.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						tvToast.setVisibility(View.GONE);
					}
				}, 1000);
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
	
	private void asyncTaskJson(String url) {
		//异步请求数据
		HttpAsyncTaskJson task = new HttpAsyncTaskJson();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskJson extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskJson() {
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
				drawDataToMap(result);
			}else {
				drawCityName();
				removePolygons();
				progressBar.setVisibility(View.GONE);
				tvToast.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						tvToast.setVisibility(View.GONE);
					}
				}, 1000);
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
	
	private void removePolygons() {
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).remove();
		}
		polygons.clear();
	}
	
	private void removeTexts() {
		for (int i = 0; i < texts.size(); i++) {
			texts.get(i).remove();
		}
		texts.clear();
	}
	
	/**
	 * 回执区域
	 */
	private void drawDataToMap(String result) {
		if (TextUtils.isEmpty(result) || aMap == null) {
			return;
		}
		removeTexts();
		removePolygons();
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONArray array = obj.getJSONArray("l");
			int length = array.length();
//			if (length > 200) {
//				length = 200;
//			}
			for (int i = 0; i < length; i++) {
				JSONObject itemObj = array.getJSONObject(i);
				JSONArray c = itemObj.getJSONArray("c");
				int r = c.getInt(0);
				int g = c.getInt(1);
				int b = c.getInt(2);
				int a = (int) (c.getInt(3)*255*1.0);
				
				double centerLat = 0;
				double centerLng = 0;
				String p = itemObj.getString("p");
				if (!TextUtils.isEmpty(p)) {
					String[] points = p.split(";");
					PolygonOptions polygonOption = new PolygonOptions();
					polygonOption.fillColor(Color.argb(a, r, g, b));
					polygonOption.strokeColor(Color.BLACK);
					polygonOption.strokeWidth(1);
					for (int j = 0; j < points.length; j++) {
						String[] value = points[j].split(",");
						double lat = Double.valueOf(value[1]);
						double lng = Double.valueOf(value[0]);
						polygonOption.add(new LatLng(lat, lng));
						if (j == points.length/2) {
							centerLat = lat;
							centerLng = lng;
						}
					}
					Polygon polygon = aMap.addPolygon(polygonOption);
					polygons.add(polygon);
				}
				
				if (!itemObj.isNull("v")) {
					int v = itemObj.getInt("v");
					TextOptions options = new TextOptions();
					options.position(new LatLng(centerLat, centerLng));
					options.fontColor(Color.BLACK);
					options.fontSize(20);
					options.text(v+"");
					options.backgroundColor(Color.TRANSPARENT);
					Text text = aMap.addText(options);
					texts.add(text);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Message msg = new Message();
		msg.what = 100;
		handler.sendMessage(msg);
	}
	
	private void removeCityNames() {
		for (int i = 0; i < cityNames.size(); i++) {
			cityNames.get(i).remove();
		}
		cityNames.clear();
		
		for (int i = 0; i < circles.size(); i++) {
			circles.get(i).remove();
		}
		circles.clear();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100:
//				tvHistory.setVisibility(View.VISIBLE);
//				tvDetail.setVisibility(View.VISIBLE);
//				ivChart.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				drawCityName();
				break;
			case 101:
				for (int i = 0; i < nameList.size(); i++) {
					ShawnRainDto dto = nameList.get(i);
					TextOptions options = new TextOptions();
					options.position(new LatLng(dto.lat+0.05, dto.lng));
					options.fontColor(Color.BLACK);
					options.fontSize(20);
					options.text(dto.cityName);
					options.backgroundColor(Color.TRANSPARENT);
					Text text = aMap.addText(options);
					cityNames.add(text);
					
					CircleOptions cOptions = new CircleOptions();
					cOptions.center(new LatLng(dto.lat, dto.lng));
					cOptions.fillColor(Color.BLACK);
					cOptions.radius(50.0f);
					Circle circle = aMap.addCircle(cOptions);
					circles.add(circle);
				}
				
				CommonUtil.drawAllDistrict(mContext, aMap, 0xff72e5f3, polylines);
				break;

			default:
				break;
			}
		};
	};
	
	private void dialogHistory() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_detail, null);
		TextView tvNegative = (TextView) view.findViewById(R.id.tvNegative);
		ListView listView = (ListView) view.findViewById(R.id.listView);
		DialogDetailAdapter mAdapter = new DialogDetailAdapter(mContext, times);
		listView.setAdapter(mAdapter);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ShawnRainDto dto = times.get(arg2);
				selectId = arg2;
				if (!TextUtils.isEmpty(url)) {
					progressBar.setVisibility(View.VISIBLE);
					asyncTask(url+dto.timeParams);
				}
				dialog.dismiss();
			}
		});
		
		tvNegative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.tvDetail:
			Intent intent = new Intent(mContext, ShawnRainDetailActivity.class);
			intent.putExtra("childId", childId);
			intent.putExtra("title", title);
			intent.putExtra("stationName", stationName);
			intent.putExtra("area", area);
			intent.putExtra("val", val);
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			intent.putParcelableArrayListExtra("realDatas", (ArrayList<? extends Parcelable>) realDatas);
			startActivity(intent);
			break;
		case R.id.tvHistory:
			dialogHistory();
			break;
		case R.id.tvControl:
			startActivity(new Intent(mContext, StaticsActivity.class));
			break;

		default:
			break;
		}
	}
	
}
