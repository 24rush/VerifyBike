package com.rush.verifybike;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;

public class ExceptionInhibitor {
	
	public static <T1, T2> void Execute(MethodInvoker2<T1, T2> method, T1 t1, T2 t2) {
		if (method == null)
			return;

		try 
		{			
			method.Call(t1, t2);
		}
		catch (Exception e) 
		{
			Log.e("MyApp", e.getMessage());
		}			
	}
}

interface MethodInvoker2 <T1, T2> {	
	public void Call(T1 t1, T2 t2) throws Exception;	
}
