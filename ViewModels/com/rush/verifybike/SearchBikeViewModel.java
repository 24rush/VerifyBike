package com.rush.verifybike;

import com.rush.verifybike.VerificationResult.BikeStatus;

public class SearchBikeViewModel {

	public Observable<String> Model = new Observable<String>("");
	public Observable<BikeStatus> Status = new Observable<BikeStatus>();	
	public Observable<String> SerialNumber = new Observable<String>("");
	
	public Observable<Boolean> IsSearchOnGoing = new Observable<Boolean>(true);

	public void SearchBike() {
		IsSearchOnGoing.set(true);		
				
		DataEndpoint.CheckSerialNumber(SerialNumber.get(), new DataReceivedCallback<VerificationResult>() {					
			public void OnDataReceived(VerificationResult result) {						
				IsSearchOnGoing.set(false);
				LoadData(result);		
			}
		});			
	}
	
	public void LoadData(VerificationResult modelData) {
		if (modelData == null) 
			return;

		Status.set(modelData.Status);	
		Model.set(modelData.Model);		
	}
}
