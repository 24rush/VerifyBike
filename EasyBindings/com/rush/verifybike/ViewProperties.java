package com.rush.verifybike;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

interface AbstractViewBinding <ControlType, ValueType>{
	void Bind(ControlType control, Observable<ValueType> value, Mode flag);
}

class ViewProperties {
	public static Enabled Enabled = new Enabled();
	public static ImageURI ImageURI = new ImageURI();
	public static ImageBitmap ImageBitmap = new ImageBitmap();
	public static Checked Checked = new Checked();
	public static Text Text = new Text();
	public static Visible Visible = new Visible();
	public static VisibleString VisibleString = new VisibleString();
}

class Enabled implements AbstractViewBinding<View, Boolean> {
	@Override
	public void Bind(final View control, Observable<Boolean> value, final Mode flag) {
		INotifier<Boolean> notifier = new INotifier<Boolean>() {			
			@Override
			public void OnValueChanged(Boolean value) {
				Boolean boolValue = value;
				if (flag == Mode.Invert) boolValue = !boolValue;
				control.setEnabled(boolValue);
				control.setClickable(boolValue);
			}
		};
		
		value.addObserver(notifier);
		notifier.OnValueChanged(value.get());			
	}
}

class ImageURI implements AbstractViewBinding<ImageView, String> {
	@Override
	public void Bind(final ImageView control, Observable<String> value, Mode flag) {
		INotifier<String> notifier = new INotifier<String>() {			
			@Override
			public void OnValueChanged(String value) {
				if (value != null && !value.isEmpty())
					control.setImageURI(Uri.parse(value));
			}
		};
		
		value.addObserver(notifier);
		notifier.OnValueChanged(value.get());		
	}	
}

class ImageBitmap implements AbstractViewBinding<ImageView, Bitmap> {
	@Override
	public void Bind(final ImageView control, Observable<Bitmap> value, Mode flag) {		
		INotifier<Bitmap> notifier = new INotifier<Bitmap>() {			
			@Override
			public void OnValueChanged(Bitmap value) {
				control.setImageBitmap(value);	
			}
		};
		
		value.addObserver(notifier);
		notifier.OnValueChanged(value.get());
	}	
}

class Checked implements AbstractViewBinding<CheckBox, Boolean> {
	private OnCheckedChangeListener _checkedListener;

	@Override
	public void Bind(final CheckBox control, final Observable<Boolean> value, final Mode flag) {
		INotifier<Boolean> notifier = new INotifier<Boolean>() {		
			@Override
			public void OnValueChanged(Boolean value) {
				boolean boolValue = value;
				if (flag == Mode.Invert) boolValue = !boolValue;
				control.setChecked(boolValue);				
			}
		};

		value.addObserver(notifier);
		notifier.OnValueChanged(value.get());

		if (flag == Mode.TwoWay) {
			if (_checkedListener == null) {
				_checkedListener = new OnCheckedChangeListener() {					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {				
						value.set(isChecked);
					}
				};

				control.setOnCheckedChangeListener(_checkedListener);
			}									
		}
	}
}

class Text implements AbstractViewBinding<View, String> {

	private TextWatcher _textWatcher;

	@Override
	public void Bind(final View control, final Observable<String> value, Mode flag) {
		INotifier<String> notifier = new INotifier<String>() {
			@Override
			public void OnValueChanged(String value) {
				TextView txtCtrl = (TextView) control;
				if (txtCtrl.getText().toString().equals(value) == false) 
					txtCtrl.setText(value);	
			}
		};

		value.addObserver(notifier);
		notifier.OnValueChanged(value.get());	

		if (flag == Mode.TwoWay) {
			if (_textWatcher == null) {
				_textWatcher = new TextWatcher() {				
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						value.set(s.toString());
					}
				};										
			} else {			
				((TextView)control).removeTextChangedListener(_textWatcher);
			}

			((TextView)control).addTextChangedListener(_textWatcher);
		}
	}	
}

class Visible implements AbstractViewBinding<View, Boolean> {
	@Override
	public void Bind(final View control, Observable<Boolean> value, final Mode flag) {		
		INotifier<Boolean> notifier = new INotifier<Boolean>() {			
			@Override
			public void OnValueChanged(Boolean value) {
				boolean boolValue = value;
				if (flag == Mode.Invert) boolValue = !boolValue;

				control.setVisibility(boolValue == true ? View.VISIBLE : View.GONE);
			}
		};

		value.addObserver(notifier);
		notifier.OnValueChanged(value.get());		
	}	
}

class VisibleString implements AbstractViewBinding<View, String> {
	@Override
	public void Bind(final View control, Observable<String> value, final Mode flag) {		
		INotifier<String> notifier = new INotifier<String>() {			
			@Override
			public void OnValueChanged(String value) {
				Boolean c = false;
				Converters.Convert(value, c);

				if (flag == Mode.Invert) c = !c;
				control.setVisibility(c == true ? View.VISIBLE : View.GONE);			
			}
		};
		
		value.addObserver(notifier);
		notifier.OnValueChanged(value.get());
	}
}