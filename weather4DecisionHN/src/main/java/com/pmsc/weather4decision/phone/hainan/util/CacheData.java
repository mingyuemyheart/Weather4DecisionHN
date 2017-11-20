package com.pmsc.weather4decision.phone.hainan.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.android.lib.util.IO;
import com.android.lib.util.LogUtil;
import com.google.bitmapcache.ImageCache;
import com.pmsc.weather4decision.phone.hainan.HNApp;


/**
 * Depiction: 缓存栏目和首页数据
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月4日 下午6:54:59
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class CacheData {
	private static HNApp context = HNApp.getInstance();
	
	private CacheData() {
	}
	
	//栏目缓存
	public static void cacheChannels(String data) {
		try {
			File file = new File(getChannelDataPath());
			file.createNewFile();
			IO.write(data, file);
		} catch (IOException e) {
			LogUtil.e(new CacheData(), "cacheChannels()--->" + e.toString());
		}
	}
	
	public static String getChannelData() {
		try {
			return IO.readAsString(new File(getChannelDataPath()));
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(new CacheData(), "getChannelData()---UnsupportedEncodingException--->" + e.toString());
		} catch (FileNotFoundException e) {
			LogUtil.e(new CacheData(), "getChannelData()---FileNotFoundException--->" + e.toString());
		} catch (IOException e) {
			LogUtil.e(new CacheData(), "getChannelData()---IOException--->" + e.toString());
		}
		return null;
	}
	
	public static String getChannelDataPath() {
		String dir = ImageCache.getDiskCacheDir(context, "json").getAbsolutePath();
		new File(dir).mkdirs();
		return dir + "/channels.json";
	}
	
	//home缓存
	public static void cacheHome(String data) {
		try {
			File file = new File(getHomeDataPath());
			if (file != null && file.exists()) {
				file.delete();
			}
			
			file.createNewFile();
			IO.write(data, file);
		} catch (IOException e) {
			LogUtil.e(new CacheData(), "cacheHome()--->" + e.toString());
		}
	}
	
	public static String getHomeData() {
		try {
			return IO.readAsString(new File(getHomeDataPath()));
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(new CacheData(), "getHomeData()---UnsupportedEncodingException--->" + e.toString());
		} catch (FileNotFoundException e) {
			LogUtil.e(new CacheData(), "getHomeData()---FileNotFoundException--->" + e.toString());
		} catch (IOException e) {
			LogUtil.e(new CacheData(), "getHomeData()---IOException--->" + e.toString());
		}
		return null;
	}
	
	public static String getHomeDataPath() {
		String dir = ImageCache.getDiskCacheDir(context, "json").getAbsolutePath();
		new File(dir).mkdirs();
		return dir + "/home.json";
	}
	
	//实况详细数据缓存
	public static void cacheLiveData(String data) {
		try {
			File file = new File(getLiveDataPath());
			if (file != null && file.exists()) {
				file.delete();
			}
			
			file.createNewFile();
			IO.write(data, file);
		} catch (IOException e) {
			LogUtil.e(new CacheData(), "cacheLiveData()--->" + e.toString());
		}
	}
	
	public static String getLiveData() {
		try {
			String data = IO.readAsString(new File(getLiveDataPath()));
			File temp = new File(CacheData.getLiveDataPath());
			if (temp != null && temp.exists()) {
				temp.delete();
			}
			return data;
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(new CacheData(), "getLiveData()---UnsupportedEncodingException--->" + e.toString());
		} catch (FileNotFoundException e) {
			LogUtil.e(new CacheData(), "getLiveData()---FileNotFoundException--->" + e.toString());
		} catch (IOException e) {
			LogUtil.e(new CacheData(), "getLiveData()---IOException--->" + e.toString());
		}
		return null;
	}
	
	public static String getLiveDataPath() {
		String dir = ImageCache.getDiskCacheDir(context, "json").getAbsolutePath();
		new File(dir).mkdirs();
		return dir + "/live_data.json";
	}
}
