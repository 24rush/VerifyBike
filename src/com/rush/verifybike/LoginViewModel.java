package com.rush.verifybike;

import java.util.Arrays;

import android.app.Activity;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ParseFacebookUtils.Permissions;

public class LoginViewModel {

	private Activity m_Activity;

	public Observable<Boolean> IsUserLinkedToFacebook = new Observable<Boolean>(false);
	public Observable<String> UserFullName = new Observable<String>("");
	public Observable<String> FacebookId = new Observable<String>(null);

	public boolean Init(Activity _parent) {
		m_Activity = _parent;

		ParseUser user = ParseUser.getCurrentUser();

		if (user == null)
			return false;

		IsUserLinkedToFacebook.set(user.isAuthenticated());
		UserFullName.set((String)user.getString("name"));
		FacebookId.set((String)user.getString("facebookId"));

		return true;
	}

	public void Login() {

		ParseFacebookUtils.logIn(Arrays.asList("public_profile", Permissions.User.EMAIL), 
				m_Activity,
				new LogInCallback() {			
			public void done(ParseUser user, ParseException err) {
				if (user == null) 
				{
					Log.d("MyApp", "User cancelled the Facebook login." + err.getCode());

					IsUserLinkedToFacebook.set(false); 

					if (err.getCode() != -1)
						MessageBox.Show(m_Activity, 
								m_Activity.getString(R.string.msg_unable_to_login), 
								err.getMessage());
					return;
				} 

				IsUserLinkedToFacebook.set(true); 

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

					return;
				}

				Log.d("MyApp", user.toString());
				ParseUser parseUser = ParseUser.getCurrentUser();

				if (parseUser != null) {
					Log.d("MyApp", "update user");

					parseUser.put("name", user.getName());	  
					parseUser.put("facebookId", user.getId());	                

					parseUser.saveInBackground();

					UserFullName.set((String)parseUser.getString("name"));
					FacebookId.set((String)parseUser.getString("facebookId"));
				}	                	                 	                                                     	                                              	                	       
			}               	                
		});

		request.executeAsync();	 
	}
}
