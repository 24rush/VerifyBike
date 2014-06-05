package com.rush.verifybike;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;

enum Mode { None, Invert, TwoWay };	

public class Bindings {
	public enum BindingType { TEXT, COMMAND, NONE };

	private List<Binding> m_Bindings = new ArrayList<Binding>();
		
	private void addBindingForSource(Observable<?> source, View control, Object extra) {
		m_Bindings.add(new Binding(source, control, extra));		
	}

	@SuppressWarnings("unchecked")
	public void Destroy() {
		for (Binding binding : m_Bindings) {
			binding.Observable.removeObserver((INotifier<Object>)binding.Extra);
		}
	}
	
	private <ControlType, ValueType> void bind(final ControlType control, Observable<ValueType> observable, final AbstractViewProperty<ControlType, ValueType> action) {
		bind(control, observable, action, Mode.None, null);
	}
	
	private <ControlType, ValueType> void bind(final ControlType control, Observable<ValueType> observable, final AbstractViewProperty<ControlType, ValueType> action, final Mode flag, ObservableToControl<ValueType, ControlType> observableAction) {
		INotifier<ValueType> observer = new INotifier<ValueType>() {
			public void OnValueChanged(ValueType val) {													
				action.Execute(control, val, flag);
			}
		};			
		
		observer.OnValueChanged(observable.get());
		observable.addObserver(observer);
		
		if (flag == Mode.TwoWay && observableAction != null) {
			observableAction.Execute(observable, control, flag);
		}
		
		addBindingForSource(observable, (View) control, observer);
	}
	
	// Enabled
	
	public void BindEnabled(final View control, Observable<Boolean> source) {
		bind(control, source, ViewProperties.Enabled);						
	}

	// Checked
	
	public void BindChecked(final CheckBox control, final Observable<Boolean> source, Mode flags) {		
		bind(control, source, ViewProperties.Checked, flags, ViewProperties.FromCheckbox);			
	}

	// ImageURI
	
	public void BindImageURI(final ImageView control, Observable<String> source) {
		bind(control, source, ViewProperties.ImageURI);
	}

	// ImageBitmap
	
	public void BindImageBitmap(final ImageView control, Observable<Bitmap> source) {
		bind(control, source, ViewProperties.ImageBitmap);
	}
	
	//
	// Visibility
	//
	
	public void BindVisible(final View control, Observable<Boolean> source) {
		bind(control, source, ViewProperties.Visible, Mode.None, null);
	}
	
	public void BindVisible(final View control, Observable<Boolean> source, final Mode flag) {
		bind(control, source, ViewProperties.Visible, flag, null);
	}

	// 
	// Text
	//

	public void BindText(final View control, Observable<String> source) {
		bind(control, source, ViewProperties.Text);
	}

	public void BindText(final View control, final Observable<String> source, final Mode flag) {
		bind(control, source, ViewProperties.Text, flag, ViewProperties.FromTextView);
	}

	//
	// Commands
	//
	
	public <Type> void BindCommand(final View source, final ICommand<Type> target, final Type context) {
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

class Binding {	
	public Observable<?> Observable;
	public Object Extra;
	public View Control;

	public Binding(Observable<?> obs, View control, Object extra) {
		Observable = obs;
		Extra = extra;
		Control = control;
	}
}