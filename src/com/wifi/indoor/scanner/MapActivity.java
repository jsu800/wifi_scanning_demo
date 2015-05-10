/*
 * Copyright (c) 2014 Joseph Su
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wifi.indoor.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


/*
 * @author Joseph Su
 * Main activity for activating the main mode.
 * 
 */
public class MapActivity extends Activity { 
	
	Button mButtonDirections;
	
	private View dotView, targetView;
	private MapBackgroundView mapImageView;
	
	private static int mSampleIndex = 1;
	private static final int NUM_OF_SAMPLINGS = 4;
	
	private static Map<String, NetworkItem> mSignalMap = new HashMap<String, NetworkItem>();		
	
	private WifiManager wifi = null;
	private Map<String, String> apLocations = LocationWithSignals.getAPLocationInstance();
	
	private Toast mToast;
		
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String toastMsg = "";
						
			if (mSampleIndex < NUM_OF_SAMPLINGS) {
				
		    	wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);	
				List<ScanResult> results = wifi.getScanResults();
				
				// store results somewhere until all sampling are done (3x)
				for (ScanResult result : results) {
										
					// go through a list of MACs to make sure we are ONLY taking those
					if (apLocations != null && apLocations.containsKey(result.BSSID) == false)
						continue;
					
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
				
				mSampleIndex++;
															
				toastMsg = "SCANNING ...";
				
				
			} else {
				
				// this is when the number of required samplings (3x) has been completed				
				//resetting sampling index
				mSampleIndex = 1;

				// Send scan results to LocationWithSignals database to pinpoint current location
				// This is where the algorithm located
				String userLocation = LocationWithSignals.findUserLocation(new ArrayList<NetworkItem>(mSignalMap.values()));
				
				if (userLocation == null) {
					toastMsg = "LOCATION NOT FOUND";
				} else {

					// animate the image icons (starting, ending) to the given location
					LocationWithSignals.setCurrentLocation(userLocation);
					animateImageView(userLocation);	
					toastMsg = "CURRENT USER LOCATION: " + userLocation;  
					

				}

				// TO BE DELETED: Expensive operation. Don't need it in production app. Print sampling results per #
				LocationWithSignals.printLocationWithSignals(new ArrayList<NetworkItem>(mSignalMap.values()), true);				
				
				// wipe mSignalsMap to allow next sampling
				mSignalMap.clear();
				
			}

			// reset the scan - keep going until user is done with the map view altogether
			wifi.startScan();
			
			display(toastMsg);
		}		
	};
	
	private void display(String msg) {
		
		if (mToast != null) {
			mToast.setText(msg);
			mToast.show();
		}
	}
 
	private void animateImageView(String loc) {
		
		mapImageView.invalidate();
		
	}
	
	private void undoReceiver() {
    	try {
        	unregisterReceiver(mReceiver);    		
    	} catch (IllegalArgumentException iae) {
    		
    	}
	}

	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		System.out.println("MapActivity: onCreate()");
				
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// initialize current location to always be "Front Lobby" if none is there
		if (LocationWithSignals.getCurrentLocation() == null)
			LocationWithSignals.setCurrentLocation("Front Lobby");
						
		// initialize Toast
		mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		
		// display 
		setupView();

		// add listener to the Directions button
		addListenerOnButton();

		// draw both current and destination icons if locations are known at onCreate() time
		// call invalidate to draw
		animateImageView(null);		
		
		// start scanning for current location
		scanWifi();
		
		// set both dotView and targetView in map view for animation later
		mapImageView.setCurrentView(dotView, targetView);
		
		// draw routes if both starting and ending locations are available
		drawRoutes();

		
	}
	
	private void scanWifi() {
		IntentFilter intentFilter = new IntentFilter();         
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);        
        registerReceiver(mReceiver, intentFilter); 

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);						
		wifi.startScan();				
	}
	
	
	private void setupView() {
		
		// set up dot and target view 
	    dotView = findViewById(R.id.icon_current_location);
	    targetView = findViewById(R.id.icon_target_location);
	    
		// set up the background map view
	    mapImageView = (MapBackgroundView) findViewById(R.id.mapBackgroundView);
	    mapImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	      
	    	@Override
			public void onGlobalLayout() {
				  
				System.out.println("onGlobalLayout");
				  
				mapImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);	
				
				// send both images off the chart 
				dotView.setVisibility(View.VISIBLE);
				targetView.setVisibility(View.VISIBLE);
				
				// set views
				mapImageView.setCurrentView(dotView, targetView);
		        
	    	}
	    });	    
	    
		
	}

	private void drawRoutes() {

		String startingLocation = LocationWithSignals.getCurrentLocation();
		String endingLocation = LocationWithSignals.getToLocationForRouting();
		
		if (startingLocation != null && endingLocation != null) {
			mapImageView.drawPathBetweenLocations(startingLocation, endingLocation);			
		}
	
	}
	
	public void addListenerOnButton() {
  
		mButtonDirections = (Button) findViewById(R.id.btnGetDirections);
//		mButtonDirections.setEnabled(false);
		mButtonDirections.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MapActivity.this, DirectionActivity.class);
				MapActivity.this.startActivity(intent);				
			}
 
		});
 
	}
	
	    
	@Override
	protected void onStop() {		
		super.onStop();
		
		System.out.println("onStop()");

    	wifi.disconnect();

		undoReceiver();

		if (mToast != null) 
			mToast.cancel();
	}


    @Override
    public void onDestroy() {		
    	super.onDestroy();
    	
    	System.out.println("onDestroy()");
    	
    	wifi.disconnect();

    	undoReceiver();
    	
    	undoLocations();    	

    	if (mToast != null) 
			mToast.cancel();

    }

    private void undoLocations() {
    	LocationWithSignals.setCurrentLocation(null);
    	LocationWithSignals.setToLocationForRouting(null);
    }
    
    private int getDisplayHeight() {
    	
    	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
    	
    	return size.y;
    }
    
}







 