package com.pmsc.weather4decision.phone.hainan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.pmsc.weather4decision.phone.hainan.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

public class CommonUtil {

	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static float dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (float) (dpValue * scale);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static float px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (float) (pxValue / scale);  
    } 
    
    /**
	 * 解决ScrollView与ListView共存的问题
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); 
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
		listView.setLayoutParams(params);
	}
	
	/**
	 * 解决ScrollView与GridView共存的问题
	 */
	public static void setGridViewHeightBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		
		Class<GridView> tempGridView = GridView.class; // 获得gridview这个类的class
		int column = -1;
        try {
 
            Field field = tempGridView.getDeclaredField("mRequestedNumColumns"); // 获得申明的字段
            field.setAccessible(true); // 设置访问权限
            column = Integer.valueOf(field.get(gridView).toString()); // 获取字段的值
        } catch (Exception e1) {
        }

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i+=column) {
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0); 
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (gridView.getVerticalSpacing() * (listAdapter.getCount()/column - 1) + 30);
		((MarginLayoutParams) params).setMargins(15, 15, 15, 0);
		gridView.setLayoutParams(params);
	}
	
	/**
	 * 根据当前时间获取日期，格式为MM/dd
	 * @param i (+1为后一天，-1为前一天，0表示当天)
	 * @return
	 */
	public static String getDate(int i) {
		String date = null;
		
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day+i);
		
		if (c.get(Calendar.MONTH) == 0) {
			date = "01";
		}else if (c.get(Calendar.MONTH) == 1) {
			date = "02";
		}else if (c.get(Calendar.MONTH) == 2) {
			date = "03";
		}else if (c.get(Calendar.MONTH) == 3) {
			date = "04";
		}else if (c.get(Calendar.MONTH) == 4) {
			date = "05";
		}else if (c.get(Calendar.MONTH) == 5) {
			date = "06";
		}else if (c.get(Calendar.MONTH) == 6) {
			date = "07";
		}else if (c.get(Calendar.MONTH) == 7) {
			date = "08";
		}else if (c.get(Calendar.MONTH) == 8) {
			date = "09";
		}else if (c.get(Calendar.MONTH) == 9) {
			date = "10";
		}else if (c.get(Calendar.MONTH) == 10) {
			date = "11";
		}else if (c.get(Calendar.MONTH) == 11) {
			date = "12";
		}
		
		return date+"/"+c.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 根据当前时间获取星期几
	 * @param context
	 * @param i (+1为后一天，-1为前一天，0表示当天)
	 * @return
	 */
	public static String getWeek(Context context, int i) {
		String week = null;
		
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day+i);
		
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			week = "周日";
		}else if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			week = "周一";
		}else if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			week = "周二";
		}else if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			week = "周三";
		}else if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			week = "周四";
		}else if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			week = "周五";
		}else if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			week = "周六";
		}
		
		return week;
	}
	
	/**
	 * 判断白天或晚上对应的天气现象
	 * @param fa 白天天气现象编号
	 * @param fb 晚上天气现象编号
	 * @return
	 */
	public static int getPheCode(int fa, int fb) {
		int pheCode = 0;
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
		
		try {
			long currentTime = new Date().getTime();//当前时间
			long eight = sdf1.parse("08:00").getTime();//早上08:00
			long twenty = sdf1.parse("20:00").getTime();//晚上20:00
			
			if (currentTime >= eight && currentTime <= twenty) {
				pheCode = fa;
			}else {
				pheCode = fb;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return pheCode;
	}
	
	/**
	 * 从Assets中读取图片
	 */
	public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * 获取圆角图片
	 * @param bitmap
	 * @param corner
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int corner) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rectF, corner, corner, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			canvas.drawBitmap(bitmap, src, rect, paint);
			bitmap.recycle();
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}
	
	/**
	 * 隐藏虚拟键盘
	 * @param editText 输入框
	 * @param context 上下文
	 */
	public static void hideInputSoft(EditText editText, Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
	
	/**
     * 获取网落图片资源 
     * @param url
     * @return
     */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 转换图片成六边形
	 * @return
	 */
	public static Bitmap getHexagonShape(Bitmap bitmap) {
		int targetWidth = bitmap.getWidth();
		int targetHeight = bitmap.getHeight();
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

		float radius = targetHeight / 2;
		float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
		float centerX = targetWidth / 2;
		float centerY = targetHeight / 2;
		
		Canvas canvas = new Canvas(targetBitmap);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG));
		Path path = new Path();
		path.moveTo(centerX, centerY + radius);
		path.lineTo(centerX - triangleHeight, centerY + radius / 2);
		path.lineTo(centerX - triangleHeight, centerY - radius / 2);
		path.lineTo(centerX, centerY - radius);
		path.lineTo(centerX + triangleHeight, centerY - radius / 2);
		path.lineTo(centerX + triangleHeight, centerY + radius / 2);
		path.moveTo(centerX, centerY + radius);
		canvas.clipPath(path);
		canvas.drawBitmap(bitmap, new Rect(0, 0, targetWidth, targetHeight), new Rect(0, 0, targetWidth, targetHeight), null);
		return targetBitmap;
	}

	/**
	 * 把本地的drawable转换成六边形图片
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
	
	/**
	 * 根据颜色值判断颜色
	 * @param colorType
	 * @param val
	 * @return
	 */
	public static int colorForValue(String colorType, float val) {
		int color = 0;
		if (TextUtils.equals(colorType, "jiangshui")) {
			if (val >= 0 && val < 1) {
				return 0xff2EAD06;
			} else if (val >= 1 && val < 10) {
				return 0xff000000;
			} else if (val >= 10 && val < 25) {
				return 0xff0901EC;
			} else if (val >= 25 && val < 50) {
				return 0xffC804C8;
			} else if (val >= 50) {
				return 0xffC50724;
			}
		} else if (TextUtils.equals(colorType, "wendu")) {
			if (val < -30) {
				return 0xff201885;
			} else if (val >= -30 && val < -20) {
				return 0xff114AD9;
			} else if (val >= -20 && val < -10) {
				return 0xff4DB4F7;
			} else if (val >= -10 && val < 0) {
				return 0xffD1F8F3;
			} else if (val >= 0 && val < 10) {
				return 0xffF9F2BB;
			} else if (val >= 10 && val < 20) {
				return 0xffF9DE45;
			} else if (val >= 20 && val < 30) {
				return 0xffFFA800;
			} else if (val >= 30 && val < 40) {
				return 0xffFF6D00;
			} else if (val >= 40 && val < 50) {
				return 0xffE60000;
			} else if (val >= 50) {
				return 0xff9E0001;
			}
		} else if (TextUtils.equals(colorType, "bianwen")) {
			if (val > 0) {
				return 0xffFF0000;
			} else if (val == 0) {
				return 0xff000000;
			} else {
				return 0xff0000FF;
			}
		} else if (TextUtils.equals(colorType, "radar")) {
			return 0xffff00ff;
		} else if (TextUtils.equals(colorType, "shidu")) {
			if (val >= 0 && val < 10) {
				return 0xffFF6000;
			} else if (val >= 10 && val < 30) {
				return 0xffFEA51A;
			} else if (val >= 30 && val < 50) {
				return 0xffFFFC9F;
			} else if (val >= 50) {
				return 0xffD6E6DA;
			}
		}

		return color;

	}
	
	/**
	 * 读取assets下文件
	 * @param fileName
	 * @return
	 */
	public static String getFromAssets(Context context, String fileName) {
		String Result = "";
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}
	
	/**
	 * 回执区域
	 * @param context
	 * @param aMap
	 */
	public static void drawDistrict(Context context, AMap aMap, String cityName, int color) {
		if (aMap == null) {
			return;
		}
		String result = CommonUtil.getFromAssets(context, "hai_nan.geo.json");
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject obj = new JSONObject(result);
				JSONArray array = obj.getJSONArray("features");
				for (int i = 0; i < array.length(); i++) {
					JSONObject itemObj = array.getJSONObject(i);
					
					JSONObject properties = itemObj.getJSONObject("properties");
					String name = properties.getString("name");
					if (TextUtils.equals(cityName, name)) {
//						JSONArray cp = properties.getJSONArray("cp");
//						for (int m = 0; m < cp.length(); m++) {
//							double lat = cp.getDouble(1);
//							double lng = cp.getDouble(0);
//							
//							LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//							View view = inflater.inflate(R.layout.rainfall_fact_marker_view2, null);
//							TextView tvName = (TextView) view.findViewById(R.id.tvName);
//							if (!TextUtils.isEmpty(name)) {
//								tvName.setText(name);
//							}
//							MarkerOptions options = new MarkerOptions();
//							options.anchor(0.5f, 0.5f);
//							options.position(new LatLng(lat, lng));
//							options.icon(BitmapDescriptorFactory.fromView(view));
//							aMap.addMarker(options);
//						}
						
						JSONObject geometry = itemObj.getJSONObject("geometry");
						JSONArray coordinates = geometry.getJSONArray("coordinates");
						for (int m = 0; m < coordinates.length(); m++) {
							JSONArray array2 = coordinates.getJSONArray(m);
							PolygonOptions polylineOption = new PolygonOptions();
							polylineOption.fillColor(color);
							polylineOption.strokeColor(color).strokeWidth(6);
							for (int j = 0; j < array2.length(); j++) {
								JSONArray itemArray = array2.getJSONArray(j);
								double lng = itemArray.getDouble(0);
								double lat = itemArray.getDouble(1);
								polylineOption.add(new LatLng(lat, lng));
							}
							aMap.addPolygon(polylineOption);
						}
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 回执区域
	 * @param context
	 * @param aMap
	 */
	public static void drawAllDistrict(Context context, AMap aMap, List<Polyline> polyLineList) {
		if (aMap == null) {
			return;
		}
		String result = CommonUtil.getFromAssets(context, "hnGeo.json");
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject obj = new JSONObject(result);
				if (!obj.isNull("polyline")) {
					String[] polylines = obj.getString("polyline").split("\\|");
					for (int i = 0; i < polylines.length; i++) {
						PolylineOptions polylineOption = new PolylineOptions();
						polylineOption.width(2).color(0xff999999);
						String[] array = polylines[i].split(";");
						for (int j = 0; j < array.length; j++) {
							String[] latLng = array[j].split(",");
							double lng = Double.valueOf(latLng[0]);
							double lat = Double.valueOf(latLng[1]);
							polylineOption.add(new LatLng(lat, lng));
                            polylineOption.zIndex(1000);
						}
						Polyline polyLine = aMap.addPolyline(polylineOption);
						polyLineList.add(polyLine);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**  
	 * 截取webView快照(webView加载的整个内容的大小)  
	 * @param webView  
	 * @return  
	 */  
	@SuppressWarnings("deprecation")
	public static Bitmap captureWebView(WebView webView){  
	    Picture snapShot = webView.capturePicture();  
	    Bitmap bitmap = Bitmap.createBitmap(snapShot.getWidth(),snapShot.getHeight(), Bitmap.Config.ARGB_8888);  
	    Canvas canvas = new Canvas(bitmap);  
	    snapShot.draw(canvas);  
	    clearCanvas(canvas);
	    return bitmap;  
	}  
	
	/**
	 * 截取scrollView
	 * @param scrollView
	 * @return
	 */
	public static Bitmap captureScrollView(ScrollView scrollView) {  
        int height = 0;  
        // 获取scrollview实际高度  
        for (int i = 0; i < scrollView.getChildCount(); i++) {  
        	height += scrollView.getChildAt(i).getHeight();  
        	scrollView.getChildAt(i).setBackgroundColor(0xffffff);  
        }  
        // 创建对应大小的bitmap  
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), height, Config.ARGB_8888);  
        Canvas canvas = new Canvas(bitmap);  
        scrollView.draw(canvas);  
        clearCanvas(canvas);
        return bitmap;  
    }  
	
	/**
	 * 截取listview
	 * @param listView
	 * @return
	 */
    public static Bitmap captureListView(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        int count = listAdapter.getCount();
        if (count > 30) {
        	count = 30;
		}
        List<View> childViews = new ArrayList<View>(count);
        int totalHeight = 0;
        for(int i = 0; i < count; i++){
        	View itemView = listAdapter.getView(i, null, listView);
        	itemView.measure(0, 0); 
			childViews.add(itemView);
			totalHeight += itemView.getMeasuredHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(listView.getWidth(), totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int yPos = 0;
        //把每个ItemView生成图片，并画到背景画布上
        for(int j = 0; j < childViews.size(); j++){
            View itemView = childViews.get(j);
            int childHeight = itemView.getMeasuredHeight();
            itemView.layout(0, 0, listView.getWidth(), childHeight);
            itemView.buildDrawingCache();
            Bitmap itemBitmap = itemView.getDrawingCache();
            if(itemBitmap!=null){
                canvas.drawBitmap(itemBitmap, 0, yPos, null);
            }
            yPos = childHeight +yPos;
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        clearCanvas(canvas);
        return bitmap;
    }
    
    /**
     * 截屏自定义view
     * @param view
     * @return
     */
    public static Bitmap captureMyView(View view) {
		if (view == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		bitmap = view.getDrawingCache();
		clearCanvas(canvas);
		return bitmap;
	}
    
	/**
     * 截屏,可是区域
     * @return
     */
	public static Bitmap captureView(View view) {
		if (view == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		clearCanvas(canvas);
		return bitmap;
	}
	
	/**
	 * 合成图片
	 * @param bitmap1
	 * @param bitmap2
	 * @param isCover 判断是否为覆盖合成
	 * @return
	 */
    @SuppressWarnings("deprecation")
	public static Bitmap mergeBitmap(Context context, Bitmap bitmap1, Bitmap bitmap2, boolean isCover) {
    	if (bitmap1 == null || bitmap2 == null) {
			return null;
		}
    	
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        bitmap1 = Bitmap.createScaledBitmap(bitmap1, width, width*bitmap1.getHeight()/bitmap1.getWidth(), true);
    	bitmap2 = Bitmap.createScaledBitmap(bitmap2, width, width*bitmap2.getHeight()/bitmap2.getWidth(), true);
    	
    	Bitmap bitmap = null;
        Canvas canvas = null;
        if (isCover) {
        	int height = bitmap1.getHeight();
        	if (bitmap1.getHeight() > bitmap2.getHeight()) {
				height = bitmap1.getHeight();
			}else {
				height = bitmap2.getHeight();
			}
        	bitmap = Bitmap.createBitmap(bitmap1.getWidth(), height, Config.ARGB_8888);
        	canvas = new Canvas(bitmap);
        	canvas.drawBitmap(bitmap1, 0, 0 , null);
        	canvas.drawBitmap(bitmap2, 0, 0, null);
		}else {
			bitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight()+bitmap2.getHeight(), Config.ARGB_8888);
			canvas = new Canvas(bitmap);
        	canvas.drawBitmap(bitmap1, 0, 0 , null);
        	canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);
		}
        clearCanvas(canvas);
        return bitmap;
    }
    
    public static void clearBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
			System.gc();
		}
	}
    
    public static void clearCanvas(Canvas canvas) {
    	if (canvas != null) {
			canvas = null;
		}
    }

	/**
	 * 分享功能
	 * @param activity
	 */
	public static void share(final Activity activity, final Bitmap bitmap) {
		ShareAction panelAction = new ShareAction(activity);
		panelAction.setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE);
		panelAction.setShareboardclickCallback(new ShareBoardlistener() {
			@Override
			public void onclick(SnsPlatform arg0, SHARE_MEDIA arg1) {
				ShareAction shareAction = new ShareAction(activity);
				shareAction.setPlatform(arg1);
				if (bitmap != null) {
					shareAction.withMedia(new UMImage(activity, bitmap));
				}
				shareAction.share();
			}
		});
		panelAction.open();
	}

	/**
	 * 分享功能
	 * @param activity
	 */
	public static void share(final Activity activity, final String title, final String content, final String imgUrl, final String url) {
		ShareAction panelAction = new ShareAction(activity);
		panelAction.setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE);
		panelAction.setShareboardclickCallback(new ShareBoardlistener() {
			@Override
			public void onclick(SnsPlatform arg0, SHARE_MEDIA arg1) {
				ShareAction sAction = new ShareAction(activity);
				sAction.setPlatform(arg1);
				UMWeb web = new UMWeb(url);
				web.setTitle(title);//标题
				if (!TextUtils.isEmpty(imgUrl)) {
					web.setThumb(new UMImage(activity, imgUrl));  //缩略图
				}else {
					web.setThumb(new UMImage(activity, R.drawable.icon));
				}
				web.setDescription(content);
				sAction.withMedia(web);
				sAction.share();
			}
		});
		panelAction.open();
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * @param context
	 * @return true 表示开启
	 */
	public static boolean isLocationOpen(final Context context) {
		LocationManager locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}
		return false;
	}

	/**
	 * 获取状态栏高度
	 * @param context
	 * @return
	 */
	public static int statusBarHeight(Context context) {
		int statusBarHeight = -1;//状态栏高度
		//获取status_bar_height资源的ID
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	/**
	 * 获取底部导航栏高度
	 * @param context
	 * @return
	 */
	public static int navigationBarHeight(Context context) {
		int navigationBarHeight = -1;//状态栏高度
		//获取status_bar_height资源的ID
		int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
		}
		return navigationBarHeight;
	}
    
}
