package com.rush.verifybike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rush.verifybike.Modes.Mode;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

class Modes {
	
	public enum Mode { None, Invert, TwoWay };	

	public class BindingMode { public Mode Type = Mode.None;  }
	public class BindingModeNone extends BindingMode {};		
	public class BindingModeInvert extends BindingMode { public Mode Type = Mode.Invert; };	
	public class BindingModeTwoWay extends BindingMode { public Mode Type = Mode.TwoWay; };

	private static BindingModeNone _none;
	public static BindingModeNone None() { if (_none == null) _none = (new Modes()).new BindingModeNone(); return _none;}

	private static BindingModeInvert _invert;
	public static BindingModeInvert Invert()  { if (_invert == null) _invert = (new Modes()).new BindingModeInvert(); return _invert;}

	private static BindingModeTwoWay _twoway;
	public static BindingModeTwoWay TwoWay()  { if (_twoway == null) _twoway = (new Modes()).new BindingModeTwoWay(); return _twoway;}
}

public class Bindings {
	public enum BindingType { TEXT, COMMAND };

	private static HashMap<Observable<?>, List<Binding>> m_Bindings = new HashMap<Observable<?>, List<Binding>>();

	public static void BindEnabled(final View control, Observable<Boolean> source) {
		INotifier<Boolean> observer = new INotifier<Boolean>() {					
			public void OnValueChanged(Boolean value) {			
				control.setEnabled(value);
			}
		};

		observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}

	public static void BindChecked(final CheckBox control, final Observable<Boolean> source, Modes.BindingMode flags) {
		INotifier<Boolean> observer = new INotifier<Boolean>() {					
			public void OnValueChanged(Boolean value) {			
				control.setChecked(value);
			}
		};

		if (flags.Type == Mode.TwoWay) {
			CheckBox chkCtrl = (CheckBox) control;
			chkCtrl.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					source.set(isChecked);
				}
			});
		}

		observer.OnValueChanged(source.get());
	}

	public static void BindImageURI(final ImageView control, Observable<String> source) {
		INotifier<String> observer = new INotifier<String>() {			
			public void OnValueChanged(String value) {				
				control.setImageURI(Uri.parse(value));			
			}
		};

		if (source.get() != null && !source.get().isEmpty())
			observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}

	public static void BindImageBitmap(final ImageView control, Observable<Bitmap> source) {
		INotifier<Bitmap> observer = new INotifier<Bitmap>() {	
			public void OnValueChanged(Bitmap value) {		
				control.setImageBitmap(value);			
			}
		};

		if (source.get() != null)
			observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}
	
	//
	// Visibility
	//

	public static void BindVisible(final View control, Observable<Boolean> source, final Modes.BindingMode flag) {
		BindVisible(control, source, flag, Converters.ForwardBoolean);		
	}

	public static void BindVisible(final View control, Observable<Boolean> source) {
		BindVisible(control, source, Modes.None(), Converters.ForwardBoolean);		
	}

	public static <S> void BindVisible(final View control, Observable<S> source, IConvert<S, Boolean> converter) {
		BindVisible(control, source, Modes.None(), converter);		
	}

	public static <S> void BindVisible(final View control, Observable<S> source, final Modes.BindingMode flag, final IConvert<S, Boolean> converter) {
		INotifier<S> observer = new INotifier<S>() {					
			public void OnValueChanged(S val) {
				Boolean value = converter.Convert(val);				
				if (flag.Type == Mode.Invert) value = !value;				
				control.setVisibility(value == true ? View.VISIBLE : View.INVISIBLE);
			}
		};			
		
		observer.OnValueChanged(source.get());
		source.addObserver(observer);			
	}

	// 
	// Text
	//

	public static void BindText(final View control, Observable<String> source) {
		BindText(control, source, Converters.ForwardString);
	}

	public static void BindText(final View control, final Observable<String> source, final Modes.BindingModeTwoWay flag) {
		BindText(control, source, Converters.ForwardString);

		TextView txtCtrl = (TextView) control;
		TextWatcher tw = new TextWatcher() {				
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				source.set(s.toString());
			}
		};

		txtCtrl.addTextChangedListener(tw);				
		addBindingForSource(BindingType.TEXT, source, control, tw);	
	}

	public static <S> void BindText(final View control, final Observable<S> source, final IConvert<S, String> converter) {
		INotifier<S> observer = new INotifier<S>() {					
			public void OnValueChanged(S val) {
				String value = converter.Convert(val);
				TextView txtCtrl = (TextView)control;				

				if (txtCtrl.getText().toString().equals(value) == false) {
					txtCtrl.setText(value);							 
				}
			}
		};

		observer.OnValueChanged(source.get());
		source.addObserver(observer);
	}

	//
	// Commands
	//
	
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