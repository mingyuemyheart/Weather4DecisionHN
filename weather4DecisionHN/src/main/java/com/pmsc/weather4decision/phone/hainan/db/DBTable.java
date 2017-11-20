package com.pmsc.weather4decision.phone.hainan.db;

import android.provider.BaseColumns;


/**
 * Depiction: 数据库表结构定义
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月7日 上午1:46:28
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
final class DBTable implements BaseColumns {
	public final static String TABLE = "citys";
	
	public final static class CITY {
		public final static String AREAID     = "areaid";
		public final static String NAMEEN     = "nameen";
		public final static String NAMECN     = "namecn";
		public final static String DISTRICTEN = "districten";
		public final static String DISTRICTCN = "districtcn";
		public final static String PROVEN     = "proven";
		public final static String PROVCN     = "provcn";
		public final static String NATIONEN   = "nationen";
		public final static String NATIONCN   = "nationcn";
	}
	
	private DBTable() {
	}
	
}
