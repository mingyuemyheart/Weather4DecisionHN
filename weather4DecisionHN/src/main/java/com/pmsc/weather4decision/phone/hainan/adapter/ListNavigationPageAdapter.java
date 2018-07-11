package com.pmsc.weather4decision.phone.hainan.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.lib.app.BaseFragment;
import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.fragment.ListDocumentFragment;
import com.pmsc.weather4decision.phone.hainan.fragment.ListWarningFragment;
import com.pmsc.weather4decision.phone.hainan.fragment.RadarFragment;
import com.pmsc.weather4decision.phone.hainan.util.Utils;


/**
 * Depiction:水平导航栏适配器
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月24日 下午9:03:03
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ListNavigationPageAdapter extends FragmentPagerAdapter {
	private List<JsonMap>      channels;
	private List<BaseFragment> fragments;
	
	public ListNavigationPageAdapter(FragmentManager fm, List<JsonMap> channels) {
		super(fm);
		this.channels = channels;
		fragments = new ArrayList<>();
		for (int i = 0, size = channels != null ? channels.size() : 0; i < size; i++) {
			JsonMap map = channels.get(i);
			BaseFragment frag = null;
			String columnType = map.getString("columnType");
			if (columnType.equalsIgnoreCase(Utils.WARNING)) {
				frag = ListWarningFragment.newInstance(i, channels.get(i));
			} else if (columnType.equalsIgnoreCase(Utils.IMAGES)) {
//				frag = ImagesFragment.newInstance(i, channels.get(i));
				frag = RadarFragment.newInstance(i, channels.get(i));
			} else if (columnType.equalsIgnoreCase(Utils.DOCUMENT) || columnType.equalsIgnoreCase(Utils.OLD_DOC)) {
				frag = ListDocumentFragment.newInstance(i, channels.get(i));
			}
			
			if (frag != null) {
				fragments.add(frag);
			}
		}
	}
	
	@Override
	public BaseFragment getItem(int position) {
		return fragments.get(position);
	}
	
	@Override
	public int getCount() {
		return channels != null ? channels.size() : 0;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return channels.get(position).getString("title");
	}
}
