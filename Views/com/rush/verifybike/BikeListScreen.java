package com.rush.verifybike;

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
	private BikeViewModel m_BikeViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bike_list_screen);		

		View header = getLayoutInflater().inflate(R.layout.header, null);
		final BikeListAdapter bikeListAdapter = new BikeListAdapter(this);

		Bindings.BindCommand((Button) ((ViewGroup)header).getChildAt(1), new ICommand<Activity>() {
			public void Execute(Activity context) {				
				m_BikeViewModel = new BikeViewModel();
				startAddEditBike(m_BikeViewModel);
			}
		}, this);			

		// Manual refresh on the adapter when the list changes
		VM.BikesViewModel.Bikes().addObserver(new INotifier<ObservableCollection<BikeViewModel>>() {
			public void OnValueChanged(ObservableCollection<BikeViewModel> value) {
				bikeListAdapter.notifyDataSetChanged();
			}
		});

		ListView bikeListView = Controls.get(R.id.lst_bikes_owned);
		bikeListView.addHeaderView(header);

		bikeListView.setAdapter(bikeListAdapter);		
		bikeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				m_BikeViewModel = VM.BikesViewModel.Bikes().get(arg2 - 1); 
				startAddEditBike(m_BikeViewModel);
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
}
