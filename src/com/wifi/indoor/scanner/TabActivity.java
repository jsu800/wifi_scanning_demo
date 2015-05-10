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


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.wifi.indoor.scanner.dijkstra.Edge;
import com.wifi.indoor.scanner.dijkstra.Vertex;


/*
 * @author Joseph Su
 * 
 * Including Dijstra (TODO)
 * 
 */
public class TabActivity extends FragmentActivity implements ActionBar.TabListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	
    private List<Vertex> nodes;
    private List<Edge> edges;
	
	
	protected void onCreate(Bundle savedInstanceState) {
    
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText("Main Menu").setTabListener(this));
        
        // load data into memory
        loadDataFromFile();
        

        //TODO: Dijstra 
        // This is a sample code that illustrates how Dijstra could be applied in the context of this application        
//        nodes = new ArrayList<Vertex>();
//        edges = new ArrayList<Edge>();
//        for (int i = 0; i < 11; i++) {
//          Vertex location = new Vertex("Node_" + i, "Node_" + i);
//          nodes.add(location);
//        }
//
//        addLane("Edge_0", 0, 1, 85);
//        addLane("Edge_1", 0, 2, 217);
//        addLane("Edge_2", 0, 4, 173);
//        addLane("Edge_3", 2, 6, 186);
//        addLane("Edge_4", 2, 7, 103);
//        addLane("Edge_5", 3, 7, 183);
//        addLane("Edge_6", 5, 8, 250);
//        addLane("Edge_7", 8, 9, 84);
//        addLane("Edge_8", 7, 9, 167);
//        addLane("Edge_9", 4, 9, 502);
//        addLane("Edge_10", 9, 10, 40);
//        addLane("Edge_11", 1, 10, 600);
//        
//
//        // Lets check from location Loc_1 to Loc_10
//        Graph graph = new Graph(nodes, edges);
//        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
//        dijkstra.execute(nodes.get(0));
//        LinkedList<Vertex> path = dijkstra.getPath(nodes.get(6));
//        
//        
//        for (Vertex vertex : path) {
//          System.out.println(vertex);
//        }
//        
    }


	
	private void loadDataFromFile() {

		try {
			
			// load any previous saved location data
			FileInputStream fis = openFileInput(LocationWithSignals.getLocationFileName());
			ObjectInputStream is = new ObjectInputStream(fis);			
			Map map = (Map<String, List<NetworkItem>>) is.readObject();
			is.close();
			
			// load data into memory
			LocationWithSignals.save(map);
			
		} catch (Exception e) {
			
		}


	}
	

	
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		System.out.println("onTabSelected");
		
		if (tab.getPosition() == 0) {
			MainFragment mainFragment = new MainFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
		} 
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		System.out.println("onTabUnselected");
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }
 
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}