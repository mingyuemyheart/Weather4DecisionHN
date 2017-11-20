package com.android.lib.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 类描述：提示信息框
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-1-6 下午1:58:20
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public final class ToastUtil {
	private static Toast         toast   = null;
	private final static Handler handler = new Handler();
	
	/**
	 * 弹出提示信息
	 * 
	 * @param context
	 * @param msg
	 *            弹出的提示信息
	 */
	public static void showToast(final Context context, final String msg) {
		cancel();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					toast = new Toast(context);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					TextView msgView = new TextView(context);
					msgView.setPadding(20, 10, 20, 10);
					msgView.setText(msg);
					msgView.setTextSize(22f);
					msgView.setTextColor(Color.WHITE);
					msgView.setBackgroundResource(android.R.drawable.toast_frame);
					toast.setView(msgView);
					toast.show();
				} catch (Exception e) {
				}
			}
		}, 500);
	}
	
	/**
	 * 弹出提示信息
	 * 
	 * @param context
	 * @param strRes
	 *            弹出的提示信息的资源id
	 */
	public static void showToast(Context context, int strRes) {
		String tip = context.getString(strRes);
		showToast(context, tip);
	}
	
	public static void cancel() {
		if (toast != null) {
			toast.cancel();
		}
	}
}
