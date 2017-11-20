package com.pmsc.weather4decision.phone.hainan.http;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.text.TextUtils;

import com.android.lib.http.HttpAsyncTask;


/**
 * Depiction: 获取天气信息，目前主要用来调用国家局的接口
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月1日 下午1:29:06
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class FetchWeather {
	public final static String APP_ID  = "a1b42a4dccd7493f";
	public final static String APP_KEY = "chinaweather_jcb_webapi_data";
	
	public FetchWeather() {
	}
	
	public void perform(String cityId, String type) {
		HttpAsyncTask http = new HttpAsyncTask(type) {
			
			@Override
			public void onStart(String taskId) {
			}
			
			@Override
			public void onFinish(String taskId, String response) {
//				LogUtil.e(this, response);
				if (onFetchWeatherListener != null) {
					onFetchWeatherListener.onFetchWeather(taskId, response);
				}
			}
		};
		
		String api = "";
		if (!TextUtils.isEmpty(cityId) && cityId.startsWith("10131")) {//海南
			api = "http://data-fusion.tianqi.cn/datafusion/GetDate?type=HN&ID="+cityId;
			http.excute(api, "");
		}else {
			api = "http://hfapi.tianqi.cn/data/?";
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("areaid", cityId);
			map.put("type", type);
			map.put("date", getDateParam());
			http.excute(getAuthUrl(api, map), "");
		}
	}
	
	@SuppressWarnings ("deprecation")
	public static String getAuthUrl(String url, LinkedHashMap<String, String> map) {
		String URLpre = url + buildParams(map);
		String publicKey = URLpre + "&appid=" + APP_ID;
		byte[] signature = getSignature(APP_KEY, publicKey);
		String encodeByte = encodeByte(signature);
		String key = URLEncoder.encode(encodeByte);
		return URLpre + "&appid=" + APP_ID.substring(0, 6) + "&key=" + key;
	}
	
	public static byte[] getSignature(String key, String data) {
		byte[] rawHmac = null;
		byte[] keyBytes = key.getBytes();
		try {
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			rawHmac = mac.doFinal(data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rawHmac;
	}
	
	public static String encodeByte(byte[] src) {
		try {
			byte[] encodeBase64 = Base64.encodeBase64(src);
			String encodeStr = new String(encodeBase64, "UTF-8");
			return encodeStr;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 组装参数Map
	 *
	 * @param paramsMap
	 * @return String
	 */
	private static String buildParams(Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return "";
		}
		StringBuilder params = new StringBuilder();
		Set<String> keySet = paramsMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = paramsMap.get(key);
			params.append(key + "=" + value + "&");
		}
		return params.toString().substring(0, params.toString().length() - 1);
	}
	
	public static String getDateParam() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		return format.format(new Date(System.currentTimeMillis()));
	}
	
	private OnFetchWeatherListener onFetchWeatherListener;
	
	public OnFetchWeatherListener getOnFetchWeatherListener() {
		return onFetchWeatherListener;
	}
	
	public void setOnFetchWeatherListener(OnFetchWeatherListener onFetchWeatherListener) {
		this.onFetchWeatherListener = onFetchWeatherListener;
	}
	
	public interface OnFetchWeatherListener {
		public void onFetchWeather(String tag, String response);
	}
}
