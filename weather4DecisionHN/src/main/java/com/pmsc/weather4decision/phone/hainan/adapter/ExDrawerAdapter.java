package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.List;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction: 左侧导航栏
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月11日 上午5:57:21
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ExDrawerAdapter extends BaseExpandableListAdapter {
	private List<JsonMap> datas;
	private int           parentIndex;
	private int           childIndex;
	
	public ExDrawerAdapter(List<JsonMap> datas) {
		this.datas = datas;
	}
	
	public void setSelected(int parentIndex, int childIndex) {
		this.parentIndex = parentIndex;
		this.childIndex = childIndex;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getGroupCount() {
		return datas != null ? datas.size() : 0;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		List<JsonMap> childList = datas.get(groupPosition).getListMap("child");
		return childList != null ? childList.size() : 0;
	}
	
	@Override
	public JsonMap getGroup(int groupPosition) {
		try {
			return datas.get(groupPosition);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "getGroup()-->"+e.toString());
		}
		return null;
	}
	
	public List<JsonMap> getChilds(int groupPosition) {
		JsonMap group = getGroup(groupPosition);
		if (group != null) {
			List<JsonMap> childList = group.getListMap("child");
			return childList;
		}
		return null;
	}
	
	@Override
	public JsonMap getChild(int groupPosition, int childPosition) {
		JsonMap group = getGroup(groupPosition);
		if (group != null) {
			List<JsonMap> childList = group.getListMap("child");
			if (childList != null && childList.size() > 0) {
				return childList.get(childPosition);
			}
		}
		return null;
	}
	
	@Override
	public long getGroupId(int groupPosition) {
		try {
			return getGroup(groupPosition).getLong("parent_id");
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "getGroupId()-->"+e.toString());
		}
		return 0l;
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		try {
			return getChild(groupPosition, childPosition).getLong("parent_id");
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "getChildId()-->"+e.toString());
		}
		return 0l;
	}
	
	@Override
	public boolean hasStableIds() {
		return false;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return !(parentIndex == groupPosition && childIndex == childPosition);
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LayoutInflater infalter = LayoutInflater.from(parent.getContext());
		TextView tv = (TextView) infalter.inflate(R.layout.drawer_item, null);
		tv.setText(getGroup(groupPosition).getString("title"));
		if (parentIndex == groupPosition && childIndex == -1) {
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundResource(R.drawable.bg_nav);
		} else {
			tv.setTextColor(tv.getContext().getResources().getColor(R.color.drawer_text_color));
			tv.setBackgroundResource(R.drawable.transparent);
		}
		return tv;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater infalter = LayoutInflater.from(parent.getContext());
		TextView tv = (TextView) infalter.inflate(R.layout.drawer_item_child, null);
		tv.setText("○ " + getChild(groupPosition, childPosition).getString("title"));
		if (parentIndex == groupPosition && childIndex == childPosition) {
			tv.setBackgroundResource(R.drawable.bg_nav);
		} else {
			tv.setBackgroundResource(R.drawable.transparent);
		}
		return tv;
	}
	
}
