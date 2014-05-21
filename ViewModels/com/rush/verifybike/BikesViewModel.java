package com.rush.verifybike;

import java.util.List;
import android.util.Log;

public class BikesViewModel {
	
	private Boolean m_initDone = false;
	private ObservableCollection<BikeViewModel> m_Bikes = new ObservableCollection<BikeViewModel>();
	
	public ObservableCollection<BikeViewModel> Bikes() {		
		return m_Bikes;
	}
	
	public void LoadBikes() {		
		if (m_initDone)
			return;
					
		DataEndpoint.RetrieveUserBikes(new DataReceivedCallback<List<BikeModel>>() {			
			@Override
			public void OnDataReceived(List<BikeModel> data) {				
				for (BikeModel bikeData : data) {					
					m_Bikes.add(new BikeViewModel(bikeData));
				}
								
				m_initDone = true;
			}
		});					
	}

	public void RemoveBike(BikeViewModel context) {
		Log.d("MyApp", "RemoveBike " + context.Model);
		context.Destroy();
		m_Bikes.remove(context);
		context.Delete();
	}
	
	public void AddBike(BikeViewModel context) {
		Log.d("MyApp", "AddBike " + context.Model);
		m_Bikes.add(context);
		context.Commit();
	}
}
