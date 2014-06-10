package com.rush.verifybike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.facebook.widget.ProfilePictureView;

public class UserProfile extends Activity {

	private LoginViewModel m_LoginViewModel = VM.LoginViewModel;
	private Controls m_Controls = new Controls(this);
	private Bindings Bindings = new Bindings();	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);			

		Bindings.BindChecked((CheckBox)m_Controls.get(R.id.chk_allow_contact_share), m_LoginViewModel.AllowContactShare, Mode.TwoWay);
		Bindings.BindCommand((CheckBox)m_Controls.get(R.id.chk_allow_contact_share), new ICommand<Boolean>() {
			@Override
			public void Execute(Boolean context) {
				Log.d("Saving user.");
				m_LoginViewModel.Save();		
			}
		}, false);		
		
		Bindings.BindEnabled(m_Controls.get(R.id.edt_user_profile_email), m_LoginViewModel.AllowContactShare);
		Bindings.BindEnabled(m_Controls.get(R.id.edt_user_profile_phone), m_LoginViewModel.AllowContactShare);

		Bindings.BindText(m_Controls.get(R.id.edt_user_profile_email), m_LoginViewModel.Email, Mode.TwoWay);
		Bindings.BindText(m_Controls.get(R.id.edt_user_profile_phone), m_LoginViewModel.Phone, Mode.TwoWay);	
	}	

	public void onLogoutFacebook(View v) {
		m_LoginViewModel.Logout();

		finish();
		
		Intent intent = new Intent(this, MainScreen.class); 				
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();  
		m_LoginViewModel.Save();
		
		Bindings.Destroy();
	}
}
