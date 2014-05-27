package com.rush.verifybike;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Button;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;

class VM {
	public static LoginViewModel LoginViewModel;
	public static SearchBikeViewModel SearchViewModel;
	public static BikesViewModel BikesViewModel;

	public VM() {
		LoginViewModel = new LoginViewModel();
		SearchViewModel = new SearchBikeViewModel();
		BikesViewModel = new BikesViewModel();
	}
}

public class MainScreen extends Activity {

	private Controls Controls = new Controls(this);
	private Bindings Bindings = new Bindings();
	private String edtSerialNumberText = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);		

		// Initialize view models
		new VM();
		edtSerialNumberText = getString(R.string.lbl_check_serial);
		VM.SearchViewModel.SerialNumber.set(edtSerialNumberText);

		Bindings.BindVisible(Controls.get(R.id.layout_must_login), VM.LoginViewModel.CanLogin);		
		Bindings.BindVisible(Controls.get(R.id.layout_user_profile), VM.LoginViewModel.IsUserLinkedToFacebook);	

		Bindings.BindText(Controls.get(R.id.lbl_user_name), VM.LoginViewModel.UserFullName);		
		Bindings.BindText(Controls.get(R.id.edt_serial_number), VM.SearchViewModel.SerialNumber, Mode.TwoWay);				

		((EditText)Controls.get(R.id.edt_serial_number)).setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (VM.SearchViewModel.SerialNumber.get().equals(edtSerialNumberText)) {
					VM.SearchViewModel.SerialNumber.set("");
				}
				
				return false;
			}
		});
		
		Bindings.BindVisible(Controls.get(R.id.btn_add_bike), VM.BikesViewModel.BikesLoaded);		

		VM.BikesViewModel.Bikes().Count.addObserver(new INotifier<Integer>() {
			public void OnValueChanged(Integer value) {			
				Button btnNoBikes = (Button) Controls.get(R.id.btn_add_bike);

				Boolean hasBikes = value > 0; 

				int idLabel =  !hasBikes ? R.string.lbl_add_bike : R.string.lbl_view_bikes;
				String label = getString(idLabel);
				if (hasBikes) label += " (" + value + ")";

				btnNoBikes.setText(label);
			}
		});

		VM.LoginViewModel.FacebookId.addObserver(new INotifier<String>() {						
			public void OnValueChanged(String value) {
				Log.d("New user Facebook id: " + value);

				ProfilePictureView userProfilePictureView = (ProfilePictureView) Controls.get(R.id.selection_profile_pic);
				userProfilePictureView.setProfileId(value);

				VM.BikesViewModel.LoadBikes();
			}
		});

		VM.LoginViewModel.Init(this);

		Bindings.BindCommand(Controls.get(R.id.selection_profile_pic), new ICommand<Activity>() {
			@Override
			public void Execute(Activity context) {				
				Intent intent = new Intent(context, UserProfile.class); 				
				startActivity(intent);	
			}
		}, this);
	}

	public void onSearchSerialNumber(View v) {
		if (VM.SearchViewModel.SerialNumber.get().equals("") || VM.SearchViewModel.SerialNumber.get().equals(edtSerialNumberText))
			return;

		final Activity activity = this;

		(new SearchPopupWindow()).Show(activity, null);

		VM.SearchViewModel.SearchBike();
	} 

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	public void onLoginFacebook(View v) {
		VM.LoginViewModel.Login();
	}

	public void onAddViewBikes(View v) {
		if (VM.BikesViewModel.Bikes().size() == 0) {
			Intent intent = new Intent(this, NewBikeScreen.class);
			DataTransfer.put(Intents.Intent_TransferBikeViewModel, new BikeViewModel());

			startActivity(intent);
		}
		else {
			Intent intent = new Intent(this, BikeListScreen.class); 				
			startActivity(intent);
		}
	}
}
