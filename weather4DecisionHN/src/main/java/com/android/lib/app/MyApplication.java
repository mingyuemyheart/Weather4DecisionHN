package com.android.lib.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application{

    private static String top_img,top_img_url,top_img_title;

    public static String getTop_img() {
        return top_img;
    }

    public static void setTop_img(String top_img) {
        MyApplication.top_img = top_img;
    }

    public static String getTop_img_url() {
        return top_img_url;
    }

    public static void setTop_img_url(String top_img_url) {
        MyApplication.top_img_url = top_img_url;
    }

    public static String getTop_img_title() {
        return top_img_title;
    }

    public static void setTop_img_title(String top_img_title) {
        MyApplication.top_img_title = top_img_title;
    }

	private static Map<String,Activity> destoryMap = new HashMap<>();

    private MyApplication() {
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
    }

    /**
     * 添加到销毁队列
     * @param activity 要销毁的activity
     */
    public static void addDestoryActivity(Activity activity,String activityName) {
        destoryMap.put(activityName,activity);
    }
    
	/**
	*销毁指定Activity
	*/
    public static void destoryActivity(String activityName) {
       Set<String> keySet=destoryMap.keySet();
        for (String key:keySet){
            destoryMap.get(key).finish();
        }
    }

}
