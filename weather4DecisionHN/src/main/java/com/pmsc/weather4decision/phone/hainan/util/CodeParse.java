package com.pmsc.weather4decision.phone.hainan.util;

import android.text.TextUtils;

import com.android.lib.data.JsonMap;
import com.android.lib.util.AssetFile;
import com.pmsc.weather4decision.phone.hainan.HNApp;


/**
 * Depiction: 各种天气信息编号解析
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月1日 下午1:04:30
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class CodeParse {
	private static HNApp context = HNApp.getInstance();
	
	private CodeParse() {
	}
	
	/**
	 * 解析天气编码
	 * 
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	public static String parseWeatherCode(String code) {
		return parse("weather", code);
	}
	
	/**
	 * 解析风向编码
	 * 
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	public static String parseWindfxCode(String code) {
		return parse("wind_fx", code);
	}
	
	/**
	 * 解析风力编码
	 * 
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	public static String parseWindflCode(String code) {
		return parse("wind_fl", code);
	}
	
	/**
	 * 解析预警种类编码
	 * 
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	public static String parseWarningCategoryCode(String code) {
		return parse("warning_category", code);
	}
	
	/**
	 * 解析预警级别编码
	 * 
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	public static String parseWarningLevelCode(String code) {
		return parse("warning_level", code);
	}
	
	/**
	 * 解析广东预警种类编码
	 * 
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	public static String parseGDWarningCategoryCode(String code) {
		return parse("warning_category_gd", code);
	}
	
	/**
	 * 解析广东预警级别编码
	 * 
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	public static String parseGDWarningLevelCode(String code) {
		return parse("warning_level_gd", code);
	}
	
	/**
	 * 解析编码
	 * 
	 * @param whatCode
	 *            编码类型
	 * @param code
	 *            编码值
	 * @return 中文信息
	 */
	private static String parse(String whatCode, String code) {
		if (TextUtils.isEmpty(code)) {
			return null;
		}
		JsonMap data = getData();
		if (data != null && data.getMap(whatCode) != null) {
			JsonMap map = data.getMap(whatCode);
			if (map.containsKey(code)) {
				return map.getMap(code).getString("zh");
			}
		}
		return null;
	}
	
	//解析编号信息
	private static JsonMap getData() {
		String json = new AssetFile(context).readAsString("weather_code.json");
		return JsonMap.parseJson(json);
	}
}
