package com.rush.verifybike;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;

public class SearchBikeViewModel {
	private Activity m_Activity;

	public Observable<String> Model = new Observable<String>("");
	public Observable<String> Status = new Observable<String>("");
	public Observable<Integer> Image = new Observable<Integer>();
	public Observable<String> SerialNumber = new Observable<String>("");
	
	public Observable<Boolean> IsSearchOnGoing = new Observable<Boolean>(true);

	public SearchBikeViewModel(Activity _parent) {
		m_Activity = _parent;
	}

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

		int lblId = R.string.txt_bikeStatusNotInDb;
		int imageStatusId = R.drawable.orange_alert;

		switch (modelData.Status) {
		case Owned:
			lblId = R.string.txt_bikeStatusOwned;
			imageStatusId = R.drawable.red_alert;
			break;

		case Stolen:
			lblId = R.string.txt_bikeStatusStolen;
			imageStatusId = R.drawable.red_alert;
		default:
			break;
		}

		Status.set(m_Activity.getString(lblId));	
		Model.set(modelData.Model);
		Image.set(imageStatusId);			
	}
}
