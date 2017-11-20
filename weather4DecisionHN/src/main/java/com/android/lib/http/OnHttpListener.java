package com.android.lib.http;

import com.android.lib.data.JsonMap;

/**
 * Depiction:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年3月20日 下午10:24:37
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public interface OnHttpListener {
	public void onStart(String taskId);
	
	public void onFinish(JsonMap datas,String response, String taskId);
}
