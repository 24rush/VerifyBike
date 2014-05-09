package com.rush.verifybike;

import android.view.View;
import android.widget.TextView;

public class Bindings {
	public enum Flag { NONE, INVERT };

	public static void BindVisible(final View control, Observable<Boolean> source) {
		BindVisible(control, source, Flag.NONE);		
	}

	public static void BindVisible(final View control, Observable<Boolean> source, final Flag flag) {
		INotifier<Boolean> observer = new INotifier<Boolean>() {					
			public void OnValueChanged(Boolean value) {				
				if (flag == Flag.INVERT) value = !value;				
				control.setVisibility(value == true ? View.VISIBLE : View.INVISIBLE);
			}
		};			
		
		observer.OnValueChanged(source.get());
		source.addObserver(observer);			
	}

	public static void BindText(final View control, Observable<String> source) {
		INotifier<String> observer = new INotifier<String>() {					
			public void OnValueChanged(String value) {								
				TextView txtCtrl = (TextView)control;				
				txtCtrl.setText(value);
			}
		};
		
		observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}
}
