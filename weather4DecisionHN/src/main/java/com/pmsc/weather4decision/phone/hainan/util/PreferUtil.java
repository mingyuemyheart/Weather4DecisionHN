package com.pmsc.weather4decision.phone.hainan.util;

import com.android.lib.util.Preferences;
import com.pmsc.weather4decision.phone.hainan.HNApp;


/**
 * Depiction: 软件基本设置
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月13日 下午12:09:27
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PreferUtil {
	private static HNApp context = HNApp.getInstance();
	
	private PreferUtil() {
	}
	
	public static void saveMenuCurrentParentIndex(int index) {
		Preferences.getPrefer(context).putInt("parent_index", index);
	}
	
	public static int getMenuCurrentParentIndex() {
		return Preferences.getPrefer(context).getInt("parent_index", 0);
	}
	
	public static void saveMenuCurrentChildIndex(int index) {
		Preferences.getPrefer(context).putInt("child_index", index);
	}
	
	public static int getMenuCurrentChildIndex() {
		return Preferences.getPrefer(context).getInt("child_index", -1);
	}
	
	public static void saveCurrentCityId(String city_id) {
		Preferences.getPrefer(context).putString("city_id", city_id);
	}
	
	public static String getCurrentCityId() {
		return Preferences.getPrefer(context).getString("city_id", "101010100");
	}
	
	public static void saveCurrentDistrict(String district) {
		Preferences.getPrefer(context).putString("district", district);
	}
	
	public static String getCurrentDistrict() {
		return Preferences.getPrefer(context).getString("district", "");
	}
	
	public static void saveCurrentCity(String city) {
		Preferences.getPrefer(context).putString("city", city);
	}
	
	public static String getCurrentCity() {
		return Preferences.getPrefer(context).getString("city", "");
	}
	
	public static void saveCurrentProvince(String province) {
		Preferences.getPrefer(context).putString("province", province);
	}
	
	public static String getCurrentProvince() {
		return Preferences.getPrefer(context).getString("province", "");
	}
	
	public static void saveUid(String uid) {
		Preferences.getPrefer(context).putString("uid", uid);
	}
	
	public static String getUid() {
		return Preferences.getPrefer(context).getString("uid", "");
	}
	
	public static void saveUserName(String name) {
		Preferences.getPrefer(context).putString("name", name);
	}
	
	public static String getUserName() {
		return Preferences.getPrefer(context).getString("name", null);
	}
	
	public static void savePassword(String passwd) {
		Preferences.getPrefer(context).putString("passwd", passwd);
	}
	
	public static String getPassword() {
		return Preferences.getPrefer(context).getString("passwd", null);
	}
	
	public static void saveUserGroup(String userGroup) {
		Preferences.getPrefer(context).putString("user_group", userGroup);
	}
	
	public static String getUserGroup() {
		return Preferences.getPrefer(context).getString("user_group", "");
	}
	
	public static void saveRole(String role) {
		Preferences.getPrefer(context).putString("role", role);
	}
	
	public static String getRole() {
		return Preferences.getPrefer(context).getString("role", "");
	}
}
