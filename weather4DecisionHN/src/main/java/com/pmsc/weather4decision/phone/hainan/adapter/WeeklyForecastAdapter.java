package com.pmsc.weather4decision.phone.hainan.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.dto.WeatherDto;

public class WeeklyForecastAdapter extends BaseAdapter{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<WeatherDto> mArrayList = new ArrayList<WeatherDto>();
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
	
	private final class ViewHolder{
		TextView tvWeek;
		TextView tvDate;
		TextView tvHighPhe;
		ImageView ivHighPhe;
		TextView tvHighTemp;
		TextView tvLowPhe;
		ImageView ivLowPhe;
		TextView tvLowTemp;
	}
	
	private ViewHolder mHolder = null;
	
	public WeeklyForecastAdapter(Context context, List<WeatherDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.weekly_forecast_cell, null);
			mHolder = new ViewHolder();
			mHolder.tvWeek = (TextView) convertView.findViewById(R.id.tvWeek);
			mHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			mHolder.tvHighPhe = (TextView) convertView.findViewById(R.id.tvHighPhe);
			mHolder.ivHighPhe = (ImageView) convertView.findViewById(R.id.ivHighPhe);
			mHolder.tvHighTemp = (TextView) convertView.findViewById(R.id.tvHighTemp);
			mHolder.tvLowPhe = (TextView) convertView.findViewById(R.id.tvLowPhe);
			mHolder.ivLowPhe = (ImageView) convertView.findViewById(R.id.ivLowPhe);
			mHolder.tvLowTemp = (TextView) convertView.findViewById(R.id.tvLowTemp);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		WeatherDto dto = mArrayList.get(position);
		if (position == 0) {
			mHolder.tvWeek.setText("今天");
		}else {
			String week = dto.week;
			mHolder.tvWeek.setText(mContext.getString(R.string.week)+week.substring(week.length()-1, week.length()));
		}
		try {
			mHolder.tvDate.setText(sdf2.format(sdf1.parse(dto.date)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		mHolder.tvLowPhe.setText(dto.lowPhe);
		mHolder.tvLowTemp.setText(dto.lowTemp+"℃");
		Drawable ld = mContext.getResources().getDrawable(R.drawable.phenomenon_drawable_night);
		ld.setLevel(dto.lowPheCode);
		mHolder.ivLowPhe.setBackground(ld);
		
		mHolder.tvHighPhe.setText(dto.highPhe);
		mHolder.tvHighTemp.setText(dto.highTemp+"℃");
		Drawable hd = mContext.getResources().getDrawable(R.drawable.phenomenon_drawable);
		hd.setLevel(dto.highPheCode);
		mHolder.ivHighPhe.setBackground(hd);
		
		return convertView;
	}

}
