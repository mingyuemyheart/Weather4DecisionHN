package com.pmsc.weather4decision.phone.hainan.act;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.MyPagerAdapter;
import com.pmsc.weather4decision.phone.hainan.fragment.MinuteFragment;
import com.pmsc.weather4decision.phone.hainan.fragment.ProvinceFragment;
import com.pmsc.weather4decision.phone.hainan.util.StatisticUtil;
import com.pmsc.weather4decision.phone.hainan.view.MainViewPager;


/**
 * Depiction: 全省预报界面
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
public class ProvinceActivity extends AbsDrawerActivity {
	
	private MainViewPager viewPager = null;
	private MyPagerAdapter pagerAdapter = null;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private String time_7 = null;
	private TextView tab1, tab2;
	private LinearLayout llTab = null;
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
		tab1 = (TextView) findViewById(R.id.tab1);
		tab1.setOnClickListener(new MyOnClickListener(0));
		tab2 = (TextView) findViewById(R.id.tab2);
		tab2.setOnClickListener(new MyOnClickListener(1));
		llTab = (LinearLayout) findViewById(R.id.llTab);
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
		try {
			JSONObject obj = new JSONObject(channelData);
			if (!obj.isNull("child")) {
				JSONArray array = obj.getJSONArray("child");
				for (int i = 0; i < array.length(); i++) {
					JSONObject itemObj = array.getJSONObject(i);
					String id = itemObj.getString("id");
					Fragment fragment = null;
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
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		viewPager = (MainViewPager) findViewById(R.id.viewPager);
		pagerAdapter = new MyPagerAdapter(ProvinceActivity.this, fragments);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setSlipping(false);//设置ViewPager是否可以滑动
		viewPager.setOffscreenPageLimit(fragments.size());
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		if (fragments.size() <= 1) {
			llTab.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
		}else {
			llTab.setVisibility(View.VISIBLE);
			divider.setVisibility(View.VISIBLE);
		}
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				tab1.setTextColor(getResources().getColor(R.color.tab_font_selected));
				tab2.setTextColor(getResources().getColor(R.color.tab_font_unselected));
			}else if (arg0 == 1) {
				tab1.setTextColor(getResources().getColor(R.color.tab_font_unselected));
				tab2.setTextColor(getResources().getColor(R.color.tab_font_selected));
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
