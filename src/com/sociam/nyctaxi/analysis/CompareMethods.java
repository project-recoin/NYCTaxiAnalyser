package com.sociam.nyctaxi.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.maps.model.LatLng;
import com.sociam.nyctaxi.objects.WayPoint;
import com.sociam.nyctaxi.objects.Trip;

public class CompareMethods {

	
	
	/**
	 * 
	 * @param trips
	 * @return a map which contains the key latlng coords as a string, concat and seperated by a comma (,). 
	 * Value is the reoccurance count of the latlng 
	 */
	public static HashMap<String, Integer> CompareAllWaypoints(ArrayList<Trip> trips){
		
		HashMap<Double,Double> allwaypoints= new HashMap<Double, Double>();
		//hacking it to do match <lat,long>
		HashMap<String, Integer> allmatchedwaypoints= new HashMap<String, Integer>();
		Trip outer;
		Trip inner;
		String key="";
		for(int i=0; i<trips.size(); i++){
			outer = trips.get(i);
			for(int j=0; j<trips.size(); j++){
				inner = trips.get(j);
				
				//make sure they're not the same
				if(i != j){
					for(LatLng outerCoord : outer.getPrimaryRoute().getWaypoints()){
						
						for(LatLng innerCoord : inner.getPrimaryRoute().getWaypoints()){
							
							if(CompareGeoCoords(outerCoord, innerCoord)){
								//matched++;
								//System.out.printf("Matched at: %f %f \n",outerCoord.lat, outerCoord.lng);
								key = Double.toString(outerCoord.lat)+","+Double.toString(outerCoord.lng);
								if(allmatchedwaypoints.containsKey(key)){
									allmatchedwaypoints.put(key, allmatchedwaypoints.get(key)+1);
									//System.out.println("Found");
								}else{
									allmatchedwaypoints.put(key, 1);

								}
							}
							allwaypoints.put(outerCoord.lat, outerCoord.lng);
							allwaypoints.put(innerCoord.lat, innerCoord.lng);
						}
					}
				}
								
				
			}
			System.out.println("In Loop: "+i+" of:"+ trips.size());

			
		}
		System.out.printf("Total Unique Waypoints: %d. Matched Total: %d \n",allwaypoints.size(),allmatchedwaypoints.size());
		for(Map.Entry<String,Integer> entry: allmatchedwaypoints.entrySet()){
			//System.out.printf("%s,%d\n",entry.getKey(),entry.getValue());
		}
		return allmatchedwaypoints;
		
	}
	
	
	public static Boolean CompareGeoCoords(LatLng x, LatLng y){
		
		if((x.lat == y.lat) && (x.lng == y.lng)){
			return true;
		}else{
			return false;
		}
		
		
	}
	
	
	
}
