package com.pmsc.weather4decision.phone.hainan.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.android.lib.http.AsyncDownloader;
import com.android.lib.util.AssetFile;
import com.android.lib.util.FileType;
import com.android.lib.util.LogUtil;
import com.android.lib.view.scaleview.ImageSource;
import com.android.lib.view.scaleview.SubsamplingScaleImageView;
import com.android.lib.view.scaleview.SubsamplingScaleImageView.OnImageEventListener;
import com.google.bitmapcache.ImageCache;
import com.google.bitmapcache.ImageFetcher;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.act.CityActivity;
import com.pmsc.weather4decision.phone.hainan.act.ListNavigationActivity;
import com.pmsc.weather4decision.phone.hainan.act.ListNormalActivity;
import com.pmsc.weather4decision.phone.hainan.act.MainActivity;
import com.pmsc.weather4decision.phone.hainan.act.ProvinceActivity;
import com.pmsc.weather4decision.phone.hainan.act.TyphoonRouteActivity;


/**
 * Depiction: 工具类
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月14日 下午1:36:58
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class Utils {
	private static HNApp                     context    = HNApp.getInstance();
	public final static String               HOME       = "home";
	public final static String               CITY       = "city";
	public final static String               OLD_DOC    = "old_doc";                      //报文数据,旧的数据格式，报文列表,重大快报，专报
	public final static String               DOCUMENT   = "document";                     //报文数据，报文列表,重大快报，专报
	public final static String               WARNING    = "warning";                      //预警数据，预警列表
	public final static String               IMAGE_TEXT = "image_text";                   //图文数据,实况信息界面
	public final static String               IMAGES     = "imgs";                         //多图数据，雷达，卫星云图界面
	public final static String               JSON_MAP   = "json_map";                     //json map地图数据，全省预报
	public final static String               TF_TRACK   = "tf_track";                     //台风路径
	private static HashMap<String, Class<?>> mimeTypes  = new HashMap<String, Class<?>>();
	static {
		mimeTypes.put(HOME, MainActivity.class);
		mimeTypes.put(CITY, CityActivity.class);
		mimeTypes.put(DOCUMENT, ListNormalActivity.class);
		mimeTypes.put(OLD_DOC, ListNormalActivity.class);
		mimeTypes.put(WARNING, ListNavigationActivity.class);
		mimeTypes.put(IMAGES, ListNavigationActivity.class);
		mimeTypes.put(JSON_MAP, ProvinceActivity.class);
		mimeTypes.put(IMAGE_TEXT, ListNavigationActivity.class);
		mimeTypes.put(TF_TRACK, TyphoonRouteActivity.class);
	}
	
	private Utils() {
	}
	
	/**
	 * 根据接口返回的数据类型，获取对应的activity界面类
	 * 
	 * @param mimeType
	 * @return Activity
	 */
	public static Class<?> getActivity(String mimeType) {
		if (TextUtils.isEmpty(mimeType) || !mimeTypes.containsKey(mimeType)) {
			return null;
		}
		return mimeTypes.get(mimeType);
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 *
	 * @param dpValue
	 *            dp
	 * @return px
	 */
	public static int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	/**
	 * 加载网络大图
	 * 
	 * @param imageView
	 * @param imageUrl
	 * @param onImageEventListener
	 */
	public static void loadScaleImage(final ProgressBar progressBar, final SubsamplingScaleImageView imageView, String imageUrl, OnImageEventListener onImageEventListener) {
		imageView.setOnImageEventListener(onImageEventListener);
		//图片下载后保存到本地的路径
		final String dest = ImageCache.getDiskCacheDir(context, "http").getAbsolutePath() + "/" + ImageCache.hashKeyForDisk(imageUrl) + ".0";
		AsyncDownloader loader = new AsyncDownloader(imageUrl, dest) {
			@Override
			protected void onDownloadSuccess(String url) {
				checkGifImage(url, dest);
				imageView.setImage(ImageSource.uri(dest));
			}
			
			@Override
			protected void onDownloadProgress(int progress) {
				//				LogUtil.e(this, "load image -->" + progress);
			}
			
			@Override
			protected void onDownloadFailure(String url) {
				LogUtil.e(this, "load image fail-->" + url);
				if (progressBar != null) {
					progressBar.setVisibility(View.GONE);
				}
			}
			
			@Override
			protected void onDownloadCancel(String url) {
			}
		};
		loader.load(false);
	}
	
	//处理GIF图片
	private static void checkGifImage(String url, String dest) {
		String fileType = FileType.getFileType(dest);
		LogUtil.d("ImageAsyncDownloader", "load image success,fileType-->" + fileType);
		if (fileType.equalsIgnoreCase("gif") || url.endsWith(".gif")) {
			Bitmap bm = ImageFetcher.decodeSampledBitmapFromFile(dest, 1000, 1000);
			try {
				FileOutputStream fos = new FileOutputStream(dest);
				bm.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getDayDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(new Date(System.currentTimeMillis()));
	}
	
	public static String checkUrl(String listUrl, String dataUrl) {
		if ((!TextUtils.isEmpty(listUrl)) && (listUrl.startsWith("http://") || listUrl.startsWith("https://"))) {
			return listUrl;
		} else if ((!TextUtils.isEmpty(dataUrl)) && (dataUrl.startsWith("http://") || dataUrl.startsWith("https://"))) {
			return dataUrl;
		}
		
		return null;
	}
	
	public static Bitmap getWeatherBitmap(boolean isDay, String code) {
		String tag = isDay ? "day" : "night";
		AssetFile asset = new AssetFile(context);
		Bitmap icon = asset.getBitmap("weather/icon_weather_" + tag + "_" + code + ".png");
		return icon;
	}
	
	public static Drawable getWeatherDrawable(boolean isDay, String code) {
		String tag = isDay ? "day" : "night";
		AssetFile asset = new AssetFile(context);
		Drawable icon = asset.getDrawable("weather/icon_weather_" + tag + "_" + code + ".png");
		return icon;
	}
	
	/**
     * 获取网落图片资源 
     * @param url
     * @return
     */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 读取assets下文件
	 * @param fileName
	 * @return
	 */
	public static String getFromAssets(Context context, String fileName) {
		String Result = "";
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}
}
