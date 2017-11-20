package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.dto.ShawnRainDto;

public class ShawnRainDetailAdapter extends BaseAdapter{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<ShawnRainDto> mArrayList = new ArrayList<ShawnRainDto>();
	
	private final class ViewHolder{
		TextView tvStationName;
		TextView tvArea;
		TextView tvValue;
	}
	
	private ViewHolder mHolder = null;
	
	public ShawnRainDetailAdapter(Context context, List<ShawnRainDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_rain_detail_cell, null);
			mHolder = new ViewHolder();
			mHolder.tvStationName = (TextView) convertView.findViewById(R.id.tvStationName);
			mHolder.tvArea = (TextView) convertView.findViewById(R.id.tvArea);
			mHolder.tvValue = (TextView) convertView.findViewById(R.id.tvValue);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		ShawnRainDto dto = mArrayList.get(position);
		
		if (!TextUtils.isEmpty(dto.stationName)) {
			mHolder.tvStationName.setText(dto.stationName);
		}
		
		if (!TextUtils.isEmpty(dto.area)) {
			mHolder.tvArea.setText(dto.area);
		}
		
		mHolder.tvValue.setText(dto.val+"");
		
		if (position % 2 == 0) {
			convertView.setBackgroundColor(0xffeaeaea);
		}else {
			convertView.setBackgroundColor(0xfff5f5f5);
		}
		
		return convertView;
	}

}
