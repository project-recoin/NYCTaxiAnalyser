package com.sociam.nyctaxi.objects;

import java.util.HashMap;

public class Trip {
	
	
	private String medallionID;
	Route primaryRoute;
	HashMap<Integer, Route> routes;
	
	
	public Trip() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void setRoutes(HashMap<Integer, Route> routes) {
		this.routes = routes;
	}public HashMap<Integer, Route> getRoutes() {
		return routes;
	}
	
	public void setMedallionID(String tripID) {
		this.medallionID = tripID;
	}public String getMedallionID() {
		return medallionID;
	}


	public void setPrimaryRoute(Route route) {
		primaryRoute = route;
	}
	
	public Route getPrimaryRoute() {
		return primaryRoute;
	}
	
	
}
