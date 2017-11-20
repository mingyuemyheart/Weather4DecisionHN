package com.pmsc.weather4decision.phone.hainan.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.app.BaseFragment;
import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.android.lib.util.LogUtil;
import com.google.gson.Gson;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.act.WarningDetailsActivity;
import com.pmsc.weather4decision.phone.hainan.adapter.ListWarningAdapter;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.umeng.analytics.MobclickAgent;


/**
 * Depiction:灾害预警的列表视图
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月14日 下午4:14:17
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ListWarningFragment extends BaseFragment implements OnClickListener {
	private int                id;
	private TextView           emptyView;
	private ListView           listview;
	private ListWarningAdapter adapter;
	
	private int                titleColor = Color.BLACK;
	private String             listUrl;
	private String             dataUrl;
	private String             title;
	
	public ListWarningFragment() {

	}

	public static ListWarningFragment newInstance(int id, JsonMap data) {
		ListWarningFragment newFragment = new ListWarningFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putString("listUrl", data.getString("listUrl"));
		bundle.putString("dataUrl", data.getString("dataUrl"));
		bundle.putString("title", data.getString("title"));
		bundle.putString("color", data.getString("color"));
		newFragment.setArguments(bundle);
		return newFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.id = getArguments().getInt("id");
		this.listUrl = getArguments().getString("listUrl");
		this.dataUrl = getArguments().getString("dataUrl");
		this.title = getArguments().getString("title");
		try {
			String c = getArguments().getString("color");
			if (c != null) {
				c = c.startsWith("#") ? c : "#" + c;
				this.titleColor = Color.parseColor(c);
			}
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "parse color error");
		}

		View view = inflater.inflate(R.layout.fragment_warning, container, false);
		emptyView = (TextView) view.findViewById(R.id.empty_view);
		listview = (ListView) view.findViewById(R.id.listview);
		
		if (id == 0) {
			initData();
		}
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (listview != null && adapter != null) {
			listview.setAdapter(adapter);
		}
		
		//友盟统计栏目打开次数
		if(TextUtils.isEmpty(title)){
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", title);
		LogUtil.e(this, "title--->" + title);
		MobclickAgent.onEvent(getActivity(), "open_channel", map);
	}
	
	public int getCount() {
		return adapter != null ? adapter.getCount() : 0;
	}
	
	@Override
	public void loadData(Object obj) {
		if (isFirst || getCount() == 0) {
			String url = Utils.checkUrl(listUrl, dataUrl);
			if (url != null) {
				doRequest(url);
			} else {
				getBaseActivity().showToast(R.string.no_data);
			}
		}
	}
	
	private void doRequest(String url) {
		final BaseActivity act = (BaseActivity) getActivity();
		HttpAsyncTask http = new HttpAsyncTask("login") {
			@Override
			public void onStart(String taskId) {
				act.showLoadingDialog(R.string.loading);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				act.cancelLoadingDialog();
				JsonMap data = JsonMap.parseJson(response);
				if (data != null) {
					if (data.getListMap("w") != null && data.getListMap("w").size() > 0) {
						emptyView.setVisibility(View.GONE);
						List<JsonMap> tempList = data.getListMap("w");
						JsonMap newData = rebuildData(tempList);
						List<String> allCity = getAllCitys(tempList);
						adapter = new ListWarningAdapter(id, allCity, newData, titleColor);
						adapter.setOnClickListener(ListWarningFragment.this);
						listview.setAdapter(adapter);
					} else {
						emptyView.setVisibility(View.VISIBLE);
					}
				} else {
					act.showToast(R.string.loading_fail);
				}
			}
		};
		http.setDebug(false);
		http.excute(url, "");
	}
	
	//按照城市重新组装预警信息
	private JsonMap rebuildData(List<JsonMap> oldDatas) {
		LinkedHashMap<String, List<JsonMap>> datas = new LinkedHashMap<String, List<JsonMap>>();
		int size = oldDatas != null ? oldDatas.size() : 0;
		for (int i = 0; i < size; i++) {
			JsonMap temp = oldDatas.get(i);
			String city = temp.getString("w2")+temp.getString("w11");
			
			List<JsonMap> cityList = datas.containsKey(city) ? datas.get(city) : new ArrayList<JsonMap>();
			cityList.add(temp);
			datas.put(city, cityList);
		}
		Gson gson = new Gson();
		
		return JsonMap.parseJson(gson.toJson(datas));
	}
	
	//获取所有城市列表
	private List<String> getAllCitys(List<JsonMap> oldDatas) {
		List<String> cityList = new ArrayList<String>();
		int size = oldDatas != null ? oldDatas.size() : 0;
		for (int i = 0; i < size; i++) {
			JsonMap temp = oldDatas.get(i);
			String city = temp.getString("w2")+temp.getString("w11");
			if (!cityList.contains(city)) {
				cityList.add(city);
			}
		}
		
		return cityList;
	}
	
	@Override
	public void onClick(View v) {
		//点击打开某一条预警详情
		JsonMap data = (JsonMap) v.getTag();
		Bundle bundle = new Bundle();
		bundle.putString("warning_data", data.toString());
		getBaseActivity().openActivity(WarningDetailsActivity.class, bundle);
	}
}
