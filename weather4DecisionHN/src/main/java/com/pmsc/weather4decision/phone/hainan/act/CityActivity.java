package com.pmsc.weather4decision.phone.hainan.act;

import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.CityHotAdapter;
import com.pmsc.weather4decision.phone.hainan.adapter.CitySearchAdapter;
import com.pmsc.weather4decision.phone.hainan.db.DBDao;


/**
 * Depiction: 城市管理界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月6日 下午2:14:37
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class CityActivity extends AbsDrawerActivity implements TextWatcher, OnItemClickListener {
	private EditText          searchView;
	private Button            cancelBtn;
	private GridView          hotGrid;   //热门城市
	private ListView          listView;
	
	private CityHotAdapter    hotAdapter;
	private CitySearchAdapter sAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_manager);
		
		searchView = (EditText) findViewById(R.id.search_view);
		searchView.addTextChangedListener(this);
		
		hotGrid = (GridView) findViewById(R.id.grid_view);
		hotGrid.setOnItemClickListener(this);
		String province = getString(R.string.hainan);
		List<JsonMap> cityList = DBDao.getInstance(getApplicationContext()).queryByProvince(province);
		hotAdapter = new CityHotAdapter(cityList);
		hotGrid.setAdapter(hotAdapter);
		
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchView.setText("");
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		searchView.setText("");
		listView.setVisibility(View.GONE);
		hotGrid.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		JsonMap data = null;
		if (parent.getId() == R.id.grid_view) {
			//grid 点击
			data = hotAdapter.getItem(position);
		} else {
			//list 点击
			data = sAdapter.getItem(position);
		}
		
		if (data != null) {
			//获取城市天气信息
			Bundle bundle = new Bundle();
			bundle.putString("data", data.toString());
			bundle.putString("cityId", data.getString("city_id"));
			openActivity(ForecastActivity.class, bundle);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (!TextUtils.isEmpty(s)) {
			hotGrid.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			
			String key = String.valueOf(s);
			List<JsonMap> cityList = DBDao.getInstance(getApplicationContext()).queryByDistrict(key);
			sAdapter = new CitySearchAdapter(cityList);
			listView.setAdapter(sAdapter);
		} else {
			hotGrid.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		
	}
}
