package com.pmsc.weather4decision.phone.hainan.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.lib.data.JsonMap;
import com.pmsc.weather4decision.phone.hainan.db.DBTable.CITY;


/**
 * Depiction: 数据库操作工具类
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年12月7日 上午1:46:11
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public final class DBDao {
	final static String  TAG = "DBDao";
	private Context      context;
	private static DBDao dao = null;
	
	private DBDao(Context context) {
		this.context = context;
	}
	
	public synchronized static DBDao getInstance(Context context) {
		if (dao == null) {
			dao = new DBDao(context);
		}
		return dao;
	}
	
	public synchronized SQLiteDatabase getDB() {
		DBHelper helper = DBHelper.getInstance(context);
		if (helper == null) {
			Log.e(getClass().getSimpleName(), "helper is null");
		}
		
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
		} catch (Exception e) {
		}
		
		return db;
	}
	
	private synchronized void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}
	
	private synchronized void checkLocked(SQLiteDatabase db) {
		while (db.isDbLockedByCurrentThread()) {
			Log.e(getClass().getSimpleName(), "db is locked by other or current threads!");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized boolean isEmpty() {
		SQLiteDatabase db = getDB();
		checkLocked(db);
		String sql = "SELECT * FROM " + DBTable.TABLE;
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor != null ? cursor.getCount() : 0;
		closeCursor(cursor);
		Log.e(getClass().getSimpleName(), "isEmpty()-->count is -->" + count);
		return count < 1;
	}
	
	public synchronized void addCityData(List<JsonMap> dataList) {
		if (dataList == null || dataList.size() == 0) {
			Log.e(getClass().getSimpleName(), "the data list is empty.");
			return;
		}
		
		SQLiteDatabase db = getDB();
		Log.e(getClass().getSimpleName(), "check db locked.");
		checkLocked(db);
		Log.e(getClass().getSimpleName(), "db is not locked,begin transaction");
		db.beginTransaction();
		Log.e(getClass().getSimpleName(), "begin insert into data to city database.");
		for (int i = 0, size = dataList.size(); i < size; i++) {
			JsonMap data = dataList.get(i);
			db.insert(DBTable.TABLE, "", getContentValues(data));
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public synchronized void addCityData(JsonMap data) {
		SQLiteDatabase db = getDB();
		checkLocked(db);
		db.insert(DBTable.TABLE, "", getContentValues(data));
	}
	
	private synchronized ContentValues getContentValues(JsonMap data) {
		ContentValues values = new ContentValues();
		values.put(CITY.AREAID, data.getString("areaid"));
		values.put(CITY.DISTRICTCN, data.getString("districtcn"));
		values.put(CITY.DISTRICTEN, data.getString("districten"));
		values.put(CITY.NAMECN, data.getString("namecn"));
		values.put(CITY.NAMEEN, data.getString("nameen"));
		values.put(CITY.PROVCN, data.getString("provcn"));
		values.put(CITY.PROVEN, data.getString("proven"));
		values.put(CITY.NATIONCN, data.getString("nationcn"));
		values.put(CITY.NATIONEN, data.getString("nationen"));
		return values;
	}
	
	/**
	 * 按照市级行政区域查询
	 * 
	 * @param district
	 *            市级
	 * @return
	 */
	public synchronized List<JsonMap> queryByDistrict(String district) {
		List<JsonMap> list = new ArrayList<JsonMap>();
		String sql = "SELECT * FROM " + DBTable.TABLE + " WHERE " + DBTable.CITY.DISTRICTCN + "='" + district + "'";
		SQLiteDatabase db = getDB();
		checkLocked(db);
		db.beginTransaction();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0, count = cursor.getCount(); i < count; i++) {
				list.add(getCity(cursor));
				cursor.moveToNext();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		closeCursor(cursor);
		
		return list;
	}
	
	/**
	 * 按照县级关键字产讯
	 * 
	 * @param xianqu
	 *            县区
	 * @return
	 */
	public synchronized List<JsonMap> queryByXianQu(String xianqu) {
		List<JsonMap> list = new ArrayList<JsonMap>();
		String sql = "select * from " + DBTable.TABLE + " where " + DBTable.CITY.NAMECN + " = '" + xianqu + "'";
		SQLiteDatabase db = getDB();
		checkLocked(db);
		db.beginTransaction();
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor != null ? cursor.getCount() : 0;
		if (count > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < count; i++) {
				list.add(getCity(cursor));
				cursor.moveToNext();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		closeCursor(cursor);
		
		return list;
	}
	
	/**
	 * 按照省级查询
	 * 
	 * @param province
	 *            省名字
	 * @return
	 */
	public synchronized List<JsonMap> queryByProvince(String province) {
		List<JsonMap> list = new ArrayList<JsonMap>();
		String sql = "select * from " + DBTable.TABLE + " where " + DBTable.CITY.PROVCN + " = '" + province + "'";
		SQLiteDatabase db = getDB();
		checkLocked(db);
		db.beginTransaction();
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor != null ? cursor.getCount() : 0;
		if (count > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < count; i++) {
				list.add(getCity(cursor));
				cursor.moveToNext();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		closeCursor(cursor);
		
		return list;
	}
	
	private JsonMap getCity(Cursor cursor) {
		String city_id = cursor.getString(cursor.getColumnIndex(DBTable.CITY.AREAID));
		String district = cursor.getString(cursor.getColumnIndex(DBTable.CITY.DISTRICTCN));
		String xianqu = cursor.getString(cursor.getColumnIndex(DBTable.CITY.NAMECN));
		String province = cursor.getString(cursor.getColumnIndex(DBTable.CITY.PROVCN));
		
		JsonMap bean = new JsonMap();
		bean.put("province", province);
		bean.put("xianqu", xianqu);
		bean.put("district", district);
		bean.put("city_id", city_id);
		
		return bean;
	}
}
