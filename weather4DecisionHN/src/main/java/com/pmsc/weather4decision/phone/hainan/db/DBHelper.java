package com.pmsc.weather4decision.phone.hainan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Depiction: 数据库创建助手
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月7日 上午1:45:52
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
class DBHelper extends SQLiteOpenHelper {
	
	private final static String NAME    = "citys.db";
	private final static int    VERSION = 1;
	
	private static DBHelper     helper  = null;
	
	public synchronized static DBHelper getInstance(Context context) {
		if (helper == null) {
			helper = new DBHelper(context);
		}
		return helper;
	}
	
	public DBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getSql());
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DBTable.TABLE);
		onCreate(db);
	}
	
	private String getSql() {
		return "create table " + DBTable.TABLE //
		        + "(" //
		        + DBTable._ID //
		        + " integer PRIMARY KEY AUTOINCREMENT," //
		        + DBTable.CITY.AREAID //
		        + " text," //
		        + DBTable.CITY.NAMEEN //
		        + " text," //
		        + DBTable.CITY.NAMECN //
		        + " text," //
		        + DBTable.CITY.DISTRICTEN //
		        + " text," //
		        + DBTable.CITY.DISTRICTCN //
		        + " text," //
		        + DBTable.CITY.PROVEN //
		        + " text," //
		        + DBTable.CITY.PROVCN //
		        + " text," //
		        + DBTable.CITY.NATIONEN //
		        + " text," //
		        + DBTable.CITY.NATIONCN //
		        + " text)"; //
	}
}
