package com.rush.verifybike;

import java.util.ArrayList;

public class Observable<Type> {
	private Type m_Value;
	private ArrayList<INotifier<Type>> m_Listeners = new ArrayList<INotifier<Type>>() {
		private static final long serialVersionUID = 1L;
	};
	
	public Observable() {
		
	}
	
	public Observable(Type value) {
		set(value);
	}
	
	public void set(Type value) {
		if (m_Value != value) {
			m_Value = value;
			
			for (INotifier<Type> observer : m_Listeners) {
				observer.OnValueChanged(m_Value);
			}
		}
	}
	
	public Type get() {
		return m_Value;
	}
	
	public void addObserver(INotifier<Type> observer) {
		m_Listeners.add(observer);
	}
	
	public void removeObserver(INotifier<Type> observer) {
		m_Listeners.remove(observer);
	}
}
