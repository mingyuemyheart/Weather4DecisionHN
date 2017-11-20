package com.pmsc.weather4decision.phone.hainan.act;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.SevenDaysAdapter;
import com.pmsc.weather4decision.phone.hainan.adapter.SevenDaysAdapter4Chart;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.pmsc.weather4decision.phone.hainan.view.ChartView;
import com.pmsc.weather4decision.phone.hainan.view.MoreView;


/**
 * Depiction: 七天预报界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月2日 上午10:39:45
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class SevenDaysActivity extends BaseActivity implements OnCheckedChangeListener {
	private FrameLayout	   contentLayout;
	private ListView	   listView;
	private RelativeLayout chartLayout;
	private ChartView	   chartView;
	private MoreView	   moreView;
	private GridView	   dayGrid;
	private GridView	   nightGrid;
						   
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_7_days);
		contentLayout = (FrameLayout) findViewById(R.id.content_layout);
		
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.switch_view);
		radioGroup.setOnCheckedChangeListener(this);
		
		listView = (ListView) findViewById(R.id.listview);
		int pubTime = getIntent().getIntExtra("pub_time", 0000);
		String cityId = getIntent().getStringExtra("cityId");
		String forecastData = getIntent().getStringExtra("forecast_data");
		List<JsonMap> datas = JsonMap.parseJsonArray(forecastData);
		listView.setAdapter(new SevenDaysAdapter(pubTime, datas, cityId));
		
		dayGrid = (GridView) findViewById(R.id.day_grid);
		dayGrid.setAdapter(new SevenDaysAdapter4Chart(pubTime, datas, true, cityId));
		nightGrid = (GridView) findViewById(R.id.night_grid);
		nightGrid.setAdapter(new SevenDaysAdapter4Chart(pubTime, datas, false, cityId));
		
		chartLayout = (RelativeLayout) findViewById(R.id.chart_layout);
		chartView = (ChartView) findViewById(R.id.chart_view);
		
		List<Integer> dayList = new ArrayList<Integer>();
		List<Integer> nightList = new ArrayList<Integer>();
		if (cityId.startsWith("10131")) {
			for (int i = 0; i < 15; i++) {
				dayList.add(datas.get(i).getInt("fc"));
				nightList.add(datas.get(i).getInt("fd"));
			}
		}else {
			int start = pubTime >= 1800 ? 1 : 0;
			for (int i = start; i < start + 14; i++) {
				dayList.add(datas.get(i).getInt("fc"));
				nightList.add(datas.get(i).getInt("fd"));
			}
		}
		chartView.setData(dayList, nightList);
		
		ImageView linewView = (ImageView) findViewById(R.id.line_view);
		RadioButton moreTab = (RadioButton) findViewById(R.id.more_view);
		
		boolean isFromHome = getIntent().getBooleanExtra("isFromHome", false);
		if (isFromHome) {
			linewView.setVisibility(View.VISIBLE);
			moreTab.setVisibility(View.VISIBLE);
			moreView = new MoreView(this);
			moreView.setVisibility(View.INVISIBLE);
			moreView.setCityData(null, PreferUtil.getCurrentCityId());
			contentLayout.addView(moreView, new FrameLayout.LayoutParams(-1, -1));
		} else {
			linewView.setVisibility(View.GONE);
			moreTab.setVisibility(View.GONE);
		}
	}
	
	public void onLeftButtonAction(View view) {
		onBackPressed();
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.yubao_view) {
			listView.setVisibility(View.VISIBLE);
			chartLayout.setVisibility(View.INVISIBLE);
			if (moreView != null) {
				moreView.setVisibility(View.INVISIBLE);
			}
		} else if (checkedId == R.id.qushi_view) {
			listView.setVisibility(View.INVISIBLE);
			chartLayout.setVisibility(View.VISIBLE);
			if (moreView != null) {
				moreView.setVisibility(View.INVISIBLE);
			}
		} else if (checkedId == R.id.more_view) {
			listView.setVisibility(View.INVISIBLE);
			chartLayout.setVisibility(View.INVISIBLE);
			moreView.setVisibility(View.VISIBLE);
		}
	}
}
