package com.rush.verifybike;

import java.util.Arrays;

import android.app.Activity;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ParseFacebookUtils.Permissions;

public class LoginViewModel {

	private Activity m_Activity;

	public Observable<Boolean> IsUserLinkedToFacebook = new Observable<Boolean>(false);
	public Observable<Boolean> CanLogin = new Observable<Boolean>(true);

	public Observable<String> UserFullName = new Observable<String>("");
	public Observable<String> FacebookId = new Observable<String>("");
	
	public Observable<Boolean> AllowContactShare = new Observable<Boolean>(true);
	public Observable<String> Phone = new Observable<String>("");
	public Observable<String> Email = new Observable<String>("");
	
	public Observable<Boolean> IsDirty = new Observable<Boolean>(false);
	
	public boolean Init(Activity _parent) {
		m_Activity = _parent;

		ParseUser user = ParseUser.getCurrentUser();

		if (user == null) {
			Log.d("MyApp", "NULL user");
			return false;
		}
		Log.d("MyApp", "user is " + user.isAuthenticated());
		IsUserLinkedToFacebook.set(user.isAuthenticated());
		CanLogin.set(!IsUserLinkedToFacebook.get());
		UserFullName.set((String)user.getString("name"));
		FacebookId.set((String)user.getString("facebookId"));
		Email.set((String)user.getString("email"));
		Phone.set((String)user.getString("mobile"));
		AllowContactShare.set(user.getBoolean("allowContactShare"));
		
		IsDirty.set(false);
		
		INotifier<Object> observerRequireSave = new INotifier<Object>() {					
			public void OnValueChanged(Object value) {
				IsDirty.set(true);
			}
		};
		
		Email.addTypelessObserver(observerRequireSave);
		Phone.addTypelessObserver(observerRequireSave);
		AllowContactShare.addTypelessObserver(observerRequireSave);
		
		return true;
	}

	public void Save() {			
				
		ParseUser parseUser = ParseUser.getCurrentUser();
		Log.d("MyApp", "save " + Phone.get());
		parseUser.put("email", Email.get());	  
		parseUser.put("mobile", Phone.get());
		parseUser.put("allowContactShare", AllowContactShare.get());
		
		parseUser.saveEventually();
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
		
		IsDirty.set(false);
	}
	
	public void Login() {

		CanLogin.set(false);

		ParseFacebookUtils.logIn(Arrays.asList("public_profile", Permissions.User.EMAIL), 
				m_Activity, new LogInCallback() {			
			public void done(ParseUser user, ParseException err) {
				if (user == null) 
				{
					Log.d("MyApp", "User cancelled the Facebook login." + err.getCode());

					CanLogin.set(true);
					IsUserLinkedToFacebook.set(false); 

					if (err.getCode() != -1)
						MessageBox.Show(m_Activity, 
								m_Activity.getString(R.string.msg_unable_to_login), 
								err.getMessage());
					return;
				} 					
				
				if (user.isNew()) 
				{																	
					Log.d("MyApp", "User signed up and logged in through Facebook!");
				}
				else 
				{											
					Log.d("MyApp", "User logged in through Facebook!");
				}											

				makeMeRequest();
			}
		});
	}

	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {	              
					public void onCompleted(GraphUser user, Response response) {
		
						if (user == null) {
							Log.d("MyApp", "NULL user");
		
							if (response.getError() != null) {
								Log.d("MyApp", response.getError().getErrorMessage());
							}

							CanLogin.set(true);
							IsUserLinkedToFacebook.set(false);
							
							return;
						}
		
						Log.d("MyApp", user.toString());
						ParseUser parseUser = ParseUser.getCurrentUser();
		
						if (parseUser != null) {
							Log.d("MyApp", "update user");
		
							String email = user.getProperty("email").toString();
							
							parseUser.put("name", user.getName());	  
							parseUser.put("facebookId", user.getId());							
							parseUser.put("allowContactShare", true);
							parseUser.put("mobile", "");
							
							if (email != null && !email.isEmpty()) {
								parseUser.put("email", email);
								Email.set(user.getProperty("email").toString());
							}
		
							parseUser.saveEventually();
		
							UserFullName.set((String)parseUser.getString("name"));
							FacebookId.set((String)parseUser.getString("facebookId"));
							AllowContactShare.set(true);
							CanLogin.set(false);
							IsUserLinkedToFacebook.set(true); 							
						}	                	                 	                                                     	                                              	                	       
					}               	                
				});

		request.executeAsync();	 
	}
}
