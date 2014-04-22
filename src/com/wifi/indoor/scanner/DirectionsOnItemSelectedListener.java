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