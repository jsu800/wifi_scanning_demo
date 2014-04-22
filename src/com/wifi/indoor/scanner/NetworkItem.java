package com.wifi.indoor.scanner;

import java.io.Serializable;

public class NetworkItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7692853216101755687L;
	private float mAverage = 0;
	private String mSSID;
	private String mBSSID;
	private int mRSSI = 0;
	private int mSize = 0;
	


	/**
	 * @return the mSize
	 */
	public int getmSize() {
		return mSize;
	}

	/**
	 * @param mSize the mSize to set
	 */
	public void setmSize(int mSize) {
		this.mSize = mSize;
	}

	public String getSsid() {
		return mSSID;
	}
	
	public void setSsid(String ssid) {
		this.mSSID = ssid;
	}
	
	public int getRssi() {
		return mRSSI;
	}
	
	public void setRssi(int rssi) {
		
		mRSSI += rssi;
		mSize++;		
		mAverage = (mRSSI / mSize);
	}

	public String getBssid() {
		return mBSSID;
	}
	
	public void setBssid(String bss) {
		this.mBSSID = bss;
	}
	
	public float getAverage() {

		return mAverage;
	}
	

	
	
}
