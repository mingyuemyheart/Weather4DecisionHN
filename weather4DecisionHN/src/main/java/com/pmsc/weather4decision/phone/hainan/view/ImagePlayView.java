package com.pmsc.weather4decision.phone.hainan.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.lib.data.JsonMap;
import com.android.lib.util.LogUtil;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction: 多图界面底部悬浮的播放控制界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月28日 下午12:17:03
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ImagePlayView extends FrameLayout {
	private Button                  playBtn;
	private SeekBar                 seekBar;
	private ImageView               arrowIv;
	
	private OnSeekBarChangeListener onSeekBarChangeListener;
	private OnClickListener         onClickListener;
	
	private List<JsonMap>           imgsData;
	
	public ImagePlayView(Context context) {
		this(context, null);
	}
	
	public ImagePlayView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ImagePlayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(getContext(), R.layout.view_image_play, this);
		setClickable(false);
		setFocusable(false);
		playBtn = (Button) findViewById(R.id.play_btn);
		playBtn.setOnClickListener(onClickListener);
		
		seekBar = (SeekBar) findViewById(R.id.seek_bar);
//		seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		
		arrowIv = (ImageView) findViewById(R.id.arrow_iv);
	}
	
	public OnSeekBarChangeListener getOnSeekBarChangeListener() {
		return onSeekBarChangeListener;
	}
	
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
		this.onSeekBarChangeListener = onSeekBarChangeListener;
//		seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}
	
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
	
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
		playBtn.setOnClickListener(onClickListener);
	}
	
	public List<JsonMap> getImgsData() {
		return imgsData;
	}
	
	public void setImgsData(List<JsonMap> imgsData) {
		this.imgsData = imgsData;
		if (imgsData == null || imgsData.size() == 0) {
			//没有数据时，播放按钮和滑动条不可操作
			LogUtil.e(this, "no images, forbidden click play button");
			playBtn.setEnabled(false);
			seekBar.setEnabled(false);
			return;
		}
		
		playBtn.setEnabled(true);
		seekBar.setEnabled(false);
		int start = imgsData.get(0).getInt("duration");
		int end = imgsData.get(imgsData.size() - 1).getInt("duration");
		int max = Math.abs(end - start);
		seekBar.setMax(max);
	}
	
	public Button getPlayBtn() {
		return playBtn;
	}
	
	public void setPlayBtn(Button playBtn) {
		this.playBtn = playBtn;
	}
	
	public SeekBar getSeekBar() {
		return seekBar;
	}
	
	public void setSeekBar(SeekBar seekBar) {
		this.seekBar = seekBar;
	}
	
	public ImageView getArrowIv() {
		return arrowIv;
	}
	
	public void setArrowIv(ImageView arrowIv) {
		this.arrowIv = arrowIv;
	}
	
}
