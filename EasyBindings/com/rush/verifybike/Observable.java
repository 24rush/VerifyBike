package com.rush.verifybike;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

import com.parse.ParseObject;

public class Observable<Type extends Object> implements Serializable, IObservable<Type> {
	private static final long serialVersionUID = 1L;
	
	protected Type m_Value;
	private IValidator<Type> m_Validator;
	
	private ArrayList<INotifier<Type>> m_Listeners = new ArrayList<INotifier<Type>>() {
		private static final long serialVersionUID = 1L;
	};
	
	private ArrayList<ContextualListener<Type>> m_ContextListeners = new ArrayList<ContextualListener<Type>>();
	
	public Observable() {		
	}
	
	public Observable(Type value) {
		m_Value = value;
	}
	
	public Observable(Type value, IValidator<Type> validator) {
		m_Value = value;
		m_Validator = validator;
	}
	
	public void Destroy() {
		m_Listeners.clear();
		m_ContextListeners.clear();
	}
	
	public boolean IsValid() {
		if (m_Validator == null)
			return true;
		
		return m_Validator.IsValid(m_Value);
	}
	
	@Override
	public String toString() {
		return m_Value.toString();
	}
	
	public void set(Type value) {
		Log.d("MyApp", value == null ? "NULL" : value.toString());
		if (m_Value == null || !m_Value.equals(value)) {
			m_Value = value;
			
			for (INotifier<Type> observer : m_Listeners) {
				observer.OnValueChanged(m_Value);
			}
			
			for (ContextualListener<Type> contextListener : m_ContextListeners) {
				contextListener.m_Observer.OnValueChanged(value, contextListener.m_Context);
			}
		}
	}
	
	public Type get() {
		return m_Value;
	}
	
	public void addObserver(INotifier<Type> observer) {
		m_Listeners.add(observer);
	}
	
	public void addTypelessObserver(INotifier<Object> observer) {
		m_Listeners.add((INotifier<Type>)observer);
	}
	
	public void addObserverContext(IContextNotifier<Type> observer, Object context) {
		m_ContextListeners.add(new ContextualListener<Type>(observer, context));
	}
	
	public void removeObserver(INotifier<Type> observer) {
		m_Listeners.remove(observer);
	}
}

interface IObservable<Type> {
	public void set(Type value);	
	public Type get();
}