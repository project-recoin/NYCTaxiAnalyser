package com.sociam.nyctaxi.objects;

import java.util.Date;

public class WayPoint {
	
	
	Trip associatedTrip;
	Route associatedRoute;
	Date timestamp;
	double lat;
	double lng;
	
	
	public WayPoint() {
		// TODO Auto-generated constructor stub
	}
	
	
	public WayPoint(double lat2, double lng2){
		
		this.lat = lat2;
		this.lng = lng2;
		
	}
	
	
	public void setLng(double lng) {
		this.lng = lng;
	}public void setLat(double lat) {
		this.lat = lat;
	}public double getLng() {
		return lng;
	}public double getLat() {
		return lat;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}public void setAssociatedRoute(Route associatedRoute) {
		this.associatedRoute = associatedRoute;
	}public Date getTimestamp() {
		return timestamp;
	}public Route getAssociatedRoute() {
		return associatedRoute;
	}
	public void setAssociatedTrip(Trip associatedTrip) {
		this.associatedTrip = associatedTrip;
	}public Trip getAssociatedTrip() {
		return associatedTrip;
	}

}
