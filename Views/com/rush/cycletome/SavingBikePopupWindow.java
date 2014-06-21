package com.rush.cycletome;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.rush.cycletome.Log;
import com.rush.cycletome.R;

public class SavingBikePopupWindow {
	private PopupWindow m_PopupWindow;		
	
	private void init(Activity activity) {				
		LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		final View popupView = layoutInflater.inflate(R.layout.layout_saving_bike, null); 		

		m_PopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public void Show(Activity activity) {
		if (activity.isFinishing() == true) {
			Log.d("Activity " + activity + " is finishing.");
			return;
		}
			
		if (m_PopupWindow == null)
			init(activity);

		m_PopupWindow.setFocusable(true);
		m_PopupWindow.showAtLocation(activity.getWindow().getCurrentFocus(), Gravity.CENTER, 0, 0);
	}

	public void Dismiss() {
		if (m_PopupWindow == null)
			return;
			
		m_PopupWindow.dismiss();
		m_PopupWindow = null;
	}
}
