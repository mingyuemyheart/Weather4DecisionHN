package com.pmsc.weather4decision.phone.hainan.act;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.dto.ShawnRainDto;
import com.pmsc.weather4decision.phone.hainan.fragment.StationDetailRainFragment;
import com.pmsc.weather4decision.phone.hainan.util.CommonUtil;
import com.pmsc.weather4decision.phone.hainan.util.CustomHttpClient;
import com.pmsc.weather4decision.phone.hainan.view.MainViewPager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StationMonitorDetailActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private MainViewPager viewPager;
	private List<Fragment> fragments = new ArrayList<>();
	private List<ShawnRainDto> dataList = new ArrayList<>();
	private ProgressBar progressBar = null;
	private LinearLayout llContainer = null, llContainer1 = null;
	private String childId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_monitor_detail);
		mContext = this;
		initWidget();
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		llContainer1 = (LinearLayout) findViewById(R.id.llContainer1);

		childId = getIntent().getExtras().getString("childId");

		ShawnRainDto data = getIntent().getParcelableExtra("data");
		if (data != null) {
			if (!TextUtils.isEmpty(data.stationName)) {
				tvTitle.setText(data.stationName);
			}
			if (!TextUtils.isEmpty(data.stationCode)) {
				asyncTask("http://59.50.130.88:8888/decision-admin/dates/getone48?id="+data.stationCode);
			}
		}
	}
	
	/**
	 * 初始化viewPager
	 */
	@SuppressWarnings("unchecked")
	private void initViewPager(List<ShawnRainDto> dataList) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		llContainer.removeAllViews();
		llContainer1.removeAllViews();
		for (int j = 0; j < 3; j++) {
			TextView tvName = new TextView(mContext);
			tvName.setGravity(Gravity.CENTER);
			tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			tvName.setPadding(0, 30, 0, 30);
			tvName.setMaxLines(1);
			tvName.setTag(j);
			
			TextView tvBar = new TextView(mContext);
			tvBar.setGravity(Gravity.CENTER);

			if (TextUtils.equals(childId, "631") || TextUtils.equals(childId, "631") || TextUtils.equals(childId, "632") || TextUtils.equals(childId, "648")) {//降水
				if (j == 0) {
					tvName.setText("降水");
					tvName.setTextColor(getResources().getColor(R.color.main_color));
					tvBar.setBackgroundColor(getResources().getColor(R.color.main_color));
				}else if (j == 1) {
					tvName.setText("温度");
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
				}else if (j == 2) {
					tvName.setText("风速");
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
			}else if (TextUtils.equals(childId, "636") || TextUtils.equals(childId, "637") || TextUtils.equals(childId, "638") || TextUtils.equals(childId, "656")) {//温度
				if (j == 0) {
					tvName.setText("降水");
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
				}else if (j == 1) {
					tvName.setText("温度");
					tvName.setTextColor(getResources().getColor(R.color.main_color));
					tvBar.setBackgroundColor(getResources().getColor(R.color.main_color));
				}else if (j == 2) {
					tvName.setText("风速");
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
			}else if (TextUtils.equals(childId, "633") || TextUtils.equals(childId, "634") || TextUtils.equals(childId, "635") || TextUtils.equals(childId, "655")) {//风速
				if (j == 0) {
					tvName.setText("降水");
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
				}else if (j == 1) {
					tvName.setText("温度");
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
				}else if (j == 2) {
					tvName.setText("风速");
					tvName.setTextColor(getResources().getColor(R.color.main_color));
					tvBar.setBackgroundColor(getResources().getColor(R.color.main_color));
				}
			}

			llContainer.addView(tvName);
			LayoutParams params = tvName.getLayoutParams();
			params.width = dm.widthPixels/3;
			tvName.setLayoutParams(params);
			
			llContainer1.addView(tvBar);
			LayoutParams params1 = tvBar.getLayoutParams();
			params1.width = dm.widthPixels/3;
			params1.height = (int) CommonUtil.dip2px(mContext, 2);
			tvBar.setLayoutParams(params1);
			
			tvName.setOnClickListener(new MyOnClickListener(j));
			tvBar.setOnClickListener(new MyOnClickListener(j));
		}
		
		for (int i = 0; i < 3; i++) {
			Fragment fragment = new StationDetailRainFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("index", i);
			bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) dataList);
			fragment.setArguments(bundle);
			fragments.add(fragment);
		}
		
		viewPager = (MainViewPager) findViewById(R.id.viewPager);
		viewPager.setSlipping(true);//设置ViewPager是否可以滑动
		viewPager.setOffscreenPageLimit(fragments.size());
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setAdapter(new MyPagerAdapter());

		if (TextUtils.equals(childId, "631") || TextUtils.equals(childId, "631") || TextUtils.equals(childId, "632") || TextUtils.equals(childId, "648")) {//降水
			viewPager.setCurrentItem(0);
		}else if (TextUtils.equals(childId, "636") || TextUtils.equals(childId, "637") || TextUtils.equals(childId, "638") || TextUtils.equals(childId, "656")) {//温度
			viewPager.setCurrentItem(1);
		}else if (TextUtils.equals(childId, "633") || TextUtils.equals(childId, "634") || TextUtils.equals(childId, "635") || TextUtils.equals(childId, "655")) {//风速
			viewPager.setCurrentItem(2);
		}
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < llContainer.getChildCount(); i++) {
				TextView tvName = (TextView) llContainer.getChildAt(i);
				if (i == arg0) {
					tvName.setTextColor(getResources().getColor(R.color.main_color));
				}else {
					tvName.setTextColor(getResources().getColor(R.color.text_color3));
				}
			}
			
			for (int i = 0; i < llContainer1.getChildCount(); i++) {
				TextView tvBar = (TextView) llContainer1.getChildAt(i);
				if (i == arg0) {
					tvBar.setBackgroundColor(getResources().getColor(R.color.main_color));
				}else {
					tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
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
	 * @ClassName: MyOnClickListener
	 * @Description: TODO头标点击监听
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:46:08
	 *
	 */
	private class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (viewPager != null) {
				viewPager.setCurrentItem(index);
			}
		}
	};

	/**
	 * @ClassName: MyPagerAdapter
	 * @Description: TODO填充ViewPager的数据适配器
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:37:47
	 *
	 */
	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(fragments.get(position).getView());
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = fragments.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				/**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中,用异步的方式来执行。
				 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
				 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
				 */
				getFragmentManager().executePendingTransactions();
			}

			if (fragment.getView().getParent() == null) {
				container.addView(fragment.getView()); // 为viewpager增加布局
			}
			return fragment.getView();
		}
	}
	
	/**
	 * 获取最近10个站点号
	 */
	private void asyncTask(String url) {
		//异步请求数据
		HttpAsyncTask2 task = new HttpAsyncTask2();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
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

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					JSONObject obj = new JSONObject(result);
					if (!obj.isNull("list")) {
						dataList.clear();
						JSONArray array = obj.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							JSONObject itemObj = array.getJSONObject(i);
							ShawnRainDto dto = new ShawnRainDto();
							if (!itemObj.isNull("Datetime")) {
								dto.factTime = itemObj.getString("Datetime");
							}
							if (!itemObj.isNull("JS")) {
								dto.factRain = (float) itemObj.getDouble("JS");
							}
							if (!itemObj.isNull("WD")) {
								dto.factTemp = (float) itemObj.getDouble("WD");
							}
							if (!itemObj.isNull("FS")) {
								dto.factWind = (float) itemObj.getDouble("FS");
							}
							dataList.add(dto);
						}
						if (dataList.size() > 0) {
							initViewPager(dataList);
							progressBar.setVisibility(View.GONE);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;

		default:
			break;
		}
	}
	
}
