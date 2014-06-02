package com.rush.verifybike;

import java.util.Arrays;

import android.app.Activity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
import com.parse.ParseFacebookUtils.Permissions;

public class LoginViewModel {

	private Activity m_Activity;

	public Observable<Boolean> IsUserLinkedToFacebook = new Observable<Boolean>(false);
	public Observable<Boolean> CanLogin = new Observable<Boolean>(true);

	public Observable<String> UserFullName = new Observable<String>("");
	public Observable<String> FacebookId = new Observable<String>("");

	public Observable<Boolean> AllowContactShare = new Observable<Boolean>(true);
	public Observable<String> Phone = new Observable<String>();
	public Observable<String> Email = new Observable<String>();

	public boolean Init(Activity _parent) {
		m_Activity = _parent;

		ParseUser user = ParseUser.getCurrentUser();

		if (user == null) {
			Log.d("No current user. Login required.");
			return false;
		}	

		IsUserLinkedToFacebook.set(user.isAuthenticated());
		CanLogin.set(!IsUserLinkedToFacebook.get());

		UserFullName.set((String)user.getString("name"));
		FacebookId.set((String)user.getString("facebookId"));

		Email.load((String)user.getString("email"));
		Phone.load((String)user.getString("mobile"));
		AllowContactShare.load(user.getBoolean("allowContactShare"));

		return true;
	}

	public void Save() {			
		if (!Email.IsChanged() && !Phone.IsChanged() && !AllowContactShare.IsChanged())
			return;

		final ParseUser parseUser = ParseUser.getCurrentUser();

		parseUser.put("email", Email.get());	  
		parseUser.put("mobile", Phone.get());
		parseUser.put("allowContactShare", AllowContactShare.get());

		parseUser.saveEventually(new SaveCallback() {			
			@Override
			public void done(ParseException arg0) {				
				parseUser.refreshInBackground(new RefreshCallback() {					
					@Override
					public void done(ParseObject arg0, ParseException arg1) {
						Log.d("Refreshed current user.");						
					}
				});	
			}
		});		
	}

	public void Logout() {		

		ParseFacebookUtils.getSession().closeAndClearTokenInformation();			
		ParseUser.logOut();

		CanLogin.set(true);
		IsUserLinkedToFacebook.set(false);

		UserFullName.set("");
		FacebookId.set("");

		AllowContactShare.set(false);
		Phone.set("");
		Email.set("");			
	}

	private void OnFacebookLoginCompleted(ParseUser user, ParseException err) {
		if (user == null) {
			Log.d("User cancelled the Facebook login." + err.getCode());

			CanLogin.set(true);
			IsUserLinkedToFacebook.set(false); 

			if (err.getCode() != -1) {
				MessageBox.Show(m_Activity, m_Activity.getString(R.string.msg_unable_to_login), err.getMessage());
			}
			return;
		} 					

		Log.d(user.isNew() ? "User signed up and logged in through Facebook." : "User logged in through Facebook.");

		// Call graph search
		Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() 
		{	              
			public void onCompleted(GraphUser user, Response response) {
				OnGraphSearchCompleted(user, response);
			}             
		}).executeAsync();	

	}

	public void Login() {
		CanLogin.set(false);

		ParseFacebookUtils.logIn(Arrays.asList("public_profile", Permissions.User.EMAIL), m_Activity, 
				new LogInCallback() 
		{			
			public void done(ParseUser user, ParseException err) {
				OnFacebookLoginCompleted(user, err);
			}	
		});
	}

	private void OnGraphSearchCompleted(GraphUser user, Response response) {
		if (user == null) {
			Log.e("Facebook graph search returned NULL user.");

			if (response.getError() != null) {
				Log.d(response.getError().getErrorMessage());
			}

			CanLogin.set(true);
			IsUserLinkedToFacebook.set(false);

			return;
		}

		final ParseUser parseUser = ParseUser.getCurrentUser();

		if (parseUser == null) {
			Log.e("Current user still NULL after Graph Search");
			
			CanLogin.set(true);
			IsUserLinkedToFacebook.set(false); 
		}

		Log.d("User retrieved from Facebook. Updating properties...");		

		Phone.load((String)parseUser.getString("mobile"));
		
		parseUser.put("name", user.getName());	  
		parseUser.put("facebookId", user.getId());							
		parseUser.put("allowContactShare", false);							

		String email = user.getProperty("email").toString();

		if (email != null && !email.isEmpty()) {
			parseUser.put("email", email);
			Email.set(email);
		}

		parseUser.saveEventually(new SaveCallback() {								
			@Override
			public void done(ParseException arg0) {
				if (arg0 != null) {
					Log.e("Exception on saving the updated user: " + arg0.getMessage());
					return;
				}

				CanLogin.set(false);
				IsUserLinkedToFacebook.set(true); 	
				
				UserFullName.set((String)parseUser.getString("name"));
				FacebookId.set((String)parseUser.getString("facebookId"));
				AllowContactShare.set(false);											
			}
		});		
	}
}
