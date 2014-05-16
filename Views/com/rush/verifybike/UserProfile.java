package com.rush.verifybike;

import com.facebook.widget.ProfilePictureView;
import com.rush.verifybike.Bindings.Mode;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;

public class UserProfile extends Activity {

	private LoginViewModel m_LoginViewModel = MainScreen.LoginViewModel;
	private Controls m_Controls = new Controls(this); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
				
		Bindings.BindText(m_Controls.get(R.id.lbl_user_name), m_LoginViewModel.UserFullName);
		
		Bindings.BindChecked((CheckBox)m_Controls.get(R.id.chk_allow_contact_share), m_LoginViewModel.AllowContactShare, Mode.TWO_WAY);
		Bindings.BindEnabled(m_Controls.get(R.id.edt_user_profile_email), m_LoginViewModel.AllowContactShare);
		Bindings.BindEnabled(m_Controls.get(R.id.edt_user_profile_phone), m_LoginViewModel.AllowContactShare);
		
		Bindings.BindText(m_Controls.get(R.id.edt_user_profile_email), m_LoginViewModel.Email, Mode.TWO_WAY);
		Bindings.BindText(m_Controls.get(R.id.edt_user_profile_phone), m_LoginViewModel.Phone, Mode.TWO_WAY);
		
		ProfilePictureView userProfilePictureView = (ProfilePictureView) m_Controls.get(R.id.selection_profile_pic);
		userProfilePictureView.setProfileId(m_LoginViewModel.FacebookId.get());
	}	
	
	public void onLogoutFacebook(View v) {
		m_LoginViewModel.Logout();
		
		finish();
	}
}
