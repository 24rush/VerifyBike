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

interface AbstractViewProperty <ControlType, ValueType>{
	void Execute(ControlType control, ValueType value, Mode flag);
}

interface ObservableToControl <ValueType, ControlType> {
	void Execute(Observable<ValueType> observable, ControlType control, Mode flag);
}

class ViewProperties {
	public static Enabled Enabled = new Enabled();
	public static ImageURI ImageURI = new ImageURI();
	public static ImageBitmap ImageBitmap = new ImageBitmap();
	public static Checked Checked = new Checked();
	public static Text Text = new Text();
	public static Visible Visible = new Visible();
	public static VisibleString VisibleString = new VisibleString();
	
	public static FromCheckbox FromCheckbox = new FromCheckbox();
	public static FromTextView FromTextView = new FromTextView();
}

class Enabled implements AbstractViewProperty<View, Boolean> {
	@Override
	public void Execute(View control, Boolean value, Mode flag) {
		if (flag == Mode.Invert) value = !value;
		control.setEnabled(value);
		control.setClickable(value);
	}
}

class ImageURI implements AbstractViewProperty<ImageView, String> {
	@Override
	public void Execute(ImageView control, String value, Mode flag) {	
		if (value != null && !value.isEmpty())
			control.setImageURI(Uri.parse(value));
	}	
}

class ImageBitmap implements AbstractViewProperty<ImageView, Bitmap> {
	@Override
	public void Execute(ImageView control, Bitmap value, Mode flag) {			
		control.setImageBitmap(value);
	}	
}

class Checked implements AbstractViewProperty<CheckBox, Boolean> {	
	@Override
	public void Execute(CheckBox control, Boolean value, Mode flag) {
		if (flag == Mode.Invert) value = !value;
		control.setChecked(value);
	}	
}

class Text implements AbstractViewProperty<View, String> {	
	@Override
	public void Execute(View control, String value, Mode flag) {
		TextView txtCtrl = (TextView) control;
		if (txtCtrl.getText().toString().equals(value) == false) 
			txtCtrl.setText(value);
	}	
}

class Visible implements AbstractViewProperty<View, Boolean> {
	@Override
	public void Execute(View control, Boolean value, Mode flag) {
		if (flag == Mode.Invert) value = !value;
		control.setVisibility(value == true ? View.VISIBLE : View.GONE);
	}	
}

class VisibleString implements AbstractViewProperty<View, String> {
	@Override
	public void Execute(View control, String value, Mode flag) {
		Boolean c = false;
		Converters.Convert(value, c);
		
		if (flag == Mode.Invert) c = !c;
		control.setVisibility(c == true ? View.VISIBLE : View.GONE);
	}	
}

class FromCheckbox implements ObservableToControl<Boolean, CheckBox> {
	@Override
	public void Execute(final Observable<Boolean> observable, CheckBox control, Mode flag) {		
		control.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				observable.set(isChecked);
			}
		});
	}
}

class FromTextView implements ObservableToControl<String, View> {
	@Override
	public void Execute(final Observable<String> observable, View control, Mode flag) {		
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
				observable.set(s.toString());
			}
		};

		((TextView)control).addTextChangedListener(tw);		
	}
}