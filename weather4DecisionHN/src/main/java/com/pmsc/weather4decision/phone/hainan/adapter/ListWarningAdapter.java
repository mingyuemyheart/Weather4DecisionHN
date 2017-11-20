package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import com.android.lib.data.JsonMap;
import com.android.lib.util.AssetFile;
import com.pmsc.weather4decision.phone.hainan.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


/**
 * Depiction: 预警列表信息适配器
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月24日 下午9:13:59
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ListWarningAdapter extends BaseAdapter {
	private int				id;
	private List<String>	cityList;
	private JsonMap			data;
	private int				titleColor;
							
	private OnClickListener	onClickListener;
							
	public ListWarningAdapter(int id, List<String> cityList, JsonMap data, int titleColor) {
		this.id = id;
		this.cityList = cityList;
		this.data = data;
		this.titleColor = titleColor;
	}
	
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
	
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
	
	@Override
	public int getCount() {
		return cityList != null ? cityList.size() + 1 : 0;
	}
	
	@Override
	public String getItem(int position) {
		return cityList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@SuppressWarnings ("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			//加载表头
			LayoutInflater infalter = LayoutInflater.from(parent.getContext());
			View header = infalter.inflate(R.layout.activity_listview_warning_adapter_header, null);
			TextView cityTv = (TextView) header.findViewById(R.id.city_tv);
			if (id > 0) {
				cityTv.setText(R.string.sea_name);
			}
			return header;
		}
		
		LayoutInflater infalter = LayoutInflater.from(parent.getContext());
		convertView = infalter.inflate(R.layout.activity_listview_warning_adapter, null);
		TextView titleTv = (TextView) convertView.findViewById(R.id.city_view);
		
		String city = getItem(position - 1);
		titleTv.setTextColor(titleColor);
		titleTv.setText(city);
//		if (id > 0) {
//			titleTv.setText(R.string.hn_sea);
//		}
		
		LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.warning_layout);
		List<JsonMap> tempList = data.getListMap(city);
		int imageSize = dip2px(parent.getContext(), 50);
		int marginV = dip2px(parent.getContext(), 8);
		int marginH = dip2px(parent.getContext(), 5);
		
		int size = tempList != null ? tempList.size() : 0;
		
		for (int i = 0; i < size; i++) {
			JsonMap warn = tempList.get(i);
			String plevel = warn.getString("w4");
			String code = warn.getString("w6");
			String iconCode = plevel + code;
			Drawable warnIcon = new AssetFile(parent.getContext()).getDrawable("warning/icon_warning_" + iconCode + ".png");
			if (warnIcon != null) {
				warnIcon.setBounds(0, 0, imageSize, imageSize);
			}
			
			ImageView iv = new ImageView(parent.getContext());
			iv.setBackgroundDrawable(warnIcon);
			iv.setOnClickListener(onClickListener);
			iv.setFocusable(true);
			iv.setClickable(true);
			iv.setTag(warn);
			
			LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
			if (i == 0) {
				params.setMargins(marginH, marginV, marginH, marginV);
			} else {
				params.setMargins(0, marginV, marginH, marginV);
			}
			
			layout.addView(iv, params);
		}
		
		return convertView;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 *
	 * @param dpValue
	 *            dp
	 * @return px
	 */
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
