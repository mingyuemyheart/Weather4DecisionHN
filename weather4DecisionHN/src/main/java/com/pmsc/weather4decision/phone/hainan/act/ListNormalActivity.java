package com.pmsc.weather4decision.phone.hainan.act;

import com.pmsc.weather4decision.phone.hainan.fragment.ListDocumentFragment;
import com.pmsc.weather4decision.phone.hainan.util.StatisticUtil;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;


/**
 * Depiction: 普通的列表界面,主要用来显示:重大快报，重要专报等数据
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
public class ListNormalActivity extends AbsDrawerActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout content = new FrameLayout(getApplicationContext());
		content.setId(1000);
		setContentView(content);
		
 		ListDocumentFragment fragment = ListDocumentFragment.newInstance(0, channelData);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction tran = fm.beginTransaction();
		tran.replace(content.getId(), fragment, "list_normal");
		tran.commit();

		if (getIntent().hasExtra("columnId")) {
			String columnId = getIntent().getStringExtra("columnId");
			StatisticUtil.statisticClickCount(columnId);
		}
	}
}
