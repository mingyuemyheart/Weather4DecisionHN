package com.android.lib.data;

import com.pmsc.weather4decision.phone.hainan.dto.WindData;

public class CONST {

	public static String SERVER_SWITHER = "0"; //服务器切换，1位本地，0为云服务器
	
	//activity对应的销毁map key
	public static final String MainActivity = "MainActivity";
	
	public static String BROADCAST_STOPLOAD = "broadcast_stopload";//停止下载雷达图、云图的广播
	
	public static WindData windData = new WindData();//存放数据对象
	
	public static String userGroup = "a418f91fc0ac4b9ca6329806f6b7f8d3";
}
