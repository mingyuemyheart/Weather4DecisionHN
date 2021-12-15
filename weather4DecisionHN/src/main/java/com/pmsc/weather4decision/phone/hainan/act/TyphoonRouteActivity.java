package com.pmsc.weather4decision.phone.hainan.act;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.android.lib.data.CONST;
import com.android.lib.util.CaiyunManager;
import com.android.lib.util.CaiyunManager.RadarListener;
import com.android.lib.util.CommonUtil;
import com.android.lib.util.RainManager;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.TyphoonNameAdapter;
import com.pmsc.weather4decision.phone.hainan.adapter.TyphoonYearAdapter;
import com.pmsc.weather4decision.phone.hainan.dto.MinuteFallDto;
import com.pmsc.weather4decision.phone.hainan.dto.TyphoonDto;
import com.pmsc.weather4decision.phone.hainan.dto.WindDto;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.pmsc.weather4decision.phone.hainan.util.StatisticUtil;
import com.pmsc.weather4decision.phone.hainan.view.WaitWindView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class TyphoonRouteActivity extends AbsDrawerActivity implements OnClickListener, OnMapClickListener,
OnMarkerClickListener, InfoWindowAdapter, RadarListener, OnCameraChangeListener, AMapLocationListener {
	
	private Context mContext = null;
	private TextView tvTyphoonName,tvMapNumber;
	private MapView mapView = null;
	private AMap aMap = null;
	private ImageView ivLegend = null;//台风标注图
	private RelativeLayout reLegend = null;
	private ImageView ivCancelLegend = null;
	private ImageView ivTyphoonList = null;
	private RelativeLayout reTyphoonList = null;
	private ImageView ivCancelList = null;
	private ListView yearListView = null;
	private TyphoonYearAdapter yearAdapter = null;
	private List<TyphoonDto> yearList = new ArrayList<>();
	private ListView nameListView = null;
	private TyphoonNameAdapter nameAdapter = null;
	private List<TyphoonDto> nameList = new ArrayList<>();//某一年所有台风
	private List<TyphoonDto> startList = new ArrayList<>();//某一年活跃台风
	private List<ArrayList<TyphoonDto>> pointsList = new ArrayList<>();//存放某一年所有活跃台风
//	private List<TyphoonDto> points = new ArrayList<TyphoonDto>();//某个台风的数据点
//	private List<TyphoonDto> forePoints = new ArrayList<TyphoonDto>();//预报的点数据
	private RoadThread mRoadThread = null;//绘制台风点的线程
//	private Marker rotateMarker = null;//台风旋转marker
	private Marker clickMarker = null;//被点击的marker
	private float zoom = 5.0f;
	private List<Polygon> windCirclePolygons = new ArrayList<>();//风圈
	private List<Polyline> fullLines = new ArrayList<Polyline>();//实线数据
	private List<Polyline> dashLines = new ArrayList<Polyline>();//虚线数据
	private List<Marker> markerPoints = new ArrayList<Marker>();//台风点数据
	private List<Marker> markerTimes = new ArrayList<Marker>();//预报点时间数据
	private ImageView ivTyphoonPlay = null;//台风回放按钮
	private int MSG_PAUSETYPHOON = 100;//暂停台风
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
	private ProgressBar progressBar = null;
	private ImageView ivTyphoonRadar = null;
	private ImageView ivTyphoonCloud = null;
	private ImageView ivTyphoonWind = null;
	private TextView tvFileTime = null;
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHH");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日HH时");
	private SimpleDateFormat sdf4 = new SimpleDateFormat("dd日HH时");
	private SimpleDateFormat sdf5 = new SimpleDateFormat("MM月dd日HH时");
	private boolean isRadarOn = false;
	private boolean isCloudOn = false;
	private boolean isWindOn = false;
	private CaiyunManager mRadarManager = null;
	private List<MinuteFallDto> radarList = new ArrayList<MinuteFallDto>();
	private RadarThread mRadarThread = null;
	private static final int HANDLER_SHOW_RADAR = 1;
	private static final int HANDLER_LOAD_FINISHED = 3;
	private GroundOverlay radarOverlay = null;
	private GroundOverlay cloudOverlay = null;
	private Bitmap cloudBitmap = null;
	private LatLng leftLatLng = null,rightLatLng = null;
	private RelativeLayout container = null;
	public RelativeLayout container2 = null;
	private int width = 0, height = 0;
	private WaitWindView waitWindView = null;
	private GroundOverlay windOverlay = null;
	private boolean isHaveWindData = false;//是否已经加载完毕风场数据
	private List<Marker> factTimeMarkers = new ArrayList<Marker>();
	private List<Marker> timeMarkers = new ArrayList<Marker>();//预报时间markers
	private List<Marker> rotateMarkers = new ArrayList<Marker>();
	private List<Marker> infoMarkers = new ArrayList<Marker>();
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private ImageView ivTyphoonRange = null;//台风测距
	private Marker locationMarker = null;
	private Map<String, List<Polyline>> rangeLinesMap = new HashMap<>();//测距虚线数据
	private Map<String, Marker> rangeMarkersMap = new HashMap<>();//测距中点距离marker
	private Map<String, TyphoonDto> lastFactPointMap = new HashMap<>();//最后一个实况点数据集合
	private boolean isRanging = false;//是否允许测距
	private LatLng locationLatLng = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.typhoon_route);
		mContext = this;
		initWidget();
		initAmap(savedInstanceState);
		initYearListView();
	}
	
	private void initWidget() {
		tvTyphoonName = (TextView) findViewById(R.id.tvTyphoonName);
		ivLegend = (ImageView) findViewById(R.id.ivLegend);
		ivLegend.setOnClickListener(this);
		reLegend = (RelativeLayout) findViewById(R.id.reLegend);
		ivCancelLegend = (ImageView) findViewById(R.id.ivCancelLegend);
		ivCancelLegend.setOnClickListener(this);
		ivTyphoonList = (ImageView) findViewById(R.id.ivTyphoonList);
		ivTyphoonList.setOnClickListener(this);
		reTyphoonList = (RelativeLayout) findViewById(R.id.reTyphoonList);
		ivCancelList = (ImageView) findViewById(R.id.ivCancelList);
		ivCancelList.setOnClickListener(this);
		ivTyphoonPlay = (ImageView) findViewById(R.id.ivTyphoonPlay);
		ivTyphoonPlay.setOnClickListener(this);
		ivTyphoonRange =  (ImageView) findViewById(R.id.ivTyphoonRange);
		ivTyphoonRange.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		ivTyphoonWind = (ImageView) findViewById(R.id.ivTyphoonWind);
		ivTyphoonWind.setOnClickListener(this);
		ivTyphoonRadar = (ImageView) findViewById(R.id.ivTyphoonRadar);
		ivTyphoonRadar.setOnClickListener(this);
		ivTyphoonCloud = (ImageView) findViewById(R.id.ivTyphoonCloud);
		ivTyphoonCloud.setOnClickListener(this);
		container = (RelativeLayout) findViewById(R.id.container);
		container2 = (RelativeLayout) findViewById(R.id.container2);
		tvFileTime = (TextView) findViewById(R.id.tvFileTime);
		tvMapNumber = (TextView) findViewById(R.id.tvMapNumber);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;

		if (getIntent().hasExtra("columnId")) {
			String columnId = getIntent().getStringExtra("columnId");
			StatisticUtil.statisticClickCount(columnId);
		}
	}
	
	private void initAmap(Bundle bundle) {
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(bundle);
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		LatLng latLng = new LatLng(17.031908,118.566315);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
		aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setRotateGesturesEnabled(false);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnMapClickListener(this);
		aMap.setInfoWindowAdapter(this);
		aMap.setOnCameraChangeListener(this);

		tvMapNumber.setText(aMap.getSatelliteImageApprovalNumber());

		startLocation();
		
		drawWarningLines();
	}

	/**
	 * 开始定位
	 */
	private void startLocation() {
		mLocationOption = new AMapLocationClientOption();//初始化定位参数
		mLocationClient = new AMapLocationClient(mContext);//初始化定位
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
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
			if (amapLocation.getLongitude() != 0 && amapLocation.getLatitude() != 0) {
				locationLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
				addLocationMarker(locationLatLng);
			}
		}
	}

	private void addLocationMarker(LatLng latLng) {
		if (latLng == null) {
			return;
		}
		MarkerOptions options = new MarkerOptions();
		options.position(latLng);
		options.anchor(0.5f, 0.5f);
		Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.iv_map_location),
				(int)(CommonUtil.dip2px(mContext, 15)), (int)(CommonUtil.dip2px(mContext, 15)));
		if (bitmap != null) {
			options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
		}else {
			options.icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_map_location));
		}
		if (locationMarker != null) {
			locationMarker.remove();
		}
		locationMarker = aMap.addMarker(options);
		locationMarker.setClickable(false);
	}
	
	/**
	 * 绘制24h、48h警戒线
	 */
	private void drawWarningLines() {
		//24小时
		PolylineOptions line1 = new PolylineOptions();
		line1.width(CommonUtil.dip2px(mContext, 2));
		line1.color(getResources().getColor(R.color.red));
		line1.add(new LatLng(34.005024, 126.993568), new LatLng(21.971252, 126.993568));
		line1.add(new LatLng(17.965860, 118.995521), new LatLng(10.971050, 118.995521));
		line1.add(new LatLng(4.486270, 113.018959) ,new LatLng(-0.035506, 104.998939));
		aMap.addPolyline(line1);
		drawWarningText(getString(R.string.line_24h), getResources().getColor(R.color.red), new LatLng(30.959474, 126.993568));
		
		//48小时
		PolylineOptions line2 = new PolylineOptions();
		line2.width(CommonUtil.dip2px(mContext, 2));
		line2.color(getResources().getColor(R.color.yellow));
		line2.add(new LatLng(-0.035506, 104.998939), new LatLng(-0.035506, 119.962318));
		line2.add(new LatLng(14.968860, 131.981361) ,new LatLng(33.959474, 131.981361));
		aMap.addPolyline(line2);
		drawWarningText(getString(R.string.line_48h), getResources().getColor(R.color.yellow), new LatLng(30.959474, 131.981361));
	}
	
	/**
	 * 绘制警戒线提示问题
	 */
	private void drawWarningText(String text, int textColor, LatLng latLng) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.warning_line_markview, null);
		TextView tvLine = (TextView) view.findViewById(R.id.tvLine);
		tvLine.setText(text);
		tvLine.setTextColor(textColor);
		MarkerOptions options = new MarkerOptions();
		options.anchor(-0.3f, 0.2f);
		options.position(latLng);
		options.icon(BitmapDescriptorFactory.fromView(view));
		aMap.addMarker(options);
	}

	private int currentYear;
	private void initYearListView() {
		yearList.clear();
		currentYear = Integer.valueOf(sdf1.format(new Date()));
		int years = 5;//要获取台风的年数
		for (int i = 0; i < years; i++) {
			TyphoonDto dto = new TyphoonDto();
			dto.yearly = currentYear - i;
			yearList.add(dto);
		}
		yearListView = (ListView) findViewById(R.id.yearListView);
		yearAdapter = new TyphoonYearAdapter(mContext, yearList);
		yearListView.setAdapter(yearAdapter);
		yearListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for (int i = 0; i < yearList.size(); i++) {
					if (i == arg2) {
						yearAdapter.isSelected.put(i, true);
					}else {
						yearAdapter.isSelected.put(i, false);
					}
				}
				if (yearAdapter != null) {
					yearAdapter.notifyDataSetChanged();
				}
				
				for (int i = 0; i < nameList.size(); i++) {
					nameAdapter.isSelected.put(i, false);
				}
				if (nameAdapter != null) {
					nameAdapter.notifyDataSetChanged();
				}
				
				clearAllPoints();
				TyphoonDto dto = yearList.get(arg2);
				String url = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/list/" + dto.yearly;
				if (!TextUtils.isEmpty(url)) {
					OkHttpTyphoonList(url, currentYear, dto.yearly);
				}
			}
		});
		
		String url = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/list/" + yearList.get(0).yearly;
		if (!TextUtils.isEmpty(url)) {
			OkHttpTyphoonList(url, currentYear, yearList.get(0).yearly);
		}
	}
	
	/**
	 * 异步请求
	 * 获取某一年的台风信息
	 */
	private void OkHttpTyphoonList(final String url, final int currentYear, final int selectYear) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						String url = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/list/" + (currentYear-1);
						if (!TextUtils.isEmpty(url)) {
							OkHttpTyphoonList(url, currentYear, currentYear-1);
						}
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String requestResult = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									nameList.clear();
									startList.clear();
									if (!TextUtils.isEmpty(requestResult)) {
										String c = "(";
										String c2 = "})";
										String result = requestResult.substring(requestResult.indexOf(c)+c.length(), requestResult.indexOf(c2)+1);
										if (!TextUtils.isEmpty(result)) {
											JSONObject obj = new JSONObject(result);
											if (!obj.isNull("typhoonList")) {
												JSONArray array = obj.getJSONArray("typhoonList");
												for (int i = 0; i < array.length(); i++) {
													JSONArray itemArray = array.getJSONArray(i);
													TyphoonDto dto = new TyphoonDto();
													dto.id = itemArray.getString(0);
													dto.enName = itemArray.getString(1);
													if (TextUtils.equals(dto.enName, "nameless")) {
														dto.code = "";
													}else {
														dto.code = itemArray.getString(4);
													}
													dto.name = itemArray.getString(2);
													dto.status = itemArray.getString(7);
													nameList.add(dto);

													//把活跃台风过滤出来存放
													if (TextUtils.equals(dto.status, "start")) {
														startList.add(dto);
													}
												}

												String typhoonName = "";
												for (int i = startList.size()-1; i >= 0; i--) {
													TyphoonDto data = startList.get(i);
													if (TextUtils.equals(data.enName, "nameless")) {
														if (!TextUtils.isEmpty(typhoonName)) {
															typhoonName = data.enName+"\n"+typhoonName;
														}else {
															typhoonName = data.enName;
														}
														String detailUrl = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/" + data.id;
														OkHttpTyphoonDetail(data.id, detailUrl, data.code + " " + data.enName);
													}else {
														if (!TextUtils.isEmpty(typhoonName)) {
															typhoonName = data.code + " " + data.name + " " + data.enName+"\n"+typhoonName;;
														}else {
															typhoonName = data.code + " " + data.name + " " + data.enName;
														}
														String detailUrl = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/" + data.id;
														OkHttpTyphoonDetail(data.id, detailUrl, data.code + " " + data.name + " " + data.enName);
													}
												}
												tvTyphoonName.setText(typhoonName);

												if (startList.size() == 0) {// 没有生效台风
													if (currentYear == selectYear) {// 判断选中年数==当前年数
														tvTyphoonName.setText(getString(R.string.no_typhoon));
													}else {
														tvTyphoonName.setText(selectYear+"年");
													}
													ivTyphoonPlay.setVisibility(View.GONE);
													ivTyphoonRange.setVisibility(View.GONE);
													progressBar.setVisibility(View.GONE);
												} else if (startList.size() == 1) {// 1个生效台风
													ivTyphoonPlay.setVisibility(View.VISIBLE);
													ivTyphoonRange.setVisibility(View.VISIBLE);
													mRadarManager = new CaiyunManager(getApplicationContext());
													OkHttpMinute();
													OkHttpCloud();
												} else {// 2个以上生效台风
													ivTyphoonPlay.setVisibility(View.GONE);
													ivTyphoonRange.setVisibility(View.VISIBLE);
													mRadarManager = new CaiyunManager(getApplicationContext());
													OkHttpMinute();
													OkHttpCloud();
												}
												tvTyphoonName.setVisibility(View.VISIBLE);
											}

											initNameListView();
										}
									}else {
										tvTyphoonName.setText(getString(R.string.no_typhoon));
										tvTyphoonName.setVisibility(View.VISIBLE);
									}
								} catch (IndexOutOfBoundsException e) {
									e.printStackTrace();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		}).start();
	}

	private String typhoonId;
	private void initNameListView() {
		nameListView = (ListView) findViewById(R.id.nameListView);
		nameAdapter = new TyphoonNameAdapter(mContext, nameList);
		nameListView.setAdapter(nameAdapter);
		nameListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				isWindOn = false;
				ivTyphoonWind.setImageResource(R.drawable.iv_typhoon_fc_off);
				container.removeAllViews();
				container2.removeAllViews();
				tvFileTime.setVisibility(View.GONE);
				
				for (int i = 0; i < nameList.size(); i++) {
					if (i == arg2) {
						nameAdapter.isSelected.put(i, true);
					}else {
						nameAdapter.isSelected.put(i, false);
					}
				}
				if (nameAdapter != null) {
					nameAdapter.notifyDataSetChanged();
				}
				
				startList.clear();
				pointsList.clear();
				TyphoonDto dto = nameList.get(arg2);
				
				if (TextUtils.equals(dto.enName, "nameless")) {
					tvTyphoonName.setText(dto.enName);
				}else {
					tvTyphoonName.setText(dto.code + " " + dto.name + " " + dto.enName);
				}
				
				clearAllPoints();
				
				ivTyphoonPlay.setVisibility(View.VISIBLE);
				ivTyphoonRange.setVisibility(View.VISIBLE);
				if (reTyphoonList.getVisibility() == View.GONE) {
					legendAnimation(false, reTyphoonList);
					reTyphoonList.setVisibility(View.VISIBLE);
					ivLegend.setClickable(false);
					ivTyphoonList.setClickable(false);
				}else {
					legendAnimation(true, reTyphoonList);
					reTyphoonList.setVisibility(View.GONE);
					ivLegend.setClickable(true);
					ivTyphoonList.setClickable(true);
				}

				typhoonId = dto.id;
				String detailUrl = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/" + dto.id;
				OkHttpTyphoonDetail(dto.id, detailUrl, tvTyphoonName.getText().toString());
			}
		});
	}
	
	private void OkHttpTyphoonDetail(final String typhoonId, final String url, final String name) {
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
						final String requestResult = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(requestResult)) {
									String c = "(";
									String result = requestResult.substring(requestResult.indexOf(c)+c.length(), requestResult.indexOf(")"));
									if (!TextUtils.isEmpty(result)) {
										try {
											JSONObject obj = new JSONObject(result);
											if (!obj.isNull("typhoon")) {
												ArrayList<TyphoonDto> points = new ArrayList<>();//台风实点
												List<TyphoonDto> forePoints = new ArrayList<>();//台风预报点
												JSONArray array = obj.getJSONArray("typhoon");
												JSONArray itemArray = array.getJSONArray(8);
												for (int j = 0; j < itemArray.length(); j++) {
													JSONArray itemArray2 = itemArray.getJSONArray(j);
													TyphoonDto dto = new TyphoonDto();
													if (!TextUtils.isEmpty(name)) {
														dto.name = name;
													}
													long longTime = itemArray2.getLong(2);
													String time = sdf2.format(new Date(longTime));
													dto.time = time;
//									String time = itemArray2.getString(1);
													String str_year = time.substring(0, 4);
													if(!TextUtils.isEmpty(str_year)){
														dto.year = Integer.parseInt(str_year);
													}
													String str_month = time.substring(4, 6);
													if(!TextUtils.isEmpty(str_month)){
														dto.month = Integer.parseInt(str_month);
													}
													String str_day = time.substring(6, 8);
													if(!TextUtils.isEmpty(str_day)){
														dto.day = Integer.parseInt(str_day);
													}
													String str_hour = time.substring(8, 10);
													if(!TextUtils.isEmpty(str_hour)){
														dto.hour = Integer.parseInt(str_hour);
													}

													dto.lng = itemArray2.getDouble(4);
													dto.lat = itemArray2.getDouble(5);
													dto.pressure = itemArray2.getString(6);
													dto.max_wind_speed = itemArray2.getString(7);
													dto.move_speed = itemArray2.getString(9);
													String fx_string = itemArray2.getString(8);
													if( !TextUtils.isEmpty(fx_string)){
														String windDir = "";
														for (int i = 0; i < fx_string.length(); i++) {
															String item = fx_string.substring(i, i+1);
															if (TextUtils.equals(item, "N")) {
																item = "北";
															}else if (TextUtils.equals(item, "S")) {
																item = "南";
															}else if (TextUtils.equals(item, "W")) {
																item = "西";
															}else if (TextUtils.equals(item, "E")) {
																item = "东";
															}
															windDir = windDir+item;
														}
														dto.wind_dir = windDir;
													}

													String type = itemArray2.getString(3);
													if (TextUtils.equals(type, "TD")) {//热带低压
														type = "1";
													}else if (TextUtils.equals(type, "TS")) {//热带风暴
														type = "2";
													}else if (TextUtils.equals(type, "STS")) {//强热带风暴
														type = "3";
													}else if (TextUtils.equals(type, "TY")) {//台风
														type = "4";
													}else if (TextUtils.equals(type, "STY")) {//强台风
														type = "5";
													}else if (TextUtils.equals(type, "SuperTY")) {//超强台风
														type = "6";
													}
													dto.type = type;
													dto.isFactPoint = true;

													JSONArray array10 = itemArray2.getJSONArray(10);
													for (int m = 0; m < array10.length(); m++) {
														JSONArray itemArray10 = array10.getJSONArray(m);
														if (m == 0) {
															dto.radius_7 = itemArray10.getString(1)+","+itemArray10.getString(2)+","+itemArray10.getString(3)+","+itemArray10.getString(4);
														}else if (m == 1) {
															dto.radius_10 = itemArray10.getString(1)+","+itemArray10.getString(2)+","+itemArray10.getString(3)+","+itemArray10.getString(4);
														}else if (m == 2) {
															dto.radius_12 = itemArray10.getString(1)+","+itemArray10.getString(2)+","+itemArray10.getString(3)+","+itemArray10.getString(4);
														}
													}
													points.add(dto);

													if (!itemArray2.get(11).equals(null)) {
														JSONObject obj11 = itemArray2.getJSONObject(11);
														JSONArray array11 = obj11.getJSONArray("BABJ");
														if (array11.length() > 0) {
															forePoints.clear();
														}
														for (int n = 0; n < array11.length(); n++) {
															JSONArray itemArray11 = array11.getJSONArray(n);
															for (int i = 0; i < itemArray11.length(); i++) {
																TyphoonDto data = new TyphoonDto();
																if (!TextUtils.isEmpty(name)) {
																	data.name = name;
																}
																data.lng = itemArray11.getDouble(2);
																data.lat = itemArray11.getDouble(3);
																data.pressure = itemArray11.getString(4);
																data.move_speed = itemArray11.getString(5);

																long t1 = longTime;
																long t2 = itemArray11.getLong(0)*3600*1000;
																long ttt = t1+t2;
																String ttime = sdf2.format(new Date(ttt));
																data.time = ttime;
																String year = ttime.substring(0, 4);
																if(!TextUtils.isEmpty(year)){
																	data.year = Integer.parseInt(year);
																}
																String month = ttime.substring(4, 6);
																if(!TextUtils.isEmpty(month)){
																	data.month = Integer.parseInt(month);
																}
																String day = ttime.substring(6, 8);
																if(!TextUtils.isEmpty(day)){
																	data.day = Integer.parseInt(day);
																}
																String hour = ttime.substring(8, 10);
																if(!TextUtils.isEmpty(hour)){
																	data.hour = Integer.parseInt(hour);
																}

																String babjType = itemArray11.getString(7);
																if (TextUtils.equals(babjType, "TD")) {//热带低压
																	babjType = "1";
																}else if (TextUtils.equals(babjType, "TS")) {//热带风暴
																	babjType = "2";
																}else if (TextUtils.equals(babjType, "STS")) {//强热带风暴
																	babjType = "3";
																}else if (TextUtils.equals(babjType, "TY")) {//台风
																	babjType = "4";
																}else if (TextUtils.equals(babjType, "STY")) {//强台风
																	babjType = "5";
																}else if (TextUtils.equals(babjType, "SuperTY")) {//超强台风
																	babjType = "6";
																}
																data.type = babjType;
																data.isFactPoint = false;

																forePoints.add(data);
															}
														}
													}
												}

												points.addAll(forePoints);
												pointsList.add(points);
												drawTyphoon(typhoonId, false, points);
												progressBar.setVisibility(View.GONE);

												try {
													Thread.sleep(300);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								}
							}
						});
					}
				});
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_PAUSETYPHOON) {
				if (ivTyphoonPlay != null) {
					ivTyphoonPlay.setImageResource(R.drawable.iv_typhoon_play);
				}

				List<TyphoonDto> mPoints = (ArrayList<TyphoonDto>)msg.obj;
				LatLngBounds.Builder builder = LatLngBounds.builder();
				for (int i = 0; i < mPoints.size(); i++) {
					TyphoonDto dto = mPoints.get(i);
					LatLng latLng = new LatLng(dto.lat, dto.lng);
					builder.include(latLng);
				}
				aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
			}
		};
	};

	/**
	 * 清除测距markers
	 */
	private void removeRange(String tid) {
		if (!TextUtils.isEmpty(tid)) {
			//清除测距虚线
			if (rangeLinesMap.containsKey(tid)) {
				List<Polyline> polylines = rangeLinesMap.get(tid);
				for (Polyline polyline : polylines) {
					if (polyline != null) {
						polyline.remove();
					}
				}
				polylines.clear();
				rangeLinesMap.remove(tid);
			}

			//清除测距marker
			if (rangeMarkersMap.containsKey(tid)) {
				Marker marker = rangeMarkersMap.get(tid);
				if (marker != null) {
					marker.remove();
				}
				rangeMarkersMap.remove(tid);
			}
		}else {
			//清除测距虚线
			for (String typhoonId : rangeLinesMap.keySet()) {
				if (rangeLinesMap.containsKey(typhoonId)) {
					List<Polyline> polylines = rangeLinesMap.get(typhoonId);
					for (Polyline polyline : polylines) {
						if (polyline != null) {
							polyline.remove();
						}
					}
					polylines.clear();
				}
			}
			rangeLinesMap.clear();

			//清除测距marker
			for (String typhoonId : rangeMarkersMap.keySet()) {
				if (rangeMarkersMap.containsKey(typhoonId)) {
					Marker marker = rangeMarkersMap.get(typhoonId);
					if (marker != null) {
						marker.remove();
					}
				}
			}
			rangeMarkersMap.clear();
		}

	}
	
	/**
	 * 清除一个台风
	 */
	private void clearOnePoint() {
		if (startList.size() <= 1) {
			for (int i = 0; i < fullLines.size(); i++) {//清除实线
				fullLines.get(i).remove();
			}
			for (int i = 0; i < dashLines.size(); i++) {//清除虚线
				dashLines.get(i).remove();
			}

			for (int i = 0; i < markerPoints.size(); i++) {//清除台风点
				markerPoints.get(i).remove();
			}
			for (int i = 0; i < markerTimes.size(); i++) {//清除预报点时间
				markerTimes.get(i).remove();
			}
			removeWindCircle();
			for (int i = 0; i < rotateMarkers.size(); i++) {
				rotateMarkers.get(i).remove();
			}
			rotateMarkers.clear();
			for (int i = 0; i < factTimeMarkers.size(); i++) {
				factTimeMarkers.get(i).remove();
			}
			factTimeMarkers.clear();
			for (int i = 0; i < timeMarkers.size(); i++) {
				timeMarkers.get(i).remove();
			}
			timeMarkers.clear();

			removeRange(null);

			//清除实况最后一个点
			lastFactPointMap.clear();

		}
		
	}
	
	/**
	 * 清除所有台风
	 */
	private void clearAllPoints() {
		for (int i = 0; i < fullLines.size(); i++) {//清除实线
			fullLines.get(i).remove();
		}
		for (int i = 0; i < dashLines.size(); i++) {//清除虚线
			dashLines.get(i).remove();
		}
		for (int i = 0; i < markerPoints.size(); i++) {//清除台风点
			markerPoints.get(i).remove();
		}
		removeWindCircle();
		for (int i = 0; i < rotateMarkers.size(); i++) {
			rotateMarkers.get(i).remove();
		}
		rotateMarkers.clear();
		for (int i = 0; i < factTimeMarkers.size(); i++) {
			factTimeMarkers.get(i).remove();
		}
		factTimeMarkers.clear();
		for (int i = 0; i < timeMarkers.size(); i++) {
			timeMarkers.get(i).remove();
		}
		timeMarkers.clear();
		for (int i = 0; i < infoMarkers.size(); i++) {
			infoMarkers.get(i).remove();
		}
		infoMarkers.clear();

		removeRange(null);

		//清除实况最后一个点
		lastFactPointMap.clear();

	}
	
	/**
	 * 绘制台风
	 * @param isAnimate
	 */
	private void drawTyphoon(String typhoonId, boolean isAnimate, List<TyphoonDto> list) {
		if (list.isEmpty()) {
			return;
		}
		
		clearOnePoint();
		
		if (mRoadThread != null) {
			mRoadThread.cancel();
			mRoadThread = null;
		}
		mRoadThread = new RoadThread(typhoonId, list, isAnimate);
		mRoadThread.start();
	}
	
	/**
	 * 绘制台风点
	 */
	private class RoadThread extends Thread {
		private boolean cancelled = false;
		private List<TyphoonDto> mPoints = null;//整个台风路径信息
		private int delay = 200;
		private boolean isAnimate = true;
		private int i = 0;
		private TyphoonDto lastShikuangPoint;
		private TyphoonDto prevShikuangPoint;
		private String typhoonId;

		public RoadThread(String typhoonId, List<TyphoonDto> points, boolean isAnimate) {
			this.typhoonId = typhoonId;
			mPoints = points;
			this.isAnimate = isAnimate;
		}

		@Override
		public void run() {
			lastShikuangPoint = null;
			final int len = mPoints.size();
			
			final List<TyphoonDto> factPointList = new ArrayList<TyphoonDto>();
			for (int j = 0; j < len; j++) {
				if (mPoints.get(j).isFactPoint) {
					factPointList.add(mPoints.get(j));
				}
			}
			
			for (i = 0; i < len; i++) {
				if (i == len-1) {
					Message msg = new Message();
					msg.what = MSG_PAUSETYPHOON;
					msg.obj = mPoints;
					handler.sendMessage(msg);
				}
				if (isAnimate) {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (cancelled) {
					break;
				}
				final TyphoonDto start = mPoints.get(i);
				final TyphoonDto end = i >= (len - 1) ? null : mPoints.get(i + 1);
				final TyphoonDto lastPoint = null == end ? start : end;
				if (null == lastShikuangPoint && (TextUtils.isEmpty(lastPoint.type) || i == len - 1)) {
					lastShikuangPoint = prevShikuangPoint == null ? lastPoint : prevShikuangPoint;
				}
				prevShikuangPoint = lastPoint;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						drawRoute(typhoonId, start, end, factPointList.get(factPointList.size()-1));
//						if (isAnimate || null != lastShikuangPoint) {
//						if (null != lastShikuangPoint) {
//							TyphoonDto point = lastShikuangPoint == null ? lastPoint : lastShikuangPoint;
//							LatLng latlng = new LatLng(point.lat, point.lng);
//							aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
//						}
					}
				});
			}

		}

		public void cancel() {
			cancelled = true;
		}
	}
	
	private void drawRoute(String typhoonId, TyphoonDto start, TyphoonDto end, TyphoonDto lastFactPoint) {
		if (end == null) {//最后一个点
			return;
		}
		ArrayList<LatLng> temp = new ArrayList<>();
		if (end.isFactPoint) {//实况线
			PolylineOptions line = new PolylineOptions();
			line.width(CommonUtil.dip2px(mContext, 2));
			line.color(Color.RED);
			temp.add(new LatLng(start.lat, start.lng));
			LatLng latlng = new LatLng(end.lat, end.lng);
			temp.add(latlng);
			line.addAll(temp);
			Polyline fullLine = aMap.addPolyline(line);
			fullLines.add(fullLine);
		} else {//预报虚线
			LatLng start_latlng = new LatLng(start.lat, start.lng);
			double lng_start = start_latlng.longitude;
			double lat_start = start_latlng.latitude;
			LatLng end_latlng = new LatLng(end.lat, end.lng);
			double lng_end = end_latlng.longitude;
			double lat_end = end_latlng.latitude;
			double dis = Math.sqrt(Math.pow(lat_start - lat_end, 2)+ Math.pow(lng_start - lng_end, 2));
			int numPoint = (int) Math.floor(dis / 0.2);
			double lng_per = (lng_end - lng_start) / numPoint;
			double lat_per = (lat_end - lat_start) / numPoint;
			for (int i = 0; i < numPoint; i++) {
				PolylineOptions line = new PolylineOptions();
				line.color(Color.RED);
				line.width(CommonUtil.dip2px(mContext, 2));
				temp.add(new LatLng(lat_start + i * lat_per, lng_start + i * lng_per));
				if (i % 2 == 1) {
					line.addAll(temp);
					Polyline dashLine = aMap.addPolyline(line);
					dashLines.add(dashLine);
					temp.clear();
				}
			}
		}
		
		MarkerOptions options = new MarkerOptions();
		options.title(start.name+"|"+start.content(mContext)+"|"+start.radius_7+"|"+start.radius_10+"|"+start.radius_12);
		options.snippet(start.radius_7+","+start.radius_10+","+start.radius_12);
		options.anchor(0.5f, 0.5f);
		options.position(new LatLng(start.lat, start.lng));
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.typhoon_point, null);
		ImageView ivPoint = (ImageView) view.findViewById(R.id.ivPoint);
		if (TextUtils.equals(start.type, "1")) {
			ivPoint.setImageResource(R.drawable.typhoon_level1);
		}else if (TextUtils.equals(start.type, "2")) {
			ivPoint.setImageResource(R.drawable.typhoon_level2);
		}else if (TextUtils.equals(start.type, "3")) {
			ivPoint.setImageResource(R.drawable.typhoon_level3);
		}else if (TextUtils.equals(start.type, "4")) {
			ivPoint.setImageResource(R.drawable.typhoon_level4);
		}else if (TextUtils.equals(start.type, "5")) {
			ivPoint.setImageResource(R.drawable.typhoon_level5);
		}else if (TextUtils.equals(start.type, "6")) {
			ivPoint.setImageResource(R.drawable.typhoon_level6);
		}else {//预报点
			ivPoint.setImageResource(R.drawable.typhoon_yb);
			
			boolean isAdd = false;//是否已添加
			String time = start.month+"月"+start.day+"日"+start.hour+"时";
			for (int i = 0; i < markerTimes.size(); i++) {
				if (TextUtils.equals(markerTimes.get(i).getSnippet(), time)) {
					isAdd = true;
					break;
				}
			}
			if (!isAdd) {
				View textView = inflater.inflate(R.layout.warning_line_markview, null);
				TextView tvLine = (TextView) textView.findViewById(R.id.tvLine);
				if (time != null) {
					tvLine.setText(time);
				}
				tvLine.setTextColor(Color.WHITE);
				MarkerOptions optionsTime = new MarkerOptions();
				optionsTime.snippet(time);
				optionsTime.anchor(-0.2f, 0.5f);
				optionsTime.position(new LatLng(start.lat, start.lng));
				optionsTime.icon(BitmapDescriptorFactory.fromView(textView));
				Marker timeMarker = aMap.addMarker(optionsTime);
				markerTimes.add(timeMarker);
			}
		}
		options.icon(BitmapDescriptorFactory.fromView(view));
		Marker marker = aMap.addMarker(options);
		markerPoints.add(marker);
		
		if (start.isFactPoint) {
//			marker.showInfoWindow();
//			clickMarker = marker;
			
			MarkerOptions tOption = new MarkerOptions();
			tOption.position(new LatLng(start.lat, start.lng));
			tOption.anchor(0.5f, 0.5f);
			ArrayList<BitmapDescriptor> iconList = new ArrayList<BitmapDescriptor>();
			for (int i = 1; i <= 9; i++) {
				iconList.add(BitmapDescriptorFactory.fromAsset("typhoon/typhoon_icon"+i+".png"));
			}
			tOption.icons(iconList);
			tOption.period(2);


			//绘制最后一个实况点对应的七级、十级风圈
			drawWindCircle(start.radius_7, start.radius_10, start.radius_12, new LatLng(start.lat, start.lng));
			
			View timeView = inflater.inflate(R.layout.layout_marker_time, null);
			TextView tvTime = (TextView) timeView.findViewById(R.id.tvTime);
			if (!TextUtils.isEmpty(start.time)) {
				try {
					tvTime.setText(sdf5.format(sdf2.parse(start.time)));
					tvTime.setTextColor(Color.BLACK);
					tvTime.setBackgroundResource(R.drawable.bg_corner_typhoon_time);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			MarkerOptions mo = new MarkerOptions();
			mo.anchor(1.2f, 0.5f);
			mo.position(new LatLng(start.lat, start.lng));
			mo.icon(BitmapDescriptorFactory.fromView(timeView));
			if (lastFactPoint == start) {
				Marker factTimeMarker = aMap.addMarker(mo);
				factTimeMarkers.add(factTimeMarker);
				
				Marker rotateMarker = aMap.addMarker(tOption);
				rotateMarker.setClickable(false);
				rotateMarkers.add(rotateMarker);
				
				MarkerOptions info = new MarkerOptions();
				info.anchor(0.5f, 1.0f);
				info.position(new LatLng(lastFactPoint.lat, lastFactPoint.lng));
				View infoView = inflater.inflate(R.layout.typhoon_marker_view_info, null);
				TextView tvName = (TextView) infoView.findViewById(R.id.tvName);
				TextView tvInfo = (TextView) infoView.findViewById(R.id.tvInfo);
				ImageView ivDelete = (ImageView) infoView.findViewById(R.id.ivDelete);
				tvName.setText(lastFactPoint.name);
				tvInfo.setText(lastFactPoint.content(mContext));
				info.icon(BitmapDescriptorFactory.fromView(infoView));
				Marker infoMarker = aMap.addMarker(info);
				infoMarkers.add(infoMarker);

				ivDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mapClick();
					}
				});
				
				if (lastFactPoint.lng < 131.981361) {
					aMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lastFactPoint.lat, lastFactPoint.lng)));
				}

				//多个台风最后实况点合在一起
				lastFactPointMap.put(typhoonId, lastFactPoint);

				ranging(typhoonId);
			}
		}else {
			View timeView = inflater.inflate(R.layout.layout_marker_time, null);
			TextView tvTime = (TextView) timeView.findViewById(R.id.tvTime);
			if (!TextUtils.isEmpty(start.time)) {
				try {
					tvTime.setText(sdf4.format(sdf2.parse(start.time)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			MarkerOptions mo = new MarkerOptions();
			mo.anchor(-0.05f, 0.5f);
			mo.position(new LatLng(start.lat, start.lng));
			mo.icon(BitmapDescriptorFactory.fromView(timeView));
			Marker m = aMap.addMarker(mo);
			timeMarkers.add(m);
		}
	}

	/**
	 * 清除七级、十级风圈
	 */
	private void removeWindCircle() {
		for (Polygon polygon : windCirclePolygons) {
			polygon.remove();
		}
		windCirclePolygons.clear();
	}

	/**
	 * 绘制七级、十级风圈
	 */
	private void drawWindCircle(String radius_7, String radius_10, String radius_12, LatLng center) {
		removeWindCircle();

		//七级风圈
		if (!TextUtils.isEmpty(radius_7) && !TextUtils.equals(radius_7, "null") && radius_7.contains(",")) {
			String[] radiuss = radius_7.split(",");
			List<LatLng> wind7Points = new ArrayList<>();
			getWindCirclePoints(center, radiuss[0], 0, wind7Points);
			getWindCirclePoints(center, radiuss[3], 90, wind7Points);
			getWindCirclePoints(center, radiuss[2], 180, wind7Points);
			getWindCirclePoints(center, radiuss[1], 270, wind7Points);
			if (wind7Points.size() > 0) {
				PolygonOptions polygonOptions = new PolygonOptions();
				polygonOptions.strokeWidth(3).strokeColor(Color.YELLOW).fillColor(0x20FFFF00);
				for (LatLng latLng : wind7Points) {
					polygonOptions.add(latLng);
				}
				Polygon polygon = aMap.addPolygon(polygonOptions);
				windCirclePolygons.add(polygon);
			}
		}

		//十级风圈
		if (!TextUtils.isEmpty(radius_10) && !TextUtils.equals(radius_10, "null") && radius_10.contains(",")) {
			String[] radiuss = radius_10.split(",");
			List<LatLng> wind10Points = new ArrayList<>();
			getWindCirclePoints(center, radiuss[0], 0, wind10Points);
			getWindCirclePoints(center, radiuss[3], 90, wind10Points);
			getWindCirclePoints(center, radiuss[2], 180, wind10Points);
			getWindCirclePoints(center, radiuss[1], 270, wind10Points);
			if (wind10Points.size() > 0) {
				PolygonOptions polygonOptions = new PolygonOptions();
				polygonOptions.strokeWidth(3).strokeColor(0xffFE9900).fillColor(0x20FF0000);
				for (LatLng latLng : wind10Points) {
					polygonOptions.add(latLng);
				}
				Polygon polygon = aMap.addPolygon(polygonOptions);
				windCirclePolygons.add(polygon);
			}
		}

		//十二级风圈
		if (!TextUtils.isEmpty(radius_12) && !TextUtils.equals(radius_12, "null") && radius_12.contains(",")) {
			String[] radiuss = radius_12.split(",");
			List<LatLng> wind12Points = new ArrayList<>();
			getWindCirclePoints(center, radiuss[0], 0, wind12Points);
			getWindCirclePoints(center, radiuss[3], 90, wind12Points);
			getWindCirclePoints(center, radiuss[2], 180, wind12Points);
			getWindCirclePoints(center, radiuss[1], 270, wind12Points);
			if (wind12Points.size() > 0) {
				PolygonOptions polygonOptions = new PolygonOptions();
				polygonOptions.strokeWidth(3).strokeColor(Color.RED).fillColor(0x20FF0000);
				for (LatLng latLng : wind12Points) {
					polygonOptions.add(latLng);
				}
				Polygon polygon = aMap.addPolygon(polygonOptions);
				windCirclePolygons.add(polygon);
			}
		}

	}

	/**
	 * 获取风圈经纬度点集合
	 * @param center
	 * @param radius
	 * @param startAngle
	 * @return
	 */
	private void getWindCirclePoints(LatLng center, String radius, double startAngle, List<LatLng> points) {
		if (!TextUtils.isEmpty(radius) && !TextUtils.equals(radius, "null")) {
			double r = 6371000.79;
			int numpoints = 90;
			double phase = Math.PI/2 / numpoints;

			for (int i = 0; i <= numpoints; i++) {
				double dx = (Integer.valueOf(radius)*1000 * Math.cos((i+startAngle) * phase));
				double dy = (Integer.valueOf(radius)*1000 * Math.sin((i+startAngle) * phase));//乘以1.6 椭圆比例
				double lng = center.longitude + dx / (r * Math.cos(center.latitude * Math.PI / 180) * Math.PI / 180);
				double lat = center.latitude + dy / (r * Math.PI / 180);
				points.add(new LatLng(lat, lng));
			}

		}
	}

	/**
	 * 测距
	 */
	private void ranging(String tid) {
		if (locationLatLng == null || !isRanging) {
			return;
		}

		if (!TextUtils.isEmpty(tid)) {
			rangingSingle(tid);
		}else {
			for (String typhoonId : lastFactPointMap.keySet()) {
				rangingSingle(typhoonId);
			}
		}
	}

	/**
	 * 单个点测距
	 * @param typhoonId
	 */
	private void rangingSingle(String typhoonId) {
		double locationLat = locationLatLng.latitude;
		double locationLng = locationLatLng.longitude;
		if (lastFactPointMap.containsKey(typhoonId)) {
			TyphoonDto dto = lastFactPointMap.get(typhoonId);
			double lat = dto.lat;
			double lng = dto.lng;
			double dis = Math.sqrt(Math.pow(locationLat-lat, 2)+ Math.pow(locationLng-lng, 2));
			int numPoint = (int) Math.floor(dis/0.2);
			double lng_per = (lng-locationLng)/numPoint;
			double lat_per = (lat-locationLat)/numPoint;
			List<Polyline> polylines = new ArrayList<>();
			List<LatLng> ranges = new ArrayList<>();
			for (int i = 0; i < numPoint; i++) {
				PolylineOptions line = new PolylineOptions();
				line.color(0xff6291E1);
				line.width(CommonUtil.dip2px(mContext, 2));
				ranges.add(new LatLng(locationLat+i*lat_per, locationLng+i*lng_per));
				if (i % 2 == 1) {
					line.addAll(ranges);
					Polyline polyline = aMap.addPolyline(line);
					polylines.add(polyline);
					ranges.clear();
				}
			}
			rangeLinesMap.put(typhoonId, polylines);

			LatLng centerLatLng = new LatLng((locationLat+lat)/2, (locationLng+lng)/2);
			addRangeMarker(typhoonId, centerLatLng, locationLng, locationLat, lng, lat);
		}
	}

	/**
	 * 添加每个台风的测距距离
	 */
	private void addRangeMarker(String typhoonId, LatLng latLng, double longitude1, double latitude1, double longitude2, double latitude2) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		MarkerOptions options = new MarkerOptions();
		options.position(latLng);
		View mView = inflater.inflate(R.layout.layout_range_marker, null);
		TextView tvName = (TextView) mView.findViewById(R.id.tvName);
		tvName.setText("距离台风"+getDistance(longitude1, latitude1, longitude2, latitude2)+"公里");

		options.icon(BitmapDescriptorFactory.fromView(mView));
		Marker marker = aMap.addMarker(options);
		marker.setClickable(false);
		rangeMarkersMap.put(typhoonId, marker);
	}

	/**
	 * 计算两点之间距离
	 *
	 * @param longitude1
	 * @param latitude1
	 * @param longitude2
	 * @param latitude2
	 * @return
	 */
	private static String getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
		double EARTH_RADIUS = 6378137;
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		BigDecimal bd = new BigDecimal(s / 1000);
		double d = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		String distance = d + "";

		String value = distance;
		if (value.length() >= 2 && value.contains(".")) {
			if (value.equals(".0")) {
				distance = "0";
			} else {
				if (TextUtils.equals(value.substring(value.length() - 2, value.length()), ".0")) {
					distance = value.substring(0, value.indexOf("."));
				} else {
					distance = value;
				}
			}
		}

		return distance;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		for (int i = 0; i < infoMarkers.size(); i++) {
			infoMarkers.get(i).remove();
		}
		infoMarkers.clear();

		if (marker != null && marker != locationMarker) {
			if (!TextUtils.isEmpty(marker.getTitle())) {
				String[] title = marker.getTitle().split("\\|");
				drawWindCircle(title[2], title[3], title[4], marker.getPosition());
			}

			clickMarker = marker;
			if (clickMarker.isInfoWindowShown()) {
				clickMarker.hideInfoWindow();
			}else {
				marker.showInfoWindow();
			}
		}
		
		return true;
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		//测距状态下
		if (isRanging) {
			removeRange(null);
			locationLatLng = arg0;
			ranging(null);
			addLocationMarker(arg0);
		}
		mapClick();
	}

	private void mapClick() {
		for (int i = 0; i < infoMarkers.size(); i++) {
			infoMarkers.get(i).remove();
		}
		infoMarkers.clear();

		if (clickMarker != null && clickMarker.isInfoWindowShown()) {
			clickMarker.hideInfoWindow();
		}
	}
	
	@Override
	public View getInfoContents(Marker arg0) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.typhoon_marker_view, null);
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		TextView tvInfo = (TextView) view.findViewById(R.id.tvInfo);
		ImageView ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
		if (!TextUtils.isEmpty(arg0.getTitle())) {
			String[] str = arg0.getTitle().split("\\|");
			if (!TextUtils.isEmpty(str[0])) {
				tvName.setText(str[0]);
			}
			if (!TextUtils.isEmpty(str[1])) {
				tvInfo.setText(str[1]);
			}
		}

		ivDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mapClick();
			}
		});

		return view;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void legendAnimation(boolean flag, final RelativeLayout reLayout) {
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation animation = null;
		if (flag == false) {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 1f, 
					Animation.RELATIVE_TO_SELF, 0);
		}else {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,1.0f);
		}
		animation.setDuration(400);
		animationSet.addAnimation(animation);
		animationSet.setFillAfter(true);
		reLayout.startAnimation(animationSet);
		animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				reLayout.clearAnimation();
			}
		});
	}
	
	/**
	 * 获取分钟级降水图
	 */
	private void OkHttpMinute() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = "http://api.tianqi.cn:8070/v1/img.py";
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
										if (!obj.isNull("status")) {
											if (obj.getString("status").equals("ok")) {
												if (!obj.isNull("radar_img")) {
													JSONArray array = new JSONArray(obj.getString("radar_img"));
													for (int i = 0; i < array.length(); i++) {
														JSONArray array0 = array.getJSONArray(i);
														MinuteFallDto dto = new MinuteFallDto();
														dto.setImgUrl(array0.optString(0));
														dto.setTime(array0.optLong(1));
														JSONArray itemArray = array0.getJSONArray(2);
														dto.setP1(itemArray.optDouble(0));
														dto.setP2(itemArray.optDouble(1));
														dto.setP3(itemArray.optDouble(2));
														dto.setP4(itemArray.optDouble(3));
														radarList.add(dto);
													}
													if (radarList.size() > 0) {
														startDownLoadImgs(radarList);
													}
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
	
	private void startDownLoadImgs(List<MinuteFallDto> list) {
		if (mRadarThread != null) {
			mRadarThread.cancel();
			mRadarThread = null;
		}
 		mRadarManager.loadImagesAsyn(list, this);
	}
	
	@Override
	public void onResult(int result, List<MinuteFallDto> images) {
		if (result == RadarListener.RESULT_SUCCESSED) {
			mHandler.sendEmptyMessage(HANDLER_LOAD_FINISHED);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HANDLER_SHOW_RADAR: 
				if (msg.obj != null) {
					MinuteFallDto dto = (MinuteFallDto) msg.obj;
					if (dto.getPath() != null) {
						Bitmap bitmap = BitmapFactory.decodeFile(dto.getPath());
						if (bitmap != null) {
							showRadar(bitmap, dto.getP1(), dto.getP2(), dto.getP3(), dto.getP4());
						}
					}
				}
				break;
			case HANDLER_LOAD_FINISHED: 
				ivTyphoonRadar.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onProgress(String url, int progress) {
//		Message msg = new Message();
//		msg.obj = progress;
//		msg.what = HANDLER_PROGRESS;
//		mHandler.sendMessage(msg);
	}
	
	private void showRadar(Bitmap bitmap, double p1, double p2, double p3, double p4) {
		BitmapDescriptor fromView = BitmapDescriptorFactory.fromBitmap(bitmap);
		LatLngBounds bounds = new LatLngBounds.Builder()
		.include(new LatLng(p3, p2))
		.include(new LatLng(p1, p4))
		.build();
		
		if (radarOverlay == null) {
			radarOverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
				.anchor(0.5f, 0.5f)
				.positionFromBounds(bounds)
				.image(fromView)
				.transparency(0.0f));
		} else {
			radarOverlay.setImage(null);
			radarOverlay.setPositionFromBounds(bounds);
			radarOverlay.setImage(fromView);
		}
		aMap.runOnDrawFrame();
	}
	
	private class RadarThread extends Thread {
		static final int STATE_NONE = 0;
		static final int STATE_PLAYING = 1;
		static final int STATE_PAUSE = 2;
		static final int STATE_CANCEL = 3;
		private List<MinuteFallDto> images;
		private int state;
		private int index;
		private int count;
		private boolean isTracking = false;
		
		public RadarThread(List<MinuteFallDto> images) {
			this.images = images;
			this.count = images.size();
			this.index = 0;
			this.state = STATE_NONE;
			this.isTracking = false;
		}
		
		public int getCurrentState() {
			return state;
		}
		
		@Override
		public void run() {
			super.run();
			this.state = STATE_PLAYING;
			while (true) {
				if (state == STATE_CANCEL) {
					break;
				}
				if (state == STATE_PAUSE) {
					continue;
				}
				if (isTracking) {
					continue;
				}
				sendRadar();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void sendRadar() {
			if (index >= count || index < 0) {
				index = 0;
				
//				if (mRadarThread != null) {
//					mRadarThread.pause();
//					
//					Message message = mHandler.obtainMessage();
//					message.what = HANDLER_PAUSE;
//					mHandler.sendMessage(message);
//					if (seekBar != null) {
//						seekBar.setProgress(100);
//					}
//				}
			}else {
				MinuteFallDto radar = images.get(index);
				Message message = mHandler.obtainMessage();
				message.what = HANDLER_SHOW_RADAR;
				message.obj = radar;
				message.arg1 = count - 1;
				message.arg2 = index ++;
				mHandler.sendMessage(message);
			}
		}
		
		public void cancel() {
			this.state = STATE_CANCEL;
		}
		public void pause() {
			this.state = STATE_PAUSE;
		}
		public void play() {
			this.state = STATE_PLAYING;
		}
		
		public void setCurrent(int index) {
			this.index = index;
		}
		
		public void startTracking() {
			isTracking = true;
		}
		
		public void stopTracking() {
			isTracking = false;
			if (this.state == STATE_PAUSE) {
				sendRadar();
			}
		}
	}

	private String getCloudSecretUrl(String url) {
		String date = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmm");
		StringBuffer buffer = new StringBuffer();
		buffer.append(url);
		buffer.append("?");
		buffer.append("date=").append(date);
		buffer.append("&");
		buffer.append("appid=").append(RainManager.APPID);

		String key = RainManager.getKey(RainManager.CHINAWEATHER_DATA, buffer.toString());
		buffer.delete(buffer.lastIndexOf("&"), buffer.length());

		buffer.append("&");
		buffer.append("appid=").append(RainManager.APPID.subSequence(0, 6));
		buffer.append("&");
		buffer.append("key=").append(key.subSequence(0, key.length() - 3));
		String result = buffer.toString();
		return result;
	}
	
	/**
	 * 获取云图数据
	 */
	private void OkHttpCloud() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = "http://59.50.130.88:8888/decision/getDecisionCloudImages.php";
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
										if (!obj.isNull("rect")) {
											JSONArray rect = obj.getJSONArray("rect");
											leftLatLng = new LatLng(rect.getDouble(2), rect.getDouble(1));
											rightLatLng = new LatLng(rect.getDouble(0), rect.getDouble(3));
										}
										if (!obj.isNull("l")) {
											JSONArray array = obj.getJSONArray("l");
											if (array.length() > 0) {
												JSONObject itemObj = array.getJSONObject(0);
												String imgUrl = itemObj.getString("l2");
												if (!TextUtils.isEmpty(imgUrl)) {
													downloadPortrait(getCloudSecretUrl(imgUrl));
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
	
	/**
	 * 下载头像保存在本地
	 */
	private void downloadPortrait(String imgUrl) {
		AsynLoadTask task = new AsynLoadTask(new AsynLoadCompleteListener() {
			@Override
			public void loadComplete(Bitmap bitmap) {
				if (bitmap != null) {
					cloudBitmap = bitmap;
					ivTyphoonCloud.setVisibility(View.VISIBLE);
				}
			}
		}, imgUrl);  
        task.execute();
	}
	
	private interface AsynLoadCompleteListener {
		void loadComplete(Bitmap bitmap);
	}
    
	private class AsynLoadTask extends AsyncTask<Void, Bitmap, Bitmap> {
		
		private String imgUrl;
		private AsynLoadCompleteListener completeListener;

		private AsynLoadTask(AsynLoadCompleteListener completeListener, String imgUrl) {
			this.imgUrl = imgUrl;
			this.completeListener = completeListener;
		}

		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected void onProgressUpdate(Bitmap... values) {
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bitmap = CommonUtil.getHttpBitmap(imgUrl);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (completeListener != null) {
				completeListener.loadComplete(bitmap);
            }
		}
	}
	
	private void showCloud(Bitmap bitmap) {
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Point leftPoint = new Point(0, dm.heightPixels);
//		Point rightPoint = new Point(dm.widthPixels, 0);
//		LatLng leftlatlng = aMap.getProjection().fromScreenLocation(leftPoint);
//		LatLng rightLatlng = aMap.getProjection().fromScreenLocation(rightPoint);
		if (bitmap == null) {
			return;
		}

		BitmapDescriptor fromView = BitmapDescriptorFactory.fromBitmap(bitmap);
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(leftLatLng)
				.include(rightLatLng)
				.build();

		if (cloudOverlay == null) {
			cloudOverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
					.anchor(0.5f, 0.5f)
					.positionFromBounds(bounds)
					.image(fromView)
					.transparency(0.2f));
		} else {
			cloudOverlay.setImage(null);
			cloudOverlay.setPositionFromBounds(bounds);
			cloudOverlay.setImage(fromView);
		}
		
	}
	
	private String getWindSecretUrl(String url, String type) {
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHH");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(url);
		buffer.append("?");
		buffer.append("type=").append(type);
		buffer.append("&");
		buffer.append("date=").append(sysdate);
		buffer.append("&");
		buffer.append("appid=").append(RainManager.APPID);
		
		String key = RainManager.getKey(RainManager.CHINAWEATHER_DATA, buffer.toString());
		buffer.delete(buffer.lastIndexOf("&"), buffer.length());
		
		buffer.append("&");
		buffer.append("appid=").append(RainManager.APPID.substring(0, 6));
		buffer.append("&");
		buffer.append("key=").append(key.substring(0, key.length() - 3));
		String result = buffer.toString();
		return result;
	}
	
	/**
	 * 获取风场数据
	 */
	private void OkHttpWind() {
		final String url = getWindSecretUrl("http://scapi.weather.com.cn/weather/getwindmincas", "1000");
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
										if (obj != null) {
											if (!obj.isNull("gridHeight")) {
												CONST.windData.height = obj.getInt("gridHeight");
											}
											if (!obj.isNull("gridWidth")) {
												CONST.windData.width = obj.getInt("gridWidth");
											}
											if (!obj.isNull("x0")) {
												CONST.windData.x0 = obj.getDouble("x0");
											}
											if (!obj.isNull("y0")) {
												CONST.windData.y0 = obj.getDouble("y0");
											}
											if (!obj.isNull("x1")) {
												CONST.windData.x1 = obj.getDouble("x1");
											}
											if (!obj.isNull("y1")) {
												CONST.windData.y1 = obj.getDouble("y1");
											}
											if (!obj.isNull("filetime")) {
												CONST.windData.filetime = obj.getString("filetime");
											}

											if (!obj.isNull("field")) {
												JSONArray array = new JSONArray(obj.getString("field"));
												for (int i = 0; i < array.length(); i+=2) {
													WindDto dto2 = new WindDto();
													dto2.initX = (float)(array.optDouble(i));
													dto2.initY = (float)(array.optDouble(i+1));
													CONST.windData.dataList.add(dto2);
												}
											}

											progressBar.setVisibility(View.GONE);
											reloadWind();
											isHaveWindData = true;
										}
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
								}
							}
						});
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onCameraChange(CameraPosition arg0) {
		container.removeAllViews();
		container2.removeAllViews();
		tvFileTime.setVisibility(View.GONE);
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		if (isWindOn && isHaveWindData) {
			reloadWind();
		}
	}
	
	long t = new Date().getTime();
	
	/**
	 * 重新加载风场
	 */
	private void reloadWind() {
		t = new Date().getTime() - t;
		if (t < 1000) {
			return;
		}

		int statusBarHeight = com.pmsc.weather4decision.phone.hainan.util.CommonUtil.statusBarHeight(mContext);
		int naviBarHeight = com.pmsc.weather4decision.phone.hainan.util.CommonUtil.navigationBarHeight(mContext);

		LatLng latLngStart = aMap.getProjection().fromScreenLocation(new Point(0, statusBarHeight));
		LatLng latLngEnd = aMap.getProjection().fromScreenLocation(new Point(width, height-naviBarHeight-statusBarHeight));
		CONST.windData.latLngStart = latLngStart;
		CONST.windData.latLngEnd = latLngEnd;
		if (waitWindView == null) {
			waitWindView = new WaitWindView(mContext);
			waitWindView.init(TyphoonRouteActivity.this);
			waitWindView.setData(CONST.windData);
			waitWindView.start();
			waitWindView.invalidate();
		}

		container.removeAllViews();
		container.addView(waitWindView);
		ivTyphoonWind.setVisibility(View.VISIBLE);
		tvFileTime.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(CONST.windData.filetime)) {
			try {
				tvFileTime.setText("GFS "+sdf3.format(sdf2.parse(CONST.windData.filetime))+"风场预报");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mapView != null) {
			mapView.onDestroy();
		}
		if (mRadarManager != null) {
			mRadarManager.onDestory();
		}
		if (mRadarThread != null) {
			mRadarThread.cancel();
			mRadarThread = null;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ivTyphoonRadar:
				if (isRadarOn == false) {//添加雷达图

					if (isCloudOn) {//删除云图
						isCloudOn = false;
						ivTyphoonCloud.setImageResource(R.drawable.iv_typhoon_cloud_off);
						if (cloudOverlay != null) {
							cloudOverlay.remove();
							cloudOverlay = null;
						}
					}

					isRadarOn = true;
					ivTyphoonRadar.setImageResource(R.drawable.iv_typhoon_radar_on);
					if (mRadarThread != null) {
						mRadarThread.cancel();
						mRadarThread = null;
					}
					mRadarThread = new RadarThread(radarList);
					mRadarThread.start();
				}else {//删除雷达图
					isRadarOn = false;
					ivTyphoonRadar.setImageResource(R.drawable.iv_typhoon_radar_off);
					if (radarOverlay != null) {
						radarOverlay.remove();
						radarOverlay = null;
					}
					if (mRadarThread != null) {
						mRadarThread.cancel();
						mRadarThread = null;
					}
				}
				break;
			case R.id.ivTyphoonCloud:
				if (isCloudOn == false) {//添加云图

					if (isRadarOn) {//删除雷达图
						isRadarOn = false;
						ivTyphoonRadar.setImageResource(R.drawable.iv_typhoon_radar_off);
						if (radarOverlay != null) {
							radarOverlay.remove();
							radarOverlay = null;
						}
						if (mRadarThread != null) {
							mRadarThread.cancel();
							mRadarThread = null;
						}
					}

					isCloudOn = true;
					ivTyphoonCloud.setImageResource(R.drawable.iv_typhoon_cloud_on);
					showCloud(cloudBitmap);
				}else {//删除云图
					isCloudOn = false;
					ivTyphoonCloud.setImageResource(R.drawable.iv_typhoon_cloud_off);
					if (cloudOverlay != null) {
						cloudOverlay.remove();
						cloudOverlay = null;
					}
				}
				break;
			case R.id.ivTyphoonWind:
				if (isWindOn == false) {//添加图层
					if (isHaveWindData == false) {
//					asyncQueryWind("http://scapi.weather.com.cn/weather/micaps/windfile");
						OkHttpWind();
					}else {
						reloadWind();
					}
					isWindOn = true;
					ivTyphoonWind.setImageResource(R.drawable.iv_typhoon_fc_on);
					tvFileTime.setVisibility(View.VISIBLE);
				}else {//清除图层
					isWindOn = false;
					ivTyphoonWind.setImageResource(R.drawable.iv_typhoon_fc_off);
					tvFileTime.setVisibility(View.GONE);
					container.removeAllViews();
					container2.removeAllViews();
					tvFileTime.setVisibility(View.GONE);
				}
				break;
			case R.id.ivLegend:
			case R.id.ivCancelLegend:
				if (reLegend.getVisibility() == View.GONE) {
					legendAnimation(false, reLegend);
					reLegend.setVisibility(View.VISIBLE);
					ivLegend.setClickable(false);
					ivTyphoonList.setClickable(false);
				}else {
					legendAnimation(true, reLegend);
					reLegend.setVisibility(View.GONE);
					ivLegend.setClickable(true);
					ivTyphoonList.setClickable(true);
				}
				break;
			case R.id.ivTyphoonList:
			case R.id.ivCancelList:
				if (reTyphoonList.getVisibility() == View.GONE) {
					legendAnimation(false, reTyphoonList);
					reTyphoonList.setVisibility(View.VISIBLE);
					ivLegend.setClickable(false);
					ivTyphoonList.setClickable(false);
				}else {
					legendAnimation(true, reTyphoonList);
					reTyphoonList.setVisibility(View.GONE);
					ivLegend.setClickable(true);
					ivTyphoonList.setClickable(true);
				}
				break;
			case R.id.ivTyphoonPlay:
				for (int i = 0; i < infoMarkers.size(); i++) {
					infoMarkers.get(i).remove();
				}
				infoMarkers.clear();
				ivTyphoonPlay.setImageResource(R.drawable.iv_typhoon_pause);
				container.removeAllViews();
				container2.removeAllViews();
				tvFileTime.setVisibility(View.GONE);
				if (!pointsList.isEmpty() && pointsList.get(0) != null) {
					drawTyphoon(typhoonId, false, pointsList.get(0));
				}
				break;
			case R.id.ivTyphoonRange:
				if (isRanging) {
					isRanging = false;
					ivTyphoonRange.setImageResource(R.drawable.iv_typhoon_cj_off);

					removeRange(null);
				}else {
					isRanging = true;
					ivTyphoonRange.setImageResource(R.drawable.iv_typhoon_cj_on);

					ranging(null);

					addLocationMarker(locationLatLng);
				}
				break;
		}

	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (mapView != null) {
			mapView.onResume();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (mapView != null) {
			mapView.onPause();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mapView != null) {
			mapView.onSaveInstanceState(outState);
		}
	}

}
