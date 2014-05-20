package com.rush.verifybike;

import java.util.HashMap;

public class DataTransfer {
	private static HashMap<String, Object> m_Objects = new HashMap<String, Object>();
	
	public static void put(String key, Object v) {
		m_Objects.put(key, v);
	}
	
	public static Object get(String key) {
		return m_Objects.get(key);
	}
	
	public static void remove(String key) {
		m_Objects.remove(key);
	}
}
