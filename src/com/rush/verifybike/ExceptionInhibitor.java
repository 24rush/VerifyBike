package com.rush.verifybike;

import com.parse.ParseException;
import com.parse.ParseFile;

public class ExceptionInhibitor {
	@SuppressWarnings("unchecked")
	public static <T, V> void Execute(MethodInvoker2<T, V> method, Object... args) {
		if (method == null)
			return;
		
		try 
		{
			int count = 0;
			for (Object object : args) {
				count++;
			}

			switch (count) {
			case 0:
				//method.Call();
				break;
			case 1:
				//method.Call(args[0]);
				break;
			case 2:
				method.Call((T)args[0], (V)args[1]);
				break;
			default:
				break;
			} 				
		}
		catch (Exception e) 
		{			
		}			
	}
}

interface MethodInvoker2<T1 extends Object, T2 extends Object> {	
	public void Call(T1 t1, T2 t2) throws ParseException;	
}
