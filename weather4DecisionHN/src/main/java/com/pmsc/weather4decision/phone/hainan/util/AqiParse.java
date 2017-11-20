package com.pmsc.weather4decision.phone.hainan.util;

import android.text.TextUtils;

import com.android.lib.data.JsonMap;
import com.android.lib.util.AssetFile;
import com.pmsc.weather4decision.phone.hainan.HNApp;


/**
 * Depiction: AQI值解析
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
public class AqiParse {
	private static HNApp context = HNApp.getInstance();
	
	private AqiParse() {
	}
	
	/**
	 * 解析aqi值
	 * 
	 * @param value
	 * @return 中文描述
	 */
	public static String parse(String value) {
		if (TextUtils.isEmpty(value)) {
			return "";
		}
		JsonMap data = getData();
		return data.getMap(getLevel(value)).getString("level");
	}
	
	private static String getLevel(String value) {
		String level = "一级";
		int v = Integer.parseInt(value);
		if (v > 0 && v <= 50) {
			level = "一级";
		} else if (v > 50 && v <= 100) {
			level = "二级";
		} else if (v > 100 && v <= 150) {
			level = "三级";
		} else if (v > 150 && v <= 200) {
			level = "四级";
		} else if (v > 200 && v <= 300) {
			level = "五级";
		} else if (v > 300) {
			level = "六级";
		}
		return level;
	}
	
	//解析编号信息
	private static JsonMap getData() {
		String json = new AssetFile(context).readAsString("aqi_code.json");
		return JsonMap.parseJson(json);
	}
}
