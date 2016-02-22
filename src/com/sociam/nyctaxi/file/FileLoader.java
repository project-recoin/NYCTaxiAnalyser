package com.sociam.nyctaxi.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReadProc;

import com.google.maps.model.LatLng;
import com.sociam.nyctaxi.misc.MiscFunctions;
import com.sociam.nyctaxi.objects.WayPoint;
import com.sociam.nyctaxi.objects.Route;
import com.sociam.nyctaxi.objects.Trip;

public class FileLoader {

	//to-do
	ArrayList<Trip> trips;
	CSV csv;

	
	public FileLoader() {
		// TODO Auto-generated constructor stub
		trips = new ArrayList<Trip>();
		csv =  CSV
			    .separator(',')
			    .quote('"')      
			    .create(); 
	}

	
	public HashMap<String, Route> loadFromOriginalFiles(String filename){
		 final HashMap<String, Route> medal_routes = new HashMap<String, Route>();
		 try{
			csv.read(filename, new CSVReadProc() {
				
				@Override
				public void procRow(int rowIndex, String... values) {
			        //System.out.println(rowIndex + ": " + Arrays.asList(values));
			        Route r = new Route();
			        try{
			        	LatLng start = new LatLng(Double.parseDouble(values[11]), Double.parseDouble(values[10]));
			        	LatLng end = new LatLng(Double.parseDouble(values[13]), Double.parseDouble(values[12]));
			        	r.setStartCoord(start);
			        	r.setEndCoord(end);
			        	r.setDistanceKM(Double.parseDouble(values[9]));
			        	r.setStartTime(MiscFunctions.createUTCDate(values[5]));
			        	r.setEndTime(MiscFunctions.createUTCDate(values[6]));
			        	r.setPassengers(Integer.parseInt(values[7]));
			        	medal_routes.put(values[0], r);
			        }catch(Exception e){
			        	
			        }
				}
			});
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return medal_routes;
	}
	
	
	public ArrayList<HashMap<String, Route>> loadFromOriginalFilesIntoParts(String filename){
		 final ArrayList< HashMap<String, Route>> routes_array = new ArrayList<HashMap<String,Route>>();
		 
		// final HashMap<String, Route> medal_routes = new HashMap<String, Route>();
		 
		 try{
			csv.read(filename, new CSVReadProc() {
				HashMap<String, Route> medal_routes = new HashMap<String, Route>();
				@Override
				public void procRow(int rowIndex, String... values) {
			        //System.out.println(rowIndex + ": " + Arrays.asList(values));
			        Route r = new Route();
			        try{
			        	LatLng start = new LatLng(Double.parseDouble(values[11]), Double.parseDouble(values[10]));
			        	LatLng end = new LatLng(Double.parseDouble(values[13]), Double.parseDouble(values[12]));
			        	r.setStartCoord(start);
			        	r.setEndCoord(end);
			        	r.setDistanceKM(Double.parseDouble(values[9]));
			        	r.setStartTime(MiscFunctions.createUTCDate(values[5]));
			        	r.setEndTime(MiscFunctions.createUTCDate(values[6]));
			        	r.setPassengers(Integer.parseInt(values[7]));
			        	medal_routes.put(values[0], r);
			        	if(medal_routes.size()>200){
			        		routes_array.add(medal_routes);
			        		medal_routes = new HashMap<String, Route>();
			        	}
			        }catch(Exception e){
			        	
			        }
				}
			});
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return routes_array;
	}
	
	public void loadTrips(String filename){
		try{
			//logger.
			//System.out.println("Loading Trips.");
			FileReader reader = new FileReader(filename);
			BufferedReader br = new BufferedReader(reader);
			String currentLine;
			Trip trip;
			//encoding trips. tripID,lat:lng|lat:lng|
			csv.read(filename, new CSVReadProc() {
				@Override
				public void procRow(int rowIndex, String... values) {
					try{
					Trip trip = new Trip();
			        //System.out.println(rowIndex + ": " + Arrays.asList(values));
			        Route route = new Route();
			        route.setStartTime(MiscFunctions.createUTCDate(values[1]));
		        	route.setEndTime(MiscFunctions.createUTCDate(values[2]));
		        	route.setPassengers(Integer.parseInt(values[3]));
		        	route.setDistanceKM(Double.parseDouble(values[4]));
		        	String[] geos = values[5].split(",");
		        	for(int i=0; i<geos.length; i++){
		        		try{
		        			LatLng geo = new LatLng(Double.parseDouble(geos[i]),Double.parseDouble(geos[i+1]));
		        			route.getWaypoints().add(geo);
		        		}catch(Exception e){
		        			//e.printStackTrace();
		        		}
		        	}
		        	trip.setMedallionID(values[0]);
		        	trip.setPrimaryRoute(route);
					trips.add(trip);
					}catch(Exception e){
						//probably a header...
					}

				}
			});			
			System.out.printf("Loaded %d trips\n", trips.size());
			//System.out.printf("Loaded %d waypoints", points);

			
		}catch (Exception e) {
			System.out.println("Couldn't Load Files");
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public ArrayList<Trip> getTrips() {
		return trips;
	}
	

	


}
