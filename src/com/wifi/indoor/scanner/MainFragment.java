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

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
 * @author Joseph Su
 * 
 * Main Fragment for chooser 
 * 
 */

public class MainFragment extends ListFragment { 

	String[] list_names = {
			"CALIBRATION MODE", 
			"MAP MODE - WIFI",
			"MAP MODE - GPS, WIFI, CELL NETWORK, SENSORS",
			"WIPE ALL DATA"
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		System.out.println("MainFragment::onCreate()");
		
		super.onCreate(savedInstanceState);
	   
		ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list_names);
		setListAdapter(listAdapter);

	}
	
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
		
		return inflater.inflate(R.layout.list_fragment, container, false);
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		
		/* Calibration mode */
		if (position == 0) {

			Intent intent = new Intent(this.getActivity(), MainActivity.class);
	        startActivity(intent);
			
	    /* Map mode - Media Labs */
		} else if (position == 1) {
			
			System.out.println("map mode - media labs");
			Intent intent = new Intent(this.getActivity(), MapActivity.class);
	        startActivity(intent);

        /* Map mode - Google Fused Location Provider */
		} else if (position == 2) {
			
			System.out.println("map mode - google fused location provider");
			Intent intent = new Intent(this.getActivity(), FusedLocationActivity.class);
			startActivity(intent);
			
	    /* Data restoration mode */
		} else if (position == 3) {
			
			deleteDataFromFile();
			
		}
		
		
	}
	
	private void deleteDataFromFile() {
		
		System.out.println("deleteDataFromFile()");
		
		// delete the locations_map file
		File dir = getActivity().getFilesDir();
		File file = new File(dir, "locations_map");
		boolean deleted = file.delete();
		
		String toastText;
		if (deleted)
			toastText = "Data file deleted: " + deleted + "path = " + dir;	
		else 
			toastText = "Data file cannot be deleted";
		 

		Toast.makeText(
				getActivity(), 
				toastText,
				Toast.LENGTH_LONG
		).show();
		
	}

	
	
}
