package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.android.lib.util.DateUtil;
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
public class SevenDaysAdapter extends BaseAdapter {
	private int           pubTime;
	private List<JsonMap> datas;
	private String cityId;
	
	public SevenDaysAdapter(int pubTime, List<JsonMap> datas, String cityId) {
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
			convertView = infalter.inflate(R.layout.activity_7_days_adapter, null);
			holder.day_icon_view = (ImageView) convertView.findViewById(R.id.day_icon_view);
			holder.night_icon_view = (ImageView) convertView.findViewById(R.id.night_icon_view);
			holder.title_tv = (TextView) convertView.findViewById(R.id.title_tv);
			holder.day_tv = (TextView) convertView.findViewById(R.id.day_tv);
			holder.night_tv = (TextView) convertView.findViewById(R.id.night_tv);
			holder.weather_tv = (TextView) convertView.findViewById(R.id.weather_tv);
			holder.wind_tv = (TextView) convertView.findViewById(R.id.wind_tv);
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
		JsonMap date = DateUtil.getDateWithYear(15).get(position);
		
		holder.title_tv.setText(date.getString("year") + " " + date.getString("week"));
		
		JsonMap data = getItem(index);
		
		//白天天气图标和温度处理
		String dayWCode = data.getString("fa");//白天天气编码
		String dayT = data.getString("fc");//白天温度
		if (dayWCode != null) {
			holder.day_icon_view.setBackground(Utils.getWeatherDrawable(true, dayWCode));
		}
		if (dayT != null) {
			holder.day_tv.setText(TextUtils.isEmpty(dayT) ? "" : (dayT + "°C"));
		}
		
		//夜晚天气图标和温度处理
		String nightWCode = data.getString("fb");//夜晚天气编码
		String nightT = data.getString("fd");//夜晚温度
		holder.night_icon_view.setBackground(Utils.getWeatherDrawable(false, nightWCode));
		holder.night_tv.setText(nightT + "°C");
		
		//天气信息处理
		String dayW = CodeParse.parseWeatherCode(dayWCode);
		String nightW = CodeParse.parseWeatherCode(nightWCode);
		String weather = "";
		if (TextUtils.isEmpty(dayW)) {
			//没有白天天气，则显示夜晚天气
			weather = nightW;
		} else {
			if (TextUtils.equals(dayW, nightW)) {
				//白天和夜晚一样，则显示一种
				weather = dayW;
			} else {
				//白天夜晚不一样则显示格式为：白天天气转夜晚天气
				weather = parent.getContext().getString(R.string.zhuan, dayW, nightW);
			}
		}
		if (weather != null) {
			holder.weather_tv.setText(weather);
		}
		
		//风向风力信息处理，优先显示白天风向风力
		String dayFx = CodeParse.parseWindfxCode(data.getString("fe"));//白天风向
		String dayFl = CodeParse.parseWindflCode(data.getString("fg"));//白天风力
		String nightFx = CodeParse.parseWindfxCode(data.getString("ff"));//夜晚风向
		String nightFl = CodeParse.parseWindflCode(data.getString("fh"));//夜晚风力
		
		if (!TextUtils.isEmpty(cityId) && cityId.startsWith("10131")) {//海南
			String fg = data.getString("fg");
			if (TextUtils.equals(fg, "0")) {
				dayFl = "微风";//白天风力
			}else {
				dayFl = fg+"级";//白天风力
			}
			String fh = data.getString("fh");
			if (TextUtils.equals(fh, "0")) {
				nightFl = "微风";//夜晚风力
			}else {
				nightFl = fh+"级";//夜晚风力
			}
		}else {
			dayFl = CodeParse.parseWindflCode(data.getString("fg"));//白天风力
			nightFl = CodeParse.parseWindflCode(data.getString("fh"));//夜晚风力
		}
		
		String fxfl = "";
		if (TextUtils.isEmpty(dayFx) || TextUtils.isEmpty(dayFl)) {
			fxfl = nightFx + " " + nightFl;
		} else {
			fxfl = dayFx + " " + dayFl;
		}
		holder.wind_tv.setText(fxfl);
		
		return convertView;
	}
	
	private static class Holder {
		TextView  title_tv;
		ImageView day_icon_view;
		ImageView night_icon_view;
		TextView  day_tv;
		TextView  night_tv;
		TextView  weather_tv;
		TextView  wind_tv;
	}
}
