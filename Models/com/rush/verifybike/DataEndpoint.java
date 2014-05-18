package com.rush.verifybike;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
	
	public static void RetrieveUserBikes(final DataReceivedCallback<List<BikeData>> cbk) {		
		if (cbk == null)
			return;
		
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null || !user.isAuthenticated())
			return;
		
		String userId = user.getString("facebookId");
		Log.d("MyApp", "Launching bike retrievel for " + userId);
		
		ParseQuery<ParseObject> queryBikes = ParseQuery.getQuery(BikeData.Class);		
		queryBikes.whereEqualTo("userId", userId);		
		queryBikes.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {		
				Log.d("MyApp", "Received bikes " + arg0);
				ArrayList<BikeData> bikes = new ArrayList<BikeData>();
				
				for (ParseObject bikeData : arg0) {
					bikes.add(new BikeData(bikeData));	
				}							
				
				cbk.OnDataReceived(bikes);
			}
		});			
	}
	
	public static void AddBike() {
		
	}
}

interface DataReceivedCallback<Type> {
	void OnDataReceived(Type data);
}