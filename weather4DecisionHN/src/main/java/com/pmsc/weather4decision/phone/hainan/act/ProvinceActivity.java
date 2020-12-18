package com.pmsc.weather4decision.phone.hainan.act;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.MyPagerAdapter;
import com.pmsc.weather4decision.phone.hainan.fragment.ListDocumentFragment;
import com.pmsc.weather4decision.phone.hainan.fragment.MinuteFragment;
import com.pmsc.weather4decision.phone.hainan.fragment.ProvinceFragment;
import com.pmsc.weather4decision.phone.hainan.fragment.WebviewFragment;
import com.pmsc.weather4decision.phone.hainan.util.StatisticUtil;
import com.pmsc.weather4decision.phone.hainan.view.MainViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Depiction: 全省预报界面
 */
public class ProvinceActivity extends AbsDrawerActivity {
	
	private MainViewPager viewPager = null;
	private MyPagerAdapter pagerAdapter = null;
	private List<Fragment> fragments = new ArrayList<>();
	private String time_7 = null;
	private LinearLayout llContainer = null;
	private TextView divider = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_province);
		initWidget();
		initViewPager();
	}
	
	private void initWidget() {
		time_7 = getIntent().getStringExtra("time_7");
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		divider = (TextView) findViewById(R.id.divider);

		if (getIntent().hasExtra("columnId")) {
			String columnId = getIntent().getStringExtra("columnId");
			StatisticUtil.statisticClickCount(columnId);
		}
	}
	
	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		llContainer.removeAllViews();
		fragments.clear();
		try {
			JSONObject obj = new JSONObject(channelData);
			if (!obj.isNull("child")) {
				JSONArray array = obj.getJSONArray("child");

				if (array.length() <= 1) {
					llContainer.setVisibility(View.GONE);
					divider.setVisibility(View.GONE);
				}else {
					llContainer.setVisibility(View.VISIBLE);
					divider.setVisibility(View.VISIBLE);
				}

				for (int i = 0; i < array.length(); i++) {
					JSONObject itemObj = array.getJSONObject(i);

					String title = itemObj.getString("title");
					TextView textView = new TextView(this);
					textView.setText(title);
					textView.setSingleLine();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					textView.setGravity(Gravity.CENTER);
					textView.setOnClickListener(new MyOnClickListener(i));
					if (i == 0) {
						textView.setTextColor(getResources().getColor(R.color.tab_font_selected));
					} else {
						textView.setTextColor(getResources().getColor(R.color.tab_font_unselected));
					}
					llContainer.addView(textView);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					params.weight = 1;
					textView.setLayoutParams(params);

					Fragment fragment;
					String id = itemObj.getString("id");
					if (TextUtils.equals(id, "650")) {
						fragment = new ProvinceFragment();
						Bundle bundle = new Bundle();
						bundle.putString("time_7", time_7);
						bundle.putString("listUrl", itemObj.getString("listUrl"));
						bundle.putString("dataUrl", itemObj.getString("dataUrl"));
						fragment.setArguments(bundle);
						fragments.add(fragment);
					} else if (TextUtils.equals(id, "651")) {
						fragment = new MinuteFragment();
						fragments.add(fragment);
					} else if (TextUtils.equals(id, "691")) {
						JsonMap data = JsonMap.parseJson(itemObj.toString());
						fragment = ListDocumentFragment.newInstance(i, data);
						fragments.add(fragment);
					} else if (TextUtils.equals(id, "692")) {
						JsonMap data = JsonMap.parseJson(itemObj.toString());
						fragment = WebviewFragment.newInstance(i, data);
						fragments.add(fragment);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		viewPager = (MainViewPager) findViewById(R.id.viewPager);
		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setSlipping(false);//设置ViewPager是否可以滑动
		viewPager.setOffscreenPageLimit(fragments.size());
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			TextView tt = (TextView) llContainer.getChildAt(arg0);
			for (int i = 0; i < llContainer.getChildCount(); i++) {
				TextView tv = (TextView) llContainer.getChildAt(i);
				if (TextUtils.equals(tv.getText().toString(), tt.getText().toString())) {
					tv.setTextColor(getResources().getColor(R.color.tab_font_selected));
				} else {
					tv.setTextColor(getResources().getColor(R.color.tab_font_unselected));
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
	
}
