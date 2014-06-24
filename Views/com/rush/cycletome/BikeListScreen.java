package com.rush.cycletome;

import com.facebook.widget.ProfilePictureView;
import com.rush.cycletome.BikeListAdapter;
import com.rush.cycletome.BikeViewModel;
import com.rush.cycletome.Bindings;
import com.rush.cycletome.Controls;
import com.rush.cycletome.DataTransfer;
import com.rush.cycletome.ICommand;
import com.rush.cycletome.INotifier;
import com.rush.cycletome.Intents;
import com.rush.cycletome.ObservableCollection;
import com.rush.cycletome.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class BikeListScreen extends Activity {

	private Controls Controls = new Controls(this);
	private Bindings Bindings = new Bindings();
	private BikeListAdapter _bikeListAdapter;
	
	private BikeViewModel m_BikeViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bike_list_screen);		
		
		View header = getLayoutInflater().inflate(R.layout.header, null);
		_bikeListAdapter = new BikeListAdapter(this);

		Bindings.BindCommand((Button) ((ViewGroup)header).getChildAt(3), new ICommand<Activity>() {
			public void Execute(Activity context) {				
				m_BikeViewModel = new BikeViewModel();
				startAddEditBike(m_BikeViewModel);
			}
		}, this);			
		
		// Manual refresh on the adapter when the list changes
		VM.BikesViewModel.Bikes().addObserver(new INotifier<ObservableCollection<BikeViewModel>>() {
			public void OnValueChanged(ObservableCollection<BikeViewModel> value) {				
				_bikeListAdapter.notifyDataSetChanged();
			}
		});

		ListView bikeListView = Controls.get(R.id.lst_bikes_owned);
		bikeListView.addHeaderView(header);

		bikeListView.setAdapter(_bikeListAdapter);		
		bikeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				m_BikeViewModel = VM.BikesViewModel.Bikes().get(arg2 - 1); 
				startAddEditBike(m_BikeViewModel);
			}
		});
		
		Bindings.BindCommand(header.findViewById(R.id.selection_profile_pic), new ICommand<Activity>() {
			@Override
			public void Execute(Activity context) {				
				onAccountSettings(null);
			}
		}, this);
		
		Bindings.BindText(Controls.get(R.id.lbl_user_name), VM.LoginViewModel.UserFullName);
		
		Bindings.BindChanged(VM.LoginViewModel.FacebookId, new INotifier<String>() {
			@Override
			public void OnValueChanged(String value) {
				ProfilePictureView userProfilePictureView = (ProfilePictureView) Controls.get(R.id.selection_profile_pic);
				userProfilePictureView.setProfileId(value);	
			}
		});					
	}

	private void startAddEditBike(BikeViewModel bikeViewModel) {
		Intent intent = new Intent(this, NewBikeScreen.class);
		DataTransfer.put(Intents.Intent_TransferBikeViewModel, bikeViewModel);		

		startActivityForResult(intent, 1);	
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_CANCELED) {
			DataTransfer.<BikeViewModel>get(Intents.Intent_TransferBikeViewModel).Reset();
		}		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();  				
		Bindings.Destroy();
		_bikeListAdapter.Destroy();
	}
	
	public void onAccountSettings(View v) {
		Intent intent = new Intent(this, UserProfile.class); 				
		startActivity(intent);	
	}
}
