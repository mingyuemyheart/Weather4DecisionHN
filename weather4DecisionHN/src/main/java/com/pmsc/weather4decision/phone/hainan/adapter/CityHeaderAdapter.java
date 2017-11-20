package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.CodeParse;
import com.pmsc.weather4decision.phone.hainan.util.Utils;


/**
 * Depiction: 七天天气界面列表数据适配器
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
public class CityHeaderAdapter extends BaseAdapter {
	private List<JsonMap> datas;
	
	public CityHeaderAdapter(List<JsonMap> datas) {
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
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			LayoutInflater infalter = LayoutInflater.from(parent.getContext());
			convertView = infalter.inflate(R.layout.activity_city_weather_header_adapter, null);
			holder.hour_tv = (TextView) convertView.findViewById(R.id.hour_tv);
			holder.weather_tv = (TextView) convertView.findViewById(R.id.weather_tv);
			holder.temp_tv = (TextView) convertView.findViewById(R.id.temp_tv);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		JsonMap data = getItem(position);
		
		//天气图标和温度处理
		String code = data.getString("ja");//天气编码
		String temp = data.getString("jb");//温度
		String hour = data.getString("jf");//时间
		holder.hour_tv.setText(parent.getContext().getString(R.string.hour, hour.substring(8, 10)));
		holder.temp_tv.setText(temp + "°C");
		
		int pubTime = Integer.parseInt(hour.substring(8, 10));
		Drawable icon = Utils.getWeatherDrawable(pubTime < 18, code);
		icon.setBounds(0, 0, dip2px(parent.getContext(), 32), dip2px(parent.getContext(), 32));
		holder.weather_tv.setCompoundDrawables(null, icon, null, null);
		holder.weather_tv.setText(CodeParse.parseWeatherCode(code));
		
		return convertView;
	}
	
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	private static class Holder {
		TextView hour_tv;
		TextView weather_tv;
		TextView temp_tv;
	}
}
