package com.sociam.nyctaxi.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.maps.model.LatLng;
import com.sociam.nyctaxi.objects.WayPoint;

import com.sociam.nyctaxi.geolookup.OpenRoutesConnection.MyRunnable;
import com.sociam.nyctaxi.misc.MiscFunctions;
import com.sociam.nyctaxi.objects.Route;
import com.sociam.nyctaxi.objects.Trip;

public class CompareMethodThreaded {

	private static final int MYTHREADS = 10;
	private ExecutorService executor;
	private HashMap<String, Integer> allmatchedwaypoints;
	private HashMap<Double, Double> allwaypoints;
	private int finishedThreads;


	public CompareMethodThreaded() {
		// TODO Auto-generated constructor stub

		allmatchedwaypoints = new HashMap<String, Integer>();
		allwaypoints = new HashMap<Double, Double>();
		executor = Executors.newFixedThreadPool(MYTHREADS);


	}
	
	/**
	 * 
	 * @param trips
	 * @return a map which contains the key latlng coords as a string, concat
	 *         and seperated by a comma (,). Value is the reoccurance count of
	 *         the latlng
	 */
	public Boolean CompareAllWaypoints(final ArrayList<Trip> trips) {

		finishedThreads = 0;
		int tripSize = trips.size();
		List<List<Trip>> tripParts = MiscFunctions.chopListIntoParts(trips, MYTHREADS);
		
		
		System.out.println("Total List Size: " + tripSize );

		
		
		for (List<Trip> tripPart : tripParts) {
			System.out.println("List Size for thread: " + tripPart.size());
			Runnable worker = new compareThread(trips, (ArrayList<Trip>) tripPart, allmatchedwaypoints, allwaypoints);
			executor.execute(worker);
		
		}
		
		try{
			executor.wait();
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
//		if(executor.isTerminated()){
//			System.out.println("Shutdown Threads");
//		}
		//		
//		while(finishedThreads!=MYTHREADS){
//		// 	System.out.println("Processing: "+allLoopCnt+" / "+ tripSize);
//
//		}
//		System.out.println("All Threads Finished");
//
//		
//		System.out.printf("Total Unique Waypoints: %d. Matched Total: %d \n", allwaypoints.size(),
//				allmatchedwaypoints.size());
//		 for(Map.Entry<String,Integer> entry: allmatchedwaypoints.entrySet()){
//		 //System.out.printf("%s,%d\n",entry.getKey(),entry.getValue());
//		 
//		 }

		return true;

	}

	public static Boolean CompareGeoCoords(LatLng x, LatLng y) {

		if ((x.lat == y.lat) && (x.lng == y.lng)) {
			return true;
		} else {
			return false;
		}

	}
	
	
	public HashMap<String, Integer> getAllmatchedwaypoints() {
		return allmatchedwaypoints;
	}public HashMap<Double, Double> getAllwaypoints() {
		return allwaypoints;
	}
	
	public static class compareThread implements Runnable {

		private final ArrayList<Trip> trips;
		private final ArrayList<Trip> tripPart;
		private final HashMap<String, Integer> allmatchedwaypoints;
		private final HashMap<Double, Double> allwaypoints;
			
		compareThread(ArrayList<Trip> trips, ArrayList<Trip> tripPart, HashMap<String, Integer> allmatchedwaypoints,
				HashMap<Double, Double> allwaypoints) {
			this.trips = trips;
			this.tripPart = tripPart;
			this.allmatchedwaypoints = allmatchedwaypoints;
			this.allwaypoints = allwaypoints;
		}

		@Override
		public void run() {

			Trip outer;
			Trip inner;
			// int matched=0;
			String key = "";
			for (int i = 0; i < tripPart.size(); i++) {
				outer = tripPart.get(i);
				for (int j = 0; j < trips.size(); j++) {
					inner = trips.get(j);

					// make sure they're not the same
					if (i != j) {
						for (LatLng outerCoord : outer.getPrimaryRoute().getWaypoints()) {

							for (LatLng innerCoord : inner.getPrimaryRoute().getWaypoints()) {

								if (CompareGeoCoords(outerCoord, innerCoord)) {
									// matched++;
									// System.out.printf("Matched at: %f %f
									// \n",outerCoord.lat, outerCoord.lng);
									key = Double.toString(outerCoord.lat) + "," + Double.toString(outerCoord.lng);
									if (allmatchedwaypoints.containsKey(key)) {
										allmatchedwaypoints.put(key, allmatchedwaypoints.get(key) + 1);
										//System.out.println("Found");
									} else {
										allmatchedwaypoints.put(key, 1);

									}
								}
								allwaypoints.put(outerCoord.lat, outerCoord.lng);
								allwaypoints.put(innerCoord.lat, innerCoord.lng);
							}
						}
					}

				}
				System.out.println("In Loop: "+i+" of:"+ tripPart.size());
				//System.out.println("All Loop Count:" +allLoopCnt);


			}
			System.out.println("Size of List in thread: "+allmatchedwaypoints.size());
			System.out.println("Size of Non-Matched List in thread: "+allwaypoints.size());
			
			

			//System.out.println("Thread count so far::  "+finishedThreads);


		}

	}

}
