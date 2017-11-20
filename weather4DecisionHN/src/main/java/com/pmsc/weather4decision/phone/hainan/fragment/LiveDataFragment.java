package com.pmsc.weather4decision.phone.hainan.fragment;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.app.BaseFragment;
import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.android.lib.util.LogUtil;
import com.android.lib.view.scaleview.SubsamplingScaleImageView;
import com.android.lib.view.scaleview.SubsamplingScaleImageView.OnImageEventListener;
import com.google.bitmapcache.ImageFetcher;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.act.AbsDrawerActivity;
import com.pmsc.weather4decision.phone.hainan.act.LiveDataDetailsActivity;
import com.pmsc.weather4decision.phone.hainan.util.CacheData;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.umeng.analytics.MobclickAgent;


/**
 * Depiction:实况信息界面
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
public class LiveDataFragment extends BaseFragment implements OnClickListener, OnImageEventListener {
	private int                       id;
//	private JsonMap                   data;
	private ImageView                 imageView;
	private TextView                  textView;
	private TextView                  timeView;
	
	private RelativeLayout            imageLayout;
	private ProgressBar               progressBar;
	private SubsamplingScaleImageView matrixImageView;
	
	private boolean                   isBigImage = false;
	
	private String                    listUrl;
	private String                    dataUrl;
	private String                    imgUrl;
	private String                    resultData;
	private List<JsonMap>             times;
	private String             title;
	
	public LiveDataFragment() {

	}

	public static LiveDataFragment newInstance(int id, JsonMap data) {
		LiveDataFragment newFragment = new LiveDataFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putString("listUrl", data.getString("listUrl"));
		bundle.putString("dataUrl", data.getString("dataUrl"));
		bundle.putString("title", data.getString("title"));
		newFragment.setArguments(bundle);
		return newFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.id = getArguments().getInt("id");
		this.listUrl = getArguments().getString("listUrl");
		this.dataUrl = getArguments().getString("dataUrl");
		this.title = getArguments().getString("title");


		View view = inflater.inflate(R.layout.fragment_live_data, container, false);
		imageView = (ImageView) view.findViewById(R.id.image_view);
		textView = (TextView) view.findViewById(R.id.text_view);
		textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		textView.getPaint().setAntiAlias(true);//抗锯齿
		timeView = (TextView) view.findViewById(R.id.time_view);
		timeView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		timeView.getPaint().setAntiAlias(true);//抗锯齿
		imageView.setOnClickListener(this);
		textView.setOnClickListener(this);
		timeView.setOnClickListener(this);
		
		if (id == 0) {
			initData();
		}
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (imageView == null) {
			imageView = (ImageView) getView().findViewById(R.id.image_view);
			imageView.setOnClickListener(this);
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
	
	@Override
	public void onClick(View v) {
		AbsDrawerActivity act = (AbsDrawerActivity) getActivity();
		if (v.getId() == R.id.image_view) {
			//放大图片
			isBigImage = true;
			imageLayout = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.activity_live_data_image, null);
			imageLayout.setOnClickListener(this);
			
			matrixImageView = (SubsamplingScaleImageView) imageLayout.findViewById(R.id.matrix_image_view);
			matrixImageView.setOnClickListener(this);
			
			if (!TextUtils.isEmpty(imgUrl)) {
				progressBar = (ProgressBar) imageLayout.findViewById(R.id.progress_bar);
				Utils.loadScaleImage(progressBar, matrixImageView, imgUrl, this);
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
				params.gravity = Gravity.CENTER;
				act.addContentView(imageLayout, params);
			}
			
		} else if (v.getId() == R.id.text_view) {
			//打开详情
			if (!TextUtils.isEmpty(resultData)) {
				Bundle bundle = new Bundle();
				bundle.putString(AbsDrawerActivity.TITLE_KEY, this.title);
				CacheData.cacheLiveData(resultData);
				act.openActivity(LiveDataDetailsActivity.class, bundle);
			}
		} else if (v.getId() == R.id.time_view) {
			//打开时间选择菜单
			initMenus();
		} else {
			closePop();
		}
	}
	
	@Override
	public void loadData(Object obj) {
		if (resultData == null) {
			String url = Utils.checkUrl(listUrl, dataUrl);
			if (url != null) {
				doRequest(url);
			} else {
				getBaseActivity().showToast(R.string.no_data);
			}
		} else {
			loadData(resultData);
		}
	}
	
	private void doRequest(String url) {
		final BaseActivity act = (BaseActivity) getActivity();
		HttpAsyncTask http = new HttpAsyncTask("image_text") {
			@Override
			public void onStart(String taskId) {
				act.showLoadingDialog(R.string.loading);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				act.cancelLoadingDialog();
				loadData(response);
			}
		};
		http.setDebug(false);
		http.excute(url, "");
	}
	
	private void loadData(String response) {
		JsonMap data = JsonMap.parseJson(response);
		if (data != null) {
			resultData = response;
			times = data.getListMap("times");
			
			imgUrl = data.getString("imgUrl");
			if (TextUtils.isEmpty(imgUrl)) {
				imageView.setClickable(false);
				getBaseActivity().showToast(R.string.no_img);
				return;
			}
			imageView.setClickable(true);
			ImageFetcher fetcher = ImageFetcher.getImageFetcher(getActivity());
			fetcher.setLoadingImage(R.drawable.loading_img);
			fetcher.setImageSize(1000);
			if (imageView != null) {
				fetcher.loadImage(imgUrl, imageView);
			}
		} else {
			resultData = null;
			getBaseActivity().showToast(R.string.loading_fail);
		}
	}
	
	@Override
	public void onTileLoadError(Exception e) {
	}
	
	@Override
	public void onReady() {
	}
	
	@Override
	public void onPreviewLoadError(Exception e) {
	}
	
	@Override
	public void onImageLoaded() {
		progressBar.setVisibility(View.GONE);
	}
	
	@Override
	public void onImageLoadError(Exception e) {
	}
	
	public void closePop() {
		//关闭大图
		isBigImage = false;
		if (imageLayout != null) {
			((ViewGroup) imageLayout.getParent()).removeView(imageLayout);
		}
	}
	
	public boolean isOpenedPop() {
		return isBigImage;
	}
	
	private void initMenus() {
		if (times == null || times.size() == 0) {
			return;
		}
		Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.choose_time);
		alertDialog.setIcon(R.drawable.icon);
		String[] dates = new String[times.size()];
		for (int i = 0; i < dates.length; i++) {
			JsonMap map = times.get(i);
			dates[i] = map.getString("timeString");
		}
		alertDialog.setItems(dates, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = Utils.checkUrl(listUrl, dataUrl);
				if (url != null) {
					JsonMap map = times.get(which);
					doRequest(url + "/" + map.getString("timeParams"));
				}
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		alertDialog.show();
	}
}
