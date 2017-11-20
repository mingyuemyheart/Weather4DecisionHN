package com.android.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;


/**
 * Depiction:处理assets目录下文件的工具
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2013-8-1 上午10:28:48
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class AssetFile {
	final static String	 TAG	= "AssetFile";
	final static String	 ENCODE	= "UTF-8";
	final static int	 BUFFER	= 4 * 1024;
	private AssetManager manager;
						 
	public AssetFile(Context context) {
		manager = context.getAssets();
	}
	
	public InputStream open(String fileName) {
		try {
			return manager.open(fileName);
		} catch (IOException e) {
			Log.e(TAG, Thread.currentThread().getStackTrace()[0].getMethodName() + "---" + e.toString());
		}
		return null;
	}
	
	public void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				Log.e(TAG, Thread.currentThread().getStackTrace()[0].getMethodName() + "---" + e.toString());
			}
		}
	}
	
	public boolean exist(String fileName) {
		if (!TextUtils.isEmpty(fileName)) {
			InputStream is = open(fileName);
			if (is != null) {
				close(is);
				return true;
			}
		}
		return false;
	}
	
	public byte[] readAsByte(String fileName) {
		return readAsByte(fileName, ENCODE);
	}
	
	public byte[] readAsByte(String fileName, String charsetName) {
		if (exist(fileName)) {
			charsetName = TextUtils.isEmpty(charsetName) ? ENCODE : charsetName;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream is = open(fileName);
			int len = 0;
			byte[] burrer = new byte[BUFFER];
			try {
				while ((len = is.read(burrer, 0, burrer.length)) != -1) {
					out.write(burrer, 0, len);
				}
				if (is != null) {
					is.close();
				}
				return out.toByteArray();
			} catch (IOException e) {
				Log.e(TAG, Thread.currentThread().getStackTrace()[0].getMethodName() + "---" + e.toString());
			}
		}
		return null;
	}
	
	public String readAsString(String fileName) {
		return readAsString(fileName, ENCODE);
	}
	
	public String readAsString(String fileName, String charsetName) {
		if (exist(fileName)) {
			charsetName = TextUtils.isEmpty(charsetName) ? ENCODE : charsetName;
			try {
				return new String(readAsByte(fileName, charsetName), charsetName);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, Thread.currentThread().getStackTrace()[0].getMethodName() + "---" + e.toString());
			}
		}
		return null;
	}
	
	public boolean copy(String assetFile, String destFile) {
		if (exist(assetFile)) {
			try {
				InputStream is = open(assetFile);
				OutputStream out = new FileOutputStream(destFile);
				byte[] burrer = new byte[BUFFER];
				for (int length = is.read(burrer); length > 0; length = is.read(burrer)) {
					out.write(burrer, 0, length);
				}
				
				if (is != null) {
					is.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (FileNotFoundException e) {
				Log.e(TAG, Thread.currentThread().getStackTrace()[0].getMethodName() + "---" + e.toString());
			} catch (IOException e) {
				Log.e(TAG, Thread.currentThread().getStackTrace()[0].getMethodName() + "---" + e.toString());
			}
		}
		return false;
	}
	
	public Bitmap getBitmap(String fileName) {
		Bitmap image = null;
		try {
			InputStream is = manager.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	@SuppressWarnings ("deprecation")
	public Drawable getDrawable(String fileName) {
		Drawable image = null;
		try {
			InputStream is = manager.open(fileName);
			if (is == null) {
				is = manager.open("warning/icon_warning_0000.png");
			}
			image = new BitmapDrawable(is);
			is.close();
		} catch (IOException e) {
		}
		return image;
	}
}
