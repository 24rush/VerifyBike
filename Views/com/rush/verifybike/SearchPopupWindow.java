package com.rush.verifybike;

import java.util.HashMap;
import com.rush.verifybike.VerificationResult.BikeStatus;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SearchPopupWindow {		

	class BikeStatusImage {
		public int TextId;
		public int ImageId;
		
		public BikeStatusImage(int textId, int imageId) {
			TextId = textId;
			ImageId = imageId;
		}
	}

	private HashMap<BikeStatus, BikeStatusImage> m_BikeStatusToImage = new HashMap<VerificationResult.BikeStatus, BikeStatusImage>();
	
	private Bindings Bindings = new Bindings();
	private static PopupWindow m_PopupWindow;		
	
	private void init(final Activity activity) {				
		LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		final View popupView = layoutInflater.inflate(R.layout.layout_popup_window_search_bike, null); 		

		m_PopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		final PopupWindow popupWindow = m_PopupWindow;
		
		m_BikeStatusToImage.put(BikeStatus.Owned, new BikeStatusImage(R.string.txt_bikeStatusOwned, R.drawable.red_alert));
		m_BikeStatusToImage.put(BikeStatus.NotInDatabase, new BikeStatusImage(R.string.txt_bikeStatusNotInDb, R.drawable.orange_alert));
		m_BikeStatusToImage.put(BikeStatus.Stolen, new BikeStatusImage(R.string.txt_bikeStatusStolen, R.drawable.red_alert));		
		
		VM.SearchViewModel.Status.addObserver(new INotifier<VerificationResult.BikeStatus>() {		
			@Override
			public void OnValueChanged(BikeStatus value) {
				BikeStatusImage bikeImagery = m_BikeStatusToImage.get(value);
				Integer textId = bikeImagery.TextId;
				((TextView)popupView.findViewById(R.id.lblBikeStatus)).setText(activity.getString(textId));
								
				ImageView imgStatus = (ImageView) popupView.findViewById(R.id.img_Status);
				imgStatus.setImageResource(bikeImagery.ImageId);						
			}
		});
				
		Bindings.BindText(popupView.findViewById(R.id.txt_Model), VM.SearchViewModel.Model);

		Bindings.BindVisible(popupView.findViewById(R.id.layout_bike_model), VM.SearchViewModel.Model, Converters.StringToBoolean);
		Bindings.BindVisible(popupView.findViewById(R.id.layout_bike_result), VM.SearchViewModel.IsSearchOnGoing, Modes.Invert());
		Bindings.BindVisible(popupView.findViewById(R.id.progress_search), VM.SearchViewModel.IsSearchOnGoing);				

		Bindings.BindText(popupView.findViewById(R.id.btn_search_ok), VM.SearchViewModel.IsSearchOnGoing, new IConvert<Boolean, String>(){
			@Override
			public String Convert(Boolean value) {
				return activity.getString(value ? R.string.txt_cancel : R.string.txt_ok);				
			}});
		
		Button btnDismiss = (Button)popupView.findViewById(R.id.btn_search_ok);
		btnDismiss.setOnClickListener(new Button.OnClickListener(){			
			public void onClick(View v) {
				popupWindow.dismiss();
			}});
	}

	public void Show(Activity activity, VerificationResult result) {
		if (m_PopupWindow == null)
			init(activity);
		
		VM.SearchViewModel.LoadData(result);

		m_PopupWindow.showAtLocation(activity.getWindow().getCurrentFocus(), Gravity.CENTER, 0, 0);
	}
}