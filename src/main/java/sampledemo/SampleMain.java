package sampledemo;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshworks.DataStore;

/** 
 * THIS CLASS WILL NOT INCLUDE IN JAR
 * SampleMain class is just a template or example class for How to use DataStore 
 * 
 **/

public class SampleMain {
	
	/**
		COVERED SCENARIOS:
		==================
		Default constructor using for default DB name and location
		parameterized constructor using for user given location and DB name
		
		Creating 5 entries with and without Time-to-Live property
		Creating a entry with existing(duplicate) key ==> Throw error as already key is present
		Read key1(having property of 10 second time to live)
		Wait for 12 Seconds
		Read Key1 ==> Key was expired so deleted from the DB
		Delete 2 Entries
		Read existing entry
		Read Non existing Entry ==> throw error as key is not present in database
	 **/	

	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
	
		DataStore ds = new DataStore();
		//Use below for windows specific path if need any perticular location
		//DataStore ds = new DataStore("C:\\Users\\Ram Kumar\\Documents\\WorkSpaces\\EclipseMars2\\ServletEg\\Freshworks\\abc.txt");


		System.out.println("-->Creating 5 entry");

		/** Creating with Time-To-Live property (will auto delete in 10 seconds **/
		ds.create("key1", new SamplePojo(1,"name1","address1"), 10);

		/** Creating normal entries **/
		ds.create("key2", new SamplePojo(2,"name2","address2"));
		ds.create("key3", new SamplePojo(3,"name3","address3"));
		ds.create("key4", new SamplePojo(4,"name4","address4"));
		ds.create("key5", new SamplePojo(5,"name5","address5"));
		
		System.out.println("-->Creating Duplicate Entry");
		ds.create("key5", new SamplePojo(5,"anyName","anyAddress"));
		
		/**  Reading key values  **/
		System.out.println("-->Read first Key (key1)");
		SamplePojo samplePojo1 = new ObjectMapper().readValue(ds.read("key1").toString(), SamplePojo.class);
		System.out.println(samplePojo1);
		
		/**  Sleep for 12 seconds for key 1 to get expired **/
		System.out.println("-->Sleep for 12seconds is started");
		try {
			Thread.sleep(12 * 1000);
		} catch (InterruptedException e) {
		}
		
		/**  Reading Expired key  **/
		try{
			System.out.println("-->Read expired Key (key1)");
			System.out.println(new ObjectMapper().readValue(ds.read("key1").toString(), SamplePojo.class));
		}
		catch(NullPointerException e){
		}
		
		
		/**  Delete Keys  **/
		System.out.println("--> Deleting key4 and key5");
		ds.delete("key4");
		ds.delete("key5");

		/**  Reading existing key and deleted key  **/
		System.out.println("--> Reading existing key");
		SamplePojo pj3 = new ObjectMapper().readValue(ds.read("key2").toString(), SamplePojo.class);
		System.out.println(pj3);
		
		System.out.println("--> Reading deleted key");
		try{		
			System.out.println(new ObjectMapper().readValue(ds.read("key5").toString(), SamplePojo.class));
		}
		catch(NullPointerException e){
		}

	}
}



/**
	CONSOLE OUTPUT AND LOGS:
	========================
		-->Creating 5 entry
		-->Creating Duplicate Entry
		Mar 04, 2020 10:08:56 AM com.freshworks.DataStore create
		INFO: Entered Key is already present
		Enter a new Key
		-->Read first Key (key1)
		SamplePOJO: 1-name1-address1
		-->Sleep for 12seconds is started
		-->Read expired Key (key1)
		Mar 04, 2020 10:09:08 AM com.freshworks.DataStore read
		INFO: Key is not found in DB
		--> Deleting key4 and key5
		--> Reading existing key
		SamplePOJO: 2-name2-address2
		--> Reading deleted key
		Mar 04, 2020 10:09:08 AM com.freshworks.DataStore read
		INFO: Key is not found in DB
**/

/**
	DATABASE ENTRIES AFTERE EXECUTEING THE PROGRAM:
	===============================================
	DataStoreDB.txt
	---------------
		key2:{"id":2,"name":"name2","address":"address2"}
		key3:{"id":3,"name":"name3","address":"address3"}
**/