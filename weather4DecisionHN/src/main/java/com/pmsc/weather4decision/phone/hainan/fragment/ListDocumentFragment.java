package com.pmsc.weather4decision.phone.hainan.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.app.BaseFragment;
import com.android.lib.data.JsonMap;
import com.android.lib.util.LogUtil;
import com.android.lib.view.PullListView;
import com.android.lib.view.PullListView.OnLoadMoreListener;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.act.AbsDrawerActivity;
import com.pmsc.weather4decision.phone.hainan.adapter.ListNormalAdapter;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Depiction:普通样式列表视图
 */
public class ListDocumentFragment extends BaseFragment implements OnItemClickListener, OnLoadMoreListener {
	private int id;
	private TextView emptyView;
	private PullListView listview;
	private ListNormalAdapter adapter;

	private int titleColor = Color.BLACK;
	private String listUrl;
	private String dataUrl;

	private boolean isOldDoc;

	private int pageNum = 1;
	private int totalPage = 1;

	private String title;

	public ListDocumentFragment() {

	}

	public static ListDocumentFragment newInstance(int id, JsonMap data) {
		ListDocumentFragment newFragment = new ListDocumentFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("id", data.getInt("id"));
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

		initData();
		if (id == 627) {//省级预警
			emptyView.setText(getString(R.string.no_warning));
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
		HashMap<String, String> map = new HashMap<>();
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
	
	private void doRequest(String url, final int page) {
		if (!isOldDoc) {
			//新接口，支持分页
			String tempUrl = url.substring(0, url.lastIndexOf("/") + 1);
			url = tempUrl + page;
		}
		final String newUrl = url;
		final BaseActivity act = (BaseActivity) getActivity();
		act.showLoadingDialog(R.string.loading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(newUrl).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								act.cancelLoadingDialog();
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									act.cancelLoadingDialog();
									listview.onRefreshComplete();
									JsonMap data = JsonMap.parseJson(result);
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
							}
						});
					}
				});
			}
		}).start();
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
