package com.rush.verifybike;

public class Converters {
	public static void Convert(String s, Boolean value)  {
		value = (s != null && !s.isEmpty());
	}
	
	public static void Convert(String s, String value) {
		value = s;
	}
	
	public static void Convert(Boolean b, Boolean value) {
		value = b;
	}	
}