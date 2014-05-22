package com.rush.verifybike;

import android.app.Activity;
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
		
		Bindings.BindText(m_Controls.get(R.id.lbl_user_name), m_LoginViewModel.UserFullName);

		Bindings.BindChecked((CheckBox)m_Controls.get(R.id.chk_allow_contact_share), m_LoginViewModel.AllowContactShare, Modes.TwoWay());
		Bindings.BindEnabled(m_Controls.get(R.id.edt_user_profile_email), m_LoginViewModel.AllowContactShare);
		Bindings.BindEnabled(m_Controls.get(R.id.edt_user_profile_phone), m_LoginViewModel.AllowContactShare);

		Bindings.BindText(m_Controls.get(R.id.edt_user_profile_email), m_LoginViewModel.Email, Modes.TwoWay());
		Bindings.BindText(m_Controls.get(R.id.edt_user_profile_phone), m_LoginViewModel.Phone, Modes.TwoWay());

		ProfilePictureView userProfilePictureView = (ProfilePictureView) m_Controls.get(R.id.selection_profile_pic);
		userProfilePictureView.setProfileId(m_LoginViewModel.FacebookId.get());
	}	

	public void onLogoutFacebook(View v) {
		m_LoginViewModel.Logout();

		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();  
		m_LoginViewModel.Save();
		
		Bindings.Destroy();
	}
}
