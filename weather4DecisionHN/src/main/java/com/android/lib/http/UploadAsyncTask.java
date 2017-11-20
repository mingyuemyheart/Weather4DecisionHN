package com.android.lib.http;

import java.io.File;

import android.os.Handler;


/**
 * Depiction:
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2013-7-22 下午3:20:50
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class UploadAsyncTask {
	private Handler    handler = new Handler();
	private FileUpload upload;
	
	public UploadAsyncTask(String path, String uploadUrl) {
		upload = new FileUpload(path, uploadUrl);
	}
	
	public UploadAsyncTask(File file, String uploadUrl) {
		upload = new FileUpload(file, uploadUrl);
	}
	
	public void excute(String formName) {
		onPreExcute();
		final String name = formName;
		handler.post(new Runnable() {
			@Override
			public void run() {
				onPostExcute(upload.upload(name));
			}
		});
	}
	
	protected void onPreExcute() {
	}
	
	protected abstract void onPostExcute(String result);
}
