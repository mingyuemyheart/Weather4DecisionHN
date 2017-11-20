package com.pmsc.weather4decision.phone.hainan.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Depiction: 自定义折线图
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月5日 下午2:08:39
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ChartView extends View implements Runnable {
	private List<Integer>     dayList;
	private List<Integer>     nightList;
	private int               OFFSET_V    = 100;
	private int               SCALE_V     = 5;
	private int               OFFSET_H    = 100;
	private int               strokeWidth = 10;
	private int               count       = 6;                            //7个节点
	private int               bgColor     = Color.TRANSPARENT;
	private int               dayColor    = Color.parseColor("#faa219");
	private int               nightColor  = Color.parseColor("#12b1fb");
	private final static long DURATION    = 100;
	
	private int               paintTimes  = 0;
	
	public ChartView(Context context) {
		super(context);
	}
	
	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public void setData(List<Integer> dayList, List<Integer> nightList) {
		this.dayList = dayList;
		this.nightList = nightList;
		
		invalidate();
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawColor(bgColor);
		OFFSET_V = getHeight() / 2 / SCALE_V;
		List<PointF> dayPoints = null;
		if (dayList != null) {
			int bottomY = (int) (computeDistance(dayList) < 6 ? getHeight() / 2 - OFFSET_V * 1.0f : getHeight() / 2 - OFFSET_V / 3);
			dayPoints = computePoints(canvas, dayColor, dayList, bottomY);
		}
		
		List<PointF> nightPoints = null;
		if (nightList != null) {
			int bottomY = (int) (computeDistance(nightList) < 6 ? getHeight() - OFFSET_V * 2.5f : getHeight() - OFFSET_V * 1.3f);
			nightPoints = computePoints(canvas, nightColor, nightList, bottomY);
		}
		
		for (int i = 0; i < paintTimes + 1; i++) {
			//画白天的折线信息
			if (i < dayPoints.size() - 1) {
				drawLine(canvas, dayColor, dayPoints.get(i), dayPoints.get(i + 1));
			}
			drawCycle(canvas, dayPoints.get(i), dayColor, strokeWidth);
			drawCycle(canvas, dayPoints.get(i), Color.WHITE, strokeWidth / 2);
			drawText(canvas, dayColor, dayList.get(i), dayPoints.get(i));
			
			//画夜晚的折线信息
			if (i < nightPoints.size() - 1) {
				drawLine(canvas, nightColor, nightPoints.get(i), nightPoints.get(i + 1));
			}
			drawCycle(canvas, nightPoints.get(i), nightColor, strokeWidth);
			drawCycle(canvas, nightPoints.get(i), Color.WHITE, strokeWidth / 2);
			drawText(canvas, nightColor, nightList.get(i), nightPoints.get(i));
			
			//刷新画下一个节点
			if (paintTimes < count) {
				postDelayed(this, DURATION);
			}
		}
	}
	
	private int computeDistance(List<Integer> dataList) {
		int min = getMinAndMax(dataList)[0];
		int max = getMinAndMax(dataList)[1];
		return Math.abs(max - min);
	}
	
	private List<PointF> computePoints(Canvas canvas, int color, List<Integer> dataList, int bottomY) {
		float w = getWidth() - OFFSET_H * 2;//除去边距之后实际可用的宽度
		float spaceH = w / count; //水平方向每两个点之间的距离
		
		int min = getMinAndMax(dataList)[0];
		float scale = computeScale(dataList);//每一个温度对应的像素数
		
		List<PointF> points = new ArrayList<PointF>();
		for (int i = 0; i < dataList.size(); i++) {
			float value = dataList.get(i);
			PointF p = new PointF();
			p.x = OFFSET_H + i * spaceH;
			p.y = bottomY - (value - min) * scale;
			points.add(p);
		}
		
		return points;
	}
	
	/**
	 * 画折线
	 * 
	 * @param canvas
	 *            画笔
	 * @param color
	 *            折线颜色
	 * @param start
	 *            起点坐标
	 * @param end
	 *            终点坐标
	 */
	private void drawLine(Canvas canvas, int color, PointF start, PointF end) {
		Paint linePaint = new Paint();
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(strokeWidth);
		linePaint.setAntiAlias(true);
		linePaint.setColor(color);
		
		Path path = new Path();
		path.moveTo(start.x, start.y);
		path.lineTo(end.x, end.y);
		canvas.drawPath(path, linePaint);
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
	private void drawText(Canvas canvas, int color, int text, PointF p) {
		Paint fontPaint = new Paint();
		fontPaint.setStyle(Paint.Style.STROKE);
		fontPaint.setAntiAlias(true);
		fontPaint.setColor(color);
		fontPaint.setTextSize(dip2px(12));
		canvas.drawText(text + "°C", p.x - dip2px(12), p.y - getFontHeight(fontPaint.getTextSize()) / 2, fontPaint);
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
		paint.setStrokeWidth(radius);
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawCircle(pointF.x, pointF.y, radius, paint);
	}
	
	/**
	 * 获取List对象中的最小值和最大值
	 * 
	 * @param datas
	 *            数据源
	 * @return int数组，第一个为最小值，第二个为最大值
	 */
	private int[] getMinAndMax(List<Integer> datas) {
		int min = datas.get(0);
		int max = datas.get(0);
		for (int i = 0; i < datas.size(); i++) {
			if (min > datas.get(i)) min = datas.get(i);
			if (max < datas.get(i)) max = datas.get(i);
		}
		return new int[] {
		        min,
		        max
		};
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
	
	//暂时不用，固定写死为50像素
	private float computeScale(final List<Integer> dataList) {
		int min = getMinAndMax(dataList)[0];
		int max = getMinAndMax(dataList)[1];
		float h = getHeight() / 2 - OFFSET_V * 2;//除去顶部和底部边距之后实际可用的高度
		float scale = h / (float) Math.abs(max - min);//根据最大和最小数之间的差值，计算出每一个温度对应的像素数
		if (scale > 100.f) {
			scale = 45;
		} else if (scale < 30) {
			scale = 30;
		}
		//		LogUtil.e(this, "pixel scale-->" + scale);
		return scale;
	}
	
	@Override
	public void run() {
		if (paintTimes < count) {
			paintTimes += 1;
			invalidate();
		}
	}
}
