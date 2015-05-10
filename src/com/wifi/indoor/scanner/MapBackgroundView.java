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

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Draws Map background that is stretched to parent's height, keeps aspect ration
 * and centers the image.
 *
 * @author Joseph Su
 */

public class MapBackgroundView extends View {
  
  private final Drawable mDrawable;
  private Path mPath;
  private Paint mPaint;
  private Rect mDrawableBounds;
  
  private static final int UPPER_BOUND_VALUE_Y = 25;
  
  //The original size from the ec_map_accurate image is 1048x2417
  private static final int ORIGINAL_MAP_PNG_WIDTH = 1048;
  private static final int ORIGINAL_MAP_PNG_HEIGHT = 2417;
  
  
  // set initial location of both starting & destination icons off the chart
  private int mLastX = -1000, mLastY = -1000;
  private int mNewX = -1000, mNewY = -1000;
  
  private String mStartingLocation = null;
  private String mEndingLocation = null;
  
  private View mCurrentView = null;
  private View mTargetView = null;
  
  
  private int canvasWidth = 0;
  /**
 * @return the canvasWidth
 */
public int getCanvasWidth() {
	return canvasWidth;
}


/**
 * @return the canvasHeight
 */
public int getCanvasHeight() {
	return canvasHeight;
}
private int canvasHeight = 0;

  

  public MapBackgroundView(Context context, AttributeSet attrs) {
    super(context, attrs);

    mDrawable = context.getResources().getDrawable(R.drawable.ec_map_accurate);
  
    // initialize paint
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setDither(true);
    mPaint.setColor(0x706173FF);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeJoin(Paint.Join.ROUND);
    mPaint.setStrokeCap(Paint.Cap.ROUND);
    mPaint.setStrokeWidth(30);
    
    // initialize path to upper bound
    mPath = new Path();
    mLastX = -1;
    mLastY = -1;
    
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    if (canvasWidth == 0)
    	canvasWidth = canvas.getWidth();
    
    if (canvasHeight == 0)
    	canvasHeight = canvas.getHeight();
    
    
    // initialize bounds
    mDrawableBounds = new Rect(0, UPPER_BOUND_VALUE_Y, canvas.getWidth(), canvas.getHeight()-UPPER_BOUND_VALUE_Y);    
    mDrawable.setBounds(mDrawableBounds);
    mDrawable.draw(canvas);

    // draw starting icon
    if (mCurrentView != null) {

    	  String startingLocation = LocationWithSignals.getCurrentLocation();
		  LocationItem startingItem = null;
		  
		  if (startingLocation != null) {

			  Map locations = LocationWithSignals.getLocationCoordinates();
			  startingItem = (LocationItem)locations.get(startingLocation);

    		  if (startingItem != null) {
    				
    			  mStartingLocation = startingLocation;
    			  mLastX = startingItem.getX() * canvas.getWidth()/ORIGINAL_MAP_PNG_WIDTH;
    			  mLastY = startingItem.getY() * canvas.getHeight()/ORIGINAL_MAP_PNG_HEIGHT;
    			    
    		  }    		

		  }
		
		  mCurrentView.animate().translationX(getX(mLastX)).start();
		  mCurrentView.animate().translationY(getY(mLastY)).start();    	
    }
    
    // draw ending icon
    if (mTargetView != null) {
    	
  	  	  String endingLocation = LocationWithSignals.getToLocationForRouting();
		  LocationItem endingItem = null;
		  
		  if (endingLocation != null) {

			  Map locations = LocationWithSignals.getLocationCoordinates();
			  endingItem = (LocationItem)locations.get(endingLocation);

	  		  if (endingItem != null) {
	  			  	mEndingLocation = endingLocation;
	  				mNewX = endingItem.getX() * canvas.getWidth()/ORIGINAL_MAP_PNG_WIDTH;
	  			    mNewY = endingItem.getY() * canvas.getHeight()/ORIGINAL_MAP_PNG_HEIGHT;
	  		  }    		
		  }    			  
		  mTargetView.animate().translationX(getX(mNewX)).start();
		  mTargetView.animate().translationY(getY(mNewY)).start();    	
    }

    // draw path
    if (mStartingLocation != null && mEndingLocation != null) {
        preparePath(canvas);
        canvas.drawPath(mPath,  mPaint);    	
    }
        
    
    
  }
  
