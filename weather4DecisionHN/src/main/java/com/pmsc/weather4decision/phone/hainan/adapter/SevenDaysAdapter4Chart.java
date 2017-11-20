package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.android.lib.util.DateUtil;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.Utils;


/**
 * Depiction: 七天天气界面折线图grid数据适配器
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
public class SevenDaysAdapter4Chart extends BaseAdapter {
	private int           pubTime;
	private boolean       isDay;
	private List<JsonMap> datas;
	private String cityId;
	
	public SevenDaysAdapter4Chart(int pubTime, List<JsonMap> datas, boolean isDay, String cityId) {
		this.pubTime = pubTime;
		this.datas = datas;
		this.isDay = isDay;
		this.cityId = cityId;
	}
	
	@Override
	public int getCount() {
		return datas != null ? datas.size() - 1 : 0;
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
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			LayoutInflater infalter = LayoutInflater.from(parent.getContext());
			int layout = isDay ? R.layout.activity_7_day_icon_text_day : R.layout.activity_7_day_icon_text_night;
			convertView = infalter.inflate(layout, null);
			holder.day_tv = (TextView) convertView.findViewById(R.id.tv);
			holder.icon_view = (ImageView) convertView.findViewById(R.id.icon_view);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		int index = 0;
		if (cityId.startsWith("10131")) {
			index = position;
		}else {
			index = pubTime >= 1800 ? position + 1 : position;
		}
		JsonMap data = getItem(index);
		
		String dayWCode = data.getString("fa");//白天天气编码
		String nightWCode = data.getString("fb");//夜晚天气编码
		
		holder.icon_view.setBackground(isDay ? Utils.getWeatherDrawable(true, dayWCode) : Utils.getWeatherDrawable(false, nightWCode));
		
		JsonMap date = DateUtil.getDateWithYear(15).get(position);
		
		holder.day_tv.setText(isDay ? date.getString("week") : date.getString("month"));
		
		return convertView;
	}
	
	private static class Holder {
		TextView  day_tv;
		ImageView icon_view;
	}
}
