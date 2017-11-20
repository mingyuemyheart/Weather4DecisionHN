package com.android.lib.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;


/**
 * 
 * 类描述：设备信息工具类
 * <p>
 * 创建人：Kevin Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:31:25  
 * <p>     
 * 修改备注：    
 * <p>
 * @version 1.0
 * @since 1.0
 */
public class DeviceInfo {
	private Context               context;
	private TelephonyManager      tm;
	private final static String[] platform = {
	        "1.0", "1.1", "1.5", "1.6", "2.0", "2.0.1", "2.1", "2.2", "2.3", "2.3.3", "3.0", "3.1", "3.2", "4.0", "4.0.3", "4.1.2", "4.2"
	                                       };
	public static String padMacAddress = null;
	/**
	 * 构造函数
	 */
	public DeviceInfo(Context context) {
		this.context = context;
		String service = Context.TELEPHONY_SERVICE;
		tm = (TelephonyManager) context.getSystemService(service);
	}
	
	/**
	 * 读取手机设备的IMEI号
	 * 
	 * @return String
	 */
	public String imei() {
		String imei = tm.getDeviceId();
		if (null == imei) {
			imei = mac();
		}
		return imei;
	}
	
	/**
	 * 读取SIM卡的的IMSI号
	 * 
	 * @return String
	 */
	public String imsi() {
		return tm.getSubscriberId();
	}
	
	/**
	 * 读取设备系统版本信息
	 * 
	 * @return the softwareVersion
	 */
	public int romVersion() {
		return Build.VERSION.SDK_INT;
	}
	
	/**
	 * 获取android系统版本名称
	 * 
	 * @return String
	 */
	public String getAndroidPlatform() {
		int sdk = Build.VERSION.SDK_INT;
		int index = sdk - 1;
		if (index > 0 && index < platform.length) {
			return "Android " + platform[index];
		}
		return "unknown";
	}
	
	/**
	 * 读取设备生产厂商
	 * 
	 * @return the phoneNumber
	 */
	public String product() {
		return Build.PRODUCT;
	}
	
	/**
	 * 获取设备厂商名字
	 * 
	 * @return String
	 */
	public String manufacturer() {
		return Build.MANUFACTURER;
	}
	
	/**
	 * 读取设备型号
	 * 
	 * @return the model
	 */
	public String model() {
		return Build.MODEL;
	}
	
	/**
	 * 读取设备电话号码
	 * 
	 * @return the phoneNumber
	 */
	public String phoneNumber() {
		return tm.getLine1Number();
	}
	
	/**
	 * 获取Numeric
	 * 
	 * @return String
	 */
	public String numeric() {
		return tm.getSimOperator();
	}
	
	/**
	 * 获取MCC:国家编号
	 * 
	 * @return String
	 */
	public String mcc() {
		String numeric = tm.getSimOperator();
		if (null != numeric && numeric.length() >= 3) {
			return numeric.substring(0, 3);
		}
		return null;
	}
	
	/**
	 * 读取通信商的MNC
	 * 
	 * @return String
	 */
	public String mnc() {
		String numeric = tm.getSimOperator();
		if (null != numeric && numeric.length() > 3) {
			return numeric.substring(3, numeric.length());
		}
		return null;
	}
	
	/**
	 * 获取mac地址
	 * @return String
	 */
	public String mac() {
		String mac = null;
		if(padMacAddress != null){
			mac = new String(padMacAddress);
			//System.out.println(mac);
		}else{
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if(wifi.isWifiEnabled()){
			    //Perform Operation
				WifiInfo myWifiInfo = wifi.getConnectionInfo();
				mac = myWifiInfo.getMacAddress();
				//System.out.println(">"+mac);
			}else{
			    //Other Operation
				wifi.setWifiEnabled(true);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				WifiInfo myWifiInfo = wifi.getConnectionInfo();
				mac = myWifiInfo.getMacAddress();
	        	wifi.setWifiEnabled(false);
	        	//System.out.println(">>"+mac);
			}
			if (null != mac) {
				mac = mac.trim().replaceAll("-|\\.|:", "");
				padMacAddress = new String(mac);
			} else {
				if(padMacAddress != null){
					mac = padMacAddress;
					//System.out.println(">>>>"+mac);
				}else{
					mac = "";
					System.out.println(">>>"+mac);
				}
			}
		}
		return mac;
	}
	
	/**
	 * 获取UUID信息，由imei-mac成
	 * 
	 * @return String
	 */
	public String uuid() {
		return imei() + "-" + mac();
	}
	
	@Override
	public String toString() {
		return "DeviceInfo [imei()=" + imei()
		        + ", imsi()="
		        + imsi()
		        + ", romVersion()="
		        + romVersion()
		        + ", getAndroidPlatform()="
		        + getAndroidPlatform()
		        + ", product()="
		        + product()
		        + ", manufacturer()="
		        + manufacturer()
		        + ", model()="
		        + model()
		        + ", phoneNumber()="
		        + phoneNumber()
		        + ", numeric()="
		        + numeric()
		        + ", mcc()="
		        + mcc()
		        + ", mnc()="
		        + mnc()
		        + ", mac()="
		        + mac()
		        + ", uuid()="
		        + uuid()
		        + "]";
	}
	
}
