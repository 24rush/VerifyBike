package com.rush.verifybike;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Bindings {
	public enum Mode { NONE, INVERT, TWO_WAY };

	public static void BindVisible(final View control, Observable<Boolean> source) {
		BindVisible(control, source, Mode.NONE);		
	}

	public static void BindVisible(final View control, Observable<Boolean> source, final Mode flag) {
		INotifier<Boolean> observer = new INotifier<Boolean>() {					
			public void OnValueChanged(Boolean value) {				
				if (flag == Mode.INVERT) value = !value;				
				control.setVisibility(value == true ? View.VISIBLE : View.INVISIBLE);
			}
		};			
		
		observer.OnValueChanged(source.get());
		source.addObserver(observer);			
	}

	public static void BindText(final View control, Observable<String> source) {
		BindText(control, source, Mode.NONE);
	}
	
	public static void BindText(final View control, final Observable<String> source, final Mode flag) {
		INotifier<String> observer = new INotifier<String>() {					
			public void OnValueChanged(String value) {								
				TextView txtCtrl = (TextView)control;				
				
				if (txtCtrl.getText().toString().equals(value) == false) {
					txtCtrl.setText(value);							 
				}
			}
		};
		
		if (flag == Mode.TWO_WAY) {
			TextView txtCtrl = (TextView)control;
			txtCtrl.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
											
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					source.set(s.toString());
				}
			});
		}
		
		observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}
	
	public static <Type> void BindCommand(final Button source, final ICommand<Type> target, final Type context) {
		source.setTag(context);
		source.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				target.Execute(context);
			}
		});
	}
}

interface ICommand<Type> {
	void Execute(Type context);
}