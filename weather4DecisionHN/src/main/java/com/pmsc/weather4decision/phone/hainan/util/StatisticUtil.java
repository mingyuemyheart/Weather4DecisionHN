package com.pmsc.weather4decision.phone.hainan.util;

import android.text.TextUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 数据统计工具类
 */

public class StatisticUtil {

    /**
     * 统计页面点击量
     * @param columnId 栏目id
     */
    public static void statisticClickCount(String columnId) {
        if (TextUtils.isEmpty(columnId)) {
            return;
        }
        String uid = PreferUtil.getUid();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        String time = sdf1.format(new Date());
        final String url = "http://59.50.130.88:8888/decision-admin/lstatistics?UID="+uid+"&LCode="+columnId+"&LTime="+time;
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
            }
        }).start();
    }

}
