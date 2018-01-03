package com.pmsc.weather4decision.phone.hainan.act;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.view.MoreView;
import com.pmsc.weather4decision.phone.hainan.view.MoreView.OnLoadDataListener;


/**
 * Depiction: 某个城市的天气信息
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
public class CityWeatherActivity extends BaseActivity implements OnLoadDataListener {
	private List<JsonMap> datas;
	String cityId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		
		JsonMap data = JsonMap.parseJson(getIntent().getStringExtra("data"));
		String city = data.getString("xianqu");
		cityId = data.getString("city_id");
		
		MoreView moreView = new MoreView(this);
		moreView.setOnLoadDataListener(this);
		moreView.setCityData(city, cityId);
		setContentView(moreView);
	}
	
	public void onLeftButtonAction(View view) {
		onBackPressed();
	}
	
	public void onRightButtonAction(View view) {
		if (datas == null) {
			return;
		}
		
		try {
			//七天预报的发布时间
			Bundle bundle = new Bundle();
			bundle.putString("cityId", cityId);
			openActivity(ForecastActivity.class, bundle);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "onRightButtonAction()-->" + e.toString());
		}
	}
	
	@Override
	public void onLoadDataFinish(List<JsonMap> datas) {
		this.datas = datas;
	}
}
