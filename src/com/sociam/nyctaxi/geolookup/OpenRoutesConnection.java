package com.sociam.nyctaxi.geolookup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.maps.model.LatLng;
import com.sociam.nyctaxi.file.FileLoader;
import com.sociam.nyctaxi.misc.MiscFunctions;
import com.sociam.nyctaxi.objects.Route;

public class OpenRoutesConnection {
	
	
	HashMap<String, Route> routes;
	private static final int MYTHREADS = 30;
	ExecutorService executor;

	
	
	public OpenRoutesConnection() {
		// TODO Auto-generated constructor stub
		routes = new HashMap<String, Route>();
		executor = Executors.newFixedThreadPool(MYTHREADS);
	
	}
	
	
	public static ArrayList<LatLng> lookupRoute(LatLng startCoord, LatLng endCoord) throws Exception {
		
		ArrayList<LatLng> allCoords = new ArrayList<LatLng>();

		
		// TODO Auto-generated method stub
		String start = String.valueOf(startCoord.lat)+","+String.valueOf(startCoord.lng);
		String end = String.valueOf(endCoord.lat)+","+String.valueOf(endCoord.lng);
		//http://openls.geog.uni-heidelberg.de/testing2015/route?Start=8.6817521,49.4173462&End=8.6828883,49.4067577&Via=&lang=de&distunit=KM&routepref=Fastest&avoidAreas=&useTMC=false&noMotorways=false&noTollways=false&instructions=true
//		System.out.println("http://openls.geog.uni-heidelberg.de/testing2015/route?Start="+
//				start+"&End="+end+
//				"&Via=&lang=en&distunit=KM&routepref=Fastest&avoidAreas=&useTMC=false&noMotorways=false&noTollways=false&instructions=false");
		URL url = new URL("http://www.yournavigation.org/api/1.0/gosmore.php?format=kml&flat="+
				 String.valueOf(startCoord.lat)+
						"&flon="+
						String.valueOf(startCoord.lng)+
						"&tlat="+
						String.valueOf(endCoord.lat)+
						"&tlon="+
						String.valueOf(endCoord.lng)+
						"&v=motorcar&fast=1&layer=mapnik");
	   //URL url = new URL("http://openls.geog.uni-heidelberg.de/testing2015/route?Start=8.6817521,49.4173462&End=8.6828883,49.4067577&Via=&lang=en&distunit=KM&routepref=Fastest&avoidAreas=&useTMC=false&noMotorways=false&noTollways=false&instructions=false");

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setRequestMethod("GET");
	    //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	    //conn.setRequestProperty("Accept", "application/vnd.zooevents.stream.v1+json");

	    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    StringBuilder stb = new StringBuilder();

	    String line;
	    Boolean startParsing =false;
	    while ((line = br.readLine()) != null) {
	      //stb.append(line);
	    	//System.out.println(line);
	    	//dont want to pass XML as it's too high cost.		
	    		if(line.contains("<coordinates>")){
	    			startParsing = true;
	    		}
	    		
	    		if(line.contains("</coordinates>")){
	    			startParsing = false;
	    		}
	    		
	    		if(startParsing){
	    			//coord line
	    			String coordstr = line.replace("<coordinates>", "").trim();
	    			//System.out.println(coordstr);
	    			LatLng coord = new LatLng(Double.parseDouble(coordstr.split(",")[0]), Double.parseDouble(coordstr.split(",")[1]));
	    			allCoords.add(coord);
	    		}
	    	
	    }

	    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

	    	//System.out.println("Success: "+ conn.getResponseCode() + conn.getResponseMessage());
	    //	System.out.println("Total Coord points added: "+allCoords.size());

	    }
	    else if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
	    
	    	throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode() + conn.getResponseMessage());

	    }
		return allCoords;
	}
		
	
	/**
	 * Do a look up for all routes in a given list with Google Maps and return a completed route object 
	 * @param medal_routes
	 * @param saveOutput 
	 * @return
	 */
	private HashMap<String, Route> findAllRoutesFromList(HashMap<String, Route> medal_routes, boolean saveOutput) throws Exception{
		
		int tally = 0;
		int total = medal_routes.size();
		for(Map.Entry<String, Route> route : medal_routes.entrySet()){
			route.getValue().setWaypoints(lookupRoute(route.getValue().getStartCoord(), route.getValue().getEndCoord()));
			tally++;
			System.out.println(tally+"/"+total);
		}
		if(saveOutput){
			saveRouteWaypointsToCSV(medal_routes, 0);
		}
		return medal_routes;
		
	}

	
	private void findAllRoutesFromListChunks(ArrayList<HashMap<String, Route>> medal_routes_chunks, boolean saveOutput) throws Exception{
		
		int tally = 0;

		for(HashMap<String, Route> medal_routes : medal_routes_chunks ){
			
			Runnable workerTwo = new MyRunnable(medal_routes, tally);
	    	executor.execute(workerTwo);
	    	tally++;
	    }
			
		
	}
	
	
	public static void saveRouteWaypointsToCSV(final HashMap<String, Route> medal_routes, int tally){
		
		CSV	csv =  CSV
			    .separator(',')
			    .quote('"')      
			    .create(); 
		
		csv.write("trips_lookedup_openRoute_part_"+Integer.toString(tally)+".csv", new CSVWriteProc() {
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
		
		
		System.out.printf("OpenRoutes API \n");
		System.out.printf("First parameter is filename containing route data \n");

		
		FileLoader fl = new FileLoader();
		String filename = "";
		try{
			filename = args[0];
		}catch(Exception e){
			
			filename = "trips.csv";
		}
		
		ArrayList<HashMap<String, Route>> medal_routes_chunks = fl.loadFromOriginalFilesIntoParts(filename);
		System.out.println("Sub lists for Threads: "+medal_routes_chunks.size());

		OpenRoutesConnection openRoute = new OpenRoutesConnection();

		try {
			openRoute.findAllRoutesFromListChunks(medal_routes_chunks,true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	
	
	public static class MyRunnable implements Runnable {

		  	private final HashMap<String, Route> medal_routes;
		  	private final int tally;
			
			MyRunnable(HashMap<String, Route> medal_routes, int tally) {
				
				this.medal_routes = medal_routes;
				this.tally = tally;
			}

			@Override
			public void run() {
				
				int processed = 0;
				int total = medal_routes.size();
				for(Map.Entry<String, Route> route : medal_routes.entrySet()){
					try{
						route.getValue().setWaypoints(lookupRoute(route.getValue().getStartCoord(), route.getValue().getEndCoord()));
						processed++;
						System.out.println("thread:"+tally+" "+processed+"/"+total);
					}catch(Exception e){
						
					}
				}
				//if(saveOutput){
					saveRouteWaypointsToCSV(medal_routes, tally);
				//}
			
			}
				
				

			}
		
	  
	
	

}
