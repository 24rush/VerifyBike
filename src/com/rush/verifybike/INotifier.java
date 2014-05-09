package com.rush.verifybike;

public interface INotifier<Type> {
	public void OnValueChanged(Type value);
}
