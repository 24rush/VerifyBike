package com.rush.verifybike;

public class Executor {	
	public static <T1, T2> void Execute(MethodInvoker2<T1, T2> method, T1 t1, T2 t2) {
		if (method == null)
			return;

		try 
		{			
			method.Call(t1, t2);
		}
		catch (Exception e) 
		{
			Log.e(e.getMessage());
		}			
	}
	
	public static void Execute(MethodInvoker method) {
		if (method == null)
			return;
		
		try 
		{			
			method.Call();
		}
		catch (Exception e) 
		{
			Log.e(e.getMessage());
		}	
	}
}

interface MethodInvoker {
	public void Call() throws Exception;
}

interface MethodInvoker2 <T1, T2> {	
	public void Call(T1 t1, T2 t2) throws Exception;	
}
