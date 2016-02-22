package com.sociam.nyctaxi.file.converter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReadProc;

import com.google.maps.model.LatLng;
import com.sociam.nyctaxi.misc.MiscFunctions;
import com.sociam.nyctaxi.objects.Route;
import com.sociam.nyctaxi.objects.Trip;

public class CSVtoSQL {

	
	//to-do
		CSV csv;
		ArrayList<String> header;
		String headerStr;
		ArrayList<String> rows;
		FileWriter fw;
		BufferedWriter bw;
		public CSVtoSQL() {
			// TODO Auto-generated constructor stub
			csv =  CSV
				    .separator(',')
				    .quote('"')
				    .lineEnd("\n")
				    .create(); 
			header = new ArrayList<String>();
			headerStr = "";
			rows = new ArrayList<String>();
		}

	
	public void readFile(String filename){
		
		try{
			csv.read(filename, new CSVReadProc() {
				
				@Override
				public void procRow(int rowIndex, String... values) {
					String row = "";
					List<String> columns = Arrays.asList(values);
					//header
					if(rowIndex==0){
						for(String column : columns){
							headerStr = headerStr + ","+column;
						}
						
					}else{
						for(String column : columns){
							if(isNumeric(column)){
								row = row + ","+column;
							}else{
								row = row + "," + "'"+column+"'";
							}
						}
						row = row.substring(1, row.length());
						rows.add(row);
					}
				
				}      
				
			});
			
			//remove trailing comma
			headerStr = headerStr.substring(1, headerStr.length());
			System.out.println(headerStr);
			System.out.println("total rows: "+rows.size());

		 }catch(Exception e){
			 e.printStackTrace();
		 }
		
	}
	
public void readFileAndWriteInserts(String filename, final String tableName, final int fromRow){
		
	
	
		try{
			fw = new FileWriter("SQLINSERTS_"+tableName+".sql");
			bw = new BufferedWriter(fw);
			
			csv.read(filename, new CSVReadProc() {
				
				String output = "";
				
				@Override
				public void procRow(int rowIndex, String... values) {
					String row = "";
					List<String> columns = Arrays.asList(values);
					//header
					if(rowIndex==0){
						for(String column : columns){
							headerStr = headerStr + ","+column;
						}
						//remove trailing comma
					headerStr = headerStr.substring(1, headerStr.length());	
					}else{
						
						try{
							
							if(rowIndex>fromRow){
								for(String column : columns){
									if(isNumeric(column)){
										row = row + ","+column;
										//System.out.println(row);
									}else{
										row = row + "," + "'"+column+"'";
									}
								}
								row = row.substring(1, row.length());
								//rows.add(row);
								output = "INSERT INTO "+tableName+"("+headerStr+") VALUES ("+row+");";
								try{
									bw.write(output+"\n");
								}catch(Exception e){
									e.printStackTrace();
								}
								System.out.println(rowIndex);

							}
						}catch(Exception e1){
							//row problem
							e1.printStackTrace();
						}
						
					}
				}    
				
				
			});
			bw.close();
			fw.close();

			System.out.println(headerStr);
			System.out.println("total rows: "+rows.size());
			
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		
	}
	
	
	
	private void createInsertStatements(String tableName) {
		try{
			FileWriter fw = new FileWriter("SQLINSERTS_"+tableName+".sql");
			BufferedWriter bw = new BufferedWriter(fw);
			String output = "";
			for(String row : rows){
				output = "INSERT INTO "+tableName+"("+headerStr+") VALUES ("+row+");";
				bw.write(output+"\n");
			}
			bw.close();
			fw.close();
			
			
		}catch(Exception e){
			
			
		}
	}
	
	
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	
	
//	INSERT INTO mytable(name,imo,mmsi,d_dwt,d_gross_tonnage,length,d_my_vessel_type,d_owner,d_manager,draught,time_started,time_finished,berth,port,locode,portcountry) VALUES ('MASTERY D',9301201,636092240,10744,8971,155.00,'container_ship','MASTERY D SCHIFFAHRTS','HAMMONIA REEDEREI GMBH',NULL,'2015-03-31 10:19:59.255','2015-04-01 18:32:41.279','Calcutta Port Trust Container Terminal','Kolkata','INCCU','India');

	public static void main(String[] args) {

		CSVtoSQL converter = new CSVtoSQL();
		//converter.readFile("D:/Dropbox/PhD/Web Observatory/Datathon 2015/Genscape/genscape_20150401.csv");
		//C:\Users\SOCIAM-Demo\Dropbox\PhD\Web Observatory\Datathon 2015\Genscape
		converter.readFileAndWriteInserts("C:/Users/SOCIAM-Demo/Dropbox/PhD/Web Observatory/Datathon 2015/Genscape/genscape_20150401_to_20150531_2.csv","genscape_20150401_to_20150531_2", 0);
		converter.createInsertStatements("genscape_20150401_to_20150531");
		
	}


	

}
