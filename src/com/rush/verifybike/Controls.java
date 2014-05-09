package com.rush.verifybike;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

public class Controls {
	
	private Activity m_Activity;
	
	private SparseArray<View> m_Controls = new SparseArray<View>();
	
	public Controls(Activity _parent) {
		m_Activity = _parent;		
		m_Controls.clear();
		
		if (_parent == null)
			return;							
	}
	
	public View get(int _ctrlId) {
		View ctrl = m_Controls.get(_ctrlId); 
		
		if (ctrl == null) {
			ctrl = m_Activity.findViewById(_ctrlId);
			
			if (ctrl == null) {
				Log.e("MyApp", "Control ID " + _ctrlId + " does not exist");
			}					
		}
		
		return ctrl;
	}		
}
