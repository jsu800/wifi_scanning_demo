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


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class DirectionActivity extends Activity {

	private Spinner mSpinnerRoomMenu = null;
	private static TextView[] mViews = null;
	private Button mButtonRouting = null;

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		System.out.println("DirectionActivity: onCreate()");

		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);
		
		setupViews();
		setupButton(); 
		
		addListenerOnSpinnerItemSelection();
		
		String fromLocation = LocationWithSignals.getCurrentLocation();
		if (fromLocation != null)
			setFromLocationOnTextView(fromLocation);
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	private void addListenerOnSpinnerItemSelection() {
		
		if (mSpinnerRoomMenu == null) {
			mSpinnerRoomMenu = (Spinner) findViewById(R.id.spinnerLocations);
			
			mSpinnerRoomMenu.setOnItemSelectedListener(new DirectionsOnItemSelectedListener());			
		}
	}	
	
	private void setupViews() {
		mViews = getViewInstance();
		mViews[0] = (TextView) findViewById(R.id.textViewFromLocation);
		mViews[1] = (TextView) findViewById(R.id.TextViewToLocation);
	}
	
	
	private void setupButton() {
		
		mButtonRouting = (Button) findViewById(R.id.btnGetDirection);
		mButtonRouting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// when Calibrate is pressed disable spinner
				mSpinnerRoomMenu.setEnabled(false);
				
				// get back to the MapActivity page with the direction polyline drawn
				Intent intent = new Intent(DirectionActivity.this, MapActivity.class);
				DirectionActivity.this.startActivity(intent);

			}
		});
	}	
	
	public void setFromLocationOnTextView(String loc) {
		
		TextView view = mViews[0];
		
		if (view != null) {
			view.setText("");
			view.append(loc);
		}
	}
	
	
	public static TextView[] getViewInstance() {
		
		if (mViews == null) {
			mViews = new TextView[2];				
		}
		
		return mViews;
		
	}
	
	
}
