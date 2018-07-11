package com.pmsc.weather4decision.phone.hainan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;

import net.tsz.afinal.FinalBitmap;

import java.util.List;


/**
 * 主页九宫格适配器
 */
public class MainAdapter extends BaseAdapter {

	private Context mContext;
	private List<JsonMap> datas;
	public int height;
	private Bitmap bitmap;
	
	public MainAdapter(Context context, List<JsonMap> datas) {
		mContext = context;
		this.datas = datas;
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_img);
	}

	private static class Holder {
		ImageView iv;
		TextView  titleTv;
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
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			LayoutInflater infalter = LayoutInflater.from(parent.getContext());
			convertView = infalter.inflate(R.layout.adapter_main, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.icon_view);
			holder.titleTv = (TextView) convertView.findViewById(R.id.title_view);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		String imgUrl = getItem(position+2).getString("icon");
		if (!TextUtils.isEmpty(imgUrl)) {
			FinalBitmap finalBitmap = FinalBitmap.create(mContext);
			finalBitmap.display(holder.iv, imgUrl, bitmap, bitmap, null, 0);
		}

		String title = getItem(position + 2).getString("title");
		if (!TextUtils.isEmpty(title)) {
			holder.titleTv.setText(title);
		}

		AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
		params.height = height/3;
		convertView.setLayoutParams(params);
		
		return convertView;
	}

}
