package com.pmsc.weather4decision.phone.hainan.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.ShawnRainDetailAdapter;
import com.pmsc.weather4decision.phone.hainan.dto.ShawnRainDto;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ShawnRainDetailActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle,tvPrompt;
	private LinearLayout ll1, ll2, ll3;
	private TextView tv1, tv2, tv3;
	private ImageView iv1, iv2, iv3;
	private boolean b1 = false, b2 = false, b3 = false;//false为将序，true为升序
	private ListView listView = null;
	private ShawnRainDetailAdapter mAdapter = null;
	private List<ShawnRainDto> realDatas = new ArrayList<>();
	private String childId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_rain_detail);
		mContext = this;
		initWidget();
		initListView();
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("详情数据");
		tvPrompt = (TextView) findViewById(R.id.tvPrompt);
		ll1 = (LinearLayout) findViewById(R.id.ll1);
		ll1.setOnClickListener(this);
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		ll2.setOnClickListener(this);
		ll3 = (LinearLayout) findViewById(R.id.ll3);
		ll3.setOnClickListener(this);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv3 = (ImageView) findViewById(R.id.iv3);

		childId = getIntent().getExtras().getString("childId");
		
		if (getIntent().hasExtra("realDatas")) {
			String title = getIntent().getStringExtra("title");
			String stationName = getIntent().getStringExtra("stationName");
			String area = getIntent().getStringExtra("area");
			String val = getIntent().getStringExtra("val");
			if (!TextUtils.isEmpty(title)) {
				tvPrompt.setText(title);
			}
			if (!TextUtils.isEmpty(stationName)) {
				tv1.setText(stationName);
			}
			if (!TextUtils.isEmpty(area)) {
				tv2.setText(area);
			}
			if (!TextUtils.isEmpty(val)) {
				tv3.setText(val);
			}
			
			List<ShawnRainDto> list = new ArrayList<>();
			list.addAll(getIntent().getExtras().<ShawnRainDto>getParcelableArrayList("realDatas"));
			realDatas.clear();
			if (TextUtils.equals(childId, "638")) {//最低温
				for (int i = list.size()-1; i >= 0; i--) {
					realDatas.add(list.get(i));
				}
			}else {
				realDatas.addAll(list);
			}
		}else {
			String area = getIntent().getStringExtra("area");
			if (TextUtils.isEmpty(area)) {
				area = "";
			}
			
			String startTime = getIntent().getStringExtra("startTime");
			if (TextUtils.isEmpty(startTime)) {
				startTime = "";
			}
			String endTime = getIntent().getStringExtra("endTime");
			if (TextUtils.isEmpty(endTime)) {
				endTime = "";
			}
			String childId = getIntent().getStringExtra("childId");
			if (TextUtils.isEmpty(childId)) {
				childId = "";
			}
			OkHttpDetail("http://59.50.130.88:8888/decision-admin/dates/getcitid?city="+area+"&start="+startTime+"&end="+endTime+"&cid="+childId, childId);
		}

		if (TextUtils.equals(childId, "638")) {//最低温
			if (!b3) {//将序
				iv3.setImageResource(R.drawable.arrow_up);
			}else {//将序
				iv3.setImageResource(R.drawable.arrow_down);
			}
		}else {
			if (!b3) {//将序
				iv3.setImageResource(R.drawable.arrow_down);
			}else {//将序
				iv3.setImageResource(R.drawable.arrow_up);
			}
		}
		iv3.setVisibility(View.VISIBLE);
	}
	
	private void initListView() {
		listView = (ListView) findViewById(R.id.listView);
		mAdapter = new ShawnRainDetailAdapter(mContext, realDatas);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ShawnRainDto dto = realDatas.get(arg2);
				Intent intent = new Intent(mContext, StationMonitorDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				bundle.putString("childId", childId);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
	
	private void OkHttpDetail(final String url, final String childId) {
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

										if (!obj.isNull("th")) {
											JSONObject itemObj = obj.getJSONObject("th");
											if (!itemObj.isNull("stationName")) {
												String stationName = itemObj.getString("stationName");
												if (!TextUtils.isEmpty(stationName)) {
													tv1.setText(stationName);
												}
											}
											if (!itemObj.isNull("area")) {
												String area = itemObj.getString("area");
												if (!TextUtils.isEmpty(area)) {
													tv2.setText(area);
												}
											}
											if (!itemObj.isNull("val")) {
												String val = itemObj.getString("val");
												if (!TextUtils.isEmpty(val)) {
													tv3.setText(val);
												}
											}
										}

										if (!obj.isNull("title")) {
											String title = obj.getString("title");
											if (!TextUtils.isEmpty(title)) {
												tvPrompt.setText(title);
											}
										}

										if (!obj.isNull("list")) {
											realDatas.clear();
											JSONArray array = new JSONArray(obj.getString("list"));
											if (TextUtils.equals(childId, "638")) {//最低温
												for (int i = array.length()-1; i >= 0; i--) {
													JSONObject itemObj = array.getJSONObject(i);
													ShawnRainDto dto = new ShawnRainDto();
													if (!itemObj.isNull("stationCode")) {
														dto.stationCode = itemObj.getString("stationCode");
													}
													if (!itemObj.isNull("stationName")) {
														dto.stationName = itemObj.getString("stationName");
													}
													if (!itemObj.isNull("area")) {
														dto.area = itemObj.getString("area");
													}
													if (!itemObj.isNull("val")) {
														dto.val = itemObj.getDouble("val");
													}

													if (!TextUtils.isEmpty(dto.stationName) && !TextUtils.isEmpty(dto.area)) {
														realDatas.add(dto);
													}
												}
											}else {
												for (int i = 0; i < array.length(); i++) {
													JSONObject itemObj = array.getJSONObject(i);
													ShawnRainDto dto = new ShawnRainDto();
													if (!itemObj.isNull("stationCode")) {
														dto.stationCode = itemObj.getString("stationCode");
													}
													if (!itemObj.isNull("stationName")) {
														dto.stationName = itemObj.getString("stationName");
													}
													if (!itemObj.isNull("area")) {
														dto.area = itemObj.getString("area");
													}
													if (!itemObj.isNull("val")) {
														dto.val = itemObj.getDouble("val");
													}

													if (!TextUtils.isEmpty(dto.stationName) && !TextUtils.isEmpty(dto.area)) {
														realDatas.add(dto);
													}
												}
											}
											if (realDatas.size() > 0 && mAdapter != null) {
												mAdapter.notifyDataSetChanged();
											}
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
	
//	public static String getPingYin(String inputString) {
//        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();  
//        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
//        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
//        format.setVCharType(HanyuPinyinVCharType.WITH_V);  
//
//        char[] input = inputString.trim().toCharArray();  
//        String output = "";  
//        try {  
//            for (int i = 0; i < input.length; i++) {  
//                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {  
//                    String[] temp = PinyinHelper.  
//                    toHanyuPinyinStringArray(input[i],  
//                    format);  
//                    output += temp[0];  
//                } else  
//                    output += java.lang.Character.toString(  
//                    input[i]);  
//            }  
//        } catch (BadHanyuPinyinOutputFormatCombination e) {  
//            e.printStackTrace();  
//        }  
//        return output;  
//    }  

	// 返回中文的首字母
    public static String getPinYinHeadChar(String str) {  
        String convert = "";  
        int size = str.length();
        if (size >= 2) {
        	size = 2;
		}
        for (int j = 0; j < size; j++) {  
            char word = str.charAt(j);  
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
            if (pinyinArray != null) {  
                convert += pinyinArray[0].charAt(0);  
            } else {  
                convert += word;  
            }  
        }  
        return convert;  
    }  
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.ll1:
			if (b1) {//升序
				b1 = false;
				iv1.setImageResource(R.drawable.arrow_up);
				iv1.setVisibility(View.VISIBLE);
				iv2.setVisibility(View.INVISIBLE);
				iv3.setVisibility(View.INVISIBLE);
				Collections.sort(realDatas, new Comparator<ShawnRainDto>() {
					@Override
					public int compare(ShawnRainDto arg0, ShawnRainDto arg1) {
						if (TextUtils.isEmpty(arg0.stationName) || TextUtils.isEmpty(arg1.stationName)) {
							return 0;
						}else {
							return getPinYinHeadChar(arg0.stationName).compareTo(getPinYinHeadChar(arg1.stationName));
						}
					}
				});
			}else {//将序
				b1 = true;
				iv1.setImageResource(R.drawable.arrow_down);
				iv1.setVisibility(View.VISIBLE);
				iv2.setVisibility(View.INVISIBLE);
				iv3.setVisibility(View.INVISIBLE);
				Collections.sort(realDatas, new Comparator<ShawnRainDto>() {
					@Override
					public int compare(ShawnRainDto arg0, ShawnRainDto arg1) {
						if (TextUtils.isEmpty(arg0.stationName) || TextUtils.isEmpty(arg1.stationName)) {
							return -1;
						}else {
							return getPinYinHeadChar(arg1.stationName).compareTo(getPinYinHeadChar(arg0.stationName));
						}
					}
				});
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.ll2:
			if (b2) {//升序
				b2 = false;
				iv2.setImageResource(R.drawable.arrow_up);
				iv1.setVisibility(View.INVISIBLE);
				iv2.setVisibility(View.VISIBLE);
				iv3.setVisibility(View.INVISIBLE);
				Collections.sort(realDatas, new Comparator<ShawnRainDto>() {
					@Override
					public int compare(ShawnRainDto arg0, ShawnRainDto arg1) {
						if (TextUtils.isEmpty(arg0.area) || TextUtils.isEmpty(arg1.area)) {
							return 0;
						}else {
							return getPinYinHeadChar(arg0.area).compareTo(getPinYinHeadChar(arg1.area));
						}
					}
				});
			}else {//将序
				b2 = true;
				iv2.setImageResource(R.drawable.arrow_down);
				iv1.setVisibility(View.INVISIBLE);
				iv2.setVisibility(View.VISIBLE);
				iv3.setVisibility(View.INVISIBLE);
				Collections.sort(realDatas, new Comparator<ShawnRainDto>() {
					@Override
					public int compare(ShawnRainDto arg0, ShawnRainDto arg1) {
						if (TextUtils.isEmpty(arg0.area) || TextUtils.isEmpty(arg1.area)) {
							return -1;
						}else {
							return getPinYinHeadChar(arg1.area).compareTo(getPinYinHeadChar(arg0.area));
						}
					}
				});
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.ll3:
			if (b3) {//升序
				b3 = false;
				iv3.setImageResource(R.drawable.arrow_up);
				iv1.setVisibility(View.INVISIBLE);
				iv2.setVisibility(View.INVISIBLE);
				iv3.setVisibility(View.VISIBLE);
				Collections.sort(realDatas, new Comparator<ShawnRainDto>() {
					@Override
					public int compare(ShawnRainDto arg0, ShawnRainDto arg1) {
						return Double.valueOf(arg0.val).compareTo(Double.valueOf(arg1.val));
					}
				});
			}else {//将序
				b3 = true;
				iv3.setImageResource(R.drawable.arrow_down);
				iv1.setVisibility(View.INVISIBLE);
				iv2.setVisibility(View.INVISIBLE);
				iv3.setVisibility(View.VISIBLE);
				Collections.sort(realDatas, new Comparator<ShawnRainDto>() {
					@Override
					public int compare(ShawnRainDto arg0, ShawnRainDto arg1) {
						return Double.valueOf(arg1.val).compareTo(Double.valueOf(arg0.val));
					}
				});
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}
	
}
