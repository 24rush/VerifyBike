package com.rush.verifybike;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseUser;

import android.util.Log;

public class DataEndpoint {
	
	public static void CheckSerialNumber(String serial, DataReceivedCallback<VerificationResult> cbk) {
		if (cbk == null)
			return;
		
		Log.d("MyApp", "Launching search for " + serial);
		
		VerificationResult result = new VerificationResult(VerificationResult.BikeStatus.NotInDatabase, "Cannon2");
		
		cbk.OnDataReceived(result);
	}
	
	public static void RetrieveUserBikes(DataReceivedCallback<List<BikeData>> cbk) {		
		if (cbk == null)
			return;
		
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null || !user.isAuthenticated())
			return;
		
		String userId = user.getString("facebookId");
		Log.d("MyApp", "Launching bike retrievel for " + userId);
		
		ArrayList<BikeData> bikes = new ArrayList<BikeData>();
		bikes.add(new BikeData("H54RF123", "Cannondale CAAD 10", 3, false));
		bikes.add(new BikeData("DH2324DS", "DHS la care se rupe ghidonul", 1, true));
		
		cbk.OnDataReceived(bikes);
	}
}

interface DataReceivedCallback<Type> {
	void OnDataReceived(Type data);
}