package com.pmsc.weather4decision.phone.hainan.db;

import java.util.ArrayList;
import java.util.List;

import com.android.lib.data.JsonMap;
import com.android.lib.util.AssetFile;
import com.android.lib.util.LogUtil;
import com.pmsc.weather4decision.phone.hainan.HNApp;
import com.pmsc.weather4decision.phone.hainan.db.DBTable.CITY;


/**
 * Depiction: 城市解析器，从csv文件中解析
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月7日 上午2:22:27
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class ParseCityTask implements Runnable {
	private static HNApp context = HNApp.getInstance();
	
	public ParseCityTask() {
	}
	
	@Override
	public void run() {
		List<JsonMap> list = new ArrayList<JsonMap>();
		String content = new AssetFile(context).readAsString("city_station.csv");
		String[] datas = content.split("\n");
		for (String line : datas) {
			String[] infos = line.split(",");
			JsonMap map = new JsonMap();
			map.put(CITY.AREAID, infos[0]);
			map.put(CITY.NAMEEN, infos[1]);
			map.put(CITY.NAMECN, infos[2]);
			map.put(CITY.DISTRICTEN, infos[3]);
			map.put(CITY.DISTRICTCN, infos[4]);
			map.put(CITY.PROVEN, infos[5]);
			map.put(CITY.PROVCN, infos[6]);
			map.put(CITY.NATIONEN, infos[7]);
			map.put(CITY.NATIONCN, infos[8]);
			list.add(map);
		}
		DBDao dao = DBDao.getInstance(context);
		if (dao.isEmpty()) {
			dao.addCityData(list);
			LogUtil.e(this, "update city info,the size -->" + (list != null ? list.size() : 0));
		} else {
			LogUtil.e(this, "had city info");
		}
	}
}
