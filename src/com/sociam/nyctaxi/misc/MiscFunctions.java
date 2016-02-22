package com.sociam.nyctaxi.misc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.google.maps.model.LatLng;
import com.sociam.nyctaxi.objects.Route;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;



public class MiscFunctions {
	
	
	public static Date createUTCDate(String dateStr){
		Date date;
		try {
try{
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				date = dateFormat.parse(dateStr);
			}catch(Exception e1){
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				date = dateFormat.parse(dateStr);
			}
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
		

	}
	
	public static String convertDateTimeToString(Date date){
		SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date_to_string = dateformatyyyyMMdd.format(date);
		return date_to_string;
		
	}
	
	
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	

	
	
	public static String wordsToReplace(String word){
		return word.replaceAll("[^A-Za-z0-9]", "");
	}
	
	public static Date getTimestampToMinute(Date timestamp) {
		// TODO Auto-generated method stub
		String tmp = MiscFunctions.convertDateTimeToString(timestamp);
		//System.out.println(MiscFunctions.createUTCDate(tmp));
		return MiscFunctions.createUTCDate(tmp);
	}

	public static void printMatchedWayPoints(final HashMap<String, Integer> matchedWayPoints) {
		try{
		CSV csv =  CSV.separator(',').create(); 
		csv.write("matchedWayPoints.csv", new CSVWriteProc() {
		    public void process(CSVWriter out) {
		        out.writeNext("lat", 
		        			  "lng",
		        			  "count");
		        for(Map.Entry<String, Integer> entry : matchedWayPoints.entrySet()){
		        	String lat = entry.getKey().split(",")[0];
		        	String lng = entry.getKey().split(",")[1];
			        out.writeNext(lat,
			        			  lng,
			        			  String.valueOf(entry.getValue()));

		        }
		   }
		});
	
		}catch(Exception e){
			System.out.println("Couldnt save Matched WayPoints Output File");
			
		}
	}
	
	
	public static <T>List<List<T>> chopListIntoParts( final List<T> ls, final int iParts )
	{
	    final List<List<T>> lsParts = new ArrayList<List<T>>();
	    final int iChunkSize = ls.size() / iParts;
	    int iLeftOver = ls.size() % iParts;
	    int iTake = iChunkSize;

	    for( int i = 0, iT = ls.size(); i < iT; i += iTake )
	    {
	        if( iLeftOver > 0 )
	        {
	            iLeftOver--;

	            iTake = iChunkSize + 1;
	        }
	        else
	        {
	            iTake = iChunkSize;
	        }

	        lsParts.add( new ArrayList<T>( ls.subList( i, Math.min( iT, i + iTake ) ) ) );
	    }

	    return lsParts;
	}
	
	
	
	
	
	
}




