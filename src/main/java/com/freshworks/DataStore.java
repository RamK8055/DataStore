package com.freshworks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;
//import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataStore {
	private String fileName;
	private final int defaultTimeToLive = 0;
	private final DataStore self = this;
	final static Logger logger = Logger.getLogger(DataStore.class.getName());
	 
	/**  Constructor: Setting Default DB path or Manual DB path  **/
	public DataStore(){
		this.fileName = "DataStoreDB.txt";
	}
	public DataStore(String DBname){
		this.fileName = DBname;
	}
	
	
	/**	 CREATE methods with or without time-to-live key  **/
	public int create(String key, Object value){
		return create(key, value, defaultTimeToLive);
	}
	
	public int create(String key, Object value, int second){
		int result = 1;
		/**  Checking whether key is already present or not **/
		try {
			List<String> allLines = Files.readAllLines(Paths.get(this.fileName));
			if(allLines!=null)
			for (String line : allLines) {
				if(line.startsWith(key)){
					result = 0;
					logger.info("Entered Key is already present\nEnter a new Key");
					break;
				}
			}
		} catch (IOException e) {
		} 

		if(result ==1){
			
			/**  Convert Value(Object) to JSON Object  **/
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = "";
			    
			try {
				
				jsonString = mapper.writeValueAsString(value);
			} catch (IOException e) {
				result = 0;
				logger.info("Exception happens on Converting Object into JSON");
			}

			/**  Appending key-value pair or if not exists, else create DB and appending it  **/
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(this.fileName, true));
				out.write(key+":"+jsonString+"\n");
				out.close();
			} catch (IOException e) {
				logger.info("Exception in writing into DB");
			}
			
			/**  Calling Time-To-Live Thread - An optional session time out for key   **/
			if(second!=0)
				new TimeToLive(second, key).start();
		}
		return result;
	}

	
	/**  READ key Json value  **/
	public JSONObject read(String key){
		
		/**  Get the value as Json format  **/
		String jsonString = null;
		boolean result = true;
		try {
			List<String> allLines = Files.readAllLines(Paths.get(this.fileName));
			for (String line : allLines) {
				if(line.startsWith(key+":")){
					jsonString = line.substring(line.indexOf(":")+1);
					result = false;
				}
			}
		} catch (IOException e) {
			logger.info("Exception in Reading Database");
		} 

		/**  Return the value  **/
		if(result){
			logger.info("Key is not found in DB");
			return null;
		}else{
			return new JSONObject(jsonString);
		}
	}
	
	
	/**  DELETE function with help of key  **/
	public void delete(String key) {
		
		boolean result = false;

		/**  Verify the Key is already present or not  **/
		try {
			List<String> allLines = Files.readAllLines(Paths.get(this.fileName));
			List<String> updatedLines = new ArrayList<>();
			for (String line : allLines) {
				if(line.startsWith(key))
					result = true;
				else
					updatedLines.add(line);
			}
			
			/**  Delete only if the key is present  **/
			if(result){
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(this.fileName));
					for(String line: updatedLines)
						out.write(line+"\n");
					out.close();
				} catch (IOException e) {
					logger.info("Exception in Deleting files");
				}
			}
		} catch (IOException e) {
			logger.info("Exception in Reading Database");
		} 
	}
	
	@Override
    public String toString() {
        return self.getClass().getName();
   }
}



/**  A Thread class to maintain session of the Key  **/
class TimeToLive extends Thread{

	private int second;
	private String key;
	
	/**  Constructor used for setting time and key  **/
	TimeToLive(){}
	TimeToLive(int second, String key){
		this.second = second;
		this.key = key;
	}
	
	public void run(){
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
			DataStore.logger.info("Exeption on TimeToLive object");
		}
		new DataStore().delete(key);
	}
}