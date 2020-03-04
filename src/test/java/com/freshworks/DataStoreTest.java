package com.freshworks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataStoreTest {

	DataStore ds = new DataStore();

	@Test
	public void creation(){
		int result;
		PojoTest pojoTest = new PojoTest(1, "name1", "address1");
		result = ds.create("key1", pojoTest);
		assertEquals(1,result);
	}
	
	@Test
	public void creationWithTimeout(){
		int result;
		PojoTest pojoTest = new PojoTest(1, "name1", "address1");
		result = ds.create("key2", pojoTest,10);
		assertEquals(1,result);
	}

	@Test
	public void creationWithExistingKey(){
		int result;
		PojoTest pojoTest = new PojoTest(1, "name1", "address1");
		result = ds.create("key2", pojoTest,10);
		assertEquals(0,result);
	}

	@Test
	public void read(){
		PojoTest pojoTest1 = new PojoTest(1, "name1", "address1");
		PojoTest pojoTest2 = null;
		try {
			pojoTest2 = new ObjectMapper().readValue(ds.read("key2").toString(), PojoTest.class);
		} catch (IOException e) {
		}
		assertTrue(pojoTest1.toString().equalsIgnoreCase(pojoTest2.toString()));
	}

	@Test
	public void readNull(){
		PojoTest pojoTest = null;
		try {
			pojoTest = new ObjectMapper().readValue(ds.read("key").toString(), PojoTest.class);
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		assertNull(pojoTest);
	}
	
	@Test
	public void deleteExistingKey(){
		String key = "key1";
		ds.delete(key);

		PojoTest pojoTest = null;
		try {
			pojoTest = new ObjectMapper().readValue(ds.read(key).toString(), PojoTest.class);
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		assertNull(pojoTest);
	
	}
	
	@Test
	public void deleteNonExistingKey(){
		String key = "no-key";
		ds.delete(key);

		PojoTest pojoTest = null;
		try {
			pojoTest = new ObjectMapper().readValue(ds.read(key).toString(), PojoTest.class);
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		assertNull(pojoTest);
	}
	
	@Test
	public void toStringMethod(){
		String string1 = ds.toString();
		String string2 = "com.freshworks.DataStore";
		assertTrue(string1.equalsIgnoreCase(string2));
	}
}
