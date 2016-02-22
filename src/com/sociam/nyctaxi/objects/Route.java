package com.sociam.nyctaxi.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.maps.model.LatLng;

public class Route {

	Date startTime; //2013-01-01 15:11:48
	Date endTime;
	LatLng startCoord;
	LatLng endCoord;
	ArrayList<LatLng> waypoints;
	double distanceKM;
	int passengers;
	HashMap<Date,LatLng> waypoints_timeSequence;
	
	public Route() {
		waypoints = new  ArrayList<LatLng>();
		waypoints_timeSequence = new HashMap<Date, LatLng>();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void setWaypoints(ArrayList<LatLng> waypoints) {
		this.waypoints = waypoints;
	}public void setStartCoord(LatLng start) {
		this.startCoord = start;
	}public void setEndCoord(LatLng endCoord) {
		this.endCoord = endCoord;
	}public ArrayList<LatLng> getWaypoints() {
		return waypoints;
	}public LatLng getStartCoord() {
		return startCoord;
	}public LatLng getEndCoord() {
		return endCoord;
	}public void setStartTime(Date date) {
		this.startTime = date;
	}public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}public Date getStartTime() {
		return startTime;
	}public Date getEndTime() {
		return endTime;
	}public void setDistanceKM(double distanceKM) {
		this.distanceKM = distanceKM;
	}public double getDistanceKM() {
		return distanceKM;
	}public void setPassengers(int passengers) {
		this.passengers = passengers;
	}public int getPassengers() {
		return passengers;
	}public void setWaypoints_timeSequence(
			HashMap<Date, LatLng> waypoints_timeSequence) {
		this.waypoints_timeSequence = waypoints_timeSequence;
	}public HashMap<Date, LatLng> getWaypoints_timeSequence() {
		return waypoints_timeSequence;
	}
	
	
}
