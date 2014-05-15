package com.rush.verifybike;

import com.rush.verifybike.Bindings.Mode;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class SearchPopupWindow {		

	private PopupWindow m_PopupWindow;	
	private SearchBikeViewModel m_SearchBikeViewModel;	

	public SearchPopupWindow(SearchBikeViewModel searchBikeViewModel) {
		m_SearchBikeViewModel = searchBikeViewModel;
	}
	
	private void init(Activity activity) {				
		LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		final View popupView = layoutInflater.inflate(R.layout.layout_popup_window_search_bike, null); 		

		m_PopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		final PopupWindow popupWindow = m_PopupWindow;

		Bindings.BindText(popupView.findViewById(R.id.lblBikeStatus), m_SearchBikeViewModel.Status);
		Bindings.BindText(popupView.findViewById(R.id.txt_Model), m_SearchBikeViewModel.Model);

		Bindings.BindVisible(popupView.findViewById(R.id.layout_bike_result), m_SearchBikeViewModel.IsSearchOnGoing, Mode.INVERT);
		Bindings.BindVisible(popupView.findViewById(R.id.progress_search), m_SearchBikeViewModel.IsSearchOnGoing);		

		m_SearchBikeViewModel.Image.addObserver(new INotifier<Integer>() {			
			public void OnValueChanged(Integer value) {
				ImageView imgStatus = (ImageView) popupView.findViewById(R.id.img_Status);
				imgStatus.setImageResource(value);				
			}
		});	

		Button btnDismiss = (Button)popupView.findViewById(R.id.btn_search_ok);
		btnDismiss.setOnClickListener(new Button.OnClickListener(){			
			public void onClick(View v) {
				popupWindow.dismiss();
			}});
	}

	public void Show(Activity activity, VerificationResult result) {
		if (m_PopupWindow == null)
			init(activity);

		m_SearchBikeViewModel.LoadData(result);

		m_PopupWindow.showAtLocation(activity.getWindow().getCurrentFocus(), Gravity.CENTER, 0, 0);
	}

	public void UpdateResult(VerificationResult result) {
		m_SearchBikeViewModel.LoadData(result);	
	}
}