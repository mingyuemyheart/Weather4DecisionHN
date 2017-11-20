package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.android.lib.util.DateUtil;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.Utils;


/**
 * Depiction: 城市管理十五天天气界面列表数据适配器
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
public class CityDaysAdapter extends BaseAdapter {
	private int           pubTime;
	private List<JsonMap> datas;
	private String cityId;
	
	public CityDaysAdapter(int pubTime, List<JsonMap> datas, String cityId) {
		this.pubTime = pubTime;
		this.datas = datas;
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
			convertView = infalter.inflate(R.layout.activity_city_weather_adapter, null);
			holder.date_tv = (TextView) convertView.findViewById(R.id.date_tv);
			holder.day_temp_tv = (TextView) convertView.findViewById(R.id.day_temp_tv);
			holder.night_temp_tv = (TextView) convertView.findViewById(R.id.night_temp_tv);
			holder.lineV = convertView.findViewById(R.id.line_v);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.lineV.setBackgroundResource(position == 0 ? R.drawable.city_line_v_1 : R.drawable.city_line_v);
		
		int index = 0;
		if (cityId.startsWith("10131")) {
			index = position;
		}else {
			index = pubTime >= 1800 ? position + 1 : position;
		}
		JsonMap date = DateUtil.getDateWithYear(15).get(position);
		
		holder.date_tv.setText(date.getString("month").replace("-", "/"));
		
		JsonMap data = getItem(index);
		
		//白天天气图标和温度处理
		int iconSize = dip2px(parent.getContext(), 24);
		String dayT = data.getString("fc");//白天温度
		if (dayT != null) {
			holder.day_temp_tv.setText(TextUtils.isEmpty(dayT) ? "" : (dayT + "°C"));
		}
		
		String dayWCode = data.getString("fa");//白天天气编码
		if (dayWCode != null) {
			Drawable dayIcon = Utils.getWeatherDrawable(true, dayWCode);
			if (dayIcon != null) {
				dayIcon.setBounds(0, 0, iconSize, iconSize);
				holder.day_temp_tv.setCompoundDrawables(dayIcon, null, null, null);
			}
		}
		
		//夜晚天气图标和温度处理
		String nightT = data.getString("fd");//夜晚温度
		holder.night_temp_tv.setText(nightT + "°C");
		
		String nightWCode = data.getString("fb");//夜晚天气编码
		Drawable nightIcon = Utils.getWeatherDrawable(false, nightWCode);
		nightIcon.setBounds(0, 0, iconSize, iconSize);
		holder.night_temp_tv.setCompoundDrawables(nightIcon, null, null, null);
		
		return convertView;
	}
	
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	private static class Holder {
		TextView date_tv;
		TextView day_temp_tv;
		TextView night_temp_tv;
		View     lineV;
	}
}
