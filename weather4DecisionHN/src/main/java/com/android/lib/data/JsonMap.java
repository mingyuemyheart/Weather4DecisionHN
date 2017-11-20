package com.android.lib.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Depiction:Json处理工具类，避免了编写大量javabean的问题
 * <p/>
 * Modify:
 * <p/>
 * Author: Kevin Lynn
 * <p/>
 * Create Date：2014-4-8 下午5:28:12
 * <p/>
 * 
 * @version 1.0
 * @since 1.0
 */
public class JsonMap extends HashMap<String, Object> {
	private static final long serialVersionUID = 4567321902312180302L;
	
	public boolean getBoolean(String key) {
		try {
			return getString(key).equals("true");
		} catch (Exception e) {
		}
		return false;
	}
	
	public int getInt(String key) {
		try {
			return (int) getFloat(key);
		} catch (Exception e) {
		}
		return 0;
	}
	
	public float getFloat(String key) {
		try {
			return Float.parseFloat(get(key).toString());
		} catch (Exception e) {
		}
		return 0.0f;
	}
	
	public double getDouble(String key) {
		try {
			return Double.parseDouble(get(key).toString());
		} catch (Exception e) {
		}
		return 0.0f;
	}
	
	public long getLong(String key) {
		try {
			return (long) getDouble(key);
		} catch (Exception e) {
		}
		return 0l;
	}
	
	public String getString(String key) {
		return get(key) != null ? get(key).toString() : null;
	}
	
	@SuppressWarnings ("unchecked")
	public JsonMap getMap(String key) {
		try {
			Map<String, Object> map = (Map<String, Object>) get(key);
			JsonMap data = new JsonMap();
			for (Iterator<String> keys = map.keySet().iterator(); keys.hasNext();) {
				String k = (String) keys.next();
				Object v = map.get(k);
				data.put(k, v);
			}
			return data;
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(), "getMap()" + e.toString());
		}
		return null;
	}
	
	@SuppressWarnings ("unchecked")
	public List<JsonMap> getListMap(String key) {
		try {
			List<Map<String, Object>> maps = (List<Map<String, Object>>) get(key);
			List<JsonMap> listMap = new ArrayList<JsonMap>();
			for (Map<String, Object> map : maps) {
				JsonMap data = new JsonMap();
				for (Iterator<String> keys = map.keySet().iterator(); keys.hasNext();) {
					String k = (String) keys.next();
					Object v = map.get(k);
					data.put(k, v);
				}
				listMap.add(data);
			}
			return listMap;
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(), "getListMap()" + e.toString());
		}
		return null;
	}
	
	@SuppressWarnings ("unchecked")
	public List<String> getStringList(String key) {
		try {
			List<String> strings = (List<String>) get(key);
			return strings;
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(), "getStringList()" + e.toString());
		}
		return null;
	}
	
	@SuppressWarnings ("unchecked")
	public List<Double> getDoubleList(String key) {
		try {
			List<Double> doubles = (List<Double>) get(key);
			return doubles;
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(), "getDoubleList()" + e.toString());
		}
		return null;
	}
	
	public static JsonMap toJsonMap(Map<String, Object> map) {
		JsonMap data = new JsonMap();
		for (Iterator<String> keys = map.keySet().iterator(); keys.hasNext();) {
			String k = (String) keys.next();
			Object v = map.get(k);
			data.put(k, v);
		}
		return data;
	}
	
	/**
	 * Json数据解析
	 * 
	 * @param json
	 *            json源串
	 * @return JsonMap
	 */
	public static JsonMap parseJson(String json) {
		return parseJson(json, JsonMap.class);
	}
	
	/**
	 * Json数据解析
	 * 
	 * @param json
	 *            json源串
	 * @return List<JsonMap>
	 */
	public static List<JsonMap> parseJsonArray(String json) {
		List<JsonMap> listMap = new ArrayList<JsonMap>();
		try {
			Type listType = new TypeToken<List<Map<String, Object>>>() {
			}.getType();
			List<Map<String, Object>> list = parseJson(json, listType);
			for (Map<String, Object> map : list) {
				listMap.add(toJsonMap(map));
			}
		} catch (Exception e) {
			Log.w("JsonMap", "parseJsonArray()" + e.toString());
		}
		return listMap;
	}
	
	/**
	 * Json数据解析
	 * 
	 * @param json
	 *            json源串
	 * @param cls
	 *            存储json的实体类
	 * @return 相应的实体类对象
	 */
	public static <T> T parseJson(String json, Type cls) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, cls);
		} catch (Exception e) {
			Log.w(cls.getClass().getSimpleName(), "parseJson()" + e.getMessage());
		}
		return null;
	}
	
	@Override
	public Object get(Object key) {
		return super.get(key);
	}
	
	@Override
	public String toString() {
		try {
			Gson gson = new Gson();
			return gson.toJson(this);
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(), "toString()" + e.getMessage());
		}
		return super.toString();
	}
}
