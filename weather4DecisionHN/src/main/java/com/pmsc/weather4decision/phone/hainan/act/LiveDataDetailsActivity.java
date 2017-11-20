package com.pmsc.weather4decision.phone.hainan.act;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.android.lib.util.LogUtil;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.LiveDataDetailsAdapter;
import com.pmsc.weather4decision.phone.hainan.util.CacheData;


/**
 * Depiction: 实况详情界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月13日 下午4:33:38
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class LiveDataDetailsActivity extends BaseActivity {
	private TextView      titleView;
	private ListView      listview;
	private TextView      headerView;
	
	private JsonMap       data;
	private JsonMap       tableHeader;
	private List<JsonMap> datas;
	
	private SortType      sortType = SortType.VALUE_ASC; //默认按值升序
	                                                     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_data_details);
		String title = getIntent().getStringExtra(AbsDrawerActivity.TITLE_KEY);
		titleView = (TextView) findViewById(R.id.title_view);
		if (!TextUtils.isEmpty(title)) {
			titleView.setText(title);
		}
		
		//		String resultData = getIntent().getStringExtra("live_data");
		String resultData = CacheData.getLiveData();
		data = JsonMap.parseJson(resultData);
		if (data == null) {
			LogUtil.e(this, "detail data is null");
			return;
		}
		LogUtil.e(this, "load detail data ");
		tableHeader = data.getMap("th");
		datas = data.getListMap("realDatas");
		
		View header = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_live_data_details_header, null);
		headerView = (TextView) header.findViewById(R.id.header_view);
		headerView.setText(data.getString("title"));
		
		listview = (ListView) findViewById(R.id.listview);
		listview.addHeaderView(header);
		
		sortByValue();
	}
	
	//升序
	private void sortByArea() {
		new Thread() {
			@Override
			public void run() {
				datas = data.getListMap("realDatas");
				Collections.sort(datas, new SortByName("area"));
				loadData();
			}
		}.start();
	}
	
	//降序
	private void reverseByArea() {
		new Thread() {
			@Override
			public void run() {
				datas = data.getListMap("realDatas");
				Collections.sort(datas, new SortByName("area"));
				Collections.reverse(datas);
				loadData();
			}
		}.start();
	}
	
	//升序
	private void sortByStation() {
		new Thread() {
			@Override
			public void run() {
				datas = data.getListMap("realDatas");
				Collections.sort(datas, new SortByName("stationName"));
				loadData();
			}
		}.start();
	}
	
	//降序
	private void reverseByStation() {
		new Thread() {
			@Override
			public void run() {
				datas = data.getListMap("realDatas");
				Collections.sort(datas, new SortByName("stationName"));
				Collections.reverse(datas);
				loadData();
			}
		}.start();
	}
	
	//升序
	private void sortByValue() {
		new Thread() {
			@Override
			public void run() {
				datas = data.getListMap("realDatas");
				Collections.sort(datas, new SortByValue());
				loadData();
			}
		}.start();
	}
	
	//降序
	private void reverseByValue() {
		new Thread() {
			@Override
			public void run() {
				datas = data.getListMap("realDatas");
				Collections.sort(datas, new SortByValue());
				Collections.reverse(datas);
				loadData();
			}
		}.start();
	}
	
	private void loadData() {
		post(new Runnable() {
			@Override
			public void run() {
				LiveDataDetailsAdapter adapter = new LiveDataDetailsAdapter(new OnSortClickListener(), sortType, tableHeader, datas);
				listview.setAdapter(adapter);
				cancelLoadingDialog();
			}
		});
	}
	
	public void onLeftButtonAction(View view) {
		onBackPressed();
	}
	
	public enum SortType {
		VALUE_ASC, //按值升序
		VALUE_DESC, //按值降序
		AREA_ASC, //按属地升序
		AREA_DESC, //按属地降序
		STATION_ASC, //按站升序
		STATION_DESC, //按站降序
	}
	
	//中文排序
	class SortByName implements Comparator<JsonMap> {
		private String name;
		
		public SortByName(String name) {
			this.name = name;
		}
		
		@Override
		public int compare(JsonMap lhs, JsonMap rhs) {
			String lv = lhs.getString(name);
			String rv = rhs.getString(name);
			return Collator.getInstance(Locale.CHINESE).compare(lv, rv);
		}
		
	}
	
	//按值排序
	class SortByValue implements Comparator<JsonMap> {
		
		public SortByValue() {
		}
		
		@Override
		public int compare(JsonMap lhs, JsonMap rhs) {
			Float lv = lhs.getFloat("val");
			Float rv = rhs.getFloat("val");
			return lv.compareTo(rv);
		}
	}
	
	public class OnSortClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			showLoadingDialog(R.string.sorting);
			datas.clear();
			int id = v.getId();
			if (id == R.id.left_view) {
				if (sortType == SortType.STATION_ASC) {
					sortType = SortType.STATION_DESC;
					reverseByStation();
				} else {
					sortType = SortType.STATION_ASC;
					sortByStation();
				}
			} else if (id == R.id.mid_view) {
				if (sortType == SortType.AREA_ASC) {
					sortType = SortType.AREA_DESC;
					reverseByArea();
				} else {
					sortType = SortType.AREA_ASC;
					sortByArea();
				}
			} else if (id == R.id.right_view) {
				if (sortType == SortType.VALUE_ASC) {
					sortType = SortType.VALUE_DESC;
					reverseByValue();
				} else {
					sortType = SortType.VALUE_ASC;
					sortByValue();
				}
			}
		}
	}
}
