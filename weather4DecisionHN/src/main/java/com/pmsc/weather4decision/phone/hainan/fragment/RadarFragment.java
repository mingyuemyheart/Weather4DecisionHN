package com.pmsc.weather4decision.phone.hainan.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.lib.app.BaseFragment;
import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.fragment.RadarManager.RadarListener;
import com.pmsc.weather4decision.phone.hainan.util.CustomHttpClient;
import com.pmsc.weather4decision.phone.hainan.view.MyDialog;
import com.pmsc.weather4decision.phone.hainan.view.PhotoView;
import com.pmsc.weather4decision.phone.hainan.view.TouchImageView;

public class RadarFragment extends BaseFragment implements OnClickListener, RadarListener{
	
	private MyDialog mDialog = null;
	private List<RadarDto> radarList = new ArrayList<RadarDto>();
	private PhotoView imageView = null;
	private RadarManager mRadarManager = null;
	private RadarThread mRadarThread = null;
	private static final int HANDLER_SHOW_RADAR = 1;
	private static final int HANDLER_PROGRESS = 2;
	private static final int HANDLER_LOAD_FINISHED = 3;
	private static final int HANDLER_PAUSE = 4;
	private LinearLayout llSeekBar = null;
	private ImageView ivPlay = null;
	private SeekBar seekBar = null;
	private TextView tvTime = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日HH时mm分");
	private String id = null;
	private String baseUrl = null;
	private int width = 0, height = 0;
	
	public RadarFragment() {

	}

	public static RadarFragment newInstance(int id, JsonMap data) {
		RadarFragment newFragment = new RadarFragment();
		Bundle bundle = new Bundle();
		bundle.putString("id", id+"");
		bundle.putString("listUrl", data.getString("listUrl"));
		bundle.putString("dataUrl", data.getString("dataUrl"));
		bundle.putString("title", data.getString("title"));
		bundle.putString("color", data.getString("color"));
		newFragment.setArguments(bundle);
		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.id = getArguments().getString("id");
		this.baseUrl = getArguments().getString("dataUrl");

		View view = inflater.inflate(R.layout.radar, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	/**
	 * 初始化dialog
	 */
	private void initDialog() {
		if (mDialog == null) {
			mDialog = new MyDialog(getActivity());
		}
		mDialog.setPercent(0);
		mDialog.show();
	}
	private void cancelDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget(View view) {
		imageView = (PhotoView) view.findViewById(R.id.imageView);
		imageView.setMaxScale(8f);
//		imageView.id = id;
		ivPlay = (ImageView) view.findViewById(R.id.ivPlay);
		ivPlay.setOnClickListener(this);
		seekBar = (SeekBar) view.findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(seekbarListener);
		tvTime = (TextView) view.findViewById(R.id.tvTime);
		llSeekBar = (LinearLayout) view.findViewById(R.id.llSeekBar);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		
		mRadarManager = new RadarManager(getActivity());
		
		getRadarData(baseUrl);
	}
	
	private OnSeekBarChangeListener seekbarListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			if (mRadarThread != null) {
				mRadarThread.setCurrent(seekBar.getProgress());
				mRadarThread.stopTracking();
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			if (mRadarThread != null) {
				mRadarThread.startTracking();
			}
		}
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		}
	};
	
