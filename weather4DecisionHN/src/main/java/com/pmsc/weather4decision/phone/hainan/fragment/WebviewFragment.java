package com.pmsc.weather4decision.phone.hainan.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 普通网页
 */
public class WebviewFragment extends BaseFragment implements OnItemClickListener {
	private int id;
	private String listUrl;
	private String dataUrl;
	private String title;
	private WebView webView;

	public WebviewFragment() {

	}

	public static WebviewFragment newInstance(int id, JsonMap data) {
		WebviewFragment newFragment = new WebviewFragment();
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

		View view = inflater.inflate(R.layout.fragment_webview, container, false);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWebView(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//友盟统计栏目打开次数
		if (TextUtils.isEmpty(title)) {
			return;
		}
		HashMap<String, String> map = new HashMap<>();
		map.put("title", title);
		LogUtil.e(this, "title--->" + title);
		MobclickAgent.onEvent(getActivity(), "open_channel", map);
	}

	@Override
	public void loadData(Object obj) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	/**
	 * 初始化webview
	 */
	private void initWebView(View view) {
		final BaseActivity act = (BaseActivity) getActivity();
		act.showLoadingDialog(R.string.loading);
		if (TextUtils.isEmpty(dataUrl)) {
			return;
		}

		webView = view.findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();

		//支持javascript
		webSettings.setJavaScriptEnabled(true);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);
		//扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		//自适应屏幕
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);
		webView.loadUrl(dataUrl);

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
//				if (title != null) {
//					tvTitle.setText(title);
//				}
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String itemUrl) {
				dataUrl = itemUrl;
				webView.loadUrl(dataUrl);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				act.cancelLoadingDialog();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (webView != null) {
			webView.reload();
		}
	}

}
