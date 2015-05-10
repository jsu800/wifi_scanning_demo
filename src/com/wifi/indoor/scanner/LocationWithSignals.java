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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.widget.TextView;
import android.widget.Toast;


public class LocationWithSignals implements Serializable {

	private static final long serialVersionUID = -369812688991034400L;

	// <Location, List of signal strengths>
	private static Map<String, List<NetworkItem>> locations = null;
	
	// <MAC address, corresponding #1 location>
	private static Map<String, String> apLocations = null;
	
	// <Location name, <LocationItem> x,y coorindates>
	private static Map<String, LocationItem> locationCoordinates = null;
	
	private static int MAX_RSSI_DIFFERENCE = 1000;
	private static int RSSI_DIFFERENCE_THRESHOLD = 150;
	private static int NUM_MATCHING_MAC_ADDRESSES = 5;
	private static int numSamplings = 6;
	
	private static List<NetworkItem> mSignals = null;
	
	

	private static final String LOCATIONS_FILE_NAME = "locations_map";

	private static String currentLocation = null;

	private static String destinationLocation = null;
	
	
	
	/**
	 * @return the currentLocation
	 */
	public static String getCurrentLocation() {
		return currentLocation;
	}

	/**
	 * @param currentLocation the currentLocation to set
	 */
	public static void setCurrentLocation(String currentLocation) {
		LocationWithSignals.currentLocation = currentLocation;
	}

	// This is not singleton 
	public static List<NetworkItem> getSignalsInstance() {		
		return mSignals;
	}
	
	public static String getLocationFileName() {
		return LOCATIONS_FILE_NAME;
	}
	
	public static Map<String, List<NetworkItem>> getMapInstance() {
		
		if (locations == null) {			
			locations = new HashMap<String, List<NetworkItem>>();
		}					
		return locations;
	}
	
	public static Map<String, LocationItem> getLocationCoordinates() {
		
		if (locationCoordinates == null) {			
			locationCoordinates = new HashMap<String, LocationItem>();
			
			// in the future we should put these in Set. We should also pass in the Context
			// to allow for pulling getResources().getStringArray(R.array....) to fill in the 
			// string menu
			locationCoordinates.put("128N – Conference Room", new LocationItem(223, 200));
			locationCoordinates.put("127N – Conference Room", new LocationItem(708, 200));
			locationCoordinates.put("128S – Conference Room", new LocationItem(223, 2250));
			locationCoordinates.put("127S – Conference Room", new LocationItem(708, 2250));
			locationCoordinates.put("Cafeteria", new LocationItem(223, 825));
			locationCoordinates.put("Rear Lobby", new LocationItem(223, 1144));
			locationCoordinates.put("Front Lobby", new LocationItem(708, 1144));
		}					
		return locationCoordinates;
	}
	
	public static Map<String, String> getAPLocationInstance() {
		
		if (apLocations == null) {			
			apLocations = new HashMap<String, String>();
			
			// in the future we should put these in Set
			apLocations.put("0c:85:25:c7:0c:30", "128S");
			apLocations.put("0c:85:25:c7:0c:31", "128S");
			apLocations.put("b4:e9:b0:b4:80:a0", "Cafeteria-Back");
			apLocations.put("b4:e9:b0:b4:80:a1", "Cafeteria-Back");
			apLocations.put("b4:e9:b0:b4:82:60", "127S");
			apLocations.put("b4:e9:b0:b4:82:61", "127S");
			apLocations.put("b4:e9:b0:ee:c8:d0", "Cafeteria-Front");
			apLocations.put("b4:e9:b0:ee:c8:d1", "Cafeteria-Front");
			apLocations.put("b4:e9:b0:ee:db:e0", "128N");
			apLocations.put("b4:e9:b0:ee:db:e1", "128N");
			apLocations.put("f0:29:29:2a:71:20", "127N");
			apLocations.put("f0:29:29:2a:71:21", "127N");
			apLocations.put("f0:29:29:2a:73:f0", "Front Lobby");
			apLocations.put("f0:29:29:2a:73:f1", "Front Lobby");
		}					
		return apLocations;
	}
	
	
	
	public static void save(Map<String, List<NetworkItem>> loc) {
		locations = loc; 
	}
	
	public static void save(String loc) {
				
		Map<String, List<NetworkItem>> map = getMapInstance();
		
		if (map.containsKey(loc) == false) {
			
			map.put(loc, getSignalsInstance());
		
		} else {
		
			// if location already exists let's remove the old data replacing it with what is new
			map.remove(loc);
			map.put(loc, getSignalsInstance());
			
		}
			
	}
	
	
	
	
	public static void saveSignals(List<NetworkItem> signals) {
		
		// Deep cloning
		try {	
			mSignals = (List<NetworkItem>) ObjectCloner.deepCopy(signals);			
		} catch (Exception e) {
			
		}

	}
	
	public static boolean isLocationSampled(String loc) {
		
		Map<String, List<NetworkItem>> map = getMapInstance();		
		return map.containsKey(loc);
	}
	
	
	public static void printTitleView(String text) {
		MainActivity.getTitleViewInstance().setText(text);
	}
	