	/**
	 * 获取雷达图片集信息
	 */
	private void getRadarData(String url) {
		HttpAsyncTask task = new HttpAsyncTask();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTask() {
		}

		@Override
		protected String doInBackground(String... url) {
			String result = null;
			if (method.equalsIgnoreCase("POST")) {
				result = CustomHttpClient.post(url[0], nvpList);
			} else if (method.equalsIgnoreCase("GET")) {
				result = CustomHttpClient.get(url[0]);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			radarList.clear();
			if (result != null) {
				try {
					JSONObject obj = new JSONObject(result.toString());
					JSONArray array = new JSONArray(obj.getString("imgs"));
					for (int i = array.length()-1; i >= 0 ; i--) {
						JSONObject itemObj = array.getJSONObject(i);
						RadarDto dto = new RadarDto();
						dto.url = itemObj.getString("i");
						dto.time = itemObj.getString("n");
						dto.id = id;
						radarList.add(dto);
						
						if (i == 0) {
							FinalBitmap finalBitmap = FinalBitmap.create(getActivity());
							finalBitmap.display(imageView, dto.url, null, 0);
							changeProgress(dto.time, array.length(), array.length());
						}
					}
						
					if (radarList.size() <= 0) {
						imageView.setImageResource(R.drawable.iv_no_pic);
						llSeekBar.setVisibility(View.GONE);
					}else {
						startDownLoadImgs(radarList);//开始下载
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("unused")
		private void setParams(NameValuePair nvp) {
			nvpList.add(nvp);
		}

		private void setMethod(String method) {
			this.method = method;
		}

		private void setTimeOut(int timeOut) {
			CustomHttpClient.TIME_OUT = timeOut;
		}

		/**
		 * 取消当前task
		 */
		@SuppressWarnings("unused")
		private void cancelTask() {
			CustomHttpClient.shuttdownRequest();
			this.cancel(true);
		}
	}
	
	private void startDownLoadImgs(List<RadarDto> list) {
		if (mRadarThread != null) {
			mRadarThread.cancel();
			mRadarThread = null;
		}
		if (list.size() > 0) {
			mRadarManager.loadImagesAsyn(list, this, id);
		}
	}
	
	@Override
	public void onResult(int result, List<RadarDto> images) {
		mHandler.sendEmptyMessage(HANDLER_LOAD_FINISHED);
		if (result == RadarListener.RESULT_SUCCESSED) {
			if (mRadarThread != null) {
				mRadarThread.cancel();
				mRadarThread = null;
			}
			if (images.size() > 0) {
				mRadarThread = new RadarThread(images);
				mRadarThread.start();
			}
		}
	}
	
	private class RadarThread extends Thread {
		static final int STATE_NONE = 0;
		static final int STATE_PLAYING = 1;
		static final int STATE_PAUSE = 2;
		static final int STATE_CANCEL = 3;
		private List<RadarDto> images;
		private int state;
		private int index;
		private int count;
		private boolean isTracking = false;
		
		public RadarThread(List<RadarDto> images) {
			this.images = images;
			this.count = images.size();
			this.index = 0;
			this.state = STATE_NONE;
			this.isTracking = false;
		}
		
		public int getCurrentState() {
			return state;
		}
		
		@Override
		public void run() {
			super.run();
			this.state = STATE_PLAYING;
			while (true) {
				if (state == STATE_CANCEL) {
					break;
				}
				if (state == STATE_PAUSE) {
					continue;
				}
				if (isTracking) {
					continue;
				}
				sendRadar();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void sendRadar() {
			if (index >= count || index < 0) {
				index = 0;
				
				if (mRadarThread != null) {
					mRadarThread.pause();
					
					Message message = mHandler.obtainMessage();
					message.what = HANDLER_PAUSE;
					mHandler.sendMessage(message);
				}
			}else {
				RadarDto radar = images.get(index);
				Message message = mHandler.obtainMessage();
				message.what = HANDLER_SHOW_RADAR;
				message.obj = radar;
				message.arg1 = count - 1;
				message.arg2 = index ++;
				mHandler.sendMessage(message);
			}
		}
		
		public void cancel() {
			this.state = STATE_CANCEL;
		}
		public void pause() {
			this.state = STATE_PAUSE;
		}
		public void play() {
			this.state = STATE_PLAYING;
		}
		
		public void setCurrent(int index) {
			this.index = index;
		}
		
		public void startTracking() {
			isTracking = true;
		}
		
		public void stopTracking() {
			isTracking = false;
			if (this.state == STATE_PAUSE) {
				sendRadar();
			}
		}
	}

	@Override
	public void onProgress(String url, int progress) {
		Message msg = new Message();
		msg.obj = progress;
		msg.what = HANDLER_PROGRESS;
		mHandler.sendMessage(msg);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HANDLER_SHOW_RADAR: 
				if (msg.obj != null) {
					RadarDto radar = (RadarDto) msg.obj;
					if (radar != null) {
						Bitmap bitmap = BitmapFactory.decodeFile(radar.url);
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
						}
					}
					changeProgress(radar.time, msg.arg2, msg.arg1);
				}
				break;
			case HANDLER_PROGRESS: 
				if (mDialog != null) {
					if (msg.obj != null) {
						int progress = (Integer) msg.obj;
						mDialog.setPercent(progress);
					}
				}
				break;
			case HANDLER_LOAD_FINISHED: 
				cancelDialog();
				llSeekBar.setVisibility(View.VISIBLE);
				if (ivPlay != null) {
					ivPlay.setImageResource(R.drawable.iv_pause);
				}
				break;
			case HANDLER_PAUSE:
				if (ivPlay != null) {
					ivPlay.setImageResource(R.drawable.iv_play);
				}
				break;
			default:
				break;
			}
			
		};
	};
	
	private void changeProgress(String time, int progress, int max) {
		if (seekBar != null) {
			seekBar.setMax(max);
			seekBar.setProgress(progress);
		}
		try {
			tvTime.setText(sdf1.format(sdf2.parse(time)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ivPlay) {
			if (mRadarThread != null && mRadarThread.getCurrentState() == RadarThread.STATE_PLAYING) {
				mRadarThread.pause();
				ivPlay.setImageResource(R.drawable.iv_play);
			} else if (mRadarThread != null && mRadarThread.getCurrentState() == RadarThread.STATE_PAUSE) {
				mRadarThread.play();
				ivPlay.setImageResource(R.drawable.iv_pause);
			} else if (mRadarThread == null) {
				initDialog();
				startDownLoadImgs(radarList);//开始下载
			}
		}
	}

	@Override
	public void loadData(Object obj) {
		// TODO Auto-generated method stub
		
	}

}
