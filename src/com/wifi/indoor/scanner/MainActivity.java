package com.wifi.indoor.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	/** Anything worse than or equal to this will show 0 bars. */
	//	private static final int MIN_RSSI = -100;

	/** Anything better than or equal to this will show the max bars. */
	 //	private static final int MAX_RSSI = -55;
	
	private WifiManager wifi = null;
	
	private static int mSampleIndex = 1;
	private static final int NUM_OF_SAMPLINGS = 7;
	
	
	// for storing <MAC address, NetworkItem> to get (NUM_OF_SAMPLINGS-1) readings per MAC for averaging in Calibration mode
	private static Map<String, NetworkItem> mSignalMap = new HashMap<String, NetworkItem>();	
	
	
	private static TextView[] mViews = null;
	private static TextView mProgressTitle = null;

	private Spinner mSpinnerRoomMenu = null;
	private static Button mButtonCalibrating = null;
	private static Button mButtonSaving = null;
	private static Button mButtonPrinting = null;
	
	private Map apLocations = LocationWithSignals.getAPLocationInstance();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		System.out.println("onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	
		setupViews();
		setupButtons(); 
		addListenerOnSpinnerItemSelection();

		
	}
	

	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
					    	    						
			if (mSampleIndex < NUM_OF_SAMPLINGS) {
				
		    	wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);	
				List<ScanResult> results = wifi.getScanResults();
				
				
				for (ScanResult result : results) {
					
					// go through a list of MACs to make sure we are ONLY taking those
					if (apLocations != null && apLocations.containsKey(result.BSSID) == false) {	
						continue;						
					}

					
					if (mSignalMap.containsKey(result.BSSID)) {
						
						// MAC exists, get the NetworkItem out and add the new result.level 
						// to get the average RSSI later
						NetworkItem item = mSignalMap.get(result.BSSID);
						item.setRssi(result.level);
						
					} else {
						
						// create new network item
						NetworkItem item = new NetworkItem();
						item.setBssid(result.BSSID);
						item.setSsid(result.SSID);
						item.setRssi(result.level);	
						
						// put into hash
						mSignalMap.put(result.BSSID, item);
						
					}
				
				}
				
				// print sampling #
				mProgressTitle.setText("PLEASE WAIT ... TAKING SAMPLING #" + mSampleIndex++);
				
				// print sampling results per #
				LocationWithSignals.printLocationWithSignals(new ArrayList<NetworkItem>(mSignalMap.values()), false);
				
				// reset the scan
				wifi.startScan();
				
			} else {

				// done with sampling, unregister receiver
				undoReceiver();
				
				// print title
				mProgressTitle.setText("Top 6 Averaged RSSIs:");
				
				// get/save the top six datasets
				saveSignalDatasetsToMap(true);

				// register for spinner onClick next
				addListenerOnSpinnerItemSelection();

				
				// print the sampling results
				LocationWithSignals.printLocationWithSignals();

				//resetting sampling index
				mSampleIndex = 1;
				
				mButtonCalibrating.setEnabled(true);
				mButtonSaving.setEnabled(true);
				mSpinnerRoomMenu.setEnabled(true);
				
			} // end if
			
			System.out.println("onReceive: " + mSampleIndex);
		}
	};
	
	public static TextView getTitleViewInstance() {
		
		return mProgressTitle;
		
	}
	
	public static TextView[] getViewInstance() {
		
		if (mViews == null) {
			mViews = new TextView[1];				
		}
		
		return mViews;
		
	}
	
	
	
	private void saveSignalDatasetsToMap(boolean toSort) {
		
		if (toSort == true) {
			
			List<NetworkItem> signalsByStrength = new ArrayList<NetworkItem>(mSignalMap.values());
			Collections.sort(signalsByStrength, new Comparator<NetworkItem>() {

				@Override
				public int compare(NetworkItem lhs, NetworkItem rhs) {					
					
					return (lhs.getAverage() > rhs.getAverage() ? -1 : (lhs.getAverage() == rhs.getAverage() ? 0 : 1));
				}
				
			});
									
			// Save signals list to LocationWithSignals right away
			LocationWithSignals.saveSignals(signalsByStrength);
						
		} else {
			
			// if we don't want to sort it just save to LocationWithSignals class
			LocationWithSignals.saveSignals(new ArrayList<NetworkItem>(mSignalMap.values()));
			
		}
		
 		
	}
	
	
	private void addListenerOnSpinnerItemSelection() {
		
		if (mSpinnerRoomMenu == null) {
			mSpinnerRoomMenu = (Spinner) findViewById(R.id.spinnerRooms1);
			mSpinnerRoomMenu.setOnItemSelectedListener(new CalibrateOnItemSelectedListener());			
		}
	}
	
	
	private void setupViews() {
		mViews = getViewInstance();
		mViews[0] = (TextView) findViewById(R.id.textViewScanning1);

		mProgressTitle = (TextView) findViewById(R.id.textViewSampling);
	}
	
	
	private void setupButtons() {
				
		mButtonCalibrating = (Button)	findViewById(R.id.btnSampling);
		mButtonCalibrating.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
		mButtonCalibrating.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// when Calibrate is pressed disable it
				mButtonCalibrating.setEnabled(false);
				
				// when Calibrate is pressed disable Save
				mButtonSaving.setEnabled(false);
				
				// when Calibrate is pressed disable spinner
				mSpinnerRoomMenu.setEnabled(false);
								
				IntentFilter intentFilter = new IntentFilter();         
		        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);        
		        registerReceiver(mReceiver, intentFilter); 

		        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);						
				wifi.startScan();	
								
			}
			
		});
		
		mButtonSaving = (Button) findViewById(R.id.btnSave);
		mButtonSaving.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
		mButtonSaving.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String loc = null;
				String toastText = null;

				// when Save is pressed disable Save
				mButtonSaving.setEnabled(false);
				
				// when Save is pressed disable Calibrate
				mButtonCalibrating.setEnabled(false);
				
				// when Save is pressed disable spinner
				mSpinnerRoomMenu.setEnabled(false);
								
				// Save button is pressed. Find out the location	
				if (mSpinnerRoomMenu.getSelectedItem() != null) {
					
					loc = mSpinnerRoomMenu.getSelectedItem().toString();

					// get current date and time
					Time now = new Time();
					now.setToNow();
					String time =  now.hour + ":" + now.minute + ":" + now.second + " on " + (now.month+1) + "/" + now.monthDay + "/" + now.year;					

					
					// save location and data to memory
					LocationWithSignals.save(loc);
	
					// Save map to disk
					try {
						FileOutputStream fos = openFileOutput(LocationWithSignals.getLocationFileName(), Context.MODE_PRIVATE);
						ObjectOutputStream os = new ObjectOutputStream(fos);
						os.writeObject(LocationWithSignals.getMapInstance());
						fos.close();							
					} catch (Exception e) {
						
					}
					
					toastText = "Saving " + loc + " at " + time;

				} else {
					
					toastText = "Please select a room, calibrate, then save";
				}
								
				// print text
				LocationWithSignals.printTitleView(toastText);				
				
				Toast.makeText(
						v.getContext(), 
						toastText,
						Toast.LENGTH_SHORT
				).show();
				
				//TEST
//				LocationWithSignals.print();
				
				// when Save is done re-enable Save
				mButtonSaving.setEnabled(true);
				
				// when Save is done re-enable Calibrate
				mButtonCalibrating.setEnabled(true);

				// when Save is pressed disable spinner
				mSpinnerRoomMenu.setEnabled(true);
				
			}
			
		});
		mButtonPrinting = (Button) findViewById(R.id.btnPrint);
		mButtonPrinting.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
		mButtonPrinting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				LocationWithSignals.print();
			}
			
		});
		
	}


	@Override
	protected void onStop() {
		
		super.onStop();
		System.out.println("onStop()");
		undoReceiver();
	}


    @Override
    public void onDestroy() {
		
    	super.onDestroy();
    	System.out.println("onDestroy()");
    	undoReceiver();
    }



	
	private void undoReceiver() {
    	try {
        	unregisterReceiver(mReceiver);    		
    	} catch (IllegalArgumentException iae) {
    		
    	}
	}

	public static void setEnabledSaveButton(boolean flag) {
		mButtonSaving.setEnabled(flag);
	}

	
}



