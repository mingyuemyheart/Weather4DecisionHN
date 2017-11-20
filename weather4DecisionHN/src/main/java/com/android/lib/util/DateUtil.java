package com.android.lib.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.android.lib.data.JsonMap;


/**
 * Depiction: 从今天开始获取未来八天的日期和星期
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月5日 下午11:49:13
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class DateUtil {
	
	private DateUtil() {
	}
	
	/**
	 * 获取未来或者往前count天的日期
	 * 
	 * @param count
	 *            多少天
	 * @return 集合
	 */
	public final static List<JsonMap> getDateWithYear(int count) {
		if (count < 1) return null;
		
		List<JsonMap> dates = new ArrayList<JsonMap>();
		for (int i = 0; i < count; i++) {
			Date date = new Date(System.currentTimeMillis());
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, i);//整数往后推,负数往前移动
			date = calendar.getTime();
			
			JsonMap map = new JsonMap();
			map.put("year", getDateParam(date, "yyyy-MM-dd"));
			map.put("month", getDateParam(date, "MM-dd"));
			map.put("week", getWeekDayString(date));
			dates.add(map);
		}
		return dates;
	}
	
	public static String getDateParam(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	/**
	 * 根据日期获取该天是星期几
	 * 
	 * @param date
	 * @return 周几
	 */
	public static String getWeekDayString(Date date) {
		String weekString = "";
		final String dayNames[] = {
		        "周日",
		        "周一",
		        "周二",
		        "周三",
		        "周四",
		        "周五",
		        "周六"
		};
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		weekString = dayNames[dayOfWeek - 1];
		return weekString;
	}
}
