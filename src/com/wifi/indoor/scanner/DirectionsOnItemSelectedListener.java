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

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
 
public class DirectionsOnItemSelectedListener implements OnItemSelectedListener {
 
	
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				
		String loc = parent.getItemAtPosition(pos).toString();		
		String toastText;
		
		
		// Save location to the TO text view and print it to screen
		LocationWithSignals.setToLocationForRouting(loc);
			
		toastText = "Location selected: " + loc;
			
		Toast.makeText(
				parent.getContext(), 
				toastText,
				Toast.LENGTH_SHORT
		).show();		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

 
}