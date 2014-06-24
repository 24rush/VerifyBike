package com.rush.cycletome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.parse.ParseFacebookUtils;
import com.rush.cycletome.BikesViewModel;
import com.rush.cycletome.Bindings;
import com.rush.cycletome.Controls;
import com.rush.cycletome.INotifier;
import com.rush.cycletome.Log;
import com.rush.cycletome.LoginViewModel;
import com.rush.cycletome.Mode;
import com.rush.cycletome.R;
import com.rush.cycletome.SearchBikeViewModel;

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

class Localize {
	private static Activity m_Activity;
	
	public Localize(Activity activity) {
		m_Activity = activity;
	}
	
	public static String Id(int id) {
		return m_Activity.getString(id);
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
		new Localize(this);
		new VM();
		edtSerialNumberText = getString(R.string.lbl_check_serial);
		VM.SearchViewModel.SerialNumber.set(edtSerialNumberText);

		Bindings.BindVisible(Controls.get(R.id.layout_must_login), VM.LoginViewModel.CanLogin);				
		
		// Bikes will be loaded only if there is a loggedin user
		Bindings.BindVisible(Controls.get(R.id.layout_user_loggedin), VM.LoginViewModel.CanLogin, Mode.Invert);	
		
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

		Bindings.BindChanged(VM.LoginViewModel.FacebookId, new INotifier<String>() {						
			public void OnValueChanged(String value) {
				Log.d("New user Facebook id: " + value);

				VM.BikesViewModel.LoadBikes();
			}
		});
		
		VM.LoginViewModel.Init(this);	
	}

	private SearchPopupWindow m_CurrentPopup;
	
	public void onSearchSerialNumber(View v) {		
		if (VM.SearchViewModel.SerialNumber.get().equals("") || VM.SearchViewModel.SerialNumber.get().equals(edtSerialNumberText))
			return;	

		m_CurrentPopup = new SearchPopupWindow(); 
		m_CurrentPopup.Show(this, null);

		VM.SearchViewModel.SearchBike();
	} 

	@Override
	public void onBackPressed() {			
		if (m_CurrentPopup != null) {
			m_CurrentPopup.Dismiss();
			m_CurrentPopup = null;
		}
		else {
			super.onBackPressed();
		}
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
			Intent intent = new Intent(this, BikeListScreen.class);
			//DataTransfer.put(Intents.Intent_TransferBikeViewModel, new BikeViewModel());

			startActivity(intent);
		}
		else {
			Intent intent = new Intent(this, BikeListScreen.class); 				
			startActivity(intent);
		}
	}
}
