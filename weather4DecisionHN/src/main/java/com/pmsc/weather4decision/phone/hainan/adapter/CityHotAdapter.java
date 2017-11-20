package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction: 热门城市界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月24日 下午9:14:14
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class CityHotAdapter extends BaseAdapter {
	private List<JsonMap> datas;
	
	public CityHotAdapter(List<JsonMap> datas) {
		this.datas = datas;
	}
	
	@Override
	public int getCount() {
		return datas != null ? datas.size() : 0;
	}
	
	@Override
	public JsonMap getItem(int position) {
		return datas.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView titleTv = null;
		if (titleTv == null) {
			LayoutInflater infalter = LayoutInflater.from(parent.getContext());
			titleTv = (TextView) infalter.inflate(R.layout.activity_city_manager_adatper_city, null);
		}
		
		String city = getItem(position).getString("xianqu");
		titleTv.setText(city);
		return titleTv;
	}
	
}
