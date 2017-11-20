package com.android.lib.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;


/**
 * Depiction:文件异步下载器
 * <p/>
 * Modify:
 * <p/>
 * Author: Kevin Lynn
 * <p/>
 * Create Date：2013-7-22 下午3:20:50
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public abstract class AsyncDownloader extends Thread {
	private Handler             handler   = new Handler();
	private boolean             override;
	private final int           DURATION  = 1000;
	private final static int    SUCCESS   = 100;
	private final static int    FAILURE   = SUCCESS + 1;
	private final static int    EXIST     = SUCCESS + 2;
	private final static int    CANCEL    = SUCCESS + 3;
	private final static String TAG       = "AsyncDownloader";
	private final static String TEMP      = ".tmp";
	private final static int    TIME_OUT  = 20000;
	private HttpURLConnection   conn      = null;
	private String              fileUrl;
	private String              destPath;
	private boolean             cancel;
	
	private long                completed = 0;
	private long                total     = 0;
	
	/**
	 * 构造函数
	 * 
	 * @param url
	 *            文件下载地址
	 * @param dest
	 *            文件存储路径
	 */
	public AsyncDownloader(String url, String dest) {
		this.fileUrl = url;
		this.destPath = dest;
		initDirs(dest);
	}
	
	public void run() {
		final int res = download(override);
		handler.post(new Runnable() {
			@Override
			public void run() {
				switch (res) {
					case SUCCESS:
						handler.post(new Runnable() {
							@Override
							public void run() {
								onDownloadSuccess(fileUrl);
							}
						});
						break;
					case FAILURE:
						handler.post(new Runnable() {
							@Override
							public void run() {
								onDownloadFailure(fileUrl);
							}
						});
						break;
					case EXIST:
						handler.post(new Runnable() {
							@Override
							public void run() {
								Log.d("AsyncDownloader", "the image \"" + fileUrl + "\" is exist");
								onDownloadSuccess(fileUrl);
							}
						});
						break;
					default:
						//取消下载
						handler.post(new Runnable() {
							@Override
							public void run() {
								onDownloadCancel(fileUrl);
							}
						});
						break;
				}
			}
		});
	}
	
	public void load(boolean isOverride) {
		this.override = isOverride;
		this.start();
	}
	
	/**
	 * 下载文件
	 * 
	 * @param isOverride
	 *            是否覆盖
	 * @return int
	 */
	private int download(boolean isOverride) {
		int res = FAILURE;
		if (isOverride) {
			deleteFile(destPath);
		} else if (existFile(destPath)) {
			return EXIST;
		}
		URL url = null;
		InputStream is = null;
		OutputStream out = null;
		try {
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
			url = new URL(fileUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setDoInput(true);
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			if (conn.getResponseCode() == 200) {
				total = conn.getContentLength();
				is = conn.getInputStream();
				int read = 0;
				byte[] buffer = new byte[1024];
				out = new FileOutputStream(destPath + TEMP);
				
				long startTime = System.currentTimeMillis();
				while ((read = is.read(buffer)) != -1) {
					out.write(buffer, 0, read);
					
					completed += read;
					if (System.currentTimeMillis() - startTime >= DURATION) {
						startTime = System.currentTimeMillis();
					}
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							onDownloadProgress(percentSize(completed, total));
						}
					});
				}
				File temp = new File(destPath + TEMP);
				File file = new File(destPath);
				temp.renameTo(file);
				res = SUCCESS;
			} else {
				Log.d(TAG, "the respond code is ---> " + conn.getResponseCode());
				Log.d(TAG, "the url is:" + fileUrl);
			}
		} catch (MalformedURLException e) {
			deleteFile(destPath + TEMP);
			Log.d(TAG, "MalformedURLException ---> " + e.toString());
		} catch (IOException e) {
			deleteFile(destPath + TEMP);
			Log.d(TAG, "IOException ---> " + e.toString());
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				deleteFile(destPath + TEMP);
				Log.d(TAG, e.toString());
			}
		}
		res = cancel ? CANCEL : res;
		return res;
	}
	
	/**
	 * 取消下载
	 */
	public void cancel() {
		try {
			cancel = true;
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
	
	/**
	 * 删除文件
	 * 
	 * @param path
	 *            文件路径
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * 判断文件是否存在
	 * 
	 * @param path
	 *            文件路径
	 * @return 存在返回true，否则返回false
	 */
	public static boolean existFile(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	private void initDirs(String destPath) {
		if (!TextUtils.isEmpty(destPath)) {
			int index = destPath.lastIndexOf("/");
			if (index != -1) {
				String destDir = destPath.substring(0, index);
				File dir = new File(destDir);
				dir.mkdirs();
			}
		} else {
			try {
				throw new FileNotFoundException("cache path is empty.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 计算百分比
	 *
	 * @param used
	 *            已经使用的
	 * @param total
	 *            总计
	 * @return String
	 */
	public static int percentSize(long progress, long total) {
		if (progress == 0 || total == 0 || (progress > total)) {
			return 0;
		}
		double u = progress;
		double t = total;
		double r = u / t * 100;
		DecimalFormat format = new DecimalFormat("0");
		int p = Integer.parseInt(format.format(r));
		return (p > 100) ? 0 : p;
	}
	
	/**
	 * 当PDF文件正在下载时，回调该方法
	 * 
	 * @param progress
	 *            下载进度，百分比
	 */
	protected abstract void onDownloadProgress(int progress);
	
	/**
	 * 当前PDF文件下载完成时，回调该方法
	 * 
	 * @param url
	 *            pdf下载地址
	 */
	protected abstract void onDownloadSuccess(String url);
	
	/**
	 * 当前PDF文件下载失败时，回调该方法
	 * 
	 * @param url
	 */
	protected abstract void onDownloadFailure(String url);
	
	/**
	 * 当前PDF文件下载取消时，回调该方法
	 * 
	 * @param url
	 */
	protected abstract void onDownloadCancel(String url);
}
