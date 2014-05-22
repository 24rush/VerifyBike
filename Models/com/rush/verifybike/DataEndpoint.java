package com.rush.verifybike;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.rush.verifybike.VerificationResult.BikeStatus;

public class DataEndpoint {
	
	private static boolean checkUserLoggedIn() {
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null || !user.isAuthenticated()) {
			Log.d("User not logged in. Aborting.");
			return false;
		}
		
		return true;
	}
	
	public static void CheckSerialNumber(String serial, final DataReceivedCallback<VerificationResult> cbk) {
		if (cbk == null || !checkUserLoggedIn())
			return;					
		
		ParseQuery<ParseObject> queryStolen = ParseQuery.getQuery(BikeModel.Class);		
		queryStolen.whereEqualTo("serial", serial);
		
		queryStolen.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {								
				if (arg0 == null || arg0.size() == 0) {
					cbk.OnDataReceived(new VerificationResult(VerificationResult.BikeStatus.NotInDatabase, ""));
					return;
				}
				
				boolean isStolen = arg0.get(0).getBoolean("status") == true;
				
				cbk.OnDataReceived(new VerificationResult(isStolen ? BikeStatus.Stolen : BikeStatus.Owned, arg0.get(0).getString("model")));
			}
		});		
	}
	
	public static void RetrieveUserBikes(final DataReceivedCallback<List<BikeModel>> cbk) {		
		if (cbk == null || !checkUserLoggedIn())
			return;
		
		String userId = VM.LoginViewModel.FacebookId.get();	
		
		ParseQuery<ParseObject> queryBikes = ParseQuery.getQuery(BikeModel.Class);		
		queryBikes.whereEqualTo("userId", userId);		
		queryBikes.findInBackground(new FindCallback<ParseObject>() {			
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {						
				ArrayList<BikeModel> bikes = new ArrayList<BikeModel>();
				
				if (arg0 != null) {
					for (ParseObject bikeData : arg0) {					
						bikes.add(new BikeModel(bikeData));	
					}	
				}														
				
				cbk.OnDataReceived(bikes);
			}
		});			
	}	
}

interface DataReceivedCallback<Type> {
	void OnDataReceived(Type data);
}