
package com.sociam.nyctaxi.run;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sociam.nyctaxi.analysis.CompareMethodThreaded;
import com.sociam.nyctaxi.analysis.CompareMethods;
import com.sociam.nyctaxi.analysis.Stats;
import com.sociam.nyctaxi.cascade.CascadeConstructor;
import com.sociam.nyctaxi.file.FileLoader;
import com.sociam.nyctaxi.misc.MiscFunctions;
import com.sociam.nyctaxi.objects.Trip;
import com.sociam.nyctaxi.objects.WayPoint;

public class MainConstructCascades {

	
	FileLoader fileLoader;
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileLoader fl = new FileLoader();
		
		String filename = "sample.csv";
		
		try{
			filename = args[0];
			System.out.println("Using filename:"+ filename);
		}catch(Exception e){
			System.out.println("Using Default filename:"+ filename);
		}
		
		fl.loadTrips(filename);
		CascadeConstructor.constructCascades(fl.getTrips());

//		//Perform General Stats
//		Stats.GeneralStats(fl.getTrips());
//		
//		
//		
//		HashMap<Date, ArrayList<WayPoint>> waypoints_timesequence = Stats.generateWayPointSequence(fl.getTrips());
//		
//		HashMap<Date, ArrayList<WayPoint>> waypoints_timesequence_matched = Stats.generateMatchedWayPointSequence(waypoints_timesequence);
//		
//		//Compare the trips (by primary route) + Save the results to file
//		HashMap<String,Integer> matchedWayPoints = new HashMap<>();
//		Boolean completed = false;
//		CompareMethods compareThreaded = new CompareMethods();
//		
//		matchedWayPoints = compareThreaded.CompareAllWaypoints(fl.getTrips());
//			
		
		
		//HashMap<String,Integer> matchedWayPoints = CompareMethods.CompareAllWaypoints(fl.getTrips());
//		System.out.println("Created Matched Waypoint List: "+ matchedWayPoints.size());		
//		MiscFunctions.printMatchedWayPoints(matchedWayPoints);
		
	}

}
