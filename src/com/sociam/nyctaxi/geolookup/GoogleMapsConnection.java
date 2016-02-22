package com.sociam.nyctaxi.geolookup;

import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.OverDailyLimitException;
import com.google.maps.errors.OverQueryLimitException;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.sociam.nyctaxi.file.FileLoader;
import com.sociam.nyctaxi.misc.MiscFunctions;
import com.sociam.nyctaxi.objects.Route;

public class GoogleMapsConnection {

	
	//Ramine
	//public static final String APIKEY = "AIzaSyDVB8wv_AbEe3HA4XLJx9yfvaEaDZTXMGs";
	
	//markus
	public static final String APIKEY = "AIzaSyCWzQE8Q5-XwPQPE3XnwcHnL8TlDFGo8II";

	
	DirectionsApi directions;
	GeoApiContext context;
	
	public GoogleMapsConnection() {
		
		context = new GeoApiContext().setApiKey(APIKEY);
		
	}
	
	

	public ArrayList<LatLng> getRoute(LatLng start, LatLng end){
		//use a map as it will remove duplicates
		HashMap<Double, Double> geoPoints_lnglat= new HashMap<Double, Double>();
		ArrayList<LatLng> allCoords = new ArrayList<LatLng>();
		
		try{
//			GeoApiContext context = new GeoApiContext().setApiKey(APIKEY);
			//DirectionsRoute[] routes = DirectionsApi.getDirections(context, "SO173RA", "EX387AJ").await();
			 DirectionsRoute[] routes = DirectionsApi.newRequest(context)
				        .mode(TravelMode.DRIVING)
//				        .avoid(RouteRestriction.HIGHWAYS, RouteRestriction.TOLLS, RouteRestriction.FERRIES)
				        .units(Unit.METRIC)
				        .region("us")
				        .origin(start)
				        .destination(end).await();
			
			for(int i=0; i<routes.length; i++){
				//System.out.println(routes[i]);
				for(int j=0; j<routes[i].legs.length; j++){
					LatLng startLeg = routes[i].legs[j].startLocation;
					LatLng endLeg = routes[i].legs[j].endLocation;					
					//System.out.printf("Leg %d, start Location %f %f. End Location %f %f \n", j, startLeg.lat, startLeg.lng, endLeg.lat, endLeg.lng);
					for(int k=0; k<routes[i].legs[j].steps.length; k++){
						//dont want to replicate the geo's, so we need to check if there are some small steps before adding bigger ones
						if(routes[i].legs[j].steps[k].steps == null){
							LatLng startStep = routes[i].legs[j].steps[k].startLocation;
							LatLng endStep = routes[i].legs[j].steps[k].endLocation;	
							//System.out.printf("\t Step %d, start Location %f %f. End Location %f %f \n", k, startStep.lat, startStep.lng, endStep.lat, endStep.lng);
							//allCoords.add(startStep);
							//allCoords.add(endStep);
							geoPoints_lnglat.put(startStep.lng, startStep.lat);
							geoPoints_lnglat.put(endStep.lng, endStep.lat);
						}else{
							try{
								for(int l=0; l<routes[i].legs[j].steps[k].steps.length; l++){
									
										LatLng startStepSmall = routes[i].legs[j].steps[k].steps[l].startLocation;
										LatLng endStepSmall = routes[i].legs[j].steps[k].steps[l].endLocation;	
										//System.out.printf("\t\t StepSmall %d, start Location %f %f. End Location %f %f \n", l, startStepSmall.lat, startStepSmall.lng, endStepSmall.lat, endStepSmall.lng);
										geoPoints_lnglat.put(startStepSmall.lng, startStepSmall.lat);
										geoPoints_lnglat.put(endStepSmall.lng, endStepSmall.lat);
										//allCoords.add(startStepSmall);
										//allCoords.add(endStepSmall);
								}
							}catch(Exception e){
								
							}
						}

					}

				}
			}
		
		}
		
		//if going over the limit, need to save the results....
		catch(OverQueryLimitException e){
			//need to wait for 24 hours really.
			return allCoords;
		}
		catch(OverDailyLimitException e){
			return allCoords;

			
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		
		//System.out.printf("-- TotalSteps %d \n", allCoords.size());
		System.out.printf("-- Total Unique Geo Points: %d \n", geoPoints_lnglat.size());

		//now throw them into LatLongs
		for(Map.Entry<Double, Double> geoLngLat: geoPoints_lnglat.entrySet()){
			allCoords.add(new LatLng(geoLngLat.getValue(), geoLngLat.getKey()));
		}
		
		return allCoords;

	}
	
	
	public static void getTestRoute(){
		ArrayList<LatLng> allCoords = new ArrayList<LatLng>();
		
		try{
			GeoApiContext context = new GeoApiContext().setApiKey(APIKEY);
			//DirectionsRoute[] routes = DirectionsApi.getDirections(context, "SO173RA", "EX387AJ").await();
			 DirectionsRoute[] routes = DirectionsApi.newRequest(context)
				        .mode(TravelMode.DRIVING)
//				        .avoid(RouteRestriction.HIGHWAYS, RouteRestriction.TOLLS, RouteRestriction.FERRIES)
				        .units(Unit.METRIC)
//				        .region("US")
				        .origin("SO171st")
				        .destination("EX387AJ").await();
			 
			 
			
			for(int i=0; i<routes.length; i++){
				System.out.println(routes[i]);
				for(int j=0; j<routes[i].legs.length; j++){
					LatLng startLeg = routes[i].legs[j].startLocation;
					LatLng endLeg = routes[i].legs[j].endLocation;					
					System.out.printf("Leg %d, start Location %f %f. End Location %f %f \n", j, startLeg.lat, startLeg.lng, endLeg.lat, endLeg.lng);
					for(int k=0; k<routes[i].legs[j].steps.length; k++){
						//dont want to replicate the geo's, so we need to check if there are some small steps before adding bigger ones
						if(routes[i].legs[j].steps[k].steps == null){
							LatLng startStep = routes[i].legs[j].steps[k].startLocation;
							LatLng endStep = routes[i].legs[j].steps[k].endLocation;	
							System.out.printf("\t Step %d, start Location %f %f. End Location %f %f \n", k, startStep.lat, startStep.lng, endStep.lat, endStep.lng);
							allCoords.add(startStep);
							allCoords.add(endStep);
						}else{
							try{
								for(int l=0; l<routes[i].legs[j].steps[k].steps.length; l++){
									
										LatLng startStepSmall = routes[i].legs[j].steps[k].steps[l].startLocation;
										LatLng endStepSmall = routes[i].legs[j].steps[k].steps[l].endLocation;	
										System.out.printf("\t\t StepSmall %d, start Location %f %f. End Location %f %f \n", l, startStepSmall.lat, startStepSmall.lng, endStepSmall.lat, endStepSmall.lng);
										allCoords.add(startStepSmall);
										allCoords.add(endStepSmall);
								}
							}catch(Exception e){
								
							}
						}

					}

				}
				//added break for now just so that it doesnt look for extra routes!
				break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		
		System.out.printf("-- TotalSteps %d \n", allCoords.size());
	}
	
	

	/**
	 * Do a look up for all routes in a given list with Google Maps and return a completed route object 
	 * @param medal_routes
	 * @param saveOutput 
	 * @return
	 */
	private HashMap<String, Route> findAllRoutesFromList(HashMap<String, Route> medal_routes, boolean saveOutput) {
		
		for(Map.Entry<String, Route> route : medal_routes.entrySet()){
			route.getValue().setWaypoints(getRoute(route.getValue().getStartCoord(), route.getValue().getEndCoord()));
		}
		if(saveOutput){
			saveRouteWaypointsToCSV(medal_routes);
		}
		return medal_routes;
		
	}

	public void saveRouteWaypointsToCSV(final HashMap<String, Route> medal_routes){
		
		CSV	csv =  CSV
			    .separator(',')
			    .quote('"')      
			    .create(); 
		
		csv.write("trips_lookedup.csv", new CSVWriteProc() {
		    public void process(CSVWriter out) {
		        out.writeNext("Medallion", 
		        			  "PickupTime",
		        			  "DropOffTime",
		        			  "Passengers",
		        			  "Distance",
		        			  "latlngList");
		        for(Map.Entry<String, Route> route : medal_routes.entrySet()){
		        	String list = "";
		        	for(LatLng waypoint: route.getValue().getWaypoints()){
		        		list = list + waypoint.lat+","+waypoint.lng+",";
		        	}
			        out.writeNext(route.getKey(),
			        		MiscFunctions.convertDateTimeToString(route.getValue().getStartTime()), 
			        		MiscFunctions.convertDateTimeToString(route.getValue().getEndTime()),
			        		String.valueOf(route.getValue().getPassengers()),
			        		String.valueOf(route.getValue().getDistanceKM()),
			        		list);

		        }
		   }
		});
		
		
	}
	
	public static void main(String[] args) {
		
		FileLoader fl = new FileLoader();
		HashMap<String, Route> medal_routes = fl.loadFromOriginalFiles("trips_23k_25k.csv");
		GoogleMapsConnection conn = new GoogleMapsConnection();
		medal_routes = conn.findAllRoutesFromList(medal_routes,true);
		

//		conn.getTestRoute();
//		
//		LatLng start = new LatLng(40.743137,-73.980072);
//		LatLng end = new LatLng(40.735336,-73.982712);
//		
//		conn.getRoute(start, end);
	}



		
		
}
