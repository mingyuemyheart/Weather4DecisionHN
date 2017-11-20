package com.pmsc.weather4decision.phone.hainan.fragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.app.BaseFragment;
import com.android.lib.data.CONST;
import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpAsyncTask;
import com.android.lib.util.LogUtil;
import com.android.lib.view.scaleview.SubsamplingScaleImageView;
import com.android.lib.view.scaleview.SubsamplingScaleImageView.OnImageEventListener;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.MultipleImgsJson;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.pmsc.weather4decision.phone.hainan.view.ImagePlayView;
import com.umeng.analytics.MobclickAgent;


/**
 * Depiction:多图信息界面，适用于雷达，卫星云图等界面
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
public class ImagesFragment extends BaseFragment implements Runnable, OnClickListener, OnImageEventListener, OnSeekBarChangeListener {
	private int                       id;
	private RelativeLayout            rootView;
	private SubsamplingScaleImageView imageView;
	private ProgressBar               progressBar;
	private ImagePlayView             playView;
	private TextView                  tipView;
	
	private String                    dataId;
	private String                    listUrl;
	private String                    dataUrl;
	
	private String                    resultData;
	
	private Handler                   handler  = new Handler();
	private List<JsonMap>             imageList;
	private int                       curIndex = 0;
	private final long                DURATION = 200;
	private String                    title;
	private int width, height;//手机屏幕宽高

	public static ImagesFragment newInstance(int id, JsonMap data) {
		ImagesFragment newFragment = new ImagesFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putString("listUrl", data.getString("listUrl"));
		bundle.putString("dataUrl", data.getString("dataUrl"));
		bundle.putString("title", data.getString("title"));
		newFragment.setArguments(bundle);
		return newFragment;
	}

	public ImagesFragment() {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.id = getArguments().getInt("id");
		this.listUrl = getArguments().getString("listUrl");
		this.dataUrl = getArguments().getString("dataUrl");
		this.title = getArguments().getString("title");

		View view = inflater.inflate(R.layout.fragment_images, container, false);
		rootView = (RelativeLayout) view.findViewById(R.id.root_view);
		imageView = (SubsamplingScaleImageView) view.findViewById(R.id.matrix_image_view);
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		playView = (ImagePlayView) view.findViewById(R.id.image_play_view);
		playView.setOnClickListener(this);
//		playView.setOnSeekBarChangeListener(this);
		
		tipView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.pop_tip, null);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dm);
		width = dm.widthPixels;
		height= dm.heightPixels;
		
		if (id == 0) {
			initData();
		}
		
		initBroadCast();
		
		return view;
	}
	
	private MyBroadCastReceiver mReceiver = null;
	private void initBroadCast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CONST.BROADCAST_STOPLOAD);
		getActivity().registerReceiver(mReceiver, intentFilter);
	}
	
	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(CONST.BROADCAST_STOPLOAD)) {
				curIndex = imageList.size();
			}
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//友盟统计栏目打开次数
		if(TextUtils.isEmpty(title)){
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", title);
		LogUtil.e(this, "title--->" + title);
		MobclickAgent.onEvent(getActivity(), "open_channel", map);
	}
	
	/**
	 * 加载数据
	 */
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
			loadImages(resultData);
		}
	}
	
	private void doRequest(String url) {
		final BaseActivity act = (BaseActivity) getActivity();
		HttpAsyncTask http = new HttpAsyncTask("images") {
			@Override
			public void onStart(String taskId) {
				act.showLoadingDialog(R.string.loading);
			}
			
			@Override
			public void onFinish(String taskId, String response) {
				act.cancelLoadingDialog();
				loadImages(new MultipleImgsJson().rebuildJson(response));
			}
		};
		http.setDebug(false);
		http.excute(url, "");
	}
	
	/**
	 * 加载多图片
	 * 
	 * @param json
	 */
	private void loadImages(String json) {
		JsonMap map = JsonMap.parseJson(json);
		if (map != null && map.getListMap("imgs") != null) {
			resultData = json;
			playView.setVisibility(View.VISIBLE);
			imageList = map.getListMap("imgs");
			playView.setImgsData(imageList);
			
			Collections.sort(imageList, new Comparator<JsonMap>() {
				@Override
				public int compare(JsonMap a, JsonMap b) {
					return a.getString("n").compareTo(b.getString("n"));
				}
			});
			
			//默认加载最后一张图片
			curIndex = imageList.size() - 1;
			loadImage(imageList.get(curIndex).getString("i"));
			onProgressChanged(playView.getSeekBar(), playView.getSeekBar().getMax(), true);
		} else {
			//请求错误
			BaseActivity act = (BaseActivity) getActivity();
			act.showToast(R.string.loading_fail);
			resultData = null;
		}
	}
	
	private void loadImage(String imageUrl) {
		if (!TextUtils.isEmpty(imageUrl)) {
//			progressBar.setVisibility(View.VISIBLE);
			Utils.loadScaleImage(progressBar, imageView, imageUrl, this);
		}
	}
	
	private float getScale() {
		float width = imageView.getWidth();
		float height = imageView.getHeight();
		return height / width;
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
		
		//区分雷达、云图
		if (TextUtils.equals(dataId, "609") || TextUtils.equals(dataId, "610") || TextUtils.equals(dataId, "611")) {
			//默认放大
			if (width >= 1080) {
				imageView.setScaleAndCenter(getScale() * 2.0f, imageView.getLeftLeft());
			}else if (width >= 720 || width < 1080) {
				imageView.setScaleAndCenter(getScale() * 1.2f, imageView.getLeftLeft());
			}
		}
		
		imageView.setMaxScale(getScale() * 10.0f);
	}
	
	@Override
	public void onImageLoadError(Exception e) {
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		ImageView arrowIv = playView.getArrowIv();
		
		float percent = (float) progress / (float) seekBar.getMax();
		float tipX = seekBar.getLeft() + (float) seekBar.getWidth() * percent;
		float tipY = seekBar.getTop() - seekBar.getHeight() - 5;
		if (percent <= 0.01f) {
			tipX = tipX - arrowIv.getWidth() / 2.0f;
		} else if (percent < 0.35f) {
			tipX = tipX - arrowIv.getWidth() + arrowIv.getWidth() / 3.0f;
		} else if (percent >= 0.35f && percent < 0.5f) {
			tipX = tipX - arrowIv.getWidth() + arrowIv.getWidth() / 6.0f;
		} else if (percent == 0.5f) {
			tipX = tipX - arrowIv.getWidth();
		} else if (percent > 0.5 && percent < 0.98f) {
			tipX = tipX - arrowIv.getWidth() * 12.5f / 10.f;
		} else if (percent >= 0.98f) {
			tipX = seekBar.getLeft() + seekBar.getWidth() - arrowIv.getWidth() * 3.0f / 2.0f;
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		params.topMargin = (int) tipY;
		params.leftMargin = (int) tipX;
		if (params.leftMargin == 0) {
			//朝下箭头位置初始化纠偏
			int offset = getScreenWidth() - getArrowWidth();
			params.leftMargin = offset;
		}
		arrowIv.setLayoutParams(params);
		arrowIv.setVisibility(View.VISIBLE);
		
		//刷新时间标签
		rootView.removeView(tipView);
		String time = imageList.get(curIndex).getString("n");
		tipView.setText(time);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(getTipWidth(), -2);
		p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		p.bottomMargin = dip2px(52);
		
		p.leftMargin = params.leftMargin;
		if (progress == seekBar.getMax()) {
			//在最右边
			p.leftMargin = getScreenWidth() - getTipWidth() - 10;
		} else {
			p.leftMargin -= seekBar.getLeft() * 2 / 3;
		}
		rootView.addView(tipView, p);
	}
	
	private int dip2px(float dpValue) {
		if (getActivity() == null) {
			return 0;
		}
		final float scale = getActivity().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	private int getScreenWidth() {
		if (getActivity() == null) {
			return 0;
		}
		return getActivity().getResources().getDisplayMetrics().widthPixels;
	}
	
	private int getArrowWidth() {
		return dip2px(45);
	}
	
	private int getTipWidth() {
		if (getActivity() == null) {
			return 0;
		}
		return getActivity().getResources().getDimensionPixelSize(R.dimen.image_play_pop_width);
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	
	@SuppressWarnings ("deprecation")
	@Override
	public void onClick(View v) {
		//play images
		LogUtil.e(this, "start play image");
		playView.getPlayBtn().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_pause));
		curIndex = 0;
		handler.post(this);
	}
	
	@SuppressWarnings ("deprecation")
	@Override
	public void run() {
		SeekBar seekBar = playView.getSeekBar();
		if (curIndex < imageList.size()) {
			int start = imageList.get(0).getInt("duration");
			int end = imageList.get(curIndex).getInt("duration");
			int progress = Math.abs(end - start);
			seekBar.setProgress(progress);
			onProgressChanged(seekBar, progress, false);
			//播放下一条
			String imageUrl = imageList.get(curIndex).getString("i");
			loadImage(imageUrl);
			
//			while (!imageView.isImageLoaded()) {
//				//图片加载完，2秒再加载下一张图片
//				LogUtil.e(this, "wait load");
//			}
			curIndex += 1;
			handler.postDelayed(this, DURATION);
			if (curIndex == imageList.size()) {
				if (getActivity() != null) {
					playView.getPlayBtn().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_play));
				}
			}
		}
	}//end run
}
