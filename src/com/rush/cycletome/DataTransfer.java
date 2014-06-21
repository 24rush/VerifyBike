package com.rush.cycletome;

import java.util.HashMap;

// 
// Alternative to using intents to transfer data between activities
//

public class DataTransfer {
	private static HashMap<String, Object> m_Objects = new HashMap<String, Object>();
	
	public static void put(String key, Object v) {
		m_Objects.put(key, v);
	}
	
	@SuppressWarnings("unchecked")
	public static <Type> Type get(String key) {
		return (Type) m_Objects.get(key);
	}
	
	public static void remove(String key) {
		m_Objects.remove(key);
	}
}
