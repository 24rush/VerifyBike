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
		context.Delete();
		m_Bikes.remove(context);
	}
	
	public void SaveBike(final BikeViewModel context) {
		Log.d("Saving bike " + context.Model);
					
		final boolean isNewObject = context.IsNewObject();
		
		context.Commit(new INotifier<Boolean>() {			
			@Override
			public void OnValueChanged(Boolean value) {
				Log.d("Saving bike returned for " + context.Model);
				if (value == true && isNewObject == true) {
					Log.d("Adding bike to list");
					m_Bikes.add(context);
				}
			}
		});
	}
}
