package com.pmsc.weather4decision.phone.hainan.dto;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps.model.LatLng;

/**
 * 装载请求接口返回的数据
 * 
 * @author shawn_sun
 */
public class WindData {

	public int width = 0;
	public int height = 0;
	public double x0 = 0;
	public double y0 = 0;
	public double x1 = 0;
	public double y1 = 0;
	public String filetime = null;
	public List<WindDto> dataList = new ArrayList<WindDto>();
	public LatLng latLngStart = null;
	public LatLng latLngEnd = null;
}