  private void preparePath(Canvas c) {

	  Canvas canvas = c;
	  
	  // reset previous path if any
	  mPath.reset();

	  Map<String, LocationItem> locations = LocationWithSignals.getLocationCoordinates();
	    
	  LocationItem startingItem = null;
	  LocationItem endingItem = null;
	  LocationItem intermediateItem = null;	  
	  if (mStartingLocation != null && mEndingLocation != null) {
		  
		  startingItem = (LocationItem)locations.get(mStartingLocation);
		  endingItem = (LocationItem)locations.get(mEndingLocation);

		  // check to see if we need intermediate points in locations hash if so print them as well

		  if (startingItem.getX() != endingItem.getX() && startingItem.getY() != endingItem.getY()) {
	        
	        	// there is one more intermediate point we need to get from map to connect 
	        	// start to end locations. Let's find that intermediate point by comparing X first
	            Iterator it = locations.entrySet().iterator();
	            
	            while (it.hasNext()) {
	            	
	            	Map.Entry pairs = (Map.Entry)it.next();				    			
	    			String loc = (String)pairs.getKey();
	    			
	    			// weeding out Cafeteria as hack. Don't want cafeteria as any intermediate route point
	    			if (!loc.equals(mStartingLocation) && !loc.equals(mEndingLocation) && !loc.equals("Cafeteria")) {
	    				
	    				LocationItem item = (LocationItem)pairs.getValue();
	    				if (item.getX() == startingItem.getX() || item.getY() == startingItem.getY()) {
	    					
	    					// check to see if itemX and itemY has at least one of them matching endingX or endingY
	    					if (item.getX() == endingItem.getX() || item.getY() == endingItem.getY()) {
	    						intermediateItem = item;
	    						break;
	    					}
	    						
	    				}
	    			}    		
	        	}                	
	        }
	    }
	    
	  	// calculate path coordinates
		mLastX = startingItem.getX() * canvas.getWidth()/ORIGINAL_MAP_PNG_WIDTH;
	    mLastY = startingItem.getY() * canvas.getHeight()/ORIGINAL_MAP_PNG_HEIGHT;
	    mNewX = endingItem.getX() * canvas.getWidth()/ORIGINAL_MAP_PNG_WIDTH;
	    mNewY = endingItem.getY() * canvas.getHeight()/ORIGINAL_MAP_PNG_HEIGHT;
	    mPath.moveTo(mLastX, mLastY);
	    
	    if (intermediateItem != null) {    	
	    	mPath.lineTo(intermediateItem.getX() * canvas.getWidth()/ORIGINAL_MAP_PNG_WIDTH, intermediateItem.getY() * canvas.getHeight()/ORIGINAL_MAP_PNG_HEIGHT);
	    }
	    	
	    mPath.lineTo(mNewX, mNewY);    
	    
  }
  
  
  
  
  // this sets the starting and ending locations and draws path
  public void drawPathBetweenLocations(String startingLoc, String endingLoc) {

	  mStartingLocation = startingLoc;
	  mEndingLocation = endingLoc;
	  
	  invalidate();
  }


  // this is for testing. Don't call this one in production
  public void movePath(int x, int y) {
	  
	  mPath.reset();
	  mPath.moveTo(mLastX, mLastY);
	  mPath.lineTo(x, y);
	  
	  // force onDraw()
	  invalidate();
  }
  

  public void setCoordinates(int x, int y) {

	  mNewX = x;
	  mNewY = y;
	  
  }
  
  public void setCurrentView(View currentView, View targetView) {
	  mCurrentView = currentView;
	  mTargetView = targetView;
  }
  
	private int getX(int x) {
	
		// by default we use 72x72 icons		
		return x - 58;
	}
	
	private int getY(int y) {
	
		// by default we use 72x72 icons
		return y + 35;
	}

  
}
