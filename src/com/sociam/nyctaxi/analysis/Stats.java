package com.sociam.nyctaxi.analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;

import com.google.maps.model.LatLng;
import com.sociam.nyctaxi.misc.MiscFunctions;
import com.sociam.nyctaxi.objects.Route;
import com.sociam.nyctaxi.objects.Trip;
import com.sociam.nyctaxi.objects.WayPoint;

public class Stats {
	
	
	
	public static void GeneralStats(ArrayList<Trip> trips){
		
		HashMap<String, Integer> medallion_dist = new HashMap<String, Integer>(); 
		double totalDistances = 0;
		double totalPassengers = 0;
		double totalWaypoints = 0;
		for(Trip trip : trips){
			//get primary route
			if(medallion_dist.containsKey(trip.getMedallionID())){
				medallion_dist.put(trip.getMedallionID(), medallion_dist.get(trip.getMedallionID())+1);
			}else{
				medallion_dist.put(trip.getMedallionID(), 1);
			}
			totalDistances = totalDistances + trip.getPrimaryRoute().getDistanceKM();
			totalPassengers = totalPassengers + trip.getPrimaryRoute().getPassengers();	
			totalWaypoints = totalWaypoints + trip.getPrimaryRoute().getWaypoints().size();	
		}
		System.out.printf("Total Trips %d. Unique Taxis %d\n",trips.size(), medallion_dist.size());
		System.out.printf("Total Distance %f. Average Distance %f\n",totalDistances, (totalDistances/trips.size()));
		System.out.printf("Total Passengers %f. Average Passenger %f\n",totalPassengers, (totalPassengers/trips.size()));
		System.out.printf("Total Waypoints: %f. Average Waypoints %f\n",totalWaypoints, (totalWaypoints/trips.size()));
	}

	
	/**
	 * Construct a Mapping between a timestamp YYYYMMDD:HHMMSS and a list of waypoints which happened a thtat point in time!
	 * @param trips
	 * @return
	 */
	
	public static HashMap<Date, ArrayList<WayPoint>> generateWayPointSequence(ArrayList<Trip> trips){
		
		HashMap<Date, ArrayList<WayPoint>> waypoints_timesequence = new HashMap<Date, ArrayList<WayPoint>>();
		
		for(Trip trip : trips){		
			ArrayList<LatLng> route_waypoints = trip.getPrimaryRoute().getWaypoints();
			
			long time_seconds = MiscFunctions.getDateDiff(trip.getPrimaryRoute().getStartTime() , trip.getPrimaryRoute().getEndTime(), TimeUnit.SECONDS);
			
			float time_delta_between_waypoints_seconds =  (float)time_seconds/route_waypoints.size();
			
			Date startTime = trip.getPrimaryRoute().getStartTime();
			
			Date waypoint_time = startTime;
			
			for(LatLng geo : route_waypoints){
				if(waypoint_time.equals(startTime)){
					trip.getPrimaryRoute().getWaypoints_timeSequence().put(waypoint_time, geo);				
				}else{
					waypoint_time = DateUtils.addSeconds(startTime, (int) time_delta_between_waypoints_seconds);
					trip.getPrimaryRoute().getWaypoints_timeSequence().put(waypoint_time, geo);
				}
			}
			
			
			for(Map.Entry<Date,LatLng> series : trip.getPrimaryRoute().getWaypoints_timeSequence().entrySet()){
				if(waypoints_timesequence.containsKey(series.getKey())){
					WayPoint wp = new WayPoint(series.getValue().lat, series.getValue().lng);
					wp.setAssociatedRoute(trip.getPrimaryRoute());
					wp.setTimestamp(series.getKey());
					wp.setAssociatedTrip(trip);

					waypoints_timesequence.get(series.getKey()).add(wp);
//					waypoints_timesequence.put(series.getKey(), waypoints_timesequence.get(series.getKey()).add(series.getValue()));
				}else{
					ArrayList<WayPoint> waypoints = new ArrayList<WayPoint>();
					WayPoint wp = new WayPoint(series.getValue().lat, series.getValue().lng);
					wp.setAssociatedRoute(trip.getPrimaryRoute());
					wp.setTimestamp(series.getKey());
					wp.setAssociatedTrip(trip);
					waypoints.add(wp);
					waypoints_timesequence.put(series.getKey(), waypoints);
				}
				
//				System.out.printf(" \n",MiscFunctions.convertDateTimeToString(series.getKey()), series.getValue().toUrlValue());
			}
			//
		}		
		System.out.printf("Grouped Waypoints by date: %d \n",waypoints_timesequence.size());
		for(Entry<Date, ArrayList<WayPoint>> series : waypoints_timesequence.entrySet()){
			
			//System.out.printf("%s, %d\n",MiscFunctions.convertDateTimeToString(series.getKey()),series.getValue().size());
			
			
		}
		
		return waypoints_timesequence;
	}
	
	
	



	public static HashMap<Date, ArrayList<WayPoint>> generateMatchedWayPointSequence(HashMap<Date, ArrayList<WayPoint>> waypoints_timesequence) {
		
		
		
		for(Map.Entry<Date, ArrayList<WayPoint>> dateWayPoints : waypoints_timesequence.entrySet()){
			boolean[][] checked = new boolean[dateWayPoints.getValue().size()][dateWayPoints.getValue().size()];
			
			for(int i=0; i<dateWayPoints.getValue().size(); i++){
				
				for(int j=0; j<dateWayPoints.getValue().size(); j++){
					
					if(i != j){
						if(!checked[i][j]){
							if((dateWayPoints.getValue().get(i).getLat() == dateWayPoints.getValue().get(j).getLat()) && (dateWayPoints.getValue().get(i).getLng() == dateWayPoints.getValue().get(j).getLng())){
								//System.out.printf("Matched Route %s with %s at time: %s ",dateWayPoints.getValue().get(i).getAssociatedTrip().getMedallionID(),dateWayPoints.getValue().get(j).getAssociatedTrip().getMedallionID(),dateWayPoints.getKey());
								//System.out.printf("Location: %s,%s \n",String.valueOf(dateWayPoints.getValue().get(i).getLat()),String.valueOf(dateWayPoints.getValue().get(i).getLng()));

							}
						}
					}
				checked[j][i] = true;
			}
				
				
			}
		}
		
		
		
		// TODO Auto-generated method stub
		return null;
	}
}
