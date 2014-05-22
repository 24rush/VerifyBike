package com.rush.verifybike;

import java.util.List;

public class BikesViewModel {
	
	private Boolean m_initDone = false;
	private ObservableCollection<BikeViewModel> m_Bikes = new ObservableCollection<BikeViewModel>();
	
	public Observable<Boolean> BikesLoaded = new Observable<Boolean>(false);
	
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
				BikesLoaded.set(true);
			}
		});					
	}

	public void RemoveBike(BikeViewModel context) {
		Log.d("Removed bike " + context.Model);		
		m_Bikes.remove(context);
		context.Delete();
	}
	
	public void SaveBike(BikeViewModel context) {
		Log.d("Added bike " + context.Model);
		
		if (context.IsNewObject())
			m_Bikes.add(context);
		
		context.Commit();
	}
}
