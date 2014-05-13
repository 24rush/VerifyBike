package com.rush.verifybike;

import java.util.List;
import android.util.Log;

public class BikesViewModel {
	
	private Boolean m_initDone = false;
	private ObservableCollection<BikeDataViewModel> m_Bikes = new ObservableCollection<BikeDataViewModel>();
	
	public ObservableCollection<BikeDataViewModel> Bikes() {		
		return m_Bikes;
	}
	
	public void LoadBikes() {		
		if (m_initDone)
			return;
					
		DataEndpoint.RetrieveUserBikes(new DataReceivedCallback<List<BikeData>>() {			
			@Override
			public void OnDataReceived(List<BikeData> data) {				
				for (BikeData bikeData : data) {
					m_Bikes.add((new BikeDataViewModel(bikeData)));
				}
								
				m_initDone = true;
			}
		});					
	}

	public void RemoveBike(BikeDataViewModel context) {
		Log.d("MyApp", "RemoveBike " + context.Model);
		context.Destroy();
		m_Bikes.remove(context);
	}
	
	public void AddBike(BikeDataViewModel context) {
		Log.d("MyApp", "AddBike " + context.Model);
		m_Bikes.add(context);
	}
}
