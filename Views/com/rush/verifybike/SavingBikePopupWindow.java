package com.rush.verifybike;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class SavingBikePopupWindow {
	private PopupWindow m_PopupWindow;		
	
	private void init(Activity activity) {				
		LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		final View popupView = layoutInflater.inflate(R.layout.layout_saving_bike, null); 		

		m_PopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public void Show(Activity activity) {
		if (m_PopupWindow == null)
			init(activity);

		m_PopupWindow.setFocusable(true);
		m_PopupWindow.showAtLocation(activity.getWindow().getCurrentFocus(), Gravity.CENTER, 0, 0);
	}

	public void Dismiss() {
		m_PopupWindow.dismiss();
	}
}
