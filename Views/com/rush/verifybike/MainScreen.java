package com.rush.verifybike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.rush.verifybike.Bindings.Mode;

public class MainScreen extends Activity {

	private Controls Controls = new Controls(this);
	public static LoginViewModel LoginViewModel;
	public static SearchBikeViewModel SearchViewModel;
	public static BikesViewModel BikesViewModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
							
		LoginViewModel = new LoginViewModel();
		SearchViewModel = new SearchBikeViewModel(this);
		BikesViewModel = new BikesViewModel();
		
		Bindings.BindVisible(Controls.get(R.id.lbl_must_login), LoginViewModel.IsUserLinkedToFacebook, Mode.INVERT);
		Bindings.BindVisible(Controls.get(R.id.img_fb_login_button), LoginViewModel.IsUserLinkedToFacebook, Mode.INVERT);
				
		Bindings.BindVisible(Controls.get(R.id.layout_user_profile), LoginViewModel.IsUserLinkedToFacebook);	
		
		Bindings.BindText(Controls.get(R.id.lbl_user_name), LoginViewModel.UserFullName);		
		Bindings.BindText(Controls.get(R.id.edt_serial_number), SearchViewModel.SerialNumber, Mode.TWO_WAY);				
		
		BikesViewModel.Bikes().Count.addObserver(new INotifier<Integer>() {
			public void OnValueChanged(Integer value) {
				Log.d("MyApp", "Count updated: " + value);
				Button btnNoBikes = (Button) Controls.get(R.id.btn_add_bike);
				
				Boolean hasBikes = value > 0; 
				
				int idLabel =  !hasBikes ? R.string.lbl_add_bike : R.string.lbl_view_bikes;
				String label = getString(idLabel);
				if (hasBikes) label += " (" + value + ")";
				
				btnNoBikes.setText(label);
			}
		});
		
		LoginViewModel.FacebookId.addObserver(new INotifier<String>() {						
			public void OnValueChanged(String value) {
				Log.d("MyApp", "new profile id: " + value);
				
				ProfilePictureView userProfilePictureView = (ProfilePictureView) Controls.get(R.id.selection_profile_pic);
				userProfilePictureView.setProfileId(value);
				
				BikesViewModel.LoadBikes();
			}
		});
		
		LoginViewModel.Init(this);
	}
	
	public void onSearchSerialNumber(View v) {
		
		final Activity _activity = this;
		
		DataEndpoint.CheckSerialNumber(SearchViewModel.SerialNumber.get(), new DataReceivedCallback<VerificationResult>() {					
			public void OnDataReceived(VerificationResult result) {
				Intent intent = new Intent(_activity, SearchResults.class); 		
				intent.putExtra(Intents.Intent_VerificationResult, result);

				startActivity(intent);				
			}
		});
		
	} 

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	public void onLoginFacebook(View v) {
		LoginViewModel.Login();
	}
	
	public void onAddViewBikes(View v) {
		Intent intent = new Intent(this, BikeListScreen.class); 				
		startActivity(intent);		
	}
}
