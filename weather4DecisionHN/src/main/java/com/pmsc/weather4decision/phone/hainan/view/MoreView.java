package com.pmsc.weather4decision.phone.hainan.view;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.android.lib.util.DateUtil;
import com.android.lib.view.HorizontalListView;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.CityDaysAdapter;
import com.pmsc.weather4decision.phone.hainan.adapter.CityHeaderAdapter;
import com.pmsc.weather4decision.phone.hainan.http.FetchWeather;
import com.pmsc.weather4decision.phone.hainan.http.FetchWeather.OnFetchWeatherListener;
import com.pmsc.weather4decision.phone.hainan.util.AqiParse;
import com.pmsc.weather4decision.phone.hainan.util.CodeParse;
import com.pmsc.weather4decision.phone.hainan.util.Utils;


/**
 * Depiction: 十五天,24小时等天气信息界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2016年1月19日 上午10:48:36
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class MoreView extends FrameLayout implements OnFetchWeatherListener {
	private BaseActivity       act;
	private RelativeLayout     titleBar;
	private ListView           listView;
	private View               header;
	private TextView           headerLeft;
	private TextView           headerRight;
	private HorizontalListView gridView;
	private List<JsonMap>      datas;
	private String cityId;
	
	public MoreView(Context context) {
		super(context);
		act = (BaseActivity) context;
		
		inflate(context, R.layout.activity_city_weather, this);
		titleBar = (RelativeLayout) findViewById(R.id.title_bar);
		
		listView = (ListView) findViewById(R.id.listview);
		
		header = LayoutInflater.from(getContext()).inflate(R.layout.activity_city_weather_header, null);
		headerLeft = (TextView) header.findViewById(R.id.header_left);
		headerRight = (TextView) header.findViewById(R.id.header_right);
		gridView = (HorizontalListView) header.findViewById(R.id.grid_view);
	}
	
	public void setCityData(String city, String cityId) {
		this.cityId = cityId;
		TextView titleView = (TextView) findViewById(R.id.title_view);
		if (!TextUtils.isEmpty(city)) {
			titleView.setText(city);
			titleBar.setVisibility(View.VISIBLE);
		}else{
			titleBar.setVisibility(View.GONE);
		}
		
		FetchWeather fetch = new FetchWeather();
		fetch.setOnFetchWeatherListener(this);
		fetch.perform(cityId, "all");
		
		act.showLoadingDialog(R.string.loading);
	}
	
	@Override
	public void onFetchWeather(String tag, String response) {
		act.cancelLoadingDialog();
		
		datas = JsonMap.parseJsonArray(response);
		if (datas != null && datas.size() > 0) {
			listView.addHeaderView(header);
			JsonMap forecast = datas.get(1).getMap("f");
			List<JsonMap> forecastList = forecast.getListMap("f1");
			JsonMap fisrtDay = forecastList.get(0);
			JsonMap secondDay = forecastList.get(1);
			
			JsonMap live = datas.get(0).getMap("l");
			String dayTemp = live.getString("l1");//今天白天温度
			String tonightTemp = fisrtDay.getString("fd");//今天夜晚温度
			String tomorrowTemp = secondDay.getString("fc");//明天白天温度
			String dayWCode = fisrtDay.getString("fa");//今天白天天气编号
			String tonightWCode = fisrtDay.getString("fb");//今天夜晚天气编码后
			String todayW = CodeParse.parseWeatherCode(dayWCode); //	今天白天天气
			String tonightW = CodeParse.parseWeatherCode(tonightWCode);//今天夜晚天气
			String tomorrowW = CodeParse.parseWeatherCode(secondDay.getString("fa"));//明天白天天气
			
			String shidu = live.getString("l2");
			String fl = live.getString("l3");
			String windFl = (TextUtils.isEmpty(fl) || fl.equalsIgnoreCase("0") ? act.getString(R.string.micro_wind) : fl + act.getString(R.string.fl));
			String fx = live.getString("l4");
			
			//15天预报发布时间
			String _7daysTime = forecast.getString("f0");
			int pubTime = Integer.parseInt(_7daysTime.substring(8, 12));
			Drawable icon = Utils.getWeatherDrawable(pubTime < 1800, pubTime < 1800 ? dayWCode : tonightWCode);
			if (icon != null) {
				icon.setBounds(0, 0, act.dip2px(32), act.dip2px(32));
				headerLeft.setCompoundDrawables(null, icon, null, null);
			}
			
			JsonMap date = DateUtil.getDateWithYear(15).get(0);
			headerRight.setText(act.getString(R.string.today, date.getString("week").replace(act.getString(R.string.week), act.getString(R.string.xingqi))));
			
			if (pubTime < 1800) {
				//晚上十八点之前发布的显示今天白天到夜晚,两则相同则不带转字
				String weather = "";
				if (TextUtils.equals(todayW, tonightW)) {
					weather = todayW;
				}else {
					weather = todayW+"转"+tonightW;
				}
				headerLeft.setText(weather);
				headerLeft.append("\n" + dayTemp + "°C");
//				headerRight.append("\n" + tonightTemp + "°C");
			} else {
				//晚上十八点以后,早上六点之前发的显示今晚到明天上午的天气信息,两则相同则不带转字
				String weather = tonightW.equalsIgnoreCase(tomorrowW) ? tonightW : act.getString(R.string.zhuan, tonightW, tomorrowW);
				headerLeft.setText(weather);
				headerLeft.append("\n" + tonightTemp + "°C");
//				headerRight.append("\n" + tomorrowTemp + "°C");
			}
			
			headerRight.append("\n" + CodeParse.parseWindfxCode(fx) + " " + windFl);
			headerRight.append("\n" + act.getString(R.string.shidu, shidu + "%"));
			
			//显示空气数据
			if (datas.get(4).containsKey("p")) {
				JsonMap air = datas.get(4).getMap("p");
				if (air != null) {
					String aqi = air.getString("p2");
					headerRight.append("\n" + act.getString(R.string.air_zhiliang, AqiParse.parse(aqi)) + aqi);
				}
			}
			
			listView.setAdapter(new CityDaysAdapter(pubTime, forecastList, cityId));
			
			List<JsonMap> hourList = datas.get(3).getListMap("jh");
			gridView.setAdapter(new CityHeaderAdapter(hourList));
			
		} else {
			act.showToast(R.string.loading_fail);
		}
		
		if (onLoadDataListener != null) {
			onLoadDataListener.onLoadDataFinish(datas);
		}
	}
	
	private OnLoadDataListener onLoadDataListener;
	
	public OnLoadDataListener getOnLoadDataListener() {
		return onLoadDataListener;
	}
	
	public void setOnLoadDataListener(OnLoadDataListener onLoadDataListener) {
		this.onLoadDataListener = onLoadDataListener;
	}
	
	public interface OnLoadDataListener {
		void onLoadDataFinish(List<JsonMap> datas);
	}
}
