package com.rush.verifybike;

import android.graphics.Bitmap;
import com.rush.verifybike.VerificationResult.BikeStatus;

public class SearchBikeViewModel {

	public Observable<String> Model = new Observable<String>("");
	public Observable<BikeStatus> Status = new Observable<BikeStatus>();	
	public Observable<String> SerialNumber = new Observable<String>("");

	public Observable<Bitmap> BikePreview = new Observable<Bitmap>();

	public Observable<String> Phone = new Observable<String>();
	public Observable<Boolean> HasPhone = new Observable<Boolean>(false);

	public Observable<String> Email = new Observable<String>();
	public Observable<Boolean> HasEmail = new Observable<Boolean>(false);

	public Observable<Boolean> IsSearchOnGoing = new Observable<Boolean>(false);

	public Observable<Boolean> IsInDatabase = new Observable<Boolean>(false);	

	public void SearchBike() {
		IsSearchOnGoing.set(true);
		Model.set("");
		Phone.set(""); HasPhone.set(false);
		Email.set(""); HasEmail.set(false);

		IsInDatabase.set(false);		

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

		IsInDatabase.set(modelData.Status != BikeStatus.NotInDatabase);

		if (modelData.Status == BikeStatus.Stolen) {
			boolean hasPhone = !modelData.OwnerPhone.equals("");
			boolean hasEmail = !modelData.OwnerEmail.equals("");			

			Phone.set(modelData.OwnerPhone);
			HasPhone.set(hasPhone);

			Email.set(modelData.OwnerEmail);
			HasEmail.set(hasEmail);

			if (modelData.BikePreview != null) {
				BikePreview.set(BitmapUtils.fromByteArray(modelData.BikePreview));
			}
		}

		if (modelData.Status == BikeStatus.Owned) {
			if (modelData.BikePreview != null) {
				BikePreview.set(BitmapUtils.fromByteArray(modelData.BikePreview));
			}
		}
	}
}
