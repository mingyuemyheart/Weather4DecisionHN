package com.pmsc.weather4decision.phone.hainan.act;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.lib.app.BaseActivity;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.PushNewsAdapter;
import com.pmsc.weather4decision.phone.hainan.dto.NewsDto;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.pmsc.weather4decision.phone.hainan.view.MyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 消息推送
 */

public class PushNewsActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext = null;
    private LinearLayout llBack = null;
    private ListView listView = null;
    private PushNewsAdapter mAdapter = null;
    private List<NewsDto> mList = new ArrayList<>();
    private MyDialog mDialog = null;
    private int page = 1, pageSize = 20;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_push_news);
        mContext = this;
        showDialog();
        initWidget();
        initListView();
    }

    private void showDialog() {
        if (mDialog == null) {
            mDialog = new MyDialog(mContext);
        }
        mDialog.show();
    }

    private void cancelDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void initWidget() {
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);

        mList.clear();
        OkHttpNews("http://59.50.130.88:8888/decision-admin/push/getpush?uid="+PreferUtil.getUid()+"&type=2&rows="+page+"&pageCount="+pageSize);
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
        mAdapter = new PushNewsAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
                    page += 1;
                    OkHttpNews("http://59.50.130.88:8888/decision-admin/push/getpush?uid="+PreferUtil.getUid()+"&type=2&rows="+page+"&pageCount="+pageSize);
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
            }
        });
    }

    /**
     * 获取未读消息数量
     * @param url
     */
    private void OkHttpNews(final String url) {
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
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("list")) {
                                            JSONArray array = obj.getJSONArray("list");
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject itemObj = array.getJSONObject(i);
                                                NewsDto dto = new NewsDto();
                                                if (!itemObj.isNull("title")) {
                                                    dto.title = itemObj.getString("title");
                                                }
                                                if (!itemObj.isNull("content")) {
                                                    dto.content = itemObj.getString("content");
                                                }
                                                if (!itemObj.isNull("publictime")) {
                                                    dto.time = itemObj.getString("publictime");
                                                }
                                                if (!itemObj.isNull("push_type")) {
                                                    dto.pushType = itemObj.getString("push_type");
                                                }
                                                mList.add(dto);
                                            }
                                            if (mList.size() > 0 && mAdapter != null) {
                                                mAdapter.notifyDataSetChanged();
                                            }
                                            cancelDialog();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
        }
    }
}
