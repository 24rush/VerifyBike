package com.rush.verifybike;

import android.graphics.Bitmap;
import com.rush.verifybike.VerificationResult.BikeStatus;

public class SearchBikeViewModel {

	public Observable<String> Model = new Observable<String>("");
	public Observable<BikeStatus> Status = new Observable<BikeStatus>();	
	public Observable<String> SerialNumber = new Observable<String>("");

	public Observable<Bitmap> BikePreview = new Observable<Bitmap>();
	public Observable<String> Phone = new Observable<String>();
	public Observable<String> Email = new Observable<String>();

	public Observable<Boolean> IsSearchOnGoing = new Observable<Boolean>(false);
	public Observable<Boolean> IsStolen = new Observable<Boolean>(false);
	
	public void SearchBike() {
		IsSearchOnGoing.set(true);		

		DataEndpoint.CheckSerialNumber(SerialNumber.get(), new DataReceivedCallback<VerificationResult>() {					
			public void OnDataReceived(VerificationResult result) {						
				IsSearchOnGoing.set(false);
				LoadData(result);		
			}
		});			
	}

	public void CancelSearch() {
		DataEndpoint.CancelQueries();
	}
	
	public void LoadData(VerificationResult modelData) {
		if (modelData == null) 
			return;

		Status.set(modelData.Status);	
		Model.set(modelData.Model);
		BikePreview.set(null);

		if (modelData.Status == BikeStatus.Stolen) {
			IsStolen.set(!modelData.OwnerPhone.equals("") || !modelData.OwnerEmail.equals(""));
			
			Phone.set(modelData.OwnerPhone);
			Email.set(modelData.OwnerEmail);
			
			if (modelData.BikePreview != null) {
				BikePreview.set(BitmapUtils.fromByteArray(modelData.BikePreview));
			}
		}
	}
}
