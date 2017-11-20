package com.android.lib.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.lib.data.JsonMap;
import com.android.lib.http.OnHttpListener;
import com.umeng.analytics.MobclickAgent;


/**
 * Depiction:
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014-4-8 下午4:16:46
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class BaseFragment extends Fragment implements OnHttpListener {
	protected boolean isFirst = true;
	
	public BaseFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public BaseActivity getBaseActivity() {
		BaseActivity act = (BaseActivity) getActivity();
		act.setOnHttpListener(this);
		return act;
	}
	
	public void initData() {
		if (isFirst) {
			loadData(null);
			isFirst = false;
		}
	}
	
	public abstract void loadData(Object obj);
	
	public void onStart(String taskId) {
		BaseActivity act = (BaseActivity) getActivity();
		if (act.isShowDialog()) {
			getBaseActivity().showLoadingDialog("加载中....");
		}
	}
	
	public void onFinish(JsonMap datas, String response, String taskId) {
	}
	
	protected int getCount() {
		return 0;
	}
	
	public void closePop() {
	}
	
	public boolean isOpenedPop() {
		return false;
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName());
		MobclickAgent.onResume(getActivity());
	}
	
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName());
		MobclickAgent.onPause(getActivity());
	}
}
