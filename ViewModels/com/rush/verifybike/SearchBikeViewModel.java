package com.rush.verifybike;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.rush.verifybike.VerificationResult.BikeStatus;

import android.app.Activity;
import android.util.Log;

public class SearchBikeViewModel {
	private Activity m_Activity;

	public Observable<String> Model = new Observable<String>("");
	public Observable<String> Status = new Observable<String>("");
	public Observable<Integer> Image = new Observable<Integer>(R.drawable.orange_alert);
	public Observable<String> SerialNumber = new Observable<String>("");
	
	public Observable<Boolean> IsSearchOnGoing = new Observable<Boolean>(true);

	class BikeStatusImage {
		public int TextId;
		public int ImageId;
		
		public BikeStatusImage(int textId, int imageId) {
			TextId = textId;
			ImageId = imageId;
		}
	}
	
	private HashMap<BikeStatus, BikeStatusImage> m_BikeStatusToImage = new HashMap<VerificationResult.BikeStatus, SearchBikeViewModel.BikeStatusImage>();
	
	public SearchBikeViewModel(Activity _parent) {
		m_Activity = _parent;
		
		m_BikeStatusToImage.put(BikeStatus.Owned, new BikeStatusImage(R.string.txt_bikeStatusOwned, R.drawable.red_alert));
		m_BikeStatusToImage.put(BikeStatus.NotInDatabase, new BikeStatusImage(R.string.txt_bikeStatusNotInDb, R.drawable.orange_alert));
		m_BikeStatusToImage.put(BikeStatus.Stolen, new BikeStatusImage(R.string.txt_bikeStatusStolen, R.drawable.red_alert));
	}

	public void SearchBike() {
		IsSearchOnGoing.set(true);		
				
		DataEndpoint.CheckSerialNumber(SerialNumber.get(), new DataReceivedCallback<VerificationResult>() {					
			public void OnDataReceived(VerificationResult result) {		
				Log.d("MyApp", "search ret");
				IsSearchOnGoing.set(false);
				LoadData(result);		
			}
		});			
	}
	
	public void LoadData(VerificationResult modelData) {
		if (modelData == null) 
			return;

		BikeStatusImage bikeStatus = m_BikeStatusToImage.get(modelData.Status);

		Status.set(m_Activity.getString(bikeStatus.TextId));	
		Model.set(modelData.Model);
		Image.set(bikeStatus.ImageId);			
	}
}
