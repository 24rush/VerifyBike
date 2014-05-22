package com.rush.verifybike;

public class Converters {
	public static StringToBoolean StringToBoolean = new StringToBoolean();
	public static Forward<String> ForwardString = new Forward<String>();
	public static Forward<Boolean> ForwardBoolean = new Forward<Boolean>(); 
}

class StringToBoolean implements IConvert<String, Boolean> {
	
	@Override
	public Boolean Convert(String s) {	
		return (s != null && !s.isEmpty());
	} 	
}

class Forward<T> implements IConvert<T, T> {
	
	@Override
	public T Convert(T s) {	
		return s;
	} 	
}

interface IConvert <S, D> {
	D Convert(S s);
}