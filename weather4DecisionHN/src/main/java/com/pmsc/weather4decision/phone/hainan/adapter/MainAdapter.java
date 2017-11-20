package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.google.bitmapcache.ImageFetcher;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction: 主页九宫格适配器
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
public class MainAdapter extends BaseAdapter {
	private List<JsonMap> datas;
	public int height = 0;
	
	public MainAdapter(List<JsonMap> datas, int height) {
		this.height = height;
		this.datas = datas;
	}
	
	@Override
	public int getCount() {
		return datas != null ? datas.size() - 2 : 0;
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
			convertView = infalter.inflate(R.layout.activity_main_gridview_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.icon_view);
			holder.titleTv = (TextView) convertView.findViewById(R.id.title_view);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		ImageFetcher fetcher = ImageFetcher.getImageFetcher(parent.getContext());
		fetcher.setImageSize(200);
		fetcher.setLoadingImage(R.drawable.loading_img);
		fetcher.loadImage(getItem(position + 2).getString("icon"), holder.iv);
		holder.titleTv.setText(getItem(position + 2).getString("title"));

		AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
		params.height = height/3;
		convertView.setLayoutParams(params);
		
		return convertView;
	}
	
	private static class Holder {
		ImageView iv;
		TextView  titleTv;
	}
}
