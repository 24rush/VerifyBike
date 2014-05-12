package com.rush.verifybike;

import java.io.Serializable;
import java.util.ArrayList;

public class Observable<Type> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Type m_Value;
	
	private ArrayList<INotifier<Type>> m_Listeners = new ArrayList<INotifier<Type>>() {
		private static final long serialVersionUID = 1L;
	};
	
	private ArrayList<ContextualListener<Type>> m_ContextListeners = new ArrayList<ContextualListener<Type>>();
	
	public Observable() {
		
	}
	
	public Observable(Type value) {
		set(value);
	}
	
	@Override
	public String toString() {
		return m_Value.toString();
	}
	
	public void set(Type value) {
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
	
	public void addObserverContext(IContextNotifier<Type> observer, Object context) {
		m_ContextListeners.add(new ContextualListener<Type>(observer, context));
	}
	
	public void removeObserver(INotifier<Type> observer) {
		m_Listeners.remove(observer);
	}
}