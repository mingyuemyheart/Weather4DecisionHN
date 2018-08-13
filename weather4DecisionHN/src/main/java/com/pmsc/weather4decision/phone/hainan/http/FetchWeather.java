package com.pmsc.weather4decision.phone.hainan.http;

import android.text.TextUtils;

import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


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

	private final static String APP_ID  = "a1b42a4dccd7493f";
	private final static String APP_KEY = "chinaweather_jcb_webapi_data";
	
	public FetchWeather() {
	}
	
	public void perform(String cityId, String type) {
		this.cityId = cityId;
		this.type = type;
		if (!TextUtils.isEmpty(cityId) && cityId.startsWith("10131")) {//海南
			String url = "http://hainan.welife100.com/Public/hnfusion?areaid="+cityId;
			OkHttpHannan(url);
		}else {
			OkHttpWeather2(weather2Url(cityId, type));
		}
	}

	private String cityId, type;

	/**
	 * 请求海南本地数据
	 * @param url
	 */
	private void OkHttpHannan(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						OkHttpWeather2(weather2Url(cityId, type));
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}

						String result = response.body().string();
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject obj = new JSONObject(result);
								if (onFetchWeatherListener != null) {
									onFetchWeatherListener.onFetchWeather(result);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								OkHttpWeather2(weather2Url(cityId, type));
							}
						}

					}
				});
			}
		}).start();
	}

	/**
	 * 获取国家站数据接口地址
	 * @param cityId
	 * @param type
	 * @return
	 */
	public static String weather2Url(String cityId, String type) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("areaid", cityId);
		map.put("type", type);
		map.put("date", getDateParam());
		String url = getAuthUrl("http://hfapi.tianqi.cn/data/?", map);
		return url;
	}

	/**
	 * 请求国家站数据
	 * @param url
	 */
	private void OkHttpWeather2(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {

					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						String result = response.body().string();
						if (!TextUtils.isEmpty(result)) {
							if (onFetchWeatherListener != null) {
								onFetchWeatherListener.onFetchWeather(result);
							}
						}
					}
				});
			}
		}).start();
	}
	
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
		void onFetchWeather(String response);
	}
}
