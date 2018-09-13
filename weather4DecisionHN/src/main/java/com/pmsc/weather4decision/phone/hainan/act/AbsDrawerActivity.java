package com.pmsc.weather4decision.phone.hainan.act;

import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.CONST;
import com.android.lib.data.JsonMap;
import com.android.lib.util.LogUtil;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.adapter.ExDrawerAdapter;
import com.pmsc.weather4decision.phone.hainan.util.PreferUtil;
import com.pmsc.weather4decision.phone.hainan.util.Utils;
import com.umeng.analytics.MobclickAgent;


/**
 * Depiction: 带有侧滑导航的基类
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月13日 下午4:33:00
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class AbsDrawerActivity extends BaseActivity implements OnGroupClickListener, OnChildClickListener {
	protected RelativeLayout       titleBar;
	protected Button               leftButton;
	protected Button               rightButton;
	protected TextView             titleView;
	protected DrawerLayout         drawerLayout;
	private boolean isCloseDrawer = false;
	private ExpandableListView     drawerListView;
	private ExDrawerAdapter        adapter;
	
	private FrameLayout            rootLayout;
	private boolean                isNeedFinish;
	
	public final static String     IS_FROM_MAIN    = "is_from_main";
	public final static String     TITLE_KEY       = "title_key";
	public final static String     CHILD_INDEX_KEY = "child_index_key";
	private final static String    CHANNEL_DATA    = "channel_data";
	protected static List<JsonMap> allChannelDataList;                 //整个栏目json数组数据
	protected JsonMap              channelData;
	private long mExitTime;//记录点击完返回按钮后的long型时间                   //某个一级栏目json对象
	                                                                    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String channelJson = getIntent().getStringExtra(CHANNEL_DATA);
		channelData = JsonMap.parseJson(channelJson);
		
		super.setContentView(R.layout.activity_abs_drawer);
		titleBar = (RelativeLayout) super.findViewById(R.id.title_bar);
		leftButton = (Button) super.findViewById(R.id.left_btn);
		rightButton = (Button) super.findViewById(R.id.right_btn);
		titleView = (TextView) super.findViewById(R.id.title_view);
		rootLayout = (FrameLayout) super.findViewById(R.id.content_layout);
		drawerLayout = (DrawerLayout) super.findViewById(R.id.drawer_layout);
		drawerListView = (ExpandableListView) super.findViewById(R.id.listview);
		drawerListView.setGroupIndicator(null);
		
		int parentId = channelData != null ? channelData.getInt("parent_id") : -1;
		int mode = parentId == 0 ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
		drawerLayout.setDrawerLockMode(mode);
		
		if (this instanceof MainActivity) {
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		} else {
			String title = getIntent().getStringExtra(TITLE_KEY);
			if (!TextUtils.isEmpty(title)) {
				setTitle(title);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (this instanceof MainActivity) {
			PreferUtil.saveMenuCurrentParentIndex(0);
			PreferUtil.saveMenuCurrentChildIndex(-1);
			isNeedFinish = false;
		} else {
			isNeedFinish = true;
		}
		try {
			initDrawerList();
        } catch (Exception e) {
        	Log.e(this.getClass().getSimpleName(), "initDrawerList()-->"+e.toString());
        }
	}
	
	/**
	 * 初始化侧滑栏数据和点击事件
	 */
	private void initDrawerList() {
		adapter = new ExDrawerAdapter(allChannelDataList);
		adapter.setSelected(PreferUtil.getMenuCurrentChildIndex(), -1);
		drawerListView.setAdapter(adapter);
		drawerListView.setOnGroupClickListener(this);
		drawerListView.setOnChildClickListener(this);
		drawerListView.expandGroup(PreferUtil.getMenuCurrentParentIndex());
		adapter.setSelected(PreferUtil.getMenuCurrentParentIndex(), PreferUtil.getMenuCurrentChildIndex());
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		LogUtil.e(this, "groupPosition-->" + groupPosition);
		if (groupPosition != PreferUtil.getMenuCurrentParentIndex()) {
			boolean hasChild = adapter.getChild(groupPosition, 0) != null;
			if (!hasChild) {
				//没有子栏目，直接打开相应页面
				PreferUtil.saveMenuCurrentParentIndex(groupPosition);
				PreferUtil.saveMenuCurrentChildIndex(-1);
				closeDrawer();
				if (groupPosition == 0) {
					//切换到了主页，关闭其它activity
					finish();
				} else {
					//所打开的新界面不是主界面
					delayOpenChannelView(groupPosition, -1, false);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		LogUtil.e(this, "groupPosition-->" + groupPosition + ", childPosition-->" + childPosition);
		
		if (groupPosition == PreferUtil.getMenuCurrentParentIndex() && childPosition == PreferUtil.getMenuCurrentChildIndex()) {
			return false;
		}
		
		closeDrawer();
		
		delayOpenChannelView(groupPosition, childPosition, false);
		
		return false;
	}
	
	//延迟打开界面，解决左侧导航栏抖动的问题
	private void delayOpenChannelView(final int parent, final int child, final boolean isFromMain) {
		post(new Runnable() {
			@Override
			public void run() {
				openChannelView(parent, child, isFromMain);
			}
		}, 250);
	}
	
	protected String time_7; //七天预报的发布时间,全长
	                         
	protected void openChannelView(int parent, int child, boolean isFromMain) {
		PreferUtil.saveMenuCurrentParentIndex(parent);
		PreferUtil.saveMenuCurrentChildIndex(child);
		
		boolean hasChild = adapter.getChild(parent, child) != null;
		Class<?> activity = null;
		
		JsonMap item = adapter.getGroup(parent);
		String id = item.getString("id");
		if (!hasChild) {
			//直接打开的是一级栏目
			String columnType = item.getString("columnType");
			activity = Utils.getActivity(columnType);
			if (activity == null) {
				showToast(R.string.cant_deal);
				return;
			}
		} else {
			//打开的是二级栏目
			if (TextUtils.equals(id, "613")) {//实况资料
				activity = ShawnRainActivity.class;
			}else if (TextUtils.equals(id, "649")) {//全省预报
				activity = ProvinceActivity.class;
			}else {
				activity = ListNavigationActivity.class;
			}
		}
		
		String title = item.getString("title");
		LogUtil.e(this, title + " has child-->" + hasChild);
		
		//友盟统计栏目打开次数
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("title",title);
		MobclickAgent.onEvent(this, "open_channel", map);
		
		Bundle bundle = new Bundle();
		bundle.putString("columnId", id);
		bundle.putString("time_7", time_7);
		if (hasChild) {
			bundle.putBoolean(IS_FROM_MAIN, isFromMain);
		}
		bundle.putString(TITLE_KEY, title);
		bundle.putInt(CHILD_INDEX_KEY, child);
		bundle.putString(CHANNEL_DATA, item.toString());
		openActivity(activity, bundle);
		
		if (isNeedFinish) {
			finish();
		}
	}
	
	public void enterPdfActivity(String pdfUrl) {
		Bundle bundle = new Bundle();
		bundle.putString(CONST.WEB_URL, pdfUrl);
		openActivity(ShawnPDFActivity.class, bundle);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		titleView.setText(title);
		super.setTitle(title);
	}
	
	@Override
	public void setTitle(int titleId) {
		titleView.setText(titleId);
		super.setTitle(titleId);
	}
	
	@Override
	public View findViewById(int id) {
		if (rootLayout != null) {
			return rootLayout.findViewById(id);
		} else {
			return super.findViewById(id);
		}
	}
	
	@Override
	public void setContentView(int layoutResID) {
		if (rootLayout != null) {
			rootLayout.addView(LayoutInflater.from(getApplicationContext()).inflate(layoutResID, null));
		} else {
			super.setContentView(layoutResID);
		}
	}
	
	@Override
	public void setContentView(View view) {
		if (rootLayout != null) {
			rootLayout.addView(view);
		} else {
			super.setContentView(view);
		}
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		if (rootLayout != null) {
			rootLayout.addView(view, params);
		} else {
			super.setContentView(view, params);
		}
	}
	
	public void closeDrawer() {
		drawerLayout.closeDrawers();
		isCloseDrawer = false;
	}
	
	public void showDrawer() {
		drawerLayout.openDrawer(Gravity.START);
		isCloseDrawer = true;
	}
	
	public void onLeftButtonAction(View view) {
		if (drawerLayout.isDrawerOpen(Gravity.START)) {
			closeDrawer();
		} else {
			showDrawer();
		}
	}
	
	public void onRightButtonAction(View view) {
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isCloseDrawer) {
				closeDrawer();
			}else if (this instanceof MainActivity) {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					showToast(R.string.press_again_quit_tip);
					mExitTime = System.currentTimeMillis();
				} else {
					finish();
				}
			}else {
				finish();
			}
		}
		return false;
	}
	
}
