package com.pmsc.weather4decision.phone.hainan.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.dto.ShawnRainDto;
import com.pmsc.weather4decision.phone.hainan.view.RainView;
import com.pmsc.weather4decision.phone.hainan.view.TemperatureView;
import com.pmsc.weather4decision.phone.hainan.view.WindView;

import java.util.ArrayList;
import java.util.List;

public class StationDetailRainFragment extends Fragment{
	
	private LinearLayout llContainer1 = null;
	private TextView tvPrompt = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.station_detail_rain_fragment, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	private void initWidget(View view) {
		llContainer1 = (LinearLayout) view.findViewById(R.id.llContainer1);
		tvPrompt = (TextView) view.findViewById(R.id.tvPrompt);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		int index = getArguments().getInt("index", 0);
		
		List<ShawnRainDto> tempList = new ArrayList<>();
		tempList.clear();
		tempList.addAll(getArguments().<ShawnRainDto>getParcelableArrayList("dataList"));
		List<ShawnRainDto> dataList = new ArrayList<>();
		dataList.clear();
		for (int i = 0; i < tempList.size(); i++) {
			ShawnRainDto dto = tempList.get(i);
			if (index == 0) {
				if (dto.factRain != 999999) {
					dataList.add(dto);
				}
			}else if (index == 1) {
				if (dto.factTemp != 999999) {
					dataList.add(dto);
				}
			}else if (index == 2) {
				if (dto.factWind != 999999) {
					dataList.add(dto);
				}
			}
		}
		if (dataList.size() > 0) {
			if (index == 0) {
				RainView rainView = new RainView(getActivity());
				rainView.setData(dataList);
				llContainer1.removeAllViews();
				int viewWidth = 0;
				if (dataList.size() <= 25) {
					viewWidth = dm.widthPixels*2;
				}else {
					viewWidth = dm.widthPixels*4;
				}
				llContainer1.addView(rainView, viewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
			}else if (index == 1) {
				TemperatureView rainView = new TemperatureView(getActivity());
				rainView.setData(dataList);
				llContainer1.removeAllViews();
				int viewWidth = 0;
				if (dataList.size() <= 25) {
					viewWidth = dm.widthPixels*2;
				}else {
					viewWidth = dm.widthPixels*4;
				}
				llContainer1.addView(rainView, viewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
			}else if (index == 2) {
				WindView rainView = new WindView(getActivity());
				rainView.setData(dataList);
				llContainer1.removeAllViews();
				int viewWidth = 0;
				if (dataList.size() <= 25) {
					viewWidth = dm.widthPixels*2;
				}else {
					viewWidth = dm.widthPixels*4;
				}
				llContainer1.addView(rainView, viewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
			}
			
		}else {
			tvPrompt.setVisibility(View.VISIBLE);
		}
	}
	
}