	public static void printLocationWithName(String loc) {
		
		Map<String, List<NetworkItem>> map = getMapInstance();		
		printLocationWithSignals((List<NetworkItem>)map.get(loc), false);
		
	}
	
	public static void printLocationWithSignals(List<NetworkItem> signals, boolean isSystemOut) {
		
		int index = 0;		
		
		// let's sort the signals if not null
		if (signals != null) {
			
			Collections.sort(signals, new Comparator<NetworkItem>() {
				@Override
				public int compare(NetworkItem lhs, NetworkItem rhs) {										
					return (lhs.getAverage() > rhs.getAverage() ? -1 : (lhs.getAverage() == rhs.getAverage() ? 0 : 1));
				}				
			});
			
		}
		
		if (isSystemOut) {
			
			for (NetworkItem signal : signals) {
				
				System.out.println("\t MAC[" + signal.getBssid() + "]");
				System.out.println("\t \t SSID " + signal.getSsid());
				System.out.println("\t \t RSSI " + signal.getAverage());
				System.out.println();
				
				if (++index == numSamplings)
					break;
			}	
			
			
		} else {
			
			TextView[] views = MainActivity.getViewInstance();
			
			if (signals == null) {
				
				for (TextView view : views) {				
					view.setText("");			
				}

				
			} else {

				TextView view = views[0];
				view.setText("");
				
				for (NetworkItem signal : signals) {
					
					view.append("\n MAC[" + signal.getBssid() + "]");
					view.append("\n SSID: " + signal.getSsid());
					view.append("\n RSSI: " + signal.getAverage());				
					view.append("\n");
					
					if (++index == numSamplings)
						break;
				}			
			}			
		}
		
	}
	
	
	public static void printLocationWithSignals() {
		
		printLocationWithSignals(getSignalsInstance(), false); 
		
	}	
	
	
	public static void print() {
		
		Map map = getMapInstance();		
		
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			
			Map.Entry pairs = (Map.Entry)it.next();
			
			System.out.println("Location: " + pairs.getKey());
			
			List<NetworkItem> values = (List<NetworkItem>)pairs.getValue();

			int index = 0;
			for (NetworkItem value : values) {
				
				System.out.println("\t MAC[" + value.getBssid() + "]");
				System.out.println("\t \t SSID " + value.getSsid());
				System.out.println("\t \t RSSI " + value.getAverage());
				
				// print the top 10 samples
				if (++index == 6) 
					break;
			}					
			
		}			
		
		
	}
	
	private static int getTotalDifference(List<NetworkItem> oldResults, List<NetworkItem> newResults) {

		int difference = 0;
		
		// comparing all the datasets containing MACs per given location

		// sort newResults, then comparing the top 6 datasets
		Collections.sort(newResults, new Comparator<NetworkItem>() {

			@Override
			public int compare(NetworkItem lhs, NetworkItem rhs) {					
				
				return (lhs.getAverage() > rhs.getAverage() ? -1 : (lhs.getAverage() == rhs.getAverage() ? 0 : 1));
			}
			
		});
				
		int index = 0;
		int matchedCount = 0;
		for (NetworkItem newResult : newResults) {
			
			for (NetworkItem oldResult : oldResults) {
				
				// find the matching MAC address and comparing the difference in RSSI between old and new
				if (newResult.getBssid().equals(oldResult.getBssid())) {
						
					matchedCount++;
					difference += Math.abs(newResult.getAverage() - oldResult.getAverage());
					
				} 
				
			}
			
			if (++index == 6)
				break;
		}
		
		// we have found the min difference here, but let's make sure that 
		// there are the difference is calculated based on at least NUM_MATCHING_MAC_ADDRESSES 
		// of MACs. If below threshold we turn difference = 0. In which case set the difference
		// to be rather large so it would never get picked up by findUserLocation() for use
		if (matchedCount <= NUM_MATCHING_MAC_ADDRESSES)
			difference = 10000;
		
		return difference;
	}
	
	public static String findUserLocation(List<NetworkItem> results) {
		
		String retVal = null;
		int difference = MAX_RSSI_DIFFERENCE;
		
		Map<String, List<NetworkItem>> storedMap = getMapInstance();
		
		Iterator it = storedMap.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Map.Entry pairs = (Map.Entry)it.next();
						
			int tmpDifference = getTotalDifference((List<NetworkItem>)pairs.getValue(), results);
						
			if (difference > tmpDifference) {
								
				// moving min rssi difference
				difference = tmpDifference;
				
				// the location associated with the min rssi difference
				retVal = (String)pairs.getKey();		
				
				// set the currentUserLocation right away
				setCurrentLocation(retVal);
				
			}
			
		}
		
		System.out.println("RSSI DIFFERENCE: " + difference);
		
		
		if (difference == MAX_RSSI_DIFFERENCE || difference > RSSI_DIFFERENCE_THRESHOLD)
			retVal = null;
		
		return retVal;
	}

	
	public static void setToLocationForRouting(String loc) {
		
		destinationLocation = loc;
	}
	
	public static String getToLocationForRouting() {
		
		return destinationLocation;
	}
}
