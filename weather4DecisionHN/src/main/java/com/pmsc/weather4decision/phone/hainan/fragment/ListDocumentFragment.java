package com.pmsc.weather4decision.phone.hainan.fragment;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.lib.app.BaseActivity;
import com.android.lib.app.BaseFragment;
import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.android.lib.util.LogUtil;
import com.android.lib.view.PullListView;
import com.android.lib.view.PullListView.OnLoadMoreListener;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.act.AbsDrawerActivity;
import com.pmsc.weather4decision.phone.hainan.adapter.ListNormalAdapter;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.umeng.analytics.MobclickAgent;


/**
 * Depiction:普通样式列表视图
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
public class ListDocumentFragment extends BaseFragment implements OnItemClickListener, OnLoadMoreListener {
	private int				  id;
	private TextView		  emptyView;
	private PullListView	  listview;
	private ListNormalAdapter adapter;
							  
	private int				  titleColor = Color.BLACK;
	private String			  listUrl;
	private String			  dataUrl;
							  
	private boolean			  isOldDoc;
							  
	private int				  pageNum	 = 1;
	private int				  totalPage	 = 1;
										 
	private String			  title;

	public ListDocumentFragment() {

	}

	public static ListDocumentFragment newInstance(int id, JsonMap data) {
		ListDocumentFragment newFragment = new ListDocumentFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putString("listUrl", data.getString("listUrl"));
		bundle.putString("dataUrl", data.getString("dataUrl"));
		bundle.putString("title", data.getString("title"));
		bundle.putString("color", data.getString("color"));
		bundle.putString("columnType", data.getString("columnType"));
		newFragment.setArguments(bundle);
		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.id = getArguments().getInt("id");
		this.listUrl = getArguments().getString("listUrl");
		this.dataUrl = getArguments().getString("dataUrl");
		this.title = getArguments().getString("title");
		this.isOldDoc = getArguments().getString("columnType").equalsIgnoreCase(Utils.OLD_DOC);
		try {
			String c = getArguments().getString("color");
			if (c != null) {
				c = c.startsWith("#") ? c : "#" + c;
				this.titleColor = Color.parseColor(c);
			}
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "parse color error");
		}

		View view = inflater.inflate(R.layout.fragment_document, container, false);
		emptyView = (TextView) view.findViewById(R.id.empty_view);
		listview = (PullListView) view.findViewById(R.id.pull_list_view);
		listview.setOnItemClickListener(this);
		
		if (id == 0) {
			initData();
		}
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtil.d(this, "onActivityCreated");
		if (listview != null && adapter != null) {
			listview.setAdapter(adapter);
		}
		
		//友盟统计栏目打开次数
		if (TextUtils.isEmpty(title)) {
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//点击打开某一条
		String key = isOldDoc ? "l2" : "filePath";
		String pdf = adapter.getItem(position - 1).getString(key);
		if (!TextUtils.isEmpty(pdf)) {
			AbsDrawerActivity act = (AbsDrawerActivity) getActivity();
			act.enterPdfActivity(pdf);
		}
	}
	
	@Override
	public void loadData(Object obj) {
		if (isFirst || getCount() == 0) {
			isFirst = false;
			String url = Utils.checkUrl(listUrl, dataUrl);
			if (url != null) {
				doRequest(url, 1);
			} else {
				getBaseActivity().showToast(R.string.no_data);
			}
		}
	}
	
	private void doRequest(String url, int page) {
		final BaseActivity act = (BaseActivity) getActivity();
		HttpAsyncTask http = new HttpAsyncTask("docment") {
			@Override
			public void onStart(String taskId) {
				act.showLoadingDialog(R.string.loading);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				act.cancelLoadingDialog();
				listview.onRefreshComplete();
				JsonMap data = JsonMap.parseJson(response);
				if (data != null) {
					List<JsonMap> datas = isOldDoc ? data.getListMap("l") : data.getListMap("products");
					if (datas != null && datas.size() > 0) {
						emptyView.setVisibility(View.GONE);
						if (adapter == null) {
							adapter = new ListNormalAdapter(titleColor, isOldDoc);
							listview.setAdapter(adapter);
						}
						adapter.addDatas(datas);
						
						if (!isOldDoc) {
							totalPage = data.getInt("totalPage");
							pageNum = data.getInt("pageNum");
							if (pageNum < totalPage) {
								listview.setOnLoadListener(ListDocumentFragment.this);
								listview.setCanLoadMore(true);
								listview.setAutoLoadMore(true);
							} else {
								listview.setCanLoadMore(false);
							}
							listview.onRefreshComplete();
						}
					} else {
						listview.setCanLoadMore(false);
						listview.setAutoLoadMore(false);
						emptyView.setVisibility(View.VISIBLE);
						listview.onRefreshComplete();
					}
				} else {
					act.showToast(R.string.loading_fail);
				}
			}
		};
		http.setDebug(false);
		
		if (!isOldDoc) {
			//新接口，支持分页
			String tempUrl = url.substring(0, url.lastIndexOf("/") + 1);
			url = tempUrl + page;
		}
		http.excute(url, "");
	}
	
	@Override
	public void onLoadMore() {
		String url = Utils.checkUrl(listUrl, dataUrl);
		if (url != null) {
			if (pageNum < totalPage) {
				doRequest(url, pageNum + 1);
			}
		}
	}
	
}
