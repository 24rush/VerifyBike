package com.rush.verifybike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.rush.verifybike.Bindings.Flag;

public class MainScreen extends Activity {

	private Controls m_Controls;
	private LoginViewModel m_LoginViewModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
			
		m_Controls = new Controls(this);
		
		m_LoginViewModel = new LoginViewModel();
		Bindings.BindVisible(m_Controls.get(R.id.lbl_must_login), m_LoginViewModel.IsUserLinkedToFacebook, Flag.INVERT);
		Bindings.BindVisible(m_Controls.get(R.id.img_fb_login_button), m_LoginViewModel.IsUserLinkedToFacebook, Flag.INVERT);
		
		Bindings.BindVisible(m_Controls.get(R.id.lbl_user_name), m_LoginViewModel.IsUserLinkedToFacebook);
		Bindings.BindVisible(m_Controls.get(R.id.selection_profile_pic), m_LoginViewModel.IsUserLinkedToFacebook);
		
		Bindings.BindText(m_Controls.get(R.id.lbl_user_name), m_LoginViewModel.UserFullName);	
		
		m_LoginViewModel.FacebookId.addObserver(new INotifier<String>() {						
			public void OnValueChanged(String value) {
				Log.d("MyApp", "profile: " + value);				
				ProfilePictureView userProfilePictureView = (ProfilePictureView) m_Controls.get(R.id.selection_profile_pic);
				userProfilePictureView.setProfileId(value);		
			}
		});
		
		m_LoginViewModel.Init(this);
	}
	
	public void onSearchSerialNumber(View v) {
		Intent intent = new Intent(this, SearchResults.class);

		// Mock status received
		VerificationResult result = new VerificationResult(VerificationResult.BikeStatus.Stolen, "Cannon");
		intent.putExtra(Intents.Intent_VerificationResult, result);

		startActivity(intent);
	} 

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	public void onLoginFacebook(View v) {
		m_LoginViewModel.Login();
	}
}
