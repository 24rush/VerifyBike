package com.rush.verifybike; 

import android.app.Application;
import com.parse.Parse; 
import com.parse.ParseFacebookUtils;

public class App extends Application { 
    @Override public void onCreate() { 
        super.onCreate();
        
        Parse.initialize(this, "0nxiZgeVLGP68frwuL749rKcYlfHzPgAf2e6q1Vo", "73RjToAA2H1qSlvJHJMTZlm2RP317QE4V0we9I3m");
		ParseFacebookUtils.initialize("500780326714460");
    }
} 

class Log {	
	public static void d(String message) {
		android.util.Log.d("MyApp", message);
	}
	
	public static void e(String message) {
		android.util.Log.e("MyApp", message);
	}
}