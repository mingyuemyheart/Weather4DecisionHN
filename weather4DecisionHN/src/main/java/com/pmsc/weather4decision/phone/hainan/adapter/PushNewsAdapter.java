package com.pmsc.weather4decision.phone.hainan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.dto.NewsDto;
import com.pmsc.weather4decision.phone.hainan.dto.ShawnRainDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息推送
 */

public class PushNewsAdapter extends BaseAdapter{

	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<NewsDto> mArrayList = new ArrayList<>();
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

	private final class ViewHolder{
		TextView tvTitle;
		TextView tvContent;
		TextView tvTime;
	}

	private ViewHolder mHolder = null;

	public PushNewsAdapter(Context context, List<NewsDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.adapter_push_news, null);
			mHolder = new ViewHolder();
			mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			mHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			mHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		NewsDto dto = mArrayList.get(position);
		
		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvTitle.setText(dto.title);
		}
		
		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvContent.setText(dto.content);
		}

		if (!TextUtils.isEmpty(dto.time)) {
			try {
				mHolder.tvTime.setText(sdf2.format(sdf1.parse(dto.time)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

}
