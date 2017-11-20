package com.pmsc.weather4decision.phone.hainan.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import org.apache.http.NameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        HttpAsyncTask task = new HttpAsyncTask();
        task.setMethod("GET");
        task.setTimeOut(CustomHttpClient.TIME_OUT);
        task.execute("http://59.50.130.88:8888/decision-admin/lstatistics?UID="+uid+"&LCode="+columnId+"&LTime="+time);
    }

    /**
     * 异步请求方法
     * @author dell
     *
     */
    private static class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private String method = "GET";
        private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();

        public HttpAsyncTask() {
        }

        @Override
        protected String doInBackground(String... url) {
            String result = null;
            if (method.equalsIgnoreCase("POST")) {
                result = CustomHttpClient.post(url[0], nvpList);
            } else if (method.equalsIgnoreCase("GET")) {
                result = CustomHttpClient.get(url[0]);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @SuppressWarnings("unused")
        private void setParams(NameValuePair nvp) {
            nvpList.add(nvp);
        }

        private void setMethod(String method) {
            this.method = method;
        }

        private void setTimeOut(int timeOut) {
            CustomHttpClient.TIME_OUT = timeOut;
        }

        /**
         * 取消当前task
         */
        @SuppressWarnings("unused")
        private void cancelTask() {
            CustomHttpClient.shuttdownRequest();
            this.cancel(true);
        }
    }

}
