package com.rush.verifybike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

		ListView bikeListView = (ListView)Controls.get(R.id.lst_bikes_owned);		
		View header = getLayoutInflater().inflate(R.layout.header, null);
		final BikeListAdapter bikeListAdapter = new BikeListAdapter(this);
		
		Bindings.BindCommand((Button) ((ViewGroup)header).getChildAt(1), new ICommand<Activity>() {
			public void Execute(Activity context) {
				Log.d("MyApp", "onAddNewBike");
				
				m_BikeViewModel = new BikeViewModel();
				startAddEditBike(m_BikeViewModel);
			}
		}, this);			

		// Manual refresh on the adapter when the list changes
		MainScreen.BikesViewModel.Bikes().addObserver(new INotifier<ObservableCollection<BikeViewModel>>() {
			public void OnValueChanged(ObservableCollection<BikeViewModel> value) {
				bikeListAdapter.notifyDataSetChanged();
			}
		});
		
		bikeListView.addHeaderView(header);
		
		bikeListView.setAdapter(bikeListAdapter);		
		bikeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				Log.d("MyApp", "tapped bike " + arg2);
				
				m_BikeViewModel = MainScreen.BikesViewModel.Bikes().get(arg2 - 1); 
				startAddEditBike(m_BikeViewModel);
			}
		});						    	
	}
	
	private void startAddEditBike(BikeViewModel bikeViewModel) {
		Intent intent = new Intent(this, NewBikeScreen.class);
		intent.putExtra("com.rush.verifybike.BikeViewModel", bikeViewModel);

		startActivityForResult(intent, 1);	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        if (resultCode == RESULT_OK) {
	            m_BikeViewModel = data.getParcelableExtra("com.rush.verifybike.BikeViewModel");
	            	           	      
	            if (m_BikeViewModel.IsNewObject())
	            	MainScreen.BikesViewModel.AddBike(m_BikeViewModel);	                   	            
	            
	            m_BikeViewModel.Commit();
	        }
	        else
		        if (resultCode == RESULT_CANCELED) {
		            //Write your code if there's no result
		        }
	    }
	}
}
