package com.pmsc.weather4decision.phone.hainan.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.android.lib.view.scaleview.SubsamplingScaleImageView;


/**
 * Depiction: 画全省预报界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月6日 下午11:45:03
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PinView extends SubsamplingScaleImageView {
	private OnCityClicklistener onCityClicklistener;
	private List<Pin>           pinList;
	private int                 radius = dip2px(22);
	private int                 blue   = Color.parseColor("#1d96f4");
	
	public PinView(Context context) {
		this(context, null);
	}
	
	public PinView(Context context, AttributeSet attr) {
		super(context, attr);
		initListener();
	}
	
	public void setPin(List<Pin> pinList) {
		this.pinList = pinList;
		invalidate();
	}
	
	public List<Pin> getPin() {
		return pinList;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Don't draw pin before image is ready so it doesn't move around during setup.
		if (!isReady()) {
			return;
		}
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		for (int i = 0, size = pinList != null ? pinList.size() : 0; i < size; i++) {
			Pin pin = pinList.get(i);
			PointF vPoint = sourceToViewCoord(pin.getLocation());//在图片上的实际坐标
			
			drawCycle(canvas, vPoint, Color.LTGRAY, radius);
			drawCycle(canvas, vPoint, Color.WHITE, radius - 5);
			drawCycle(canvas, vPoint, blue, radius - 10);
			
			PointF fontP = new PointF(vPoint.x, vPoint.y - radius / 2 - dip2px(2));
			drawText(canvas, Color.BLACK, pin.text, fontP);
			
			float bmSize = radius * 1.65f;
			float vX = vPoint.x - bmSize / 2;
			float vY = vPoint.y - bmSize / 2 - dip2px(2);
			Bitmap bm = Bitmap.createScaledBitmap(pin.getBitmap(), (int) bmSize, (int) bmSize, true);
			canvas.drawBitmap(bm, vX, vY, paint);
		}
	}
	
	/**
	 * 画文字
	 * 
	 * @param canvas
	 *            画笔
	 * @param color
	 *            文字颜色
	 * @param text
	 *            文本
	 * @param p
	 *            坐标
	 */
	private void drawText(Canvas canvas, int color, String text, PointF p) {
		Paint fontPaint = new Paint();
		fontPaint.setStyle(Paint.Style.STROKE);
		fontPaint.setAntiAlias(true);
		fontPaint.setColor(color);
		fontPaint.setTextSize(dip2px(16));
		canvas.drawText(text, p.x - dip2px(12), p.y - getFontHeight(fontPaint.getTextSize()) / 2, fontPaint);
	}
	
	/**
	 * 画圆圈
	 * 
	 * @param canvas
	 *            画布
	 * @param points
	 *            坐标点
	 * @param color
	 *            圆圈的颜色
	 * @param radius
	 *            圆圈的半径大小
	 */
	private void drawCycle(Canvas canvas, PointF pointF, int color, float radius) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(color);
		canvas.drawCircle(pointF.x, pointF.y, radius, paint);
	}
	
	/**
	 * 获取字体高度
	 * 
	 * @param fontSize
	 *            字号大小
	 * @return 字体高度
	 */
	public int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}
	
	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	//初始化点击事件
	private void initListener() {
		final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (PinView.this.isReady()) {
					PointF sCoord = PinView.this.viewToSourceCoord(e.getX(), e.getY());
					for (int i = 0, size = pinList != null ? pinList.size() : 0; i < size; i++) {
						Pin pin = pinList.get(i);
						PointF vPoint = pin.getLocation();
						float x2 = Math.abs(vPoint.x - sCoord.x);
						float y2 = Math.abs(vPoint.y - sCoord.y);
						double distance = Math.sqrt(x2 * x2 + y2 * y2);
						if (distance <= radius) {
							if (onCityClicklistener != null) {
								onCityClicklistener.onCityClick(pin);
							}
							break;
						}
					}
				}
				return true;
			}
		});
		
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureDetector.onTouchEvent(motionEvent);
			}
		});
	}
	
	public OnCityClicklistener getOnCityClicklistener() {
		return onCityClicklistener;
	}
	
	public void setOnCityClicklistener(OnCityClicklistener onCityClicklistener) {
		this.onCityClicklistener = onCityClicklistener;
	}
	
	public static class Pin {
		private String id;
		private String text;
		private Bitmap bitmap;
		private PointF point;
		
		public Pin() {
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
		
		public Bitmap getBitmap() {
			return bitmap;
		}
		
		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}
		
		public PointF getLocation() {
			return point;
		}
		
		public void setLocation(PointF location) {
			this.point = location;
		}
	}//end pin
	
	public interface OnCityClicklistener {
		void onCityClick(Pin pin);
	}
}
