package com.rush.verifybike;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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

	public static void CancelQueries() {
		Log.d("cancelQueries ");
		final ParseQuery<ParseObject> qStolen = queryStolen;
		final ParseQuery<ParseUser> qQwner = queryOwner;
		
		if (qStolen == null && qQwner == null)
			return;		

		Thread cancelThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try 
				{
					if (qStolen != null) qStolen.cancel();
					if (qQwner != null) qQwner.cancel();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		cancelThread.start();
	}

	private static ParseQuery<ParseObject> queryStolen = null;
	private static ParseQuery<ParseUser> queryOwner = null;

	public static void CheckSerialNumber(String serial, final DataReceivedCallback<VerificationResult> cbk) {
		if (cbk == null)
			return;	

		Log.d("Checking " + serial);
		queryStolen = ParseQuery.getQuery(BikeModel.Class);		
		queryStolen.whereEqualTo("serial", serial);

		queryStolen.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {								
				if (arg0 == null || arg0.size() == 0) {
					cbk.OnDataReceived(new VerificationResult(VerificationResult.BikeStatus.NotInDatabase, ""));
					return;
				}

				final ParseObject bikeData = arg0.get(0);
				final boolean isStolen = bikeData.getBoolean("status") == true;

				// If it's not stolen, we can send the result
				if (!isStolen) {
					dispatchResult(cbk, isStolen, bikeData, null);
					return;
				}

				// If it is stolen, then we need to get the owner
				String ownerId = bikeData.getString("userId");

				if (ownerId == null || ownerId.isEmpty()) {
					dispatchResult(cbk, isStolen, bikeData, null);
					Log.d("Bike has no owner id.");
					return;
				}

				queryOwner = ParseUser.getQuery();
				queryOwner.whereEqualTo("facebookId", ownerId);
				queryOwner.whereEqualTo("allowContactShare", true);

				Log.d("Search for ownerID=" + ownerId + " started.");

				queryOwner.findInBackground(new FindCallback<ParseUser>() {							
					@Override
					public void done(List<ParseUser> arg0, ParseException arg1) {
						if (arg1 != null) { 
							Log.e(arg1.getMessage());
							return;
						}

						Log.d("Search for owner returned " + (arg0 != null ? arg0.size() : 0) + " results.");

						dispatchResult(cbk, isStolen, bikeData, (arg0 != null && arg0.size() > 0) ? arg0.get(0) : null);

						queryStolen = null;
						queryOwner = null;
					}
				});
			}
		});		
	}

	private static void dispatchResult(DataReceivedCallback<VerificationResult> cbk, Boolean isStolen, ParseObject bikeData, ParseObject ownerData) {				
		try
		{
			VerificationResult result = null;
			ParseFile previewFile = (ParseFile) bikeData.get("pic0");

			String ownerPhone = "";
			String ownerEmail = "";

			if (isStolen && ownerData != null) {							
				ownerPhone = ownerData.getString("mobile");
				ownerEmail = ownerData.getString("email");							
			}

			Log.d("Owner: " + ownerEmail + " phone: " + ownerPhone);

			result = new VerificationResult(isStolen ? BikeStatus.Stolen : BikeStatus.Owned, 
					bikeData.getString("model"), 
					previewFile != null ? previewFile.getData() : null,
							ownerPhone, ownerEmail);

			cbk.OnDataReceived(result);
		}
		catch (ParseException e) {					
			e.printStackTrace();
		}
	}

	public static void RetrieveUserBikes(final DataReceivedCallback<List<BikeModel>> cbk) {		
		if (cbk == null || !checkUserLoggedIn())
			return;

		String userId = VM.LoginViewModel.FacebookId.get();	

		ParseQuery<ParseObject> queryBikes = ParseQuery.getQuery(BikeModel.Class);	
		//queryBikes.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
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

	public static void ClearCachedBikes() {
		ParseQuery<ParseObject> queryBikes = ParseQuery.getQuery(BikeModel.Class);			
		queryBikes.whereEqualTo("userId", VM.LoginViewModel.FacebookId.get());
		queryBikes.clearCachedResult();
	}
}

interface DataReceivedCallback<Type> {
	void OnDataReceived(Type data);
}