package com.pmsc.weather4decision.phone.hainan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction: 普通列表样式适配器
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月24日 下午9:13:42
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ListNormalAdapter extends BaseAdapter {
	private List<JsonMap> datas;
	private boolean       isOldDoc;
	private int           titleColor;
	
	public ListNormalAdapter(int titleColor, boolean isOldDoc) {
		this.titleColor = titleColor;
		this.isOldDoc = isOldDoc;
		this.datas = new ArrayList<JsonMap>();
	}
	
	public void addDatas(List<JsonMap> dataList) {
		if (dataList != null) {
			datas.addAll(dataList);
			notifyDataSetChanged();
		}
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
			convertView = infalter.inflate(R.layout.activity_listview_normal_adapter, null);
			holder.titleTv = (TextView) convertView.findViewById(R.id.title_view);
			holder.timeTv = (TextView) convertView.findViewById(R.id.pub_time_view);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		JsonMap data = getItem(position);
		holder.titleTv.setTextColor(titleColor);
		String titleKey = isOldDoc ? "l1" : "title";
		holder.titleTv.setText(data.getString(titleKey));
		
		String timeKey = isOldDoc ? "l3" : "publicTime";
		holder.timeTv.setText(data.getString(timeKey));
		return convertView;
	}
	
	private static class Holder {
		TextView titleTv;
		TextView timeTv;
	}
}
