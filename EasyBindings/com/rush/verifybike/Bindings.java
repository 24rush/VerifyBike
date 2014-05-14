package com.rush.verifybike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Bindings {
	public enum Mode { NONE, INVERT, TWO_WAY };
	
	public enum BindingType { TEXT, COMMAND };

	private static HashMap<Observable<?>, List<Binding>> m_Bindings = new HashMap<Observable<?>, List<Binding>>();

	public static void BindImageURI(final ImageView control, Observable<String> source) {
		INotifier<String> observer = new INotifier<String>() {			
			public void OnValueChanged(String value) {
				control.setImageURI(Uri.parse(value));			
			}
		};
		
		if (!source.get().isEmpty())
			observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}
	
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
			TextView txtCtrl = (TextView) control;
			TextWatcher tw = new TextWatcher() {				
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
			};
			
			txtCtrl.addTextChangedListener(tw);				
			addBindingForSource(BindingType.TEXT, source, control, tw);
		}
		
		observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}
	
	public static <Type> void BindCommand(final View source, final ICommand<Type> target, final Type context) {
		source.setTag(context);
		source.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				target.Execute(context);
			}
		});			
	}
	
	private static void addBindingForSource(BindingType type, Observable<?> source, View control, Object extra) {
		List<Binding> existingBindings = m_Bindings.get(source);
		
		if (existingBindings == null)
			existingBindings = new ArrayList<Binding>();
					
		existingBindings.add(new Binding(type, control, extra));
		m_Bindings.put(source, existingBindings);
	}
	
	public static void Remove(Observable<?> source) {
		List<Binding> existingBindings = m_Bindings.get(source);
		
		if (existingBindings == null)
			return;
		
		for (Binding binding : existingBindings) {
			if (binding.Type == BindingType.TEXT) {
				TextView txtCtrl = (TextView) binding.Control;
				txtCtrl.removeTextChangedListener((TextWatcher) binding.Extra);
				
				existingBindings.remove(binding);			
				return;
			}
		}
	}
}

interface ICommand<Type> {
	void Execute(Type context);
}

class Binding {
	public Bindings.BindingType Type;
	public Object Extra;
	public View Control;
	
	public Binding(Bindings.BindingType type, View control, Object extra) {
		Type = type;
		Extra = extra;
		Control = control;
	}
}