package com.android.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;


/**
 * Depiction:不会滚动的GridView
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Email: kevin185@foxmail.com
 * <p>
 * Create Date: 2015年4月1日 下午6:37:55
 * <p>
 * Modify:
 * 
 * @version 1.0
 * @since 1.0
 */
public class StillGridView extends GridView {
	
	/**
	 * @param context
	 */
	public StillGridView(Context context) {
		super(context);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public StillGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public StillGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return true;//禁止Gridview进行滑动
		}
		return super.dispatchTouchEvent(ev);
	}
}
