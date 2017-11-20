package com.pmsc.weather4decision.phone.hainan.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.act.LiveDataDetailsActivity.OnSortClickListener;
import com.pmsc.weather4decision.phone.hainan.act.LiveDataDetailsActivity.SortType;


/**
 * Depiction: 实况详情列表样式适配器
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
public class LiveDataDetailsAdapter extends BaseAdapter {
	private OnSortClickListener onSortClickListener;
	private List<JsonMap>       datas;
	private SortType            sortType;
	
	public LiveDataDetailsAdapter(OnSortClickListener onSortClickListener, SortType sortType, JsonMap tableHeader, List<JsonMap> datas) {
		this.onSortClickListener = onSortClickListener;
		this.sortType = sortType;
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<JsonMap>();
		}
		this.datas.add(0, tableHeader);
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
			convertView = infalter.inflate(R.layout.activity_live_data_details_adapter, null);
			holder.leftTv = (TextView) convertView.findViewById(R.id.left_view);
			holder.midTv = (TextView) convertView.findViewById(R.id.mid_view);
			holder.rightTv = (TextView) convertView.findViewById(R.id.right_view);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		if (position == 0) {
			holder.leftTv.setOnClickListener(onSortClickListener);
			holder.midTv.setOnClickListener(onSortClickListener);
			holder.rightTv.setOnClickListener(onSortClickListener);
			
			holder.leftTv.setPadding(0, 30, 0, 30);
			holder.midTv.setPadding(0, 30, 0, 30);
			holder.rightTv.setPadding(0, 30, 0, 30);
			
			refreshIconAndBg(holder.leftTv, holder.midTv, holder.rightTv);
		} else {
			int itemColor = HNApp.getInstance().getResources().getColor(R.color.transparent);
			holder.leftTv.setBackgroundColor(itemColor);
			holder.midTv.setBackgroundColor(itemColor);
			holder.rightTv.setBackgroundColor(itemColor);
		}
		
		int textColor = position == 0 ? Color.WHITE : Color.BLACK;
		holder.leftTv.setTextColor(textColor);
		holder.midTv.setTextColor(textColor);
		holder.rightTv.setTextColor(textColor);
		
		JsonMap data = getItem(position);
		if (position != 0) {
			holder.leftTv.setCompoundDrawables(null, null, null, null);
			holder.midTv.setCompoundDrawables(null, null, null, null);
			holder.rightTv.setCompoundDrawables(null, null, null, null);
			holder.leftTv.setText(data.getString("stationName"));
			holder.midTv.setText(data.getString("area"));
			if (!TextUtils.isEmpty(data.getString("val"))) {
				float value = Float.valueOf(data.getString("val"));
				BigDecimal bd = new BigDecimal(value);
				float newValue = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				holder.rightTv.setText(newValue+"");
			}
		}else {
			holder.leftTv.setText(data.getString("stationName"));
			holder.midTv.setText(data.getString("area"));
			holder.rightTv.setText(data.getString("val"));
		}
		
		return convertView;
	}
	
	private void refreshIconAndBg(TextView leftTv, TextView midTv, TextView rightTv) {
		int headerDefaultColor = HNApp.getInstance().getResources().getColor(R.color.live_header_default_color);
		int headerFucosColor = HNApp.getInstance().getResources().getColor(R.color.live_header_fucos_color);
		
		if (sortType == SortType.AREA_ASC) {
			//区域升序
			leftTv.setBackgroundColor(headerDefaultColor);
			midTv.setBackgroundColor(headerFucosColor);
			rightTv.setBackgroundColor(headerDefaultColor);
			
			midTv.setCompoundDrawables(null, null, getDrawable(midTv.getContext(), true), null);
		} else if (sortType == SortType.AREA_DESC) {
			//区域降序
			leftTv.setBackgroundColor(headerDefaultColor);
			midTv.setBackgroundColor(headerFucosColor);
			rightTv.setBackgroundColor(headerDefaultColor);
			
			midTv.setCompoundDrawables(null, null, getDrawable(midTv.getContext(), false), null);
		} else if (sortType == SortType.STATION_ASC) {
			//站升序
			leftTv.setBackgroundColor(headerFucosColor);
			midTv.setBackgroundColor(headerDefaultColor);
			rightTv.setBackgroundColor(headerDefaultColor);
			
			leftTv.setCompoundDrawables(null, null, getDrawable(midTv.getContext(), true), null);
		} else if (sortType == SortType.STATION_DESC) {
			//站降序
			leftTv.setBackgroundColor(headerFucosColor);
			midTv.setBackgroundColor(headerDefaultColor);
			rightTv.setBackgroundColor(headerDefaultColor);
			
			leftTv.setCompoundDrawables(null, null, getDrawable(midTv.getContext(), false), null);
		} else if (sortType == SortType.VALUE_ASC) {
			//值升序
			leftTv.setBackgroundColor(headerDefaultColor);
			midTv.setBackgroundColor(headerDefaultColor);
			rightTv.setBackgroundColor(headerFucosColor);
			
			rightTv.setCompoundDrawables(null, null, getDrawable(midTv.getContext(), true), null);
		} else if (sortType == SortType.VALUE_DESC) {
			//值降序
			leftTv.setBackgroundColor(headerDefaultColor);
			midTv.setBackgroundColor(headerDefaultColor);
			rightTv.setBackgroundColor(headerFucosColor);
			
			rightTv.setCompoundDrawables(null, null, getDrawable(midTv.getContext(), false), null);
		}
	}
	
	@SuppressWarnings ("deprecation")
    private Drawable getDrawable(Context context, boolean isAsc) {
		int iconId = isAsc ? R.drawable.arrow_up : R.drawable.arrow_down;
		Drawable icon = context.getResources().getDrawable(iconId);
		icon.setBounds(0, 0, 30, 50);
		return icon;
	}
	
	private static class Holder {
		TextView leftTv;
		TextView midTv;
		TextView rightTv;
	}
}
